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

package top.r3944realms.superleadrope.content.capability.impi;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.LeashStateSyncPacket;
import top.r3944realms.superleadrope.util.nbt.NBTReader;
import top.r3944realms.superleadrope.util.nbt.NBTWriter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Leash state.
 */
//TODO: 将拴绳状态与数据联系在一起
public class LeashStateImpl implements ILeashState {
    private Entity entity;
    private boolean needsSync = false;
    private long lastSyncTime;
    private final Map<UUID, ILeashState.LeashState> leashHolders = new ConcurrentHashMap<>();
    private final Map<BlockPos, ILeashState.LeashState> leashKnots = new ConcurrentHashMap<>();
    @Nullable
    private volatile Vec3 staticApplyEntityLocationOffset;
    private volatile Vec3 defaultApplyEntityLocationOffset;

    /**
     * Instantiates a new Leash state.
     *
     * @param entity                           the entity
     * @param defaultApplyEntityLocationOffset the default apply entity location offset
     */
    public LeashStateImpl(Entity entity, Vec3 defaultApplyEntityLocationOffset) {
        this.entity = entity;
        this.defaultApplyEntityLocationOffset = defaultApplyEntityLocationOffset;
    }

    @Override
    public void markForSync() {
        if (!entity.level().isClientSide) {
            needsSync = true;
            immediateSync(); // 立即同步一次
        }
    }

    @Override
    public void immediateSync() {
        syncNow();
    }

    @Override
    public void checkSync() {
        if (!needsSync || entity.level().isClientSide) return;

        long now = System.currentTimeMillis();
        // 每隔 2 秒同步一次
        if (now - lastSyncTime > 2000) {
            syncNow();
        }
    }

    private void syncNow() {
        CompoundTag currentData = serializeNBT();

        NetworkHandler.sendToPlayer(
                new LeashStateSyncPacket(entity.getId(), currentData),
                entity,
                PacketDistributor.TRACKING_ENTITY_AND_SELF
        );
        lastSyncTime = System.currentTimeMillis();
        needsSync = false;
    }

    @Override
    public Entity self() {
        return entity;
    }

    @Override
    public boolean hasLeashState() {
        return !leashKnots.isEmpty() || !leashHolders.isEmpty();
    }

    @Override
    public Map<UUID, LeashState> getHolderLeashStates() {
        ConcurrentMap<UUID, LeashState> retMap = Maps.newConcurrentMap();
        retMap.putAll(leashHolders);
        return retMap;
    }

    @Override
    public Map<BlockPos, LeashState> getKnotLeashStates() {
        ConcurrentMap<BlockPos, LeashState> retMap = Maps.newConcurrentMap();
        retMap.putAll(leashKnots);
        return retMap;
    }

    @Override
    public Optional<LeashState> getLeashState(Entity holder) {
        return holder instanceof SuperLeashKnotEntity leashKnot ? getLeashState(leashKnot.getPos())
                : getLeashState(holder.getUUID());
    }

    @Override
    public Optional<LeashState> getLeashState(UUID uuid) {
        return leashHolders.containsKey(uuid) ? Optional.of(leashHolders.get(uuid)) : Optional.empty();
    }

    @Override
    public Optional<LeashState> getLeashState(BlockPos pos) {
        return leashKnots.containsKey(pos) ? Optional.of(leashKnots.get(pos)) : Optional.empty();
    }

    @Override
    public Optional<Vec3> getHolderLocationOffset(Entity entity) {
        return (entity instanceof SuperLeashKnotEntity leashKnot) ?
                getHolderLocationOffset(leashKnot.getPos()) : getHolderLocationOffset(entity.getUUID());
    }

    @Override
    public Optional<Vec3> getHolderLocationOffset(UUID uuid) {
        return Optional.ofNullable(leashHolders.get(uuid)).map(LeashState::holderLocationOffset);
    }

