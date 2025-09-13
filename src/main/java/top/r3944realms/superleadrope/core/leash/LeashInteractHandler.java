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

package top.r3944realms.superleadrope.core.leash;


import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.ILeashData;
import top.r3944realms.superleadrope.content.item.SuperLeadRopeItem;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;

public class LeashInteractHandler {
    //只有玩家可以互动触发（其它的暂不支持（考虑到0 Mixin)
    public static void onEntityRightInteract(Level level, InteractionHand hand, Entity target , Player player, PlayerInteractEvent.EntityInteract event) {
        //WARNING: 主手和副手都会触发一次该事件

        // ===== 卫语句 =====
        if (level.isClientSide) {
            if (hand == InteractionHand.MAIN_HAND &&
                    (player.getItemInHand(InteractionHand.MAIN_HAND).is(SLPItems.SUPER_LEAD_ROPE.get()) ||
                    player.getItemInHand(InteractionHand.OFF_HAND).is(SLPItems.SUPER_LEAD_ROPE.get()))
            ) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
            return;
        }
        if (hand == InteractionHand.OFF_HAND) {
            return;
        }

        if (!LeashDataImpl.isLeashable(target)) {
            return;
        }
        LazyOptional<ILeashData> LeashCap = target.getCapability(CapabilityHandler.LEASH_DATA_CAP);
        if (!LeashCap.isPresent()) {
            return;
        }

        ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        //shift 按下 && 主副手无物品 attach拴绳 && 互动实体是Alive的 && 实体可被拴（保留改方法，未来使用）
        if (
                mainHandItem.isEmpty() && offHandItem.isEmpty() &&
                        target.isAlive() && player.isSecondaryUseActive() &&
                        LeashCap.map(ILeashData::canBeLeashed).orElse(false)

        ) {

            boolean isSuccess = SuperLeadRopeItem.bindToEntity(target, player, player.level(), player.getOnPos(), ItemStack.EMPTY);
            if (isSuccess) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        } else {
            if (LeashDataImpl.isLeashHolder(target, player)) {
                LeashCap.ifPresent(
                        iLeashDataCapability -> iLeashDataCapability.removeLeash(player.getUUID())
                );
                target.gameEvent(GameEvent.ENTITY_INTERACT, player);
                level.playSound(null, target.getOnPos(), SLPSoundEvents.LEAD_UNTIED.get(), SoundSource.PLAYERS);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }
            ItemStack itemStack;
            if (mainHandItem.is(SLPItems.SUPER_LEAD_ROPE.get()) && SuperLeadRopeItem.canUse(mainHandItem)) {
                itemStack = mainHandItem;
            } else if (offHandItem.is(SLPItems.SUPER_LEAD_ROPE.get()) && SuperLeadRopeItem.canUse(offHandItem)) {
                itemStack = offHandItem;
            } else {
                itemStack = ItemStack.EMPTY;
            }
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() == SLPItems.SUPER_LEAD_ROPE.get()) {
                    LeashCap.ifPresent(iLeashDataCapability -> {
                        if (iLeashDataCapability.canBeAttachedTo(player)) {
                            boolean success = iLeashDataCapability.addLeash(player);
                            if (success) {
                                if(!player.isCreative())
                                    itemStack.hurtAndBreak(24, player, e->{});
                                level.playSound(null, target.getOnPos(), SLPSoundEvents.LEAD_TIED.get(), SoundSource.PLAYERS);
                                event.setCanceled(true);
                                event.setCancellationResult(InteractionResult.SUCCESS);
                             }
                        }
                    });
                }
            }
        }

    }
    public static void onEntityLeftInteract(Level level, Entity target , Player player, AttackEntityEvent event) {
        boolean flag = LeashDataImpl.isLeashable(target) && player.getItemInHand(InteractionHand.MAIN_HAND).is(SLPItems.SUPER_LEAD_ROPE.get());
        if (level.isClientSide) {
            if (flag) {
                event.setCanceled(true);
            }
        } else {
            if (flag) {
                target.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(leashDataCapability -> {
                    if (leashDataCapability.hasLeash()){
                        int size = leashDataCapability.getAllLeashes().size();
                        if (player.isSecondaryUseActive())
                            leashDataCapability.removeAllLeashes();
                        else
                            leashDataCapability.removeAllKnotLeashes();
                        if(size > leashDataCapability.getAllLeashes().size()) level.playSound(null, target.getOnPos(), SLPSoundEvents.LEAD_UNTIED.get(), SoundSource.PLAYERS);
                    }
                    event.setCanceled(true);
                });
            }
        }
    }
}
