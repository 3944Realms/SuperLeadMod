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

package top.r3944realms.superleadrope.workspace;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.api.SLPCapability;
import top.r3944realms.superleadrope.api.SuperLeadRopeApi;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The type Leash helper.
 */
public class LeashHelper implements ILeashHelper {
    @Override
    public IHolder getHolderHelper(Entity holder) {
        return new IHolder() {
            @Override
            public Entity getHolderEntity() {
                return holder;
            }

            @Override
            public Set<ILeashData> getAllLeash() {
                return SuperLeadRopeApi.leashableInArea(getHolderEntity())
                        .stream()
                        .map(i -> i.getCapability(SLPCapability.LEASH_DATA_CAP).resolve())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
            }

            @Override
            public <T extends Entity> Set<ILeashData> getLeash(Class<T> clazz, AABB box, Predicate<T> filter) {
                return SuperLeadRopeApi.leashableInArea(getHolderEntity(), clazz, box, filter)
                        .stream()
                        .map(i -> i.getCapability(SLPCapability.LEASH_DATA_CAP).resolve())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
            }

            @Override
            public boolean leashEntity(UUID uuid) {
                Entity entity = CommonEventHandler.Game.getServerLevel().getEntity(uuid);

                if (entity != null && SuperLeadRopeApi.isLeashable(entity)) {
                    return LeashDataInnerAPI.getLeashData(entity).map(i-> i.addLeash(getHolderEntity())).orElse(false);
                }
                return false;
            }

            @Override
            public boolean unleashEntity(UUID uuid) {
                Entity entity = CommonEventHandler.Game.getServerLevel().getEntity(uuid);
                if (entity != null && SuperLeadRopeApi.isLeashable(entity)) {
                    return LeashDataInnerAPI.getLeashData(entity).map(i-> i.removeLeash(getHolderEntity())).orElse(false);
                }
                return false;
            }
        };
    }

    @Override
    public ILeashed getLeashedHelper(Entity leashed) {
        return () -> LeashDataInnerAPI.getLeashData(leashed).orElseThrow();
    }
}