    @Override
    public Optional<Vec3> getHolderLocationOffset(BlockPos pos) {
        return Optional.ofNullable(leashKnots.get(pos)).map(LeashState::holderLocationOffset);
    }

    @Override
    public Vec3 getDefaultLeashApplyEntityLocationOffset() {
        return defaultApplyEntityLocationOffset;
    }
    @Override
    public Optional<Vec3> getLeashApplyEntityLocationOffset() {
        return Optional.ofNullable(staticApplyEntityLocationOffset);
    }

    @Override
    public void resetLeashHolderLocationOffset(Entity holder) {
        if (holder instanceof SuperLeashKnotEntity leashKnot) {
            resetLeashHolderLocationOffset(leashKnot.getPos());
        } else resetLeashHolderLocationOffset(holder.getUUID());
    }

    @Override
    public void resetLeashHolderLocationOffset(UUID holderUUID) {
        leashHolders.computeIfPresent(holderUUID, (uuid, state) -> state.resetHolderLocationOffset());
        markForSync();
    }

    @Override
    public void resetLeashHolderLocationOffset(BlockPos knotPos) {
        leashKnots.computeIfPresent(knotPos, (blockPos, state) -> state.resetHolderLocationOffset());
        markForSync();
    }

    @Override
    public void setLeashHolderLocationOffset(Entity holder, @Nullable Vec3 offset) {
        if (holder instanceof SuperLeashKnotEntity leashKnot) {
            setLeashHolderLocationOffset(leashKnot.getPos(), offset);
        } else setLeashHolderLocationOffset(holder.getUUID(), offset);
    }

    @Override
    public void setLeashHolderLocationOffset(UUID holderUUID, Vec3 offset) {
        if (entity.level() instanceof ServerLevel level) {
            LeashState currentState = leashHolders.get(holderUUID);
            if (currentState == null) {
                Entity holder = level.getEntity(holderUUID);
                Vec3 defaultHolderLocationOffset = Vec3.ZERO;
                if(holder != null) {
                    defaultHolderLocationOffset = CommonEventHandler.leashConfigManager.getDefaultHolderOffset(holder);
                }
                // 创建新的状态，使用默认的应用实体偏移量
                leashHolders.put(holderUUID, new LeashState(
                        offset,
                        Vec3.ZERO,
                        defaultHolderLocationOffset
                ));
            } else {
                // 更新现有状态
                leashHolders.put(holderUUID,
                        currentState.setHolderLocationOffset(offset)
                );
            }
            markForSync();
        }
    }

    @Override
    public void setLeashHolderLocationOffset(BlockPos knotPos, Vec3 offset) {
        if (entity.level() instanceof ServerLevel level) {
            LeashState currentState = leashKnots.get(knotPos);
            if (currentState == null) {
                // 创建新的状态
                leashKnots.put(knotPos, new LeashState(
                        offset,
                        Vec3.ZERO,
                        SuperLeashKnotEntity.get(level, knotPos)
                                .map(CommonEventHandler.leashConfigManager::getDefaultHolderOffset)
                                .orElse(Vec3.ZERO)
                ));
            } else {
                // 更新现有状态
                leashKnots.put(knotPos,
                        currentState.setHolderLocationOffset(offset)
                );
            }
            markForSync();
        }
    }

    @Override
    public void addLeashHolderLocationOffset(Entity holder, Vec3 offset) {
        if (holder instanceof SuperLeashKnotEntity leashKnot) {
            addLeashHolderLocationOffset(leashKnot.getPos(), offset);
        } else addLeashHolderLocationOffset(holder.getUUID(), offset);
    }

    @Override
    public void addLeashHolderLocationOffset(UUID holderUUID, Vec3 offset) {
        LeashState currentState = leashHolders.get(holderUUID);
        if (currentState != null) {
            Vec3 newHolderLocationOffset;
            if (currentState.holderLocationOffset() == null) {
                newHolderLocationOffset = currentState.defaultHolderLocationOffset().add(offset);
            } else  newHolderLocationOffset = currentState.holderLocationOffset().add(offset);
            leashHolders.put(holderUUID,
                    currentState.setHolderLocationOffset(newHolderLocationOffset)
            );
        }
        markForSync();
    }

