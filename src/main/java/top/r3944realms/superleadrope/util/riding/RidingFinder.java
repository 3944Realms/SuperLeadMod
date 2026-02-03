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

package top.r3944realms.superleadrope.util.riding;

import net.minecraft.world.entity.Entity;
import top.r3944realms.superleadrope.util.model.RidingRelationship;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * The type Riding finder.
 */
public class RidingFinder {
    /**
     * 从JSON字符串应用骑乘关系
     *
     * @param ship           the ship
     * @param entityProvider the entity provider
     * @return the entity from riding ship
     */
    public static List<Entity> getEntityFromRidingShip(RidingRelationship ship,
                                                       Function<UUID, Entity> entityProvider) {
        List<Entity> ret = new ArrayList<>();
        Queue<RidingRelationship> queue = new LinkedList<>();
        queue.offer(ship);
        while (!queue.isEmpty()) {
            RidingRelationship poll = queue.poll();
            ret.add(entityProvider.apply(ship.getEntityId()));
            List<RidingRelationship> passengers = poll.getPassengers();
            if (!passengers.isEmpty()) {
                queue.addAll(passengers);
            }
        }
        return ret;
    }

    /**
     * 查找根载具
     *
     * @param entity the entity
     * @return the entity
     */
    @Nullable
    public static Entity findRootVehicle(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }

        Entity current = entity;
        while (current.getVehicle() != null) {
            current = current.getVehicle();
            // 安全保护，防止意外循环
            if (current == entity) {
                break;
            }
        }
        return current;
    }

    /**
     * 获取所有乘客（包括嵌套乘客）
     *
     * @param entity the entity
     * @return the all passengers
     */
    public static List<Entity> getAllPassengers(@Nullable Entity entity) {
        return getAllPassengers(entity, true);
    }

    /**
     * 获取所有乘客（包括嵌套乘客）
     *
     * @param entity   the entity
     * @param findRoot the find root
     * @return the all passengers
     */
    public static List<Entity> getAllPassengers(@Nullable Entity entity, boolean findRoot) {
        if (entity == null) {
            return Collections.emptyList();
        }

        Entity rootEntity = findRoot ? findRootVehicle(entity) : entity;
        if (rootEntity == null) {
            return Collections.emptyList();
        }

        List<Entity> result = new ArrayList<>();
        Queue<Entity> queue = new LinkedList<>();
        queue.offer(rootEntity);

        while (!queue.isEmpty()) {
            Entity current = queue.poll();
            result.add(current); // 把当前实体加入列表

            List<Entity> passengers = current.getPassengers();
            if (!passengers.isEmpty()) {
                queue.addAll(passengers);
            }
        }

        return Collections.unmodifiableList(result);
    }

}
