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

package top.r3944realms.superleadrope.api.type.capabilty;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import top.r3944realms.superleadrope.api.SuperLeadRopeApi;

import java.util.*;

/**
 * The type Leash info.
 */
/* ----------------------
 * Data record
 * ---------------------- */
@SuppressWarnings({"RegExpRedundantEscape", "DuplicatedCode"})
public record LeashInfo(
        Optional<BlockPos> blockPosOpt,
        Optional<UUID> holderUUIDOpt,
        Optional<Integer> holderIdOpt, // Only for client side use
        Set<String> marks,             // 标记字段
        String reserved,               // 保留字段
        Double maxDistance,            // 最大距离 (可null,null则用全局&默认)
        Double elasticDistanceScale,   // 弹性距离比例 (可null，null则用全局&默认)
        int keepLeashTicks,            // 剩余 Tick 数
        int maxKeepLeashTicks          // 最大保持 Tick 数
) {
    /**
     * The constant MARK_NOT_UPDATE.
     */
// 预定义的标记常量
    public static final String MARK_NOT_UPDATE = "NOT_UPDATE";
    /**
     * The constant MARK_ONLY_NOT_UPDATE_MAX_DISTANCE.
     */
    public static final String MARK_ONLY_NOT_UPDATE_MAX_DISTANCE = "NOT_UPDATE_MAX_DISTANCE";
    /**
     * The constant MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE.
     */
    public static final String MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE = "NOT_UPDATE_ELASTIC_DISTANCE_SCALE";

    /**
     * The constant EMPTY.
     */
    public static final LeashInfo EMPTY = new LeashInfo(
            Optional.empty(), Optional.empty(), Optional.empty(),
            Set.of(), "", 12.0D, 6.0D, 0, 0
    );

    /**
     * Create leash info.
     *
     * @param entity               the entity
     * @param marks                the marks
     * @param reserved             the reserved
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param keepTicks            the keep ticks
     * @param maxKeepTicks         the max keep ticks
     * @return the leash info
     */
    /* ---------- Factory ---------- */
    public static LeashInfo create(
            Entity entity,
            Set<String> marks,
            String reserved,
            Double maxDistance,
            Double elasticDistanceScale,
            int keepTicks,
            int maxKeepTicks
    ) {
        return SuperLeadRopeApi.isSuperLeadKnot(entity)
                ? new LeashInfo(SuperLeadRopeApi.getSuperLeadKnotPos(entity), entity.getId(), marks, reserved,
                maxDistance, elasticDistanceScale, keepTicks, maxKeepTicks)
                : new LeashInfo(entity.getUUID(), entity.getId(), marks, reserved, maxDistance, elasticDistanceScale, keepTicks, maxKeepTicks);
    }

    /**
     * Create leash info.
     *
     * @param entity               the entity
     * @param reserved             the reserved
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param keepTicks            the keep ticks
     * @param maxKeepTicks         the max keep ticks
     * @return the leash info
     */
// 向后兼容的工厂方法
    public static LeashInfo create(
            Entity entity,
            String reserved,
            Double maxDistance,
            Double elasticDistanceScale,
            int keepTicks,
            int maxKeepTicks
    ) {
        return create(entity, Set.of(), reserved, maxDistance, elasticDistanceScale, keepTicks, maxKeepTicks);
    }

    /**
     * Instantiates a new Leash info.
     *
     * @param holderUUID           the holder uuid
     * @param holderId             the holder id
     * @param reserved             the reserved
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param keepTicks            the keep ticks
     * @param maxKeepTicks         the max keep ticks
     */
    public LeashInfo(UUID holderUUID, int holderId, String reserved,
                     Double maxDistance, Double elasticDistanceScale, int keepTicks, int maxKeepTicks) {
        this(Optional.empty(), Optional.of(holderUUID), Optional.of(holderId),
                Set.of(), reserved, maxDistance, elasticDistanceScale, keepTicks, maxKeepTicks);
    }

    /**
     * Instantiates a new Leash info.
     *
     * @param holderUUID           the holder uuid
     * @param holderId             the holder id
     * @param marks                the marks
     * @param reserved             the reserved
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param keepTicks            the keep ticks
     * @param maxKeepTicks         the max keep ticks
     */
    public LeashInfo(UUID holderUUID, int holderId, Set<String> marks, String reserved,
                     Double maxDistance, Double elasticDistanceScale, int keepTicks, int maxKeepTicks) {
        this(Optional.empty(), Optional.of(holderUUID), Optional.of(holderId),
                marks, reserved, maxDistance, elasticDistanceScale, keepTicks, maxKeepTicks);
    }

    /**
     * Instantiates a new Leash info.
     *
     * @param knotPos              the knot pos
     * @param holderId             the holder id
     * @param reserved             the reserved
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param keepTicks            the keep ticks
     * @param maxKeepTicks         the max keep ticks
     */
    public LeashInfo(BlockPos knotPos, int holderId, String reserved,
                     Double maxDistance, Double elasticDistanceScale, int keepTicks, int maxKeepTicks) {
        this(Optional.of(knotPos), Optional.empty(), Optional.of(holderId),
                Set.of(), reserved, maxDistance, elasticDistanceScale, keepTicks, maxKeepTicks);
    }

    /**
     * Instantiates a new Leash info.
     *
     * @param knotPos              the knot pos
     * @param holderId             the holder id
     * @param marks                the marks
     * @param reserved             the reserved
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param keepTicks            the keep ticks
     * @param maxKeepTicks         the max keep ticks
     */
    public LeashInfo(BlockPos knotPos, int holderId, Set<String> marks, String reserved,
                     Double maxDistance, Double elasticDistanceScale, int keepTicks, int maxKeepTicks) {
        this(Optional.of(knotPos), Optional.empty(), Optional.of(holderId),
                marks, reserved, maxDistance, elasticDistanceScale, keepTicks, maxKeepTicks);
    }

    /**
     * Decrement keep ticks leash info.
     *
     * @return the leash info
     */
    /* ---------- State updates ---------- */
    public LeashInfo decrementKeepTicks() {
        return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, marks, reserved,
                maxDistance, elasticDistanceScale,
                Math.max(0, keepLeashTicks - 1), maxKeepLeashTicks);
    }

    /**
     * Reset keep ticks leash info.
     *
     * @return the leash info
     */
    public LeashInfo resetKeepTicks() {
        return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, marks, reserved,
                maxDistance, elasticDistanceScale,
                maxKeepLeashTicks, maxKeepLeashTicks);
    }

    /**
     * Transfer holder leash info.
     *
     * @param entity the entity
     * @return the leash info
     */
    public LeashInfo transferHolder(Entity entity) {
        return transferHolder(entity, reserved);
    }

    /**
     * Transfer holder leash info.
     *
     * @param entity      the entity
     * @param newReserved the new reserved
     * @return the leash info
     */
    public LeashInfo transferHolder(Entity entity, String newReserved) {
        boolean isKnot = SuperLeadRopeApi.isSuperLeadKnot(entity);
        return new LeashInfo(
                isKnot ? Optional.of(SuperLeadRopeApi.getSuperLeadKnotPos(entity)) : Optional.empty(),
                !isKnot ? Optional.of(entity.getUUID()) : Optional.empty(),
                Optional.of(entity.getId()),
                marks, newReserved, maxDistance, elasticDistanceScale,
                maxKeepLeashTicks, maxKeepLeashTicks
        );
    }

    /**
     * Transfer holder leash info.
     *
     * @param entity            the entity
     * @param maxKeepLeashTicks the max keep leash ticks
     * @return the leash info
     */
    public LeashInfo transferHolder(Entity entity, int maxKeepLeashTicks) {
        return transferHolder(entity, maxKeepLeashTicks, reserved);
    }

    /**
     * Transfer holder leash info.
     *
     * @param entity            the entity
     * @param maxKeepLeashTicks the max keep leash ticks
     * @param newReserved       the new reserved
     * @return the leash info
     */
    public LeashInfo transferHolder(Entity entity, int maxKeepLeashTicks, String newReserved) {
        boolean isKnot = SuperLeadRopeApi.isSuperLeadKnot(entity);
        return new LeashInfo(
                isKnot ? Optional.of(SuperLeadRopeApi.getSuperLeadKnotPos(entity)) : Optional.empty(),
                !isKnot ? Optional.of(entity.getUUID()) : Optional.empty(),
                Optional.of(entity.getId()),
                marks, newReserved, maxDistance, elasticDistanceScale,
                maxKeepLeashTicks, maxKeepLeashTicks
        );
    }

    /**
     * 修改保留字段
     *
     * @param newReserved the new reserved
     * @return the leash info
     */
    public LeashInfo withReserved(String newReserved) {
        return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, marks, newReserved,
                maxDistance, elasticDistanceScale, keepLeashTicks, maxKeepLeashTicks);
    }

    /**
     * 修改标记集合
     *
     * @param newMarks the new marks
     * @return the leash info
     */
    public LeashInfo withMarks(Set<String> newMarks) {
        return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, Set.copyOf(newMarks), reserved,
                maxDistance, elasticDistanceScale, keepLeashTicks, maxKeepLeashTicks);
    }

    /* ---------- 标记管理方法（基于Set的新实现） ---------- */

    /**
     * 添加无需更新标记（如果不存在则添加）
     *
     * @return the leash info
     */
    public LeashInfo markNotUpdate() {
        return hasMark(MARK_NOT_UPDATE) ? this : addMark(MARK_NOT_UPDATE);
    }

    /**
     * Mark not update distance leash info.
     *
     * @return the leash info
     */
    public LeashInfo markNotUpdateDistance() {
        if (hasMark(MARK_NOT_UPDATE)) {
            return this;
        } else {
            if (hasMark(MARK_ONLY_NOT_UPDATE_MAX_DISTANCE)) {
                return this;
            } else if (hasMark(MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE)) {
                return this.removeMark(MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE).addMark(MARK_NOT_UPDATE);
            }
        }
        return this.addMark(MARK_ONLY_NOT_UPDATE_MAX_DISTANCE);
    }

    /**
     * Mark not update scale leash info.
     *
     * @return the leash info
     */
    public LeashInfo markNotUpdateScale() {
        if (hasMark(MARK_NOT_UPDATE)) {
            return this;
        } else {
            if (hasMark(MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE)) {
                return this;
            } else if (hasMark(MARK_ONLY_NOT_UPDATE_MAX_DISTANCE)) {
                return this.removeMark(MARK_ONLY_NOT_UPDATE_MAX_DISTANCE).addMark(MARK_NOT_UPDATE);
            }
        }
        return this.addMark(MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE);
    }

    /**
     * 移除无需更新标记
     *
     * @return the leash info
     */
    public LeashInfo unmarkNotUpdate() {
        return removeMarks(MARK_NOT_UPDATE, MARK_ONLY_NOT_UPDATE_MAX_DISTANCE, MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE);
    }

    /**
     * 移除无需更新距离标记
     *
     * @return the leash info
     */
    public LeashInfo unmarkNotUpdateDistance() {
        return removeMarks(MARK_NOT_UPDATE, MARK_ONLY_NOT_UPDATE_MAX_DISTANCE);
    }

    /**
     * 移除无需更新比例标记
     *
     * @return the leash info
     */
    public LeashInfo unmarkNotUpdateScale() {
        return removeMarks(MARK_NOT_UPDATE, MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE);
    }

    /**
     * 检查是否包含无需更新标记
     *
     * @return the boolean
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isNotUpdate() {
        return hasMark(MARK_NOT_UPDATE);
    }

    /**
     * 是否需距离更新标记
     *
     * @return the boolean
     */
    public boolean isNeedUpdateDistance() {
        return !isNotUpdate() && !hasMark(MARK_ONLY_NOT_UPDATE_MAX_DISTANCE);
    }

    /**
     * 是否需比例更新
     *
     * @return the boolean
     */
    public boolean isNeedUpdateScale() {
        return !isNotUpdate() && !hasMark(MARK_ONLY_NOT_UPDATE_ELASTIC_DISTANCE_SCALE);
    }

    /**
     * 添加标记（如果不存在则添加）
     *
     * @param mark the mark
     * @return the leash info
     */
    public LeashInfo addMark(String mark) {
        if (marks.contains(mark)) {
            return this;
        }
        Set<String> newMarks = new HashSet<>(marks);
        newMarks.add(mark);
        return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, newMarks, reserved,
                maxDistance, elasticDistanceScale, keepLeashTicks, maxKeepLeashTicks);
    }

    /**
     * 添加多个标记（自动检测并跳过重复标记）
     *
     * @param marksToAdd the marks to add
     * @return the leash info
     */
    public LeashInfo addMarks(String @NotNull ... marksToAdd) {
        Set<String> newMarks = new HashSet<>(marks);
        boolean changed = false;
        for (String mark : marksToAdd) {
            if (newMarks.add(mark)) {
                changed = true;
            }
        }
        return changed ? new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, newMarks, reserved,
                maxDistance, elasticDistanceScale, keepLeashTicks, maxKeepLeashTicks) : this;
    }

    /**
     * 添加多个标记（集合版本）
     *
     * @param marksToAdd the marks to add
     * @return the leash info
     */
    public LeashInfo addMarks(@NotNull Collection<String> marksToAdd) {
        return addMarks(marksToAdd.toArray(new String[0]));
    }

    /**
     * 移除单个标记
     *
     * @param mark the mark
     * @return the leash info
     */
    public LeashInfo removeMark(String mark) {
        if (!marks.contains(mark)) {
            return this;
        }
        Set<String> newMarks = new HashSet<>(marks);
        newMarks.remove(mark);
        return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, newMarks, reserved,
                maxDistance, elasticDistanceScale, keepLeashTicks, maxKeepLeashTicks);
    }

    /**
     * 移除多个标记
     *
     * @param marksToRemove the marks to remove
     * @return the leash info
     */
    public LeashInfo removeMarks(String @NotNull ... marksToRemove) {
        Set<String> newMarks = new HashSet<>(marks);
        boolean changed = false;
        for (String mark : marksToRemove) {
            if (newMarks.remove(mark)) {
                changed = true;
            }
        }
        return changed ? new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, newMarks, reserved,
                maxDistance, elasticDistanceScale, keepLeashTicks, maxKeepLeashTicks) : this;
    }

    /**
     * 移除多个标记（集合版本）
     *
     * @param marksToRemove the marks to remove
     * @return the leash info
     */
    public LeashInfo removeMarks(@NotNull Collection<String> marksToRemove) {
        return removeMarks(marksToRemove.toArray(new String[0]));
    }

    /**
     * 检查是否包含指定标记
     *
     * @param mark the mark
     * @return the boolean
     */
    public boolean hasMark(String mark) {
        return marks.contains(mark);
    }

    /**
     * 检查是否包含所有指定标记
     *
     * @param marksToCheck the marks to check
     * @return the boolean
     */
    @Contract(pure = true)
    public boolean hasAllMarks(String @NotNull ... marksToCheck) {
        for (String mark : marksToCheck) {
            if (!marks.contains(mark)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否包含任意指定标记
     *
     * @param marksToCheck the marks to check
     * @return the boolean
     */
    @Contract(pure = true)
    public boolean hasAnyMark(String @NotNull ... marksToCheck) {
        for (String mark : marksToCheck) {
            if (marks.contains(mark)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有标记（不可修改的视图）
     *
     * @return the marks
     */
    @Contract(pure = true)
    public @Unmodifiable Set<String> getMarks() {
        return Set.copyOf(marks);
    }

    /**
     * 清除所有标记
     *
     * @return the leash info
     */
    public LeashInfo clearAllMarks() {
        if (marks.isEmpty()) {
            return this;
        }
        return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, Set.of(), reserved,
                maxDistance, elasticDistanceScale, keepLeashTicks, maxKeepLeashTicks);
    }
}
