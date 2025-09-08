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

package top.r3944realms.superleadrope.content.capability.inter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ILeashDataCapability extends INBTSerializable<CompoundTag> {
    // 原LeashData的方法接口
    boolean addLeash(Entity holder, ItemStack leashStack, double maxDistance);
    boolean addLeash(Entity holder, ItemStack leashStack, double maxDistance, double elasticDistance, int maxKeepLeashTicks);
    boolean addLeash(Entity holder, LeashInfo leashInfo);

    boolean setMaxDistance(Entity holder, double newMaxDistance);
    boolean setMaxDistance(Entity holder,double newMaxDistance, int newMaxKeepLeashTicks);
    boolean setMaxDistance(UUID holderUUID, double newMaxDistance);
    boolean setMaxDistance(UUID holderUUID, double newMaxDistance, int newMaxKeepLeashTicks);
    boolean setMaxDistance(BlockPos knotPos, double newMaxDistance);
    boolean setMaxDistance(BlockPos knotPos, double newMaxDistance, int newMaxKeepLeashTicks);
    boolean setElasticDistance(Entity holder, double newElasticDistance);
    boolean setElasticDistance(UUID holderUUID, double newElasticDistance);
    boolean setElasticDistance(BlockPos knotPos, double newElasticDistance);
    // 动态修改弹性距离
    boolean setElasticDistance(Entity holder, double newElasticDistance, int newMaxKeepLeashTicks);
    boolean setElasticDistance(UUID holderUUID, double newElasticDistance, int newMaxKeepLeashTicks);
    boolean setElasticDistance(BlockPos knotPos, double newElasticDistance, int newMaxKeepLeashTicks);

    void applyLeashForces();
    boolean removeLeash(Entity holder);
    boolean removeLeash(UUID holderUUID);
    boolean removeLeash(BlockPos knotPos);

    boolean transferLeash(Entity holder, Entity newHolder);
    boolean transferLeash(Entity holder, Entity newHolder, ItemStack stack);
    boolean transferLeash(UUID holderUUID, Entity newHolder);
    boolean transferLeash(UUID holderUUID, Entity newHolder, ItemStack stack);
    boolean transferLeash(BlockPos knotPos, Entity newHolder);
    boolean transferLeash(BlockPos knotPos, Entity newHolder, ItemStack stack);

    // 查询方法
    Collection<LeashInfo> getAllLeashes();
    boolean isLeashedBy(Entity holder);
    boolean isLeashedBy(UUID holderUUID);
    boolean isLeashedBy(BlockPos knotPos);
    Optional<LeashInfo> getLeashInfo(Entity holder);
    Optional<LeashInfo> getLeashInfo(UUID holderUUID);
    Optional<LeashInfo> getLeashInfo(BlockPos knotPos);

    boolean canBeLeashed();
    boolean canBeAttachedTo(Entity pEntity);
    void markForSync();
    void immediateSync();
    void checkSync();

    record LeashInfo(
            Optional<BlockPos> blockPosOpt,
            Optional<UUID> holderUUIDOpt,
            Optional<Integer> holderIdOpt,//Only for client side use
            String reserved, //保留字段
            Vec3 attachOffset,
            double maxDistance,
            double elasticDistance,
            int keepLeashTicks,      // 新增：保持拴绳的剩余Tick数
            int maxKeepLeashTicks    // 新增：最大保持Tick数（可配置）
    ) {
        public static final LeashInfo EMPTY = new LeashInfo(
                Optional.empty(), Optional.empty(), Optional.empty(),
                "", Vec3.ZERO, 12.0D, 6.0D, 0, 0
        );
        public static LeashInfo CreateLeashInfo(
                Entity entity,
                String reserved,
                Vec3 attachOffset,
                double maxDistance,
                double elasticDistance,
                int keepLeashTicks,
                int maxKeepLeashTicks
        ) {
            return entity instanceof SuperLeashKnotEntity superLeashKnot ?
                    new LeashInfo(superLeashKnot.getPos(), entity.getId(), reserved, attachOffset, maxDistance, elasticDistance, keepLeashTicks, maxKeepLeashTicks) :
                    new LeashInfo(entity.getUUID(), entity.getId(), reserved, attachOffset, maxDistance, elasticDistance, keepLeashTicks, maxKeepLeashTicks);
        }
        public LeashInfo(
                UUID holderUUID,
                int holderId,
                String reserved,
                Vec3 attachOffset,
                double maxDistance,
                double elasticDistance,
                int keepLeashTicks,
                int maxKeepLeashTicks
        ) {
            this(Optional.empty() ,Optional.of(holderUUID), Optional.of(holderId),  reserved, attachOffset, maxDistance, elasticDistance, keepLeashTicks, maxKeepLeashTicks);
        }
        public LeashInfo(
                BlockPos knotPos,
                int holderId,
                String reserved,
                Vec3 attachOffset,
                double maxDistance,
                double elasticDistance,
                int keepLeashTicks,
                int maxKeepLeashTicks
        ) {
            this(Optional.of(knotPos), Optional.empty(), Optional.of(holderId),  reserved, attachOffset, maxDistance, elasticDistance, keepLeashTicks, maxKeepLeashTicks);
        }

        // 返回一个减少剩余Tick的新实例
        public LeashInfo decrementKeepLeashTicks() {
            return new LeashInfo(
                    blockPosOpt, holderUUIDOpt, holderIdOpt, reserved, attachOffset,
                    maxDistance, elasticDistance,
                    Math.max(0, keepLeashTicks - 1),
                    maxKeepLeashTicks
            );
        }

        // 重置Tick为最大值
        public LeashInfo resetKeepLeashTicks() {
            return new LeashInfo(
                    blockPosOpt, holderUUIDOpt, holderIdOpt, reserved, attachOffset,
                    maxDistance, elasticDistance,
                    maxKeepLeashTicks,
                    maxKeepLeashTicks
            );
        }
        public LeashInfo transferHolder (Entity entity) {
            boolean isSuperKnot = entity instanceof SuperLeashKnotEntity;
            return new LeashInfo(
                    isSuperKnot ? Optional.of(((SuperLeashKnotEntity) entity).getPos()) : Optional.empty(),
                    !isSuperKnot ? Optional.of(entity.getUUID()) : Optional.empty(),
                    Optional.of(entity.getId()),
                    reserved, attachOffset, maxDistance,elasticDistance, keepLeashTicks, maxKeepLeashTicks
            );
        }
        public LeashInfo transferHolder (Entity entity, String reserved) {
            boolean isSuperKnot = entity instanceof SuperLeashKnotEntity;
            return new LeashInfo(
                    isSuperKnot ? Optional.of(((SuperLeashKnotEntity) entity).getPos()) : Optional.empty(),
                    !isSuperKnot ? Optional.of(entity.getUUID()) : Optional.empty(),
                    Optional.of(entity.getId()),
                    reserved, attachOffset, maxDistance,elasticDistance, keepLeashTicks, maxKeepLeashTicks
            );
        }

    }
}
