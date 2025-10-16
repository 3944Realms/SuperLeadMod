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

package top.r3944realms.superleadrope.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;
import top.r3944realms.superleadrope.api.workspace.Services;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class SuperLeadRopeApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(SuperLeadRopeApi.class);
    public static String MOD_ID = "superleadrope";
    public static ILeashHelper getLeashHelper() {
        return Services.WORK_SPACE.getLeashHelper();
    }
    public static @NotNull List<Entity> leashableInArea(Level pLevel, Vec3 pPos, Predicate<Entity> filter) {
        return leashableInArea(pLevel, pPos, filter, 1024D);
    }
    public static @NotNull List<Entity> leashableInArea(@NotNull Entity entity, Predicate<Entity> filter, double fetchDistance) {
        return leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), filter, fetchDistance);
    }
    public static @NotNull List<Entity> leashableInArea(@NotNull Level pLevel, Vec3 pPos, Predicate<Entity> filter, double fetchDistance) {
        return Services.WORK_SPACE.leashableInArea(pLevel, pPos, filter, fetchDistance);
    }
    public static @NotNull List<Entity> leashableInArea(Entity entity, Predicate<Entity> filter) {
        return leashableInArea(entity, filter, 1024D);
    }
    public static @NotNull List<Entity> leashableInArea(Entity holder) {
        return leashableInArea(holder, i -> isLeashHolder(i, holder), 1024D);
    }
    public static boolean isSuperLeadKnot(Entity entity) {
        return Services.WORK_SPACE.isSuperLeadKnot(entity);
    }
    public static BlockPos getSuperLeadKnotPos(Entity entity) {
        return Services.WORK_SPACE.getSuperLeadPos(entity);
    }
    public static boolean isLeashable(Entity entity) {
        return Services.WORK_SPACE.isLeashable(entity);
    }

    public static boolean isLeashHolder(@NotNull Entity pEntity, Entity pTestHolder) {
        return pTestHolder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                isLeashHolder(pEntity, superLeashKnotEntity.getPos()) :
                isLeashHolder(pEntity, pTestHolder.getUUID());
    }

    public static boolean isLeashHolder(@NotNull Entity pEntity, UUID pHolderUUID) {
        return LeashDataInnerAPI.getLeashData(pEntity)
                .map(leashData -> leashData.isLeashedBy(pHolderUUID))
                .orElse(false);
    }

    public static boolean isLeashHolder(@NotNull Entity pEntity, BlockPos pKnotPos) {
        return LeashDataInnerAPI.getLeashData(pEntity)
                .map(leashData -> leashData.isLeashedBy(pKnotPos))
                .orElse(false);
    }
}
