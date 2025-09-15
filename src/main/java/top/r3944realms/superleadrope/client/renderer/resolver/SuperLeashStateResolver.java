/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope.client.renderer.resolver;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.client.renderer.state.SuperLeashRenderState;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.ILeashData;
import top.r3944realms.superleadrope.content.capability.inter.ILeashState;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.util.capability.LeashStateAPI;

import java.util.*;

//TODO: 未来实现更高级的渲染
public class SuperLeashStateResolver {

    private static final float MAX_TENSION = 1.5f;
    private static final float THICKNESS_BASE = 0.1f;
    private static final float THICKNESS_TENSION = 0.15f;

    /** 上一帧缓存 */
    private static final Map<UUID, FrameCache> frameCacheMap = new HashMap<>();

    private record FrameCache(Vec3 lastStartPos, Vec3 lastEndPos, float lastSwingAngle, float lastSwingSpeed) {}

    private record SwingDynamics(float angle, float speed) {}

    /* ------------------------ 主解析方法 ------------------------ */

    public static Optional<SuperLeashRenderState> resolve(Entity holder, Entity leashedEntity,
                                                          ILeashData.LeashInfo leashInfo, float partialTicks) {

        if (holder == null || leashedEntity == null) return Optional.empty();

        Optional<ILeashState> leashedEntityStateOpt = LeashStateAPI.getLeashState(leashedEntity);
        if (leashedEntityStateOpt.isEmpty()) return Optional.empty();

        ILeashState leashedEntityState = leashedEntityStateOpt.get();
        Optional<ILeashState.LeashState> leashStateOpt = leashedEntityState.getLeashState(holder);
        if (leashStateOpt.isEmpty()) return Optional.empty();

        ILeashState.LeashState leashState = leashStateOpt.get();

        // 计算实体端偏移
        Vec3 currentEntityOffset = getEntityLeashOffset(leashedEntity, leashState, partialTicks);

        // 计算持有者端偏移
        Vec3 holderOffset = getHolderOffset(holder, leashState);

        // 获取插值后的位置
        Vec3 currentHolderPos;
        if (holder instanceof Player player) {
             // 玩家手部偏移 + 旋转矩阵（第一人称/第三人称都适用）
            if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                currentHolderPos = getFirstPersonLeashPos(player, partialTicks);
            } else {
                currentHolderPos = getEntityLeashHolderPos(player, holderOffset, partialTicks);
            }

        } else if (holder instanceof SuperLeashKnotEntity) {
            currentHolderPos = getInterpolatedPosition(holder, partialTicks);
        } else {
            // 普通实体，偏移 + 插值 + 旋转
            currentHolderPos = getEntityLeashHolderPos(holder, holderOffset, partialTicks);
        }
        Vec3 currentEntityPos = getInterpolatedPosition(leashedEntity, partialTicks)
                .add(currentEntityOffset);

        // 上一帧缓存
        FrameCache cache = frameCacheMap.get(leashedEntity.getUUID());
        Vec3 lastHolderPos = cache != null ? cache.lastStartPos() : currentHolderPos;
        Vec3 lastEntityPos = cache != null ? cache.lastEndPos() : currentEntityPos;
        float lastAngle = cache != null ? cache.lastSwingAngle() : 0f;
        float lastSpeed = cache != null ? cache.lastSwingSpeed() : 0f;

        // 物理参数
        double distance = currentHolderPos.distanceTo(currentEntityPos);
        double maxDistance = leashInfo.maxDistance();
        double elasticDistance = leashInfo.elasticDistance();
        float tension = calculateTension(distance, maxDistance, elasticDistance);
        float stretchRatio = (float) (distance / maxDistance);
        boolean isCritical = distance > maxDistance * 1.5;

        // 摆动动态
        SwingDynamics swing = calculateSwingDynamics(
                currentHolderPos, currentEntityPos,
                lastHolderPos, lastEntityPos,
                lastAngle, lastSpeed,
                tension
        );

        // 更新缓存
        frameCacheMap.put(leashedEntity.getUUID(),
                new FrameCache(currentHolderPos, currentEntityPos, swing.angle(), swing.speed()));

