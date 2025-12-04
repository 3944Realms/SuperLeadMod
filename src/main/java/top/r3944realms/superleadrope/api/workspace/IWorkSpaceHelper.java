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

package top.r3944realms.superleadrope.api.workspace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The interface Work space helper.
 */
public interface IWorkSpaceHelper {
    /**
     * Leashable in area list.
     *
     * @param pLevel        the p level
     * @param pPos          the p pos
     * @param filter        the filter
     * @param fetchDistance the fetch distance
     * @return the list
     */
    @NotNull List<Entity> leashableInArea(@NotNull Level pLevel, Vec3 pPos, Predicate<Entity> filter, double fetchDistance);

    /**
     * Leashable in area list.
     *
     * @param <T>    实体类型
     * @param pLevel the p level
     * @param clazz  the clazz
     * @param filter the filter
     * @param box    the box
     * @return the list
     */
    @NotNull <T extends Entity> List<T> leashableInArea(@NotNull Level pLevel, Class<T> clazz,Predicate<T> filter, AABB box);


    /**
     * Gets leash helper.
     *
     * @return the leash helper
     */
    ILeashHelper getLeashHelper();

    /**
     * Is super lead knot boolean.
     *
     * @param pEntity the p entity
     * @return the boolean
     */
    boolean isSuperLeadKnot(Entity pEntity);

    /**
     * Gets super lead pos.
     *
     * @param pEntity the p entity
     * @return the super lead pos
     * @throws IllegalArgumentException the illegal argument exception
     */
    BlockPos getSuperLeadPos(Entity pEntity) throws IllegalArgumentException;

    /**
     * Is leashable boolean.
     *
     * @param pEntity the p entity
     * @return the boolean
     */
    boolean isLeashable(@NotNull Entity pEntity);

    /**
     * Gets leash data.
     *
     * @param pEntity the p entity
     * @return the leash data
     */
    Optional<ILeashData> getLeashData(@NotNull Entity pEntity);

    /**
     * Gets leash state.
     *
     * @param pEntity the p entity
     * @return the leash state
     */
    Optional<ILeashState> getLeashState(@NotNull Entity pEntity);
}
