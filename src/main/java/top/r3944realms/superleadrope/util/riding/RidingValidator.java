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

package top.r3944realms.superleadrope.util.riding;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import top.r3944realms.superleadrope.config.LeashCommonConfig;

import java.util.LinkedList;
import java.util.Queue;

public class RidingValidator {
    /**
     * 是否在配置白名单里
     */
    @SuppressWarnings("deprecation")
    public static boolean isInWhitelist(EntityType<?> type) {
        String key = type.builtInRegistryHolder().key().location().toString();
        String modid = key.split(":")[0];

        for (String entry : LeashCommonConfig.COMMON.teleportWhitelist.get()) {
            if (entry.startsWith("#")) {
                String body = entry.substring(1);

                // Case 1: #modid → allow all entities from this mod
                if (!body.contains(":")) {
                    if (modid.equals(body)) {
                        return true;
                    }
                }
                // Case 2: #modid:tag_name → allow all entities under this tag
                else {
                    ResourceLocation tagId = new ResourceLocation(body);
                    TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
                    if (type.builtInRegistryHolder().is(tag)) {
                        return true;
                    }
                }
            } else {
                // Case 3: modid:entity_name → allow a specific entity
                if (entry.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 检查骑乘是否会产生循环引用
     */
    public static boolean wouldCreateCycle(Entity entity, Entity vehicle) {
        // 如果实体就是载具本身，直接产生循环
        if (entity == vehicle) {
            return true;
        }

        // 检查载具是否已经是实体的乘客（直接或间接）
        return isIndirectPassenger(vehicle, entity);
    }

    /**
     * 检查target是否是entity的间接乘客
     */
    public static boolean isIndirectPassenger(Entity target, Entity entity) {
        Queue<Entity> queue = new LinkedList<>();
        queue.offer(entity);

        while (!queue.isEmpty()) {
            Entity current = queue.poll();
            if (current == target) {
                return true;
            }

            // 检查当前实体的所有乘客
            for (Entity passenger : current.getPassengers()) {
                queue.offer(passenger);
            }
        }

        return false;
    }
}
