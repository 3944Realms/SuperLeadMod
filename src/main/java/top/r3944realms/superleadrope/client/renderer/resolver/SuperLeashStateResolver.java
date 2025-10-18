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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;
import top.r3944realms.superleadrope.client.renderer.state.SuperLeashRenderState;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;
import top.r3944realms.superleadrope.util.capability.LeashStateInnerAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
                                                          LeashInfo leashInfo, float partialTicks) {

        if (holder == null || leashedEntity == null) return Optional.empty();

        Optional<ILeashState> leashedEntityStateOpt = LeashStateInnerAPI.getLeashState(leashedEntity);
        if (leashedEntityStateOpt.isEmpty()) return Optional.empty();
        ILeashData leashData = LeashDataInnerAPI.getLeashData(leashedEntity).orElse(null);
        ILeashState leashedEntityState = leashedEntityStateOpt.get();
        Optional<ILeashState.LeashState> leashStateOpt = leashedEntityState.getLeashState(holder);
        if (leashStateOpt.isEmpty()) return Optional.empty();
        ILeashState.LeashState leashState = leashStateOpt.get();

        // 计算实体端偏移
        Vec3 currentEntityOffset = getEntityLeashOffset(leashedEntity, leashState);

        // 计算持有者端偏移
        Vec3 holderOffset = getHolderOffset(holder, leashState);
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
        // 获取插值后的位置
        Vec3 currentHolderPos;
        if (holder instanceof Player player) {
             // 玩家手部偏移 + 旋转矩阵（第一人称适用）
            if (isFirstPerson && player == Minecraft.getInstance().player) {
                currentHolderPos = getFirstPersonLeashPos(player, partialTicks);
            } else {
                currentHolderPos = getEntityLeashHolderPos(player, (Minecraft.getInstance().player == holder) ? holderOffset : holderOffset.add(0,0,0.4), partialTicks);
            }

        } else if (holder instanceof SuperLeashKnotEntity) {
            currentHolderPos = getInterpolatedPosition(holder, partialTicks);
        } else {
            // 普通实体，偏移 + 插值 + 旋转
            currentHolderPos = getEntityLeashHolderPos(holder, holderOffset, partialTicks);
        }
        Vec3 currentEntityPos = applyOffsetWithRotation(leashedEntity, currentEntityOffset, partialTicks);

        // 上一帧缓存
        FrameCache cache = frameCacheMap.get(leashedEntity.getUUID());
        Vec3 lastHolderPos = cache != null ? cache.lastStartPos() : currentHolderPos;
        Vec3 lastEntityPos = cache != null ? cache.lastEndPos() : currentEntityPos;
        float lastAngle = cache != null ? cache.lastSwingAngle() : 0f;
        float lastSpeed = cache != null ? cache.lastSwingSpeed() : 0f;

        // 物理参数
        double distance = currentHolderPos.distanceTo(currentEntityPos);
        double maxDistance = 6.0d;
        double elasticDistanceScale = 1.0d;
        if (leashData != null) {
            maxDistance = leashInfo.maxDistance() == null ? leashData.getCurrentMaxDistance() : leashInfo.maxDistance();
            elasticDistanceScale = leashInfo.elasticDistanceScale() == null ? leashData.getCurrentElasticDistanceScale() : leashInfo.elasticDistanceScale();
        }
        float tension = calculateTension(distance, maxDistance, elasticDistanceScale);
        float stretchRatio = (float) (distance / maxDistance);
        boolean isCritical = distance > maxDistance * elasticDistanceScale * 1.5;

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
                maxDistance,
                isFirstPerson, holder.blockPosition()


        ));
    }

    /* ------------------------ 实体偏移计算 ------------------------ */
    private static @NotNull Vec3 getEntityLeashOffset(Entity entity, ILeashState.@NotNull LeashState leashState) {
        Optional<ILeashState> entityStateOpt = LeashStateInnerAPI.getLeashState(entity);
        Vec3 baseOffset = entityStateOpt
                .map(eState -> eState.getLeashApplyEntityLocationOffset()
                        .orElse(eState.getDefaultLeashApplyEntityLocationOffset()))
                .orElse(Vec3.ZERO);
        return baseOffset.add(leashState.applyEntityLocationOffset());
    }
    private static Vec3 getHolderOffset(Entity holder, ILeashState.LeashState leashState) {
        if (LeashStateInnerAPI.Query.hasLeashState(holder)) {
            Optional<ILeashState> holderStateOpt = LeashStateInnerAPI.getLeashState(holder);
            if (holderStateOpt.isPresent()) {
                ILeashState holderState = holderStateOpt.get();
                return holderState.getLeashApplyEntityLocationOffset()
                        .orElse(holderState.getDefaultLeashApplyEntityLocationOffset());
            }
        }
        return Optional.ofNullable(leashState.holderLocationOffset())
                .orElse(leashState.defaultHolderLocationOffset());
    }
    /**
     * 将局部偏移向量应用到实体旋转，返回世界坐标位置
     * @param entity 实体
     * @param localOffset 局部偏移（相对于实体局部坐标系）
     * @param partialTicks 插值参数
     * @return 偏移旋转后的世界坐标位置
     */
    public static @NotNull Vec3 applyOffsetWithRotation(Entity entity, Vec3 localOffset, float partialTicks) {
        // 实体中心位置
        Vec3 centerPos = getInterpolatedPosition(entity, partialTicks);

        // 旋转角度
//        float pitch = Mth.lerp(partialTicks, entity.getXRot(), entity.xRotO) * ((float)Math.PI / 180F);
        float yaw   = Mth.lerp(partialTicks, entity.getYRot(), entity.yRotO) * ((float)Math.PI / 180F);
        float roll  = 0f;

        if (entity instanceof Player player && (player.isFallFlying() || player.isAutoSpinAttack())) {
            roll = getRoll(player, partialTicks, roll);
        }
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
        // 应用旋转到局部偏移
        Vec3 rotatedOffset = localOffset;
        if (!isFirstPerson && entity instanceof Player)  {
            rotatedOffset = rotatedOffset.add(0,0,0.2);
        }
        rotatedOffset = rotatedOffset.yRot(-yaw).zRot(-roll);

        // 返回世界坐标
        return centerPos.add(rotatedOffset);
    }
    /**
     * 获取玩家挂点位置，支持旋转偏移
     */
    private static @NotNull Vec3 getFirstPersonLeashPos(@NotNull Player player, float partialTicks) {
        // 插值旋转角度
        float yaw = Mth.lerp(partialTicks, player.yRotO, player.getYRot()) * ((float)Math.PI / 180F);
//        float pitch = Mth.lerp(partialTicks, player.xRotO, player.getXRot()) * ((float)Math.PI / 180F);
        float roll = 0f;

        // 计算 roll（飞行或旋转攻击时）
        if (player.isFallFlying() || player.isAutoSpinAttack()) {
            roll = getRoll(player, partialTicks, roll);
        }

        // 手部偏移
        double side = player.getMainArm() == HumanoidArm.RIGHT ? -1.0D : 1.0D;
        Vec3 localPos = new Vec3(0.39D * side, -0.6D, -0.5D);

        // 局部坐标旋转到世界坐标
        Vec3 rotatedPos = localPos.yRot(-yaw).zRot(-roll);

        // 返回世界坐标（玩家眼睛位置 + 手部旋转偏移）
        return player.getEyePosition(partialTicks).add(rotatedPos);
    }

    private static float getRoll(@NotNull Player player, float partialTicks, float roll) {
        Vec3 view = player.getViewVector(partialTicks);
        Vec3 motion = player.getDeltaMovement();
        double motionLenSq = motion.horizontalDistanceSqr();
        double viewLenSq = view.horizontalDistanceSqr();
        if (motionLenSq > 0 && viewLenSq > 0) {
            double dot = (motion.x*view.x + motion.z*view.z) / Math.sqrt(motionLenSq*viewLenSq);
            double cross = motion.x*view.z - motion.z*view.x;
            roll = (float)(Math.signum(cross) * Math.acos(dot));
        }
        return roll;
    }

    /**
     * 获取实体挂点位置，支持旋转偏移
     */
    public static Vec3 getEntityLeashHolderPos(Entity entity, Vec3 baseOffset, float partialTicks) {
        // 从眼睛位置（头部）开始
        Vec3 pos = entity.getEyePosition(partialTicks);

        double xOffset = baseOffset.x();
        double yOffset = baseOffset.y();
        double zOffset = baseOffset.z();

        float yaw = Mth.lerp(partialTicks, entity.getYRot(), entity.yRotO) * ((float)Math.PI / 180F);
        if (entity instanceof Player player) {
           xOffset += 0.19D * (player.getMainArm() == HumanoidArm.RIGHT ? -1.0D : 1.0D);
        }
        // 直接在头部位置应用偏移（不管手、飞行、游泳）
        pos = pos.add(new Vec3(xOffset, yOffset, zOffset).yRot(-yaw));

        return pos;
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
        angleChange *= (float) Math.signum(cross.y);

        float newSpeed = lastSpeed * 0.9f + angleChange * 0.5f * tension;
        float newAngle = lastAngle + newSpeed;

        if (tension > 0.3f) {
            newSpeed += (float) ((Math.random() - 0.5) * 0.05 * tension);
        }

        return new SwingDynamics(newAngle, newSpeed);
    }

    private static float calculateTension(double distance, double maxDistance, double elasticDistanceScale) {
        double elasticDistance = maxDistance * elasticDistanceScale;
        if (distance <= elasticDistance) return 0f;
        double ratio = (distance - elasticDistance) / (elasticDistance);
        return easeOutQuad(Math.min((float)ratio, MAX_TENSION));
    }

    private static float easeOutQuad(float x) { return 1 - (1 - x) * (1 - x); }

    private static int selectColor(float tension, boolean isCritical) {
        if (isCritical) return SuperLeashRenderState.COLOR_CRITICAL;
        return tension > 0.7f ? SuperLeashRenderState.COLOR_TENSION : SuperLeashRenderState.COLOR_NORMAL;
    }

}