    @Override
    public void addLeashHolderLocationOffset(BlockPos knotPos, Vec3 offset) {
        LeashState currentState = leashKnots.get(knotPos);
        if (currentState != null) {
            Vec3 newHolderLocationOffset;
            if (currentState.holderLocationOffset() == null) {
                newHolderLocationOffset = currentState.defaultHolderLocationOffset().add(offset);
            } else  newHolderLocationOffset = currentState.holderLocationOffset().add(offset);
            leashKnots.put(knotPos,
                    currentState.setHolderLocationOffset(newHolderLocationOffset)
            );
        }
        markForSync();
    }

    @Override
    public void removeLeashHolderLocationOffset(Entity holder) {
        if (holder instanceof SuperLeashKnotEntity leashKnot) {
            removeLeashHolderLocationOffset(leashKnot.getPos());
        } else removeLeashHolderLocationOffset(holder.getUUID());
    }

    @Override
    public void removeLeashHolderLocationOffset(UUID holderUUID) {
        leashHolders.remove(holderUUID);
        markForSync();
    }

    @Override
    public void removeLeashHolderLocationOffset(BlockPos knotPos) {
        leashKnots.remove(knotPos);
        markForSync();
    }

    @Override
    public void removeAllLeashHolderLocationOffset() {
        leashKnots.clear();
        leashHolders.clear();
        markForSync();
    }

    @Override
    public void removeAllLeashHolderUUIDLocationOffset() {
        leashHolders.clear();
        markForSync();
    }

    @Override
    public void removeAllLeashHolderBlockPosLocationOffset() {
        leashKnots.clear();
        markForSync();
    }

    @Override
    public void resetAllLeashHolderLocationsOffset() {
        leashKnots.replaceAll((pos, leashState) -> leashState.resetHolderLocationOffset());
        leashHolders.replaceAll((uuid, leashState) -> leashState.resetHolderLocationOffset());
        markForSync();
    }

    @Override
    public void resetAllLeashApplyEntityLocationsOffset() {
        leashKnots.replaceAll((pos, leashState) -> leashState.setApplyEntityLocationOffset(defaultApplyEntityLocationOffset));
        leashHolders.replaceAll((uuid, leashState) -> leashState.setApplyEntityLocationOffset(defaultApplyEntityLocationOffset));
        markForSync();
    }

    @Override
    public void removeLeashApplyEntityLocationOffset() {
        staticApplyEntityLocationOffset = null;
        markForSync();
    }

    @Override
    public void setLeashApplyEntityLocationOffset(Vec3 leashHolderLocationOffset) {
        staticApplyEntityLocationOffset = leashHolderLocationOffset;
        markForSync();
    }

    @Override
    public void addLeashApplyEntityLocationOffset(Vec3 offset) {
        Optional.ofNullable(this.staticApplyEntityLocationOffset)
                .ifPresentOrElse(vec3 -> vec3.add(offset), () -> this.staticApplyEntityLocationOffset = offset);
        markForSync();
    }

