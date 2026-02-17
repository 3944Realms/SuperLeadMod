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

package top.r3944realms.superleadrope.api.type.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.SuperLeadRopeApi;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 拴绳数据Helper
 */
public interface ILeashHelper {
    /**
     * 持有者角度Helper
     *
     * @param holder 持有者
     * @return IHolder holder helper
     */
    IHolder getHolderHelper(Entity holder);

    /**
     * Gets leashed helper.
     *
     * @param leashed the leashed
     * @return the leashed helper
     */
    ILeashed getLeashedHelper(Entity leashed);

    /**
     * The interface Holder.
     */
    interface IHolder {
        /**
         * 获取持有者实体
         *
         * @return 持有者 holder entity
         */
        Entity getHolderEntity();

        /**
         * 获取该实体持有的所有实体的拴绳数据能力
         *
         * @return {@link Set<ILeashData> 持有的实体的拴绳数据能力集合}
         */
        Set<ILeashData> getAllLeash();

        /**
         * 获取该实体持有的所有实体实例
         *
         * @return {@link Set<Entity> 持有的实体集合}
         */
        default Set<Entity> getAllLeashedEntities() {
            return getAllLeash().stream().map(ILeashData::self).collect(Collectors.toSet());
        }

        /**
         * 获取该实体持有的指定实体实例
         *
         * @param <T>    the type parameter
         * @param clazz  实体类型
         * @param box    搜索范围
         * @param filter {@link Predicate<Entity> 实体过滤器}
         * @return {@link Set<Entity> 持有的实体集合}
         */
        @SuppressWarnings("unchecked")
         default <T extends Entity> Set<T> getLeashEntities (Class<T> clazz, AABB box, Predicate<T> filter) {
            return (Set<T>) getLeash(clazz, box, filter).stream().map(ILeashData::self).collect(Collectors.toSet());
        }

        /**
         * 获取该实体持有的符合条件实体的拴绳数据能力
         *
         * @param <T>    the type parameter
         * @param clazz  实体类型
         * @param box    搜索范围
         * @param filter {@link Predicate<Entity> 实体过滤器}
         * @return {@link Set<ILeashData> 持有的实体的拴绳数据能力集合}
         */
        <T extends Entity> Set<ILeashData> getLeash(Class<T> clazz, AABB box, Predicate<T> filter);

        /**
         * 获取该实体持有的符合条件实体实例
         *
         * @param <T>           the type parameter
         * @param clazz         实体类型
         * @param fetchDistance 搜索范围(以实体为中心的正方体{@link AABB#ofSize(Vec3, double, double, double) 包围盒})
         * @param filter        {@link Predicate<Entity> 实体过滤器}
         * @return {@link Set<Entity> 持有的实体实例}
         */
        @SuppressWarnings("unchecked")
        default <T extends Entity> Set<T> getLeashEntities (Class<T> clazz, double fetchDistance, Predicate<T> filter) {
            return (Set<T>) getLeash(clazz, fetchDistance, filter).stream().map(ILeashData::self).collect(Collectors.toSet());
        }

        /**
         * 获取该实体持有的符合条件实体的拴绳数据能力
         *
         * @param <T>           the type parameter
         * @param clazz         实体类型
         * @param fetchDistance 搜索范围(以实体为中心的正方体{@link AABB#ofSize(Vec3, double, double, double) 包围盒})
         * @param filter        {@link Predicate<Entity> 实体过滤器}
         * @return {@link Set<ILeashData> 持有的实体的拴绳数据能力集合}
         */
        default <T extends Entity> Set<ILeashData> getLeash(Class<T> clazz, double fetchDistance, Predicate<T> filter) {
            return getLeash(clazz, AABB.ofSize(getHolderEntity().position(), fetchDistance, fetchDistance, fetchDistance), filter);
        }

        /**
         * 获取该实体持有的符合条件实体实例
         *
         * @param <T>    the type parameter
         * @param clazz  实体类型
         * @param filter {@link Predicate<Entity> 实体过滤器}
         * @return {@link Set<Entity> 持有的实体实例}
         */
        @SuppressWarnings("unchecked")
        default <T extends Entity> Set<T> getLeashEntities (Class<T> clazz, Predicate<T> filter) {
            return (Set<T>) getLeash(clazz, filter).stream().map(ILeashData::self).collect(Collectors.toSet());
        }

