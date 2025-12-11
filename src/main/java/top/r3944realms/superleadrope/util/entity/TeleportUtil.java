package top.r3944realms.superleadrope.util.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.TicketType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class TeleportUtil {

    /**
     * 通用实体传送（支持玩家、普通生物、跨维度、生物停飞、导航停止等）
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
