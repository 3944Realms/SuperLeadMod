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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.UpdatePlayerMovementPacket;

public class RindingLeash {
    /**
     * 获取乘坐链中第一个在白名单的载具，如果没有则返回null
     */
    @Nullable
    public static Entity getSafeWhitelistRoot(Entity entity) {
        if (entity == null) return null;

        Entity root = RidingFinder.findRootVehicle(entity);
        if (root == null) return null;

        Entity current = root;
        while (current != null) {
            if (CommonEventHandler.leashConfigManager.isEntityTeleportAllowed(current.getType())) {
                return current; // 找到白名单载具
            }
            current = current.getVehicle();
        }
        return null; // 整条链条没有白名单载具
    }

    /**
     * 获取最终可作用的载具，用于拴绳合力应用。
     * 当链条中没有白名单载具时解除骑乘并返回自身
     * 仅在拴绳合力不为零时调用
     */
    public static Entity getFinalEntityForLeashIfForce(Entity entity, boolean hasForce) {
        if (!hasForce || entity == null) {
            return entity; // 没有力时，直接返回原实体，不做处理
        }

        Entity root = RidingFinder.findRootVehicle(entity);
        if (root == null) return entity;

        Entity current = root;
        while (current != null) {
            if (CommonEventHandler.leashConfigManager.isEntityTeleportAllowed(current.getType())) {
                return current; // 找到白名单载具
            }
            current = current.getVehicle();
        }

        // 没有白名单载具，解除骑乘
        RidingDismounts.dismountRootEntity(entity);
        return entity; // 返回自身作为最终应用对象
    }


    /**
     * 给动物应用拴绳力前的移动控制保护
     */
    public static void protectAnimalMovement(Entity entity, boolean hasLeash) {
        if (entity instanceof Animal mob) {
            if (hasLeash) {
                mob.goalSelector.disableControlFlag(Goal.Flag.MOVE);
                entity.resetFallDistance();
            } else {
                mob.goalSelector.enableControlFlag(Goal.Flag.MOVE);
            }
        }
    }

    /**
     * 给玩家应用拴绳力前的发包处理
     */
    public static void applyForceToPlayer(ServerPlayer player, Vec3 force) {
        NetworkHandler.sendToPlayer(
                new UpdatePlayerMovementPacket(
                        UpdatePlayerMovementPacket.Operation.ADD,
                        force
                ), player
        );
    }

}