        /**
         * 获取该实体持有的符合条件实体的拴绳数据能力
         *
         * @param <T>    the type parameter
         * @param clazz  实体类型
         * @param filter {@link Predicate<Entity> 实体过滤器}
         * @return {@link Set<ILeashData> 持有的实体的拴绳数据能力集合}
         */
        default <T extends Entity> Set<ILeashData> getLeash(Class<T> clazz, Predicate<T> filter) {
            return getLeash(clazz, 1024D, filter);
        }

        /**
         * 获取该实体持有的符合条件实体实例
         *
         * @param <T>   the type parameter
         * @param clazz 实体类型
         * @return {@link Set<Entity> 持有的实体实例}
         */
        @SuppressWarnings("unchecked")
        default <T extends Entity> Set<T> getLeashEntities (Class<T> clazz) {
            return (Set<T>) getLeash(clazz).stream().map(ILeashData::self).collect(Collectors.toSet());
        }

        /**
         * 获取该实体持有的符合条件实体的拴绳数据能力
         *
         * @param <T>   the type parameter
         * @param clazz 实体类型
         * @return {@link Set<ILeashData> 持有的实体的拴绳数据能力集合}
         */
        default <T extends Entity> Set<ILeashData> getLeash(Class<T> clazz) {
            return getLeash(clazz, 1024D, null);
        }


        /**
         * 获取该实体持有的拴绳数量
         *
         * @return the leash count
         */
        default int getLeashCount() {
            return getAllLeash().size();
        }

        /**
         * 检查是否持有任何拴绳
         *
         * @return the boolean
         */
        default boolean hasLeashes() {
            return !getAllLeash().isEmpty();
        }

        /**
         * 释放所有持有的拴绳
         */
        default void releaseAllLeashes() {
            getAllLeash().forEach(i -> i.removeLeash(getHolderEntity()));
        }

        /**
         * 检查是否持有特定实体的拴绳
         *
         * @param entity the entity
         * @return the boolean
         */
        default boolean isHoldingLeash(Entity entity) {
            return getAllLeash().stream().anyMatch(i -> Objects.equals(i.self(), entity));
        }

        /**
         * 检查是否持有特定实体的拴绳
         *
         * @param uuid the uuid
         * @return the boolean
         */
        default boolean isHoldingLeash(UUID uuid) {
            return getAllLeash().stream().anyMatch(i -> Objects.equals(i.self().getUUID(), uuid));
        }

        /**
         * 拴住另一个实体
         *
         * @param target the target
         * @return the boolean
         */
        default boolean leashEntity(Entity target) {
            if (SuperLeadRopeApi.isLeashable(target)) {
                if (isHoldingLeash(target)) {
                    return false;
                } else return leashEntity(target.getUUID());
            }
            return false;
        }

        /**
         * Leash entity boolean.
         *
         * @param uuid the uuid
         * @return the boolean
         */
        boolean leashEntity(UUID uuid);

        /**
         * 解拴另一个实体
         *
         * @param target the target
         * @return the boolean
         */
        default boolean unleashEntity(Entity target) {
            if (SuperLeadRopeApi.isLeashable(target)) {
                if (!isHoldingLeash(target)) {
                    return false;
                } else return unleashEntity(target.getUUID());
            }
            return false;
        }

        /**
         * Unleash entity boolean.
         *
         * @param uuid the uuid
         * @return the boolean
         */
        boolean unleashEntity(UUID uuid);
    }

    /**
     * The interface Leashed.
     */
    interface ILeashed {
        /**
         * 获取该实体的拴绳数据
         *
         * @return the leash data
         */
        ILeashData getLeashData();

        /**
         * 绑定拴绳到实体
         *
         * @param holder the holder
         * @return the boolean
         */
        default boolean attachLeash(Entity holder) {
            return getLeashData().addLeash(holder);
        }

        /**
         * 绑定拴绳到实体
         *
         * @param holder   the holder
         * @param reserved the reserved
         * @return the boolean
         */
        default boolean attachLeash(Entity holder, String reserved) {
            return getLeashData().addLeash(holder, reserved);
        }