        return Optional.of(new SuperLeashRenderState(
                currentHolderPos, currentEntityPos,
                lastHolderPos, lastEntityPos,
                tension, stretchRatio, isCritical,
                leashInfo.keepLeashTicks(),
                selectColor(tension, isCritical),
                THICKNESS_BASE + tension * THICKNESS_TENSION,
                swing.angle(), swing.speed(),
                (float) leashInfo.maxDistance()
        ));
    }

    /* ------------------------ 实体偏移计算 ------------------------ */
    private static Vec3 getEntityLeashOffset(Entity entity, ILeashState.LeashState leashState, float partialTicks) {
        // 获取实体自身的 LeashState 偏移
        Optional<ILeashState> entityStateOpt = LeashStateAPI.getLeashState(entity);
        Vec3 baseOffset = entityStateOpt
                .map(eState -> eState.getLeashApplyEntityLocationOffset()
                        .orElse(eState.getDefaultLeashApplyEntityLocationOffset()))
                .orElse(Vec3.ZERO);

        // 加上单条绳子的额外偏移
        baseOffset = baseOffset.add(leashState.applyEntityLocationOffset());

        // 获取旋转角度
        float pitch = Mth.lerp(partialTicks, entity.getXRot(), entity.xRotO) * ((float)Math.PI / 180F);
        float yaw   = Mth.lerp(partialTicks, entity.getYRot(), entity.yRotO) * ((float)Math.PI / 180F);
        float roll  = 0f;

        // 玩家特殊处理：飞行/旋转攻击可能需要 roll
        if (entity instanceof Player player && (player.isFallFlying() || player.isAutoSpinAttack())) {
            Vec3 view = player.getViewVector(partialTicks);
            Vec3 motion = player.getDeltaMovement();
            double motionLenSq = motion.horizontalDistanceSqr();
            double viewLenSq = view.horizontalDistanceSqr();
            if (motionLenSq > 0 && viewLenSq > 0) {
                double dot = (motion.x * view.x + motion.z * view.z) / Math.sqrt(motionLenSq * viewLenSq);
                double cross = motion.x * view.z - motion.z * view.x;
                roll = (float)(Math.signum(cross) * Math.acos(dot));
            }
        }

        // 将偏移从局部坐标转换到世界坐标
        return baseOffset.xRot(-pitch).yRot(-yaw).zRot(-roll);
    }
    private static Vec3 getHolderOffset(Entity holder, ILeashState.LeashState leashState) {
        if (LeashStateAPI.Query.hasLeashState(holder)) {
            Optional<ILeashState> holderStateOpt = LeashStateAPI.getLeashState(holder);
            if (holderStateOpt.isPresent()) {
                ILeashState holderState = holderStateOpt.get();
                return holderState.getLeashApplyEntityLocationOffset()
                        .orElse(holderState.getDefaultLeashApplyEntityLocationOffset());
            }
        }
        return Optional.ofNullable(leashState.holderLocationOffset())
                .orElse(leashState.defaultHolderLocationOffset());
    }
    private static boolean holderHasLeashState(Entity holder) {
        if (LeashStateAPI.Query.hasLeashState(holder)) {
            Optional<ILeashState> holderStateOpt = LeashStateAPI.getLeashState(holder);
            return holderStateOpt.isPresent();
        }
        return false;
    }
    /**
     * 获取玩家挂点位置，支持旋转偏移
     */
    private static Vec3 getFirstPersonLeashPos(Player player, float partialTicks) {
        // 插值旋转角度
        float yaw = Mth.lerp(partialTicks, player.yRotO, player.getYRot()) * ((float)Math.PI / 180F);
        float pitch = Mth.lerp(partialTicks, player.xRotO, player.getXRot()) * ((float)Math.PI / 180F);
        float roll = 0f;

        // 计算 roll（飞行或旋转攻击时）
        if (player.isFallFlying() || player.isAutoSpinAttack()) {
            Vec3 view = player.getViewVector(partialTicks);
            Vec3 motion = player.getDeltaMovement();
            double motionLenSq = motion.horizontalDistanceSqr();
            double viewLenSq = view.horizontalDistanceSqr();
            if (motionLenSq > 0 && viewLenSq > 0) {
                double dot = (motion.x*view.x + motion.z*view.z) / Math.sqrt(motionLenSq*viewLenSq);
                double cross = motion.x*view.z - motion.z*view.x;
                roll = (float)(Math.signum(cross) * Math.acos(dot));
            }
        }

        // 手部偏移
        double side = player.getMainArm() == HumanoidArm.RIGHT ? -1.0D : 1.0D;
        Vec3 localPos = new Vec3(0.39D * side, -0.6D, 0.3D);

        // 局部坐标旋转到世界坐标
        Vec3 rotatedPos = localPos.xRot(-pitch).yRot(-yaw).zRot(-roll);

        // 返回世界坐标（玩家眼睛位置 + 手部旋转偏移）
        return player.getEyePosition(partialTicks).add(rotatedPos);
    }
    /**
     * 获取实体挂点位置，支持旋转偏移
     */
    public static Vec3 getEntityLeashHolderPos(Entity entity, Vec3 baseOffset, float partialTicks) {

        Vec3 pos = entity.getPosition(partialTicks);
        double xOffset = baseOffset.x();
        double yOffset = baseOffset.y();
        double zOffset = baseOffset.z();

        float pitch = Mth.lerp(partialTicks, entity.getXRot(), entity.xRotO) * ((float)Math.PI / 180F);
        float yaw = Mth.lerp(partialTicks, entity.getYRot(), entity.yRotO) * ((float)Math.PI / 180F);

        if (entity instanceof Player player) {
            xOffset *= player.getMainArm() == HumanoidArm.RIGHT ? -1.0D : 1.0D;

            if (!player.isFallFlying() && !player.isAutoSpinAttack()) {
                if (player.isVisuallySwimming()) {
                    pos = pos.add(new Vec3(xOffset, 0.2D, -0.15D).xRot(-pitch).yRot(-yaw));
                } else {
                    double yBase = player.getBoundingBox().getYsize() - 1.0D;
                    double yAdjust = player.isCrouching() ? -0.2D : 0.07D;
                    pos = pos.add(new Vec3(xOffset, yBase, yAdjust).yRot(-yaw));
                }
            } else {
                Vec3 view = player.getViewVector(partialTicks);
                Vec3 motion = player.getDeltaMovement();
                float roll = computeRoll(view, motion);
                pos = pos.add(new Vec3(xOffset, -0.11D, 0.85D).zRot(-roll).xRot(-pitch).yRot(-yaw));
            }
        } else {
            double yBase = entity.getBbHeight() * 0.5 + yOffset;
            pos = pos.add(new Vec3(xOffset, yBase, zOffset).yRot(-yaw).xRot(-pitch));
        }

        return pos;
    }

    private static float computeRoll(Vec3 view, Vec3 motion) {
        double d1 = motion.horizontalDistanceSqr();
        double d2 = view.horizontalDistanceSqr();
        if (d1 > 0.0 && d2 > 0.0) {
            double dot = (motion.x * view.x + motion.z * view.z) / Math.sqrt(d1 * d2);
            double cross = motion.x * view.z - motion.z * view.x;
            return (float)(Math.signum(cross) * Math.acos(dot));
        }
        return 0.0F;
    }

    private static Vec3 getInterpolatedPosition(Entity entity, float partialTicks) {
        return entity.getEyePosition(partialTicks);
    }

    /* ------------------------ 摆动/张力 ------------------------ */

    private static SwingDynamics calculateSwingDynamics(Vec3 currentStart, Vec3 currentEnd,
                                                        Vec3 lastStart, Vec3 lastEnd,
                                                        float lastAngle, float lastSpeed,
                                                        float tension) {

        Vec3 currentDir = currentEnd.subtract(currentStart).normalize();
        Vec3 lastDir = lastEnd.subtract(lastStart).normalize();

        Vec3 cross = lastDir.cross(currentDir);
        float angleChange = (float) Math.acos(Math.min(1.0, lastDir.dot(currentDir)));
        angleChange *= Math.signum(cross.y);

        float newSpeed = lastSpeed * 0.9f + angleChange * 0.5f * tension;
        float newAngle = lastAngle + newSpeed;

        if (tension > 0.3f) {
            newSpeed += (Math.random() - 0.5) * 0.05 * tension;
        }

        return new SwingDynamics(newAngle, newSpeed);
    }

    private static float calculateTension(double distance, double maxDistance, double elasticDistance) {
        if (distance <= elasticDistance) return 0f;
        double ratio = (distance - elasticDistance) / (maxDistance - elasticDistance);
        return easeOutQuad(Math.min((float)ratio, MAX_TENSION));
    }

    private static float easeOutQuad(float x) { return 1 - (1 - x) * (1 - x); }

    private static int selectColor(float tension, boolean isCritical) {
        if (isCritical) return SuperLeashRenderState.COLOR_CRITICAL;
        return tension > 0.7f ? SuperLeashRenderState.COLOR_TENSION : SuperLeashRenderState.COLOR_NORMAL;
    }

    /* ------------------------ 批量解析 ------------------------ */

    public static List<SuperLeashRenderState> resolveAll(Entity leashedEntity,
                                                         LeashDataImpl leashData, float partialTicks) {

        List<SuperLeashRenderState> states = new ArrayList<>();
        Level level = leashedEntity.level();

        for (ILeashData.LeashInfo leashInfo : leashData.getAllLeashes()) {
            Entity holder = null;
            if (leashInfo.blockPosOpt().isEmpty() && leashInfo.holderIdOpt().isPresent()){
                holder = level.getEntity(leashInfo.holderIdOpt().get());
            } else if (leashInfo.blockPosOpt().isPresent()) {
                holder = SuperLeashKnotEntity.getOrCreateKnot(level, leashInfo.blockPosOpt().get());
            }
            if (holder != null) {
                resolve(holder, leashedEntity, leashInfo, partialTicks)
                        .ifPresent(states::add);
            } else SuperLeadRope.logger.error("Holder not found for leash of " + leashedEntity);
        }

        return states;
    }
}
