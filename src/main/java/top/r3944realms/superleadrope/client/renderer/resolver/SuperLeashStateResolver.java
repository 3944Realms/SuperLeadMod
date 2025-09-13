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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.client.renderer.state.SuperLeashRenderState;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.ILeashData;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.util.capability.LeashUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

//TODO: 未来实现更高级的渲染
public class SuperLeashStateResolver {
    private static final float MAX_TENSION = 1.5f;
    private static final float THICKNESS_BASE = 0.1f;
    private static final float THICKNESS_TENSION = 0.15f;

    // 摆动计算缓存(存储上一帧数据)
    private static final Map<UUID, FrameCache> frameCacheMap = new HashMap<>();

    private record FrameCache(
            Vec3 lastStartPos,
            Vec3 lastEndPos,
            float lastSwingAngle,
            float lastSwingSpeed
    ) {}

    public static Optional<SuperLeashRenderState> resolve(
            Entity holder,
            Entity leashedEntity,
            ILeashData.LeashInfo leashInfo,
            float partialTicks) {

        if (holder == null || leashedEntity == null) {
            return Optional.empty();
        }
        AtomicReference<Vec3> holderOffset = new AtomicReference<>();
        AtomicReference<Vec3> entityOffset = new AtomicReference<>();
        LeashUtil.getLeashState(leashedEntity).ifPresent(state ->
                state
                    .getLeashState(holder)
                    .ifPresent(ls -> {
                                holderOffset.set(
                                        Optional.ofNullable(ls.holderLocationOffset())
                                                .orElse(ls.defaultHolderLocationOffset())
                                );
                                entityOffset.set(
                                        Optional.ofNullable(ls.applyEntityLocationOffset())
                                                .orElse(
                                                        state
                                                        .getLeashApplyEntityLocationOffset()
                                                        .orElse(state.getDefaultLeashApplyEntityLocationOffset())
                                                )
                                );
                            }
                    ));

        // 获取当前帧位置(带插值)
        Vec3 currentHolderPos = getInterpolatedPosition(holder, partialTicks).add(holderOffset.get());
        Vec3 currentEntityPos = getInterpolatedPosition(leashedEntity, partialTicks).add(entityOffset.get());


        // 获取上一帧数据
        FrameCache cache = frameCacheMap.get(leashedEntity.getUUID());
        Vec3 lastHolderPos = cache != null ? cache.lastStartPos() : currentHolderPos;
        Vec3 lastEntityPos = cache != null ? cache.lastEndPos() : currentEntityPos;
        float lastAngle = cache != null ? cache.lastSwingAngle() : 0f;
        float lastSpeed = cache != null ? cache.lastSwingSpeed() : 0f;

        // 计算物理参数
        double distance = currentHolderPos.distanceTo(currentEntityPos);
        double maxDistance = leashInfo.maxDistance();
        double elasticDistance = leashInfo.elasticDistance();
        float tension = calculateTension(distance, maxDistance, elasticDistance);//这里暂时没用上
        float stretchRatio = (float) (distance / maxDistance);
        boolean isCritical = distance > maxDistance * 1.5;

        // 计算摆动动态
        SwingDynamics swing = calculateSwingDynamics(
                currentHolderPos, currentEntityPos,
                lastHolderPos, lastEntityPos,
                lastAngle, lastSpeed,
                tension);

        // 更新缓存
        frameCacheMap.put(leashedEntity.getUUID(),
                new FrameCache(currentHolderPos, currentEntityPos,
                        swing.angle(), swing.speed()));

        return Optional.of(new SuperLeashRenderState(
                currentHolderPos,
                currentEntityPos,
                leashInfo.attachOffset(),
                lastHolderPos,
                lastEntityPos,
                tension,
                stretchRatio,
                isCritical,
                leashInfo.keepLeashTicks(),
                selectColor(tension, isCritical),
                THICKNESS_BASE + (tension * THICKNESS_TENSION),
                swing.angle(),
                swing.speed(),
                (float) leashInfo.maxDistance()
        ));
    }

    private record SwingDynamics(float angle, float speed) {}

    private static SwingDynamics calculateSwingDynamics(
            Vec3 currentStart, Vec3 currentEnd,
            Vec3 lastStart, Vec3 lastEnd,
            float lastAngle, float lastSpeed,
            float tension) {

        // 计算当前方向向量
        Vec3 currentDir = currentEnd.subtract(currentStart).normalize();
        Vec3 lastDir = lastEnd.subtract(lastStart).normalize();

        // 计算方向变化角度(使用叉积确定变化方向)
        Vec3 cross = lastDir.cross(currentDir);
        float angleChange = (float) Math.acos(Math.min(1.0, lastDir.dot(currentDir)));
        angleChange *= (float) Math.signum(cross.y); // 使用y分量确定摆动方向

        // 模拟物理摆动(简谐运动)
        float newSpeed = lastSpeed * 0.9f + angleChange * 0.5f * tension;
        float newAngle = lastAngle + newSpeed;

        // 添加随机扰动(模拟风等外力)
        if (tension > 0.3f) {
            newSpeed += (float) ((Math.random() - 0.5) * 0.05 * tension);
        }

        return new SwingDynamics(newAngle, newSpeed);
    }

    private static Vec3 getInterpolatedPosition(Entity entity, float partialTicks) {
        // 实体位置插值(使移动更平滑)
        return new Vec3(
                lerp(partialTicks, (float) entity.xo, (float) entity.getX()),
                lerp(partialTicks, (float) entity.yo, (float) entity.getY()),
                lerp(partialTicks, (float) entity.zo, (float) entity.getZ())
        );
    }

    private static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    private static float calculateTension(double distance, double maxDistance, double elasticDistance) {
        if (distance <= elasticDistance) {
            return 0f; // 完全松弛
        }

        // 非线性张力计算
        double excess = distance - elasticDistance;
        double range = maxDistance - elasticDistance;
        float ratio = (float) (excess / range);

        // 应用缓动函数使变化更自然
        return easeOutQuad(Math.min(ratio, MAX_TENSION));
    }

    private static float easeOutQuad(float x) {
        return 1 - (1 - x) * (1 - x);
    }

    private static int selectColor(float tension, boolean isCritical) {
        if (isCritical) {
            return SuperLeashRenderState.COLOR_CRITICAL;
        }
        return tension > 0.7f ?
                SuperLeashRenderState.COLOR_TENSION :
                SuperLeashRenderState.COLOR_NORMAL;
    }

    /**
     * 获取实体所有拴绳的渲染状态
     */
    public static List<SuperLeashRenderState> resolveAll(
            Entity leashedEntity,
            LeashDataImpl leashData,
            float partialTicks) {

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
            }
           else SuperLeadRope.logger.error("Holder is not found");
        }

        return states;
    }
}
