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

import java.util.*;
import java.util.function.Function;

/**
 * The type Riding dismounts.
 */
public class RidingDismounts {
    /**
     * 解除单个实体的骑乘关系
     *
     * @param entity the entity
     */
    public static void dismountEntity(Entity entity) {
        if (entity == null) {
            return;
        }

        // 如果实体正在骑乘，先下车
        if (entity.isPassenger()) {
            entity.stopRiding();
        }

        // 让所有乘客下车
        dismountAllPassengers(entity);
    }

    /**
     * 解除实体及其所有乘客的骑乘关系（非递归）
     *
     * @param entity the entity
     */
    public static void dismountAllPassengers(Entity entity) {
        if (entity == null) {
            return;
        }

        // 使用队列进行广度优先遍历
        Queue<Entity> queue = new LinkedList<>();
        queue.offer(entity);

        while (!queue.isEmpty()) {
            Entity current = queue.poll();

            // 让当前实体的所有乘客下车
            List<Entity> passengers = new ArrayList<>(current.getPassengers());
            for (Entity passenger : passengers) {
                passenger.stopRiding();
                queue.offer(passenger);
            }
        }
    }

    /**
     * 解除根实体的骑乘关系（包括从载具下车）
     *
     * @param entity the entity
     */
    public static void dismountRootEntity(Entity entity) {
        if (entity == null) {
            return;
        }

        // 找到根载具
        Entity rootVehicle = RidingFinder.findRootVehicle(entity);
        if (rootVehicle != null) {
            // 让根载具的所有乘客下车
            dismountAllPassengers(rootVehicle);

            // 根载具本身也下车（如果有载具的话）
            if (rootVehicle.isPassenger()) {
                rootVehicle.stopRiding();
            }
        }
    }

    /**
     * 安全解除骑乘关系（带超时保护）
     *
     * @param entity        the entity
     * @param maxIterations the max iterations
     * @return the boolean
     */
    public static boolean safeDismountAll(Entity entity, int maxIterations) {
        if (entity == null) {
            return true;
        }

        int iteration = 0;
        Queue<Entity> queue = new LinkedList<>();
        queue.offer(entity);

        while (!queue.isEmpty() && iteration < maxIterations) {
            Entity current = queue.poll();
            iteration++;

            // 让当前实体下车（如果是乘客）
            if (current.isPassenger()) {
                current.stopRiding();
            }

            // 处理当前实体的乘客
            List<Entity> passengers = new ArrayList<>(current.getPassengers());
            for (Entity passenger : passengers) {
                passenger.stopRiding();
                queue.offer(passenger);
            }
        }

        return queue.isEmpty(); // 如果队列为空表示全部解除成功
    }

    /**
     * 批量解除多个实体的骑乘关系
     *
     * @param entities the entities
     */
    public static void dismountEntities(Collection<Entity> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        Set<Entity> processed = new HashSet<>();
        Queue<Entity> queue = new LinkedList<>(entities);

        while (!queue.isEmpty()) {
            Entity current = queue.poll();

            if (current != null && !processed.contains(current)) {
                processed.add(current);

                // 让当前实体下车
                if (current.isPassenger()) {
                    current.stopRiding();
                }

                // 处理乘客
                List<Entity> passengers = new ArrayList<>(current.getPassengers());
                for (Entity passenger : passengers) {
                    if (!processed.contains(passenger)) {
                        queue.offer(passenger);
                    }
                }
            }
        }
    }

    /**
     * 根据骑乘关系数据结构解除骑乘
     *
     * @param relationship   the relationship
     * @param entityProvider the entity provider
     */
    public static void dismountByRelationship(RidingRelationship relationship,
                                              Function<UUID, Entity> entityProvider) {
        if (relationship == null || entityProvider == null) {
            return;
        }

        // 使用栈进行深度优先遍历解除
        Deque<RidingRelationship> stack = new ArrayDeque<>();
        stack.push(relationship);

        while (!stack.isEmpty()) {
            RidingRelationship current = stack.pop();

            // 解除当前实体的骑乘
            Entity entity = entityProvider.apply(current.getEntityId());
            if (entity != null && entity.isPassenger()) {
                entity.stopRiding();
            }

            // 将子乘客加入栈中（后进先出，深度优先）
            List<RidingRelationship> passengers = current.getPassengers();
            for (int i = passengers.size() - 1; i >= 0; i--) {
                stack.push(passengers.get(i));
            }
        }
    }

    /**
     * 立即解除所有骑乘关系（强制方式）
     *
     * @param entity the entity
     */
    public static void forceDismountAll(Entity entity) {
        if (entity == null) {
            return;
        }

        // 先让自己下车
        if (entity.isPassenger()) {
            entity.stopRiding();
        }

        // 使用广度优先让所有乘客下车
        List<Entity> allPassengers = RidingFinder.getAllPassengers(entity, false);
        for (Entity passenger : allPassengers) {
            if (passenger.isPassenger()) {
                passenger.stopRiding();
            }
        }

        // 再次检查并清理（确保完全解除）
        if (!entity.getPassengers().isEmpty()) {
            entity.ejectPassengers();
        }
    }
}
