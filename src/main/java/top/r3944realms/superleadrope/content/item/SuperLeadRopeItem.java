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

package top.r3944realms.superleadrope.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

//TODO:
// 1. 可抛掷使用 ***（下个版本实现）
// - 可转移拴绳
// 2. 多绑定（一个生物可以被多个加强拴绳绑定，产生共同作用效果）
// 3. 支持更多支撑方块
// 4. 可配置的生物栓效果（通过标签系统可配置哪些额外实体可被拴住）
// 5. 可自定义拴绳渲染位置（以匹配不同的生物模型）
// 6. 不可消耗的（但是有耐久，需要修复, 通过消耗原版拴绳恢复，每次多重绑定就会消耗1点耐久）
// 实现拴生物，在生物的interact方法里去写相关逻辑
// （尝试0 mixin 实现 加强拴绳逻辑）
public class SuperLeadRopeItem extends LeadItem implements IForgeItem {

    // 配置常量
    // 【手动调节,可以通过附魔获取更远抛掷和抛掷距离 - x1.3】//TODO:将可抛掷实现留到下次编写
    // 可以做个大于一定距离时远距离使用时抛出拴绳的实体，击中生物才栓中的
    private static final double SHORT_RANGE = 5.0D;
    private static final float THROW_SPEED = 1.5F;


    public SuperLeadRopeItem(@NotNull Properties pProperties) {
        super(
                pProperties
                        .durability(1024)
                        .setNoRepair()
        );
    }
    //通过按键 可抛掷启用/关闭实现（不会影响use逻辑）
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack lead = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide) {
            if (canUse(lead)) return InteractionResultHolder.pass(lead);
            return super.use(pLevel, pPlayer, pUsedHand);

        }
        return InteractionResultHolder.pass(lead);
    }

    public static boolean canUse(ItemStack itemStack) {
        return itemStack.getDamageValue() < 974;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack itemStack = context.getItemInHand();
        if (canUse(itemStack)) return InteractionResult.PASS;
        if(SuperLeashKnotEntity.isSupportBlock(state)) {
            Player player = context.getPlayer();
            if(!level.isClientSide && player != null) {
                return bindToBlock(player, level, pos, itemStack);
            }
        }
        return InteractionResult.PASS;
    }
    public static InteractionResult bindToEntity (Entity newHolder, Player player, Level level, BlockPos pos, ItemStack leashStack) {
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        List<Entity> list = LeashDataImpl.leashableInArea(level, pos.getCenter(), entity -> LeashDataImpl.isLeashHolder(entity, player.getUUID()));
        for(Entity e : list) {
            AtomicBoolean canBeAttachedTo = new AtomicBoolean(false);
            LazyOptional<ILeashDataCapability> iLeashDataCapability = e.getCapability(CapabilityHandler.LEASH_DATA_CAP);
            iLeashDataCapability.ifPresent(i -> {
                if (i instanceof LeashDataImpl li) {
                    canBeAttachedTo.set(li.canBeAttachedTo(newHolder));
                }
            });
            if(canBeAttachedTo.get()) {//canBeAttachedTo
                iLeashDataCapability.ifPresent(i -> {
                    if(i instanceof LeashDataImpl li) {
                        li.transferLeash(player.getUUID(), newHolder, leashStack);
                        isSuccess.set(true);
                    }
                });
            }
        }
        if(!isSuccess.get()) {
            return InteractionResult.PASS;
        }
        else {
            level.gameEvent(GameEvent.ENTITY_INTERACT, pos, GameEvent.Context.of(player));
            newHolder.playSound(SLPSoundEvents.LEAD_TIED.get());
            return InteractionResult.CONSUME;
        }
    }
    public static InteractionResult bindToBlock(Player player, Level level, BlockPos pos, ItemStack leashStack) {
        //实现个加强绳结实体
        SuperLeashKnotEntity knot = null;
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        UUID uuid = player.getUUID();
        List<Entity> list = LeashDataImpl.leashableInArea(level, pos.getCenter(), entity -> LeashDataImpl.isLeashHolder(entity, uuid));
        if(list.isEmpty()) {//拴自己 to new knot
            if (leashStack.isEmpty() || !canUse(leashStack)) return InteractionResult.PASS;
            knot = SuperLeashKnotEntity.getOrCreateKnot(level, pos);;
            knot.playPlacementSound();

            SuperLeashKnotEntity finalKnot1 = knot;
            player.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(i -> {
                if(i instanceof LeashDataImpl li) {
                    li.addLeash(finalKnot1, leashStack, 8D);
                    isSuccess.set(true);
                }
            });

        }
        else {
            for(Entity e : list) {
                if(knot == null) {
                    knot = SuperLeashKnotEntity.getOrCreateKnot(level, pos);;
                    knot.playPlacementSound();
                }
                AtomicBoolean canBeAttachedTo = new AtomicBoolean(false);
                SuperLeashKnotEntity finalKnot = knot;
                LazyOptional<ILeashDataCapability> iLeashDataCapability = e.getCapability(CapabilityHandler.LEASH_DATA_CAP);
                iLeashDataCapability.ifPresent(i -> {
                    if (i instanceof LeashDataImpl li) {
                        canBeAttachedTo.set(li.canBeAttachedTo(finalKnot));
                    }
                });
                if(canBeAttachedTo.get()) {//canBeAttachedTo
                    iLeashDataCapability.ifPresent(i -> {
                        if(i instanceof LeashDataImpl li) {
                            li.transferLeash(uuid, finalKnot);
                            isSuccess.set(true);
                        }
                    });
                }
            }
        }
        if (isSuccess.get()) {
            if(!player.isCreative()) {
                leashStack.hurtAndBreak(50, player, e->{});
            }
            level.gameEvent(GameEvent.BLOCK_ATTACH, pos, GameEvent.Context.of(player));
            return InteractionResult.SUCCESS;
        } else
            return InteractionResult.PASS;
    }
}