    @Override
    public void copy(ILeashState other, Entity newEntity) {
        this.entity = newEntity;
        this.defaultApplyEntityLocationOffset = CommonEventHandler.leashConfigManager.getDefaultEntityOffset(newEntity);
        this.staticApplyEntityLocationOffset = other.getLeashApplyEntityLocationOffset().orElse(null);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag holdersList = new ListTag();
        leashHolders.forEach((uuid, state ) -> {
            CompoundTag infoTag = generateCompoundTagFromUUIDLeashInfo(uuid, state);
            holdersList.add(infoTag);
        });
        leashKnots.forEach((blockPos, state ) -> {
            CompoundTag infoTag = generateCompoundTagFromBlockPosLeashState(blockPos, state);
            holdersList.add(infoTag);
        });
        tag.put("LeashHolders", holdersList);
        if (staticApplyEntityLocationOffset != null) {
            tag.put("StaticApplyEntityLocationOffset", NBTWriter.writeVec3(staticApplyEntityLocationOffset));
        }
        tag.put("DefaultApplyEntityLocationOffset", NBTWriter.writeVec3(defaultApplyEntityLocationOffset));
        return tag;
    }
    private static @NotNull CompoundTag generateCompoundTagFromUUIDLeashInfo(@NotNull UUID uuid, @NotNull ILeashState.LeashState info) {
        CompoundTag infoTag = new CompoundTag();
        infoTag.putUUID("HolderUUID", uuid);
        return getCommonCompoundTag(info ,infoTag);
    }
    private static @NotNull CompoundTag generateCompoundTagFromBlockPosLeashState(@NotNull BlockPos blockpos, @NotNull ILeashState.LeashState info) {
        CompoundTag infoTag = new CompoundTag();
        infoTag.put("KnotBlockPos", NbtUtils.writeBlockPos(blockpos));
        return getCommonCompoundTag(info, infoTag);
    }

    private static @NotNull CompoundTag getCommonCompoundTag(@NotNull ILeashState.LeashState info, CompoundTag infoTag) {
        infoTag.put("ApplyEntityLocationOffset", NBTWriter.writeVec3(info.applyEntityLocationOffset()));
        if(info.holderLocationOffset() != null) infoTag.put("HolderEntityLocationOffset", NBTWriter.writeVec3(info.holderLocationOffset()));
        infoTag.put("DefaultHolderLocationOffset",  NBTWriter.writeVec3(info.defaultHolderLocationOffset()));
        return infoTag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        leashHolders.clear();
        leashKnots.clear();
        if (nbt.contains("LeashHolders", ListTag.TAG_LIST)) {
            ListTag holdersList = nbt.getList("LeashHolders", ListTag.TAG_COMPOUND);
            if(nbt.contains("StaticApplyEntityLocationOffset")) {
                staticApplyEntityLocationOffset = NBTReader.readVec3(nbt.getCompound("StaticApplyEntityLocationOffset"));
            }
            if (nbt.contains("DefaultApplyEntityLocationOffset")) {
                defaultApplyEntityLocationOffset = NBTReader.readVec3(nbt.getCompound("DefaultApplyEntityLocationOffset"));
            } else throw new IllegalArgumentException("Nbt Lost DefaultApplyEntityLocationOffset Value");
            for (int i = 0; i < holdersList.size(); i++) {
                CompoundTag infoTag = holdersList.getCompound(i);
                if (infoTag.contains("HolderUUID")) {
                    ILeashState.LeashState uuidLeashDataFormListTag = getUUIDLeashStateForm(infoTag);
                    leashHolders.put(infoTag.getUUID("HolderUUID"), uuidLeashDataFormListTag);
                } else {
                    ILeashState.LeashState blockPosLeashDataFormListTag = getUUIDLeashStateForm(infoTag);
                    leashKnots.put(NbtUtils.readBlockPos(infoTag.getCompound("KnotBlockPos")), blockPosLeashDataFormListTag);
                }
            }

        }
    }
    @Contract("_ -> new")
    private static @NotNull ILeashState.LeashState getUUIDLeashStateForm(@NotNull CompoundTag infoTag) {
        return new ILeashState.LeashState(
                infoTag.contains("HolderEntityLocationOffset") ? NBTReader.readVec3(infoTag.getCompound("HolderEntityLocationOffset")) : null,
                NBTReader.readVec3(infoTag.getCompound("ApplyEntityLocationOffset")),
                NBTReader.readVec3(infoTag.getCompound("DefaultHolderLocationOffset"))
        );
    }

}