        /**
         * 绑定拴绳并设置参数
         *
         * @param holder               the holder
         * @param maxDistance          the max distance
         * @param elasticDistanceScale the elastic distance scale
         * @return the boolean
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale);
        }

        /**
         * 绑定拴绳并设置参数
         *
         * @param holder               the holder
         * @param maxDistance          the max distance
         * @param elasticDistanceScale the elastic distance scale
         * @param reserved             the reserved
         * @return the boolean
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, String reserved) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale, reserved);
        }

        /**
         * 绑定拴绳并设置参数
         *
         * @param holder               the holder
         * @param maxDistance          the max distance
         * @param elasticDistanceScale the elastic distance scale
         * @param maxKeepTicks         the max keep ticks
         * @return the boolean
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale, maxKeepTicks);
        }

        /**
         * 绑定拴绳并设置参数
         *
         * @param holder               the holder
         * @param maxDistance          the max distance
         * @param elasticDistanceScale the elastic distance scale
         * @param maxKeepTicks         the max keep ticks
         * @param reserved             the reserved
         * @return the boolean
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks, String reserved) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale, maxKeepTicks, reserved);
        }

        /**
         * 解绑拴绳（自动判断类型）
         *
         * @param entity the entity
         * @return the boolean
         */
        default boolean detachLeash(Entity entity) {
            return getLeashData().removeLeash(entity);
        }

        /**
         * 解绑拴绳
         *
         * @param uuid the uuid
         * @return the boolean
         */
        default boolean detachLeash(UUID uuid) {
            return getLeashData().removeLeash(uuid);
        }

        /**
         * 解绑拴绳
         *
         * @param blockPos the block pos
         * @return the boolean
         */
        default boolean detachLeash(BlockPos blockPos) {
            return getLeashData().removeLeash(blockPos);
        }

        /**
         * 解绑所有拴绳
         */
        default void detachAllLeashes() {
            getLeashData().removeAllLeashes();
        }

        /**
         * 解绑所有实体拴绳
         */
        default void removeAllHolderLeashes() {
            getLeashData().removeAllHolderLeashes();
        }

        /**
         * 解绑所有绳结拴绳
         */
        default void removeAllKnotLeashes() {
            getLeashData().removeAllKnotLeashes();
        }

        /**
         * 检查是否被拴住
         *
         * @return the boolean
         */
        default boolean isLeashed() {
            return getLeashData().hasLeash();
        }

        /**
         * 检查是否被持有者拴住
         *
         * @return the boolean
         */
        default boolean hasHolderLeashed() {
            return getLeashData().hasHolderLeash();
        }

        /**
         * 检查是否被绳结拴住
         *
         * @return the boolean
         */
        default boolean isKnotLeashed() {
            return getLeashData().hasKnotLeash();
        }

        /**
         * 检查是否被特定实体拴住（自动判断类型）
         *
         * @param entity the entity
         * @return the boolean
         */
        default boolean isLeashedBy(Entity entity) {
            return getLeashData().isLeashedBy(entity);
        }

        /**
         * 检查是否被特定UUID实体拴住
         *
         * @param uuid the uuid
         * @return the boolean
         */
        default boolean isLeashedBy(UUID uuid) {
            return getLeashData().isLeashedBy(uuid);
        }

        /**
         * 检查是否被特定绳结实体拴住
         *
         * @param blockPos the block pos
         * @return the boolean
         */
        default boolean isLeashedBy(BlockPos blockPos) {
            return getLeashData().isLeashedBy(blockPos);
        }

        /**
         * 获取拴绳信息（自动判断类型）
         *
         * @param entity the entity
         * @return the leash info
         */
        default Optional<LeashInfo> getLeashInfo(Entity entity) {
            return getLeashData().getLeashInfo(entity);
        }

        /**
         * 获取拴绳信息
         *
         * @param holderUUID the holder uuid
         * @return the leash info
         */
        default Optional<LeashInfo> getLeashInfo(UUID holderUUID) {
            return getLeashData().getLeashInfo(holderUUID);
        }

