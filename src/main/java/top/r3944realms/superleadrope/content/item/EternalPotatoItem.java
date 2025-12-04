/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR 阿 PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package top.r3944realms.superleadrope.content.item;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.SLPDamageTypes;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.provider.EternalPotatoProvider;
import top.r3944realms.superleadrope.datagen.data.SLPLangKeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * The type Eternal potato item.
 */
public class EternalPotatoItem extends Item {

    private static final String ITEM_UUID_TAG = "item_uuid";

    /**
     * Instantiates a new Eternal potato item.
     *
     * @param properties the properties
     */
    public EternalPotatoItem(Properties properties) {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new EternalPotatoProvider(stack);
    }

    /**
     * 获取或生成 ItemStack 的唯一 UUID  @param stack the stack
     *
     * @param stack the stack
     * @return the or create item uuid
     */
    public static UUID getOrCreateItemUUID(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.hasUUID(ITEM_UUID_TAG)) {
            return tag.getUUID(ITEM_UUID_TAG);
        } else {
            UUID uuid = UUID.randomUUID();
            tag.putUUID(ITEM_UUID_TAG, uuid);
            return uuid;
        }
    }

    /** 使用契约物品完成义务 */
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || context.getLevel().isClientSide) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();

        stack.getCapability(CapabilityHandler.ETERNAL_POTATO_CAP).ifPresent(cap -> {
            if (cap.getCompletionRule().isCompleted((ServerPlayer) player, stack)) {
                cap.getCompletionRule().onCompleted((ServerPlayer) player, stack);
                cap.beginInit();
                cap.setPendingPunishments(Math.max(0, cap.getPendingPunishments() - 1));
                cap.endInit();
            }
        });

        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        stack.getCapability(CapabilityHandler.ETERNAL_POTATO_CAP).ifPresent(cap -> {
            tooltip.add(Component.translatable(SLPLangKeyValue.EP_TOOLTIP_TITLE.getKey()));
            tooltip.add(Component.translatable(SLPLangKeyValue.EP_DESC_TOOLTIP.getKey()));
            if (cap.getOwnerUUID() != null) {
                tooltip.add(Component.translatable(SLPLangKeyValue.EP_OBLIGATION_TOOLTIP.getKey(), cap.getFinalTaskCount(), cap.getPendingPunishments()));
                if (cap.getPendingPunishments() > cap.getGracePunishments())
                    tooltip.add(Component.translatable(SLPLangKeyValue.EP_PUNISH_TOOLTIP.getKey(), cap.getPendingPunishments(), cap.getGracePunishments()));
                tooltip.add(Component.translatable(SLPLangKeyValue.EP_BIND_OWNER.getKey(), cap.getOwnerName()));
            } else {
                tooltip.add(Component.translatable(SLPLangKeyValue.EP_UNBOUND.getKey()));
            }
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // 客户端只返回结果，不修改任何数据
            return InteractionResultHolder.consume(stack);
        }

        // 服务端逻辑
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);


        stack.getCapability(CapabilityHandler.ETERNAL_POTATO_CAP).ifPresent(cap -> {
            // 首次绑定
            if (cap.getOwnerUUID() == null) {
                cap.beginInit();
                if (player instanceof ServerPlayer serverPlayer) cap.bindSyncContext(serverPlayer);
                cap.setOwner(player.getUUID(), player.getDisplayName().getString());
                cap.setDailyObligations(99);
                cap.setPendingPunishments(0);
                cap.endInit();
                player.displayClientMessage(Component.translatable(SLPLangKeyValue.EP_BIND_MSG.getKey()), true);
                cap.syncToClient(player); // 只在服务端发包
                return;
            }

            // 已绑定 → 只在服务端更新
            if (cap.getBoundPlayer() == null || !cap.getBoundPlayer().getUUID().equals(player.getUUID())) {
                if (player instanceof ServerPlayer serverPlayer) cap.bindSyncContext(serverPlayer);
                assert player instanceof ServerPlayer;
                cap.syncToClient(player);
            }

            boolean isOwner = cap.getOwnerUUID().equals(player.getUUID());
            if (!isOwner) {
                cap.getPunishment().execute(
                        (ServerPlayer) player,
                        new DamageSource(Holder.direct(SLPDamageTypes.ETERNAL_POTATO_NOT_OWNER), player),
                        Component.translatable(SLPLangKeyValue.EP_PUNISH_NOT_OWNER.getKey())
                );
            } else {
                player.startUsingItem(hand);
                player.heal(50.0F);
                player.getFoodData().eat(18, 8);
                player.displayClientMessage(Component.translatable(SLPLangKeyValue.EP_OBLIGATION_INFO.getKey(), cap.getDailyObligations()), true);
                player.getCooldowns().addCooldown(this, 20 * 3);
            }
        });

        return InteractionResultHolder.sidedSuccess(stack, false);
    }


    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (entity instanceof Player player && !level.isClientSide) {
            player.heal(50.0F);
            player.getFoodData().eat(18, 8);
            player.displayClientMessage(Component.translatable(SLPLangKeyValue.EP_POTATO_HEAL.getKey()), true);
            player.getCooldowns().addCooldown(this, 20 * 3);
        }
        return stack.copy();
    }

    /**
     * Gets desc key.
     *
     * @param name the name
     * @return the desc key
     */
    public static String getDescKey(String name) {
        return "item.eternal_potato.tooltip." + name;
    }

    /**
     * Gets msg key.
     *
     * @param name the name
     * @return the msg key
     */
    public static String getMsgKey(String name) {
        return "item.eternal_potato.msg." + name;
    }

    @Override
    public boolean canBeHurtBy(@NotNull DamageSource source) {
        return false;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        // 延迟初始化客户端逻辑
//        if (level.isClientSide) { // 只在客户端执行
//            // 直接通过 ItemStack 的 UUID 调用 Manager
//            UUID uuid = EternalPotatoItem.getOrCreateItemUUID(stack);
//            EternalPotatoFacade.getOrCreate(uuid);
//        }
    }

    /**
     * Ensure item in inventory.
     *
     * @param player the player
     * @param stack  the stack
     */
    public static void ensureItemInInventory(Player player, ItemStack stack) {
        if (player.getInventory().add(stack)) return;

        List<ItemStack> nonEmptySlots = new ArrayList<>();
        for (ItemStack s : player.getInventory().items) {
            if (!s.isEmpty() && s != stack) nonEmptySlots.add(s);
        }

        if (!nonEmptySlots.isEmpty()) {
            ItemStack toDrop = nonEmptySlots.get(player.getRandom().nextInt(nonEmptySlots.size()));
            player.drop(toDrop.copy(), true, false);
            player.getInventory().removeItem(toDrop);

            player.getInventory().add(stack);
            return;
        }

        ItemStack currentHelmet = player.getInventory().armor.get(3);
        if (!currentHelmet.isEmpty()) {
            Objects.requireNonNull(player.drop(currentHelmet.copy(), true, false))
                    .setPos(player.getX(), player.getY() + 1, player.getZ());
        }
        player.getInventory().armor.set(3, stack);
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }
}