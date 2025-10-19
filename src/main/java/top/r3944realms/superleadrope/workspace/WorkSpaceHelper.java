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

package top.r3944realms.superleadrope.workspace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;
import top.r3944realms.superleadrope.api.workspace.IWorkSpaceHelper;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;
import top.r3944realms.superleadrope.util.capability.LeashStateInnerAPI;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The type Work space helper.
 */
public class WorkSpaceHelper implements IWorkSpaceHelper {
    @Override
    public @NotNull List<Entity> leashableInArea(@NotNull Level pLevel, Vec3 pPos, @Nullable Predicate<Entity> filter, double fetchDistance) {
        AABB box = AABB.ofSize(pPos, fetchDistance, fetchDistance, fetchDistance);
        return pLevel.getEntitiesOfClass(Entity.class, box, e -> LeashDataImpl.isLeashable(e) && Objects.requireNonNullElse(filter, entity -> true).test(e));
    }

    @Override
    public @NotNull List<Entity> leashableInArea(@NotNull Level pLevel, Class<Entity> clazz, @Nullable Predicate<Entity> filter, AABB box) {
        return pLevel.getEntitiesOfClass(clazz, box, e -> LeashDataImpl.isLeashable(e) && Objects.requireNonNullElse(filter, entity -> true).test(e));
    }

    @Override
    public ILeashHelper getLeashHelper() {
        return new LeashHelper();
    }

    @Override
    public boolean isSuperLeadKnot(Entity pEntity) {
        return pEntity instanceof SuperLeashKnotEntity;
    }

    @Override
    public BlockPos getSuperLeadPos(Entity pEntity) throws IllegalArgumentException {
        if (isSuperLeadKnot(pEntity)) {
            return (((SuperLeashKnotEntity) pEntity).getPos());
        }
        else throw new IllegalArgumentException(pEntity.getClass().getName() + " is not a superlead knot");
    }

    @Override
    public boolean isLeashable(@NotNull Entity pEntity) {
        return LeashDataImpl.isLeashable(pEntity);
    }

    @Override
    public Optional<ILeashData> getLeashData(@NotNull Entity pEntity) {
        return LeashDataInnerAPI.getLeashData(pEntity);
    }

    @Override
    public Optional<ILeashState> getLeashState(@NotNull Entity pEntity) {
        return LeashStateInnerAPI.getLeashState(pEntity);
    }

}
