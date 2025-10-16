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

package top.r3944realms.superleadrope.api.workspace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;

import java.util.List;
import java.util.function.Predicate;

public interface IWorkSpaceHelper {
    @NotNull List<Entity> leashableInArea(@NotNull Level pLevel, Vec3 pPos, Predicate<Entity> filter, double fetchDistance);
    ILeashHelper getLeashHelper();
    boolean isSuperLeadKnot(Entity pEntity);
    BlockPos getSuperLeadPos(Entity pEntity) throws IllegalArgumentException;
    boolean isLeashable(@NotNull Entity pEntity);
}
