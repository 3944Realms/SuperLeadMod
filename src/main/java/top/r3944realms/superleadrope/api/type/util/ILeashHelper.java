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

package top.r3944realms.superleadrope.api.type.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.SuperLeadRopeApi;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public interface ILeashHelper {
    IHolder getHolderHelper(Entity holder);
    ILeashed getLeashedHelper(Entity leashed);
    interface IHolder {
        Entity getHolderEntity();

        /**
         * 获取该实体持有的所有拴绳数据
         */
        Set<ILeashData> getAllLeashData();

        /**
         * 获取该实体持有的拴绳数量
         */
        default int getLeashCount() {
            return getAllLeashData().size();
        }

        /**
         * 检查是否持有任何拴绳
         */
        default boolean hasLeashes() {
            return !getAllLeashData().isEmpty();
        }

        /**
         * 释放所有持有的拴绳
         */
        default void releaseAllLeashes() {
            getAllLeashData().forEach(i -> i.removeLeash(getHolderEntity()));
        }

        /**
         * 检查是否持有特定实体的拴绳
         */
        default boolean isHoldingLeash(Entity entity) {
            return getAllLeashData().stream().anyMatch(i -> i.isLeashedBy(entity));
        }

        /**
         * 检查是否持有特定实体的拴绳
         */
        default boolean isHoldingLeash(UUID uuid) {
            return getAllLeashData().stream().anyMatch(i -> i.isLeashedBy(uuid));
        }

        /**
         * 检查是否持有特定实体的拴绳
         */
        default boolean isHoldingLeash(BlockPos blockPos) {
            return getAllLeashData().stream().anyMatch(i -> i.isLeashedBy(blockPos));
        }
        /**
         * 拴住另一个实体
         */
        default boolean leashEntity(Entity target) {
            if (SuperLeadRopeApi.isLeashable(target)) {
                if (isHoldingLeash(target)) {
                    return false;
                } else return leashEntity(target.getUUID());
            }
            return false;
        }
        boolean leashEntity(UUID uuid);
        /**
         * 解拴另一个实体
         */
        default boolean unleashEntity(Entity target) {
            if (SuperLeadRopeApi.isLeashable(target)) {
                if (!isHoldingLeash(target)) {
                    return false;
                } else return unleashEntity(target.getUUID());
            }
            return false;
        }
        boolean unleashEntity(UUID uuid);
    }

    interface ILeashed {
        /**
         * 获取该实体的拴绳数据
         */
        ILeashData getLeashData();

        /**
         * 绑定拴绳到实体
         */
        default boolean attachLeash(Entity holder) {
            return getLeashData().addLeash(holder);
        }
        /**
         * 绑定拴绳到实体
         */
        default boolean attachLeash(Entity holder, String reserved) {
            return getLeashData().addLeash(holder, reserved);
        }
        /**
         * 绑定拴绳并设置参数
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale);
        }
        /**
         * 绑定拴绳并设置参数
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, String reserved) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale, reserved);
        }
        /**
         * 绑定拴绳并设置参数
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale, maxKeepTicks);
        }
        /**
         * 绑定拴绳并设置参数
         */
        default boolean attachLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks, String reserved) {
            return getLeashData().addLeash(holder, maxDistance, elasticDistanceScale, maxKeepTicks, reserved);
        }
        /**
         * 解绑拴绳（自动判断类型）
         */
        default boolean detachLeash(Entity entity) {
            return getLeashData().removeLeash(entity);
        }
        /**
         * 解绑拴绳
         */
        default boolean detachLeash(UUID uuid) {
            return getLeashData().removeLeash(uuid);
        }
        /**
         * 解绑拴绳
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
         */
        default boolean isLeashed() {
            return getLeashData().hasLeash();
        }
        /**
         * 检查是否被持有者拴住
         */
        default boolean hasHolderLeashed() {
            return getLeashData().hasHolderLeash();
        }
        /**
         * 检查是否被绳结拴住
         */
        default boolean isKnotLeashed() {
            return getLeashData().hasKnotLeash();
        }
        /**
         * 检查是否被特定实体拴住（自动判断类型）
         */
        default boolean isLeashedBy(Entity entity) {
            return getLeashData().isLeashedBy(entity);
        }

        /**
         * 检查是否被特定UUID实体拴住
         */
        default boolean isLeashedBy(UUID uuid) {
            return getLeashData().isLeashedBy(uuid);
        }

        /**
         * 检查是否被特定绳结实体拴住
         */
        default boolean isLeashedBy(BlockPos blockPos) {
            return getLeashData().isLeashedBy(blockPos);
        }

        /**
         * 获取拴绳信息（自动判断类型）
         */
        default Optional<LeashInfo> getLeashInfo(Entity entity) {
            return getLeashData().getLeashInfo(entity);
        }
        /**
         * 获取拴绳信息
         */
        default Optional<LeashInfo> getLeashInfo(UUID holderUUID) {
            return getLeashData().getLeashInfo(holderUUID);
        }
        /**
         * 获取拴绳信息
         */
        default Optional<LeashInfo> getLeashInfo(BlockPos knotPos) {
            return getLeashData().getLeashInfo(knotPos);
        }
        /**
         * 转移拴绳到新持有者（自动判断类型）
         */
        default boolean transferLeash(Entity fromEntity, Entity toEntity) {
            return getLeashData().transferLeash(fromEntity, toEntity);
        }
        /**
         * 转移拴绳到新持有者（自动判断类型）
         */
        default boolean transferLeash(Entity fromEntity, Entity toEntity, String reserved) {
            return getLeashData().transferLeash(fromEntity, toEntity, reserved);
        }
        /**
         * 转移拴绳到新持有者
         */
        default boolean transferLeash(UUID fromEntityUUID, Entity toEntity) {
            return getLeashData().transferLeash(fromEntityUUID, toEntity);
        }
        /**
         * 转移拴绳到新持有者
         */
        default boolean transferLeash(UUID fromEntityUUID, Entity toEntity, String reserved) {
            return getLeashData().transferLeash(fromEntityUUID, toEntity, reserved);
        }
        /**
         * 转移拴绳到新持有者
         */
        default boolean transferLeash(BlockPos fromKnotBlockPos, Entity toEntity) {
            return getLeashData().transferLeash(fromKnotBlockPos, toEntity);
        }
        /**
         * 转移拴绳到新持有者
         */
        default boolean transferLeash(BlockPos fromKnotBlockPos, Entity toEntity, String reserved) {
            return getLeashData().transferLeash(fromKnotBlockPos, toEntity, reserved);
        }
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
         */
        default boolean canBeLeashed() {
            return getLeashData().canBeLeashed();
        }

        /**
         * 检查是否可以拴到特定目标
         */
        default boolean canBeAttachedTo(Entity holder) {
            return getLeashData().canBeAttachedTo(holder);
        }
    }
    static boolean chooseMethodWithBooleanRet(@NotNull Supplier<Boolean> cond, Supplier<Boolean> trueSupplier, Supplier<Boolean> falseSupplier) {
        if (cond.get()) {
            return trueSupplier.get();
        }
        return falseSupplier.get();
    }
    static void chooseMethodWithVoidRet(@NotNull Supplier<Boolean> cond, Supplier<Void> trueSupplier, Supplier<Void> falseSupplier) {
        if (cond.get()) {
            trueSupplier.get();
            return;
        }
        falseSupplier.get();
    }
}
