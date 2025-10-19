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

package top.r3944realms.superleadrope.core.hook;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Leash render hook.
 */
public class LeashRenderHook {
    /**
     * Should render extra boolean.
     *
     * @param mob    the mob
     * @param camera the camera
     * @return the boolean
     */
    public static boolean shouldRenderExtra(Mob mob, Frustum camera) {
        SuperLeadRope.logger.debug("[SuperLeash] Checking entity: {} at position: {}, {}, {}", mob.getName().getString(), mob.getX(), mob.getY(), mob.getZ());
        AtomicBoolean flag = new AtomicBoolean(false);
        LeashDataInnerAPI.getLeashData(mob).ifPresent(i -> {
            i.getAllLeashes().forEach(j -> {
                Optional<Integer> i1 = j.holderIdOpt();
                if (i1.isPresent()) {
                    Entity entity = mob.level().getEntity(i1.get());
                    if (entity != null) {
                        flag.set(camera.isVisible(entity.getBoundingBoxForCulling()));
                    }
                }
            });
        });
        return flag.get();
    }
}