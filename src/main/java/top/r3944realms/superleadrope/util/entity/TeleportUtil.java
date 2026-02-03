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

package top.r3944realms.superleadrope.util.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * The type Teleport util.
 */
public class TeleportUtil {

    /**
     * 通用实体传送（支持玩家、普通生物、跨维度、生物停飞、导航停止等）
     *
     * @param oldEntity   the old entity
     * @param targetWorld the target world
     * @param targetPos   the target pos
     * @param direction   the direction
     * @return the entity
     */
    public static @NotNull Entity teleportEntity(Entity oldEntity, ServerLevel targetWorld, @NotNull Vec3 targetPos, @NotNull Direction direction) {
        float yaw = direction.toYRot();
        double x = targetPos.x;
        double y = targetPos.y;
        double z = targetPos.z;

        if (oldEntity instanceof ServerPlayer player) {
            teleportPlayer(player, targetWorld, x, y, z, yaw);
        } else {
            oldEntity = teleportNonPlayer(oldEntity, targetWorld, x, y, z, yaw);
        }

        // 停止滑翔，否则 Elytra 无限飞
        if (!(oldEntity instanceof LivingEntity living) || !living.isFallFlying()) {
            oldEntity.setDeltaMovement(oldEntity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            oldEntity.setOnGround(true);
        }

        // 停止 AI 导航，避免卡行为树
        if (oldEntity instanceof PathfinderMob mob) {
            mob.getNavigation().stop();
        }

        sendHackySyncPacketsAfterTeleport(oldEntity);

        return oldEntity;
    }


    // --- 分离后的子方法 ------------------------------------------------

    private static void teleportPlayer(@NotNull ServerPlayer player, @NotNull ServerLevel targetWorld,
                                       double x, double y, double z, float yaw) {

        ChunkPos chunkPos = new ChunkPos(BlockPos.containing(x, y, z));
        targetWorld.getChunkSource().addRegionTicket(
                TicketType.POST_TELEPORT,
                chunkPos,
                1,
                player.getId()
        );

        // 玩家睡觉状态处理
        if (player.isSleeping()) {
            player.stopSleepInBed(true, true);
        }

        player.stopRiding();

        // 同维度直接 connection.teleport
        if (targetWorld == player.level()) {
            player.connection.teleport(x, y, z, yaw, player.getXRot(), Collections.emptySet());
        } else {
            player.teleportTo(targetWorld, x, y, z, yaw, player.getXRot());
        }

        player.setYHeadRot(yaw);
    }


    private static @NotNull Entity teleportNonPlayer(@NotNull Entity entity, ServerLevel targetWorld,
                                                     double x, double y, double z, float yaw) {

        float pitch = Mth.clamp(entity.getXRot(), -90, 90);

        // 同维度移动
        if (targetWorld == entity.level()) {
            entity.moveTo(x, y, z, yaw, pitch);
            entity.setYHeadRot(yaw);
            return entity;
        }

        // 跨维度传送
        entity.unRide();

        Entity newEntity = entity.getType().create(targetWorld);

        if (newEntity == null) {
            return entity;
        }

        newEntity.restoreFrom(entity);
        newEntity.moveTo(x, y, z, yaw, pitch);
        newEntity.setYHeadRot(yaw);

        entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        targetWorld.addDuringTeleport(newEntity);

        return newEntity;
    }


    /**
     * 修复玩家经验条显示等问题的同步包
     */
    private static void sendHackySyncPacketsAfterTeleport(Entity entity) {
        if (entity instanceof ServerPlayer player) {
            player.connection.send(
                    new ClientboundSetExperiencePacket(
                            player.experienceProgress,
                            player.totalExperience,
                            player.experienceLevel
                    )
            );
        }
    }
}
