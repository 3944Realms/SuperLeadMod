/*
 *  Super Lead rope mod
 *  Copyright (C)  2026  R3944Realms
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

package top.r3944realms.superleadrope.util.riding;

import net.minecraft.world.entity.Entity;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.core.exception.RidingCycleException;
import top.r3944realms.superleadrope.util.model.RidingRelationship;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Function;

/**
 * The type Riding applier.
 */
public class RidingApplier {
    /**
     * 应用骑乘关系（在服务器端调用）
     *
     * @param relationship   骑乘关系
     * @param entityProvider 实体提供器（根据UUID获取实体）
     * @return 应用成功的实体数量 int
     */
    public static int applyRidingRelationship(RidingRelationship relationship,
                                              Function<UUID, Entity> entityProvider) {
        if (relationship == null || entityProvider == null) {
            return 0;
        }

        int appliedCount = 0;
        Queue<RidingRelationship> queue = new LinkedList<>();
        queue.offer(relationship);

        while (!queue.isEmpty()) {
            RidingRelationship current = queue.poll();
            UUID entityId = current.getEntityId();
            UUID vehicleId = current.getVehicleId();

            // 获取实体和载具
            Entity entity = entityProvider.apply(entityId);
            Entity vehicle = vehicleId != null ? entityProvider.apply(vehicleId) : null;

            if (entity == null) continue;

            // ---------- 白名单保护 ----------
            if (!CommonEventHandler.leashConfigManager.isEntityTeleportAllowed(entity)) {
                // 不在白名单，跳过本节点，但保留其乘客挂回上层
                if (vehicle != null) {
                    // 将当前节点的乘客挂回上层载具
                    for (RidingRelationship child : current.getPassengers()) {
                        child.setVehicleId(vehicle.getUUID());
                        queue.offer(child);
                    }
                }
                continue; // 跳过本实体的骑乘操作
            }

            appliedCount++;

            // 如果实体已经有载具，先下车
            if (entity.getVehicle() != null) {
                entity.stopRiding();
            }

            // 如果有指定的载具，尝试上车
            if (vehicle != null && CommonEventHandler.leashConfigManager.isEntityTeleportAllowed(vehicle)) {
                if (RidingValidator.wouldCreateCycle(entity, vehicle)) {
                    throw new RidingCycleException(entityId, vehicleId);
                }
                boolean success = entity.startRiding(vehicle, true);
                if (!success) {
                    SuperLeadRope.logger.error("Failed to mount entity {} to vehicle {}", entityId, vehicleId);
                }
            }

            // 处理子乘客
            queue.addAll(current.getPassengers());
        }

        return appliedCount;
    }

    /**
     * 批量应用骑乘关系（适用于世界加载时）
     *
     * @param relationships  the relationships
     * @param entityProvider the entity provider
     */
    public static void applyRidingRelationships(Collection<RidingRelationship> relationships,
                                                Function<UUID, Entity> entityProvider) {
        if (relationships == null || relationships.isEmpty()) {
            return;
        }

        for (RidingRelationship relationship : relationships) {
            try {
                applyRidingRelationship(relationship, entityProvider);
            } catch (RidingCycleException e) {
                // 记录循环引用错误，但继续处理其他关系
                SuperLeadRope.logger.warn("Cyclic riding reference detected and skipped: {}", e.getMessage());
            }
        }
    }

    /**
     * 从JSON字符串应用骑乘关系
     *
     * @param json           the json
     * @param entityProvider the entity provider
     * @return the int
     */
    public static int applyRidingRelationshipFromJson(String json,
                                                      Function<UUID, Entity> entityProvider) {
        RidingRelationship relationship = RidingSerializer.deserialize(json);
        return applyRidingRelationship(relationship, entityProvider);
    }

}
