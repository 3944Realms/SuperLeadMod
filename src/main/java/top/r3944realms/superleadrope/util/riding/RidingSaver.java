/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR 阿 PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope.util.riding;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.core.exception.RidingCycleException;
import top.r3944realms.superleadrope.core.util.ImmutablePair;
import top.r3944realms.superleadrope.util.model.RidingRelationship;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * The type Riding saver.
 */
public class RidingSaver {
    /**
     * 保存骑乘关系
     *
     * @param entity the entity
     * @return the riding relationship
     */
    @Contract("null -> new")
    public static @NotNull RidingRelationship save(@Nullable Entity entity) {
        return save(entity, true);
    }

    /**
     * 保存骑乘关系
     *
     * @param entity   the entity
     * @param findRoot the find root
     * @return the riding relationship
     */
    @Contract("null, _ -> new")
    public static @NotNull RidingRelationship save(@Nullable Entity entity, boolean findRoot) {
        if (entity == null) {
            return new RidingRelationship(Collections.emptyList(), null, null);
        }

        Entity rootEntity = findRoot ? RidingFinder.findRootVehicle(entity) : entity;
        if (rootEntity == null) {
            return new RidingRelationship(Collections.emptyList(), null, null);
        }

        RidingRelationship rootRelationship = new RidingRelationship();
        rootRelationship.setEntityId(rootEntity.getUUID());
        rootRelationship.setVehicleId(null);
        rootRelationship.setPassengers(new ArrayList<>());

        Queue<ImmutablePair<Entity, RidingRelationship>> queue = new LinkedList<>();
        queue.offer(ImmutablePair.of(rootEntity, rootRelationship));

        Set<UUID> processedEntities = new HashSet<>();
        processedEntities.add(rootEntity.getUUID());

        while (!queue.isEmpty()) {
            ImmutablePair<Entity, RidingRelationship> current = queue.poll();
            Entity currentEntity = current.first();
            RidingRelationship currentRelation = current.second();

            List<Entity> passengers = currentEntity.getPassengers();

            if (!passengers.isEmpty()) {
                for (Entity passenger : passengers) {
                    UUID passengerId = passenger.getUUID();

                    if (!processedEntities.contains(passengerId)) {
                        processedEntities.add(passengerId);

                        // ✅ 校验白名单
                        if (!CommonEventHandler.leashConfigManager.isEntityTeleportAllowed(passenger.getType())) {
                            // ❌ 不在白名单，直接截断
                            continue;
                        }

                        // ⬇ 构建子关系
                        RidingRelationship passengerRelation = new RidingRelationship();
                        passengerRelation.setEntityId(passengerId);
                        passengerRelation.setVehicleId(currentEntity.getUUID());
                        passengerRelation.setPassengers(new ArrayList<>());

                        currentRelation.addPassenger(passengerRelation);
                        queue.offer(ImmutablePair.of(passenger, passengerRelation));
                    } else {
                        throw new RidingCycleException(
                                passengerId,
                                currentEntity.getUUID()
                        );
                    }
                }
            }
        }

        return rootRelationship;
    }

    /**
     * 过滤骑乘关系，只保留白名单根节点及其合法子树
     * 如果根节点不在白名单，则回退到第一个合法父节点
     *
     * @param relationship the relationship
     * @return the riding relationship
     */
    public static RidingRelationship filterByWhitelistRoot(RidingRelationship relationship) {
        if (relationship == null) return null;
        try {
            // 如果当前根节点在白名单，则直接处理子节点
            if (CommonEventHandler.leashConfigManager.isEntityTeleportAllowed(Objects.requireNonNull(getEntityType(relationship.getEntityId())))) {
                RidingRelationship filtered = new RidingRelationship();
                filtered.setEntityId(relationship.getEntityId());
                filtered.setVehicleId(relationship.getVehicleId());
                filtered.setPassengers(filterPassengers(relationship.getPassengers()));
                return filtered;
            } else {
                // 根节点不在白名单，尝试找到合法的子节点作为新的根
                for (RidingRelationship child : relationship.getPassengers()) {
                    if (CommonEventHandler.leashConfigManager.isEntityTeleportAllowed(Objects.requireNonNull(getEntityType(child.getEntityId())))) {
                        // 设置父节点为当前节点的父（倒二叉逻辑）
                        RidingRelationship newRoot = new RidingRelationship();
                        newRoot.setEntityId(child.getEntityId());
                        newRoot.setVehicleId(relationship.getVehicleId());
                        newRoot.setPassengers(filterPassengers(child.getPassengers()));
                        return newRoot;
                    }
                }
            }
        } catch (NullPointerException e) {
            SuperLeadRope.logger.error("Catch null", e);
        }

        // 如果整个子树都不在白名单，返回空关系
        return new RidingRelationship(new ArrayList<>(), null, null);
    }

    @Contract("null -> new")
    private static @NotNull List<RidingRelationship> filterPassengers(List<RidingRelationship> passengers) {
        if (passengers == null || passengers.isEmpty()) return new ArrayList<>();
        List<RidingRelationship> filtered = new ArrayList<>();
        for (RidingRelationship passenger : passengers) {
            RidingRelationship childFiltered = filterByWhitelistRoot(passenger);
            if (childFiltered != null && childFiltered.getEntityId() != null) {
                filtered.add(childFiltered);
            }
        }
        return filtered;
    }

    // 传入一个实体提供器 Function<UUID, Entity>，通常在服务器侧就是 level::getEntity
    private static Function<UUID, Entity> entityProvider;

    /**
     * Sets entity provider.
     *
     * @param provider the provider
     */
    public static void setEntityProvider(Function<UUID, Entity> provider) {
        entityProvider = provider;
    }

    /**
     * 根据UUID获取EntityType
     */
    private static EntityType<?> getEntityType(UUID entityId) {
        if (entityProvider == null) return null;
        Entity entity = entityProvider.apply(entityId);
        if (entity == null) return null;
        return entity.getType();
    }
}