        /**
         * 获取拴绳信息
         *
         * @param knotPos the knot pos
         * @return the leash info
         */
        default Optional<LeashInfo> getLeashInfo(BlockPos knotPos) {
            return getLeashData().getLeashInfo(knotPos);
        }

        /**
         * 转移拴绳到新持有者（自动判断类型）
         *
         * @param fromEntity the from entity
         * @param toEntity   the to entity
         * @return the boolean
         */
        default boolean transferLeash(Entity fromEntity, Entity toEntity) {
            return getLeashData().transferLeash(fromEntity, toEntity);
        }

        /**
         * 转移拴绳到新持有者（自动判断类型）
         *
         * @param fromEntity the from entity
         * @param toEntity   the to entity
         * @param reserved   the reserved
         * @return the boolean
         */
        default boolean transferLeash(Entity fromEntity, Entity toEntity, String reserved) {
            return getLeashData().transferLeash(fromEntity, toEntity, reserved);
        }

        /**
         * 转移拴绳到新持有者
         *
         * @param fromEntityUUID the from entity uuid
         * @param toEntity       the to entity
         * @return the boolean
         */
        default boolean transferLeash(UUID fromEntityUUID, Entity toEntity) {
            return getLeashData().transferLeash(fromEntityUUID, toEntity);
        }

        /**
         * 转移拴绳到新持有者
         *
         * @param fromEntityUUID the from entity uuid
         * @param toEntity       the to entity
         * @param reserved       the reserved
         * @return the boolean
         */
        default boolean transferLeash(UUID fromEntityUUID, Entity toEntity, String reserved) {
            return getLeashData().transferLeash(fromEntityUUID, toEntity, reserved);
        }

        /**
         * 转移拴绳到新持有者
         *
         * @param fromKnotBlockPos the from knot block pos
         * @param toEntity         the to entity
         * @return the boolean
         */
        default boolean transferLeash(BlockPos fromKnotBlockPos, Entity toEntity) {
            return getLeashData().transferLeash(fromKnotBlockPos, toEntity);
        }

        /**
         * 转移拴绳到新持有者
         *
         * @param fromKnotBlockPos the from knot block pos
         * @param toEntity         the to entity
         * @param reserved         the reserved
         * @return the boolean
         */
        default boolean transferLeash(BlockPos fromKnotBlockPos, Entity toEntity, String reserved) {
            return getLeashData().transferLeash(fromKnotBlockPos, toEntity, reserved);
        }

        /**
         * Is in delayed leash boolean.
         *
         * @param holderUUID the holder uuid
         * @return the boolean
         */
        default boolean isInDelayedLeash(UUID holderUUID) {
            return getLeashData().isInDelayedLeash(holderUUID);
        }


        /**
         * 应用拴绳物理效果
         */
        default void applyLeashPhysics() {
            getLeashData().applyLeashForces();
        }

        /**
         * 检查是否可以被拴绳
         *
         * @return the boolean
         */
        default boolean canBeLeashed() {
            return getLeashData().canBeLeashed();
        }

        /**
         * 检查是否可以拴到特定目标
         *
         * @param holder the holder
         * @return the boolean
         */
        default boolean canBeAttachedTo(Entity holder) {
            return getLeashData().canBeAttachedTo(holder);
        }
    }

    /**
     * Choose method with boolean ret boolean.
     *
     * @param cond          the cond
     * @param trueSupplier  the true supplier
     * @param falseSupplier the false supplier
     * @return the boolean
     */
    static boolean chooseMethodWithBooleanRet(@NotNull Supplier<Boolean> cond, Supplier<Boolean> trueSupplier, Supplier<Boolean> falseSupplier) {
        if (cond.get()) {
            return trueSupplier.get();
        }
        return falseSupplier.get();
    }

    /**
     * Choose method with void ret.
     *
     * @param cond          the cond
     * @param trueSupplier  the true supplier
     * @param falseSupplier the false supplier
     */
    static void chooseMethodWithVoidRet(@NotNull Supplier<Boolean> cond, Supplier<Void> trueSupplier, Supplier<Void> falseSupplier) {
        if (cond.get()) {
            trueSupplier.get();
            return;
        }
        falseSupplier.get();
    }
}
