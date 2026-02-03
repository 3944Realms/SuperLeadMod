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

package top.r3944realms.superleadrope.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;
import top.r3944realms.superleadrope.api.workspace.Services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * The type Super lead rope api.
 */
@SuppressWarnings("unused")
public class SuperLeadRopeApi {
    /**
     * The constant LOGGER.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SuperLeadRopeApi.class);
    /**
     * The constant MOD_ID.
     */
    public static String MOD_ID = "superleadrope";

    /**
     * Gets leash helper.
     *
     * @return the leash helper
     */
    public static ILeashHelper getLeashHelper() {
        return Services.WORK_SPACE.getLeashHelper();
    }

    /**
     * Leashable in area list.
     *
     * @param pLevel the p level
     * @param pPos   the p pos
     * @param filter the filter
     * @return the list
     */
    public static @NotNull List<Entity> leashableInArea(Level pLevel, Vec3 pPos, Predicate<Entity> filter) {
        return leashableInArea(pLevel, pPos, filter, 1024D);
    }

    /**
     * Leashable in area list.
     *
     * @param entity        the entity
     * @param filter        the filter
     * @param fetchDistance the fetch distance
     * @return the list
     */
    public static @NotNull List<Entity> leashableInArea(@NotNull Entity entity, Predicate<Entity> filter, double fetchDistance) {
        return leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), filter, fetchDistance);
    }

    /**
     * Leashable in area list.
     *
     * @param pLevel        the p level
     * @param pPos          the p pos
     * @param filter        the filter
     * @param fetchDistance the fetch distance
     * @return the list
     */
    public static @NotNull List<Entity> leashableInArea(@NotNull Level pLevel, Vec3 pPos, Predicate<Entity> filter, double fetchDistance) {
        return Services.WORK_SPACE.leashableInArea(pLevel, pPos, filter, fetchDistance);
    }

    /**
     * Leashable in area list.
     *
     * @param entity the entity
     * @param filter the filter
     * @return the list
     */
    public static @NotNull List<Entity> leashableInArea(Entity entity, Predicate<Entity> filter) {
        return leashableInArea(entity, filter, 1024D);
    }

    /**
     * Leashable in area list.
     *
     * @param holder the holder
     * @return the list
     */
    public static @NotNull List<Entity> leashableInArea(Entity holder) {
        return leashableInArea(holder, i -> isLeashHolder(i, holder), 1024D);
    }

    /**
     * Leashable in area list.
     *
     * @param <T>    the type parameter
     * @param holder the holder
     * @param clazz  the clazz
     * @param box    the box
     * @param filter the filter
     * @return the list
     */
    public static  <T extends Entity> @NotNull List<T> leashableInArea(Entity holder, Class<T> clazz, AABB box, Predicate<T> filter) {
        return Services.WORK_SPACE.leashableInArea(holder.level(), clazz, i -> isLeashHolder(i, holder), box);
    }

    /**
     * Is super lead knot boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public static boolean isSuperLeadKnot(Entity entity) {
        return Services.WORK_SPACE.isSuperLeadKnot(entity);
    }

    /**
     * Gets super lead knot pos.
     *
     * @param entity the entity
     * @return the super lead knot pos
     */
    public static BlockPos getSuperLeadKnotPos(Entity entity) {
        return Services.WORK_SPACE.getSuperLeadPos(entity);
    }

    /**
     * Is leashable boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public static boolean isLeashable(Entity entity) {
        return Services.WORK_SPACE.isLeashable(entity);
    }

    /**
     * Is leash holder boolean.
     *
     * @param pEntity     the p entity
     * @param pTestHolder the p test holder
     * @return the boolean
     */
    public static boolean isLeashHolder(@NotNull Entity pEntity, Entity pTestHolder) {
        return Services.WORK_SPACE.isSuperLeadKnot(pTestHolder) ?
                isLeashHolder(pEntity, Services.WORK_SPACE.getSuperLeadPos(pTestHolder)) :
                isLeashHolder(pEntity, pTestHolder.getUUID());
    }

    /**
     * Is leash holder boolean.
     *
     * @param pEntity     the p entity
     * @param pHolderUUID the p holder uuid
     * @return the boolean
     */
    public static boolean isLeashHolder(@NotNull Entity pEntity, UUID pHolderUUID) {
        return Services.WORK_SPACE.getLeashData(pEntity)
                .map(leashData -> leashData.isLeashedBy(pHolderUUID))
                .orElse(false);
    }

    /**
     * Is leash holder boolean.
     *
     * @param pEntity  the p entity
     * @param pKnotPos the p knot pos
     * @return the boolean
     */
    public static boolean isLeashHolder(@NotNull Entity pEntity, BlockPos pKnotPos) {
        return Services.WORK_SPACE.getLeashData(pEntity)
                .map(leashData -> leashData.isLeashedBy(pKnotPos))
                .orElse(false);
    }

    /**
     * Gets leash state.
     *
     * @param pEntity the p entity
     * @return the leash state
     */
    public static Optional<ILeashState> getLeashState(Entity pEntity) {
        return Services.WORK_SPACE.getLeashState(pEntity);
    }

    /**
     * Gets leash data.
     *
     * @param pEntity the p entity
     * @return the leash data
     */
    public static Optional<ILeashData> getLeashData(Entity pEntity) {
        return Services.WORK_SPACE.getLeashData(pEntity);
    }
}
