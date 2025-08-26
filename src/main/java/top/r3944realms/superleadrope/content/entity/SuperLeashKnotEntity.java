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

package top.r3944realms.superleadrope.content.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.LeashDataImpl;
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SuperLeashKnotEntity extends LeashFenceKnotEntity {
    // 默认尺寸
    private static final float DEFAULT_WIDTH = 0.5f;
    private static final float DEFAULT_HEIGHT = 0.5f;

    // 当前计算出的尺寸
    private float currentWidth = DEFAULT_WIDTH;
    private float currentHeight = DEFAULT_HEIGHT;

    // 支持的方块标签 //TODO:未来可配置化
    private static final List<TagKey<Block>> SUPPORTED_BLOCK = Arrays.asList(
            BlockTags.FENCES,
            BlockTags.WALLS
    );

    public SuperLeashKnotEntity(EntityType<? extends SuperLeashKnotEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SuperLeashKnotEntity(Level pLevel, BlockPos pPos) {
        this(SLPEntityTypes.SUPER_LEAD_KNOT.get(), pLevel);
        this.setPos(pPos.getX(), pPos.getY(), pPos.getZ());
    }


    @Override
    public boolean survives() {
        return SuperLeashKnotEntity.isSupportBlock(this.level().getBlockState(this.pos));
    }

    public static @NotNull SuperLeashKnotEntity getOrCreateKnot(@NotNull Level pLevel, @NotNull BlockPos pPos) {
        int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();

        for(SuperLeashKnotEntity superLeashKnotEntity : pLevel.getEntitiesOfClass(SuperLeashKnotEntity.class, new AABB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
            if (superLeashKnotEntity.getPos().equals(pPos)) {
                return superLeashKnotEntity;
            }
        }

        SuperLeashKnotEntity superLeashKnotEntity1 = new SuperLeashKnotEntity(pLevel, pPos);
        pLevel.addFreshEntity(superLeashKnotEntity1);
        return superLeashKnotEntity1;
    }

    @Override
    protected void recalculateBoundingBox() {
        updateDimensionsBasedOnBlock();
        double halfWidth = currentWidth / 2.0f;
        this.setBoundingBox(new AABB(
                this.getX() - halfWidth,
                this.getY(),
                this.getZ() - halfWidth,
                this.getX() + halfWidth,
                this.getY() + currentHeight,
                this.getZ() + halfWidth
        ));
    }

    private void updateDimensionsBasedOnBlock() {
        BlockState state = this.level().getBlockState(this.pos);

        // 根据方块类型调整尺寸
        if (state.is(BlockTags.WALLS)) {
            // 墙类方块 - 更窄更高
            currentWidth = 0.25f;
            currentHeight = 0.75f;
        } else {
            // 默认栅栏尺寸
            currentWidth = DEFAULT_WIDTH;
            currentHeight = DEFAULT_HEIGHT;
        }
        //TODO: 未来扩展可配置化大小
    }
    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        // 确保位置与方块中心对齐
        BlockPos pos = this.getPos();
        this.setPosRaw(pos.getX() + 0.5, pos.getY() + 0.375, pos.getZ() + 0.5);
    }

    public static boolean isSupportBlock(BlockState state) {
        for(TagKey<Block> tagKey : SUPPORTED_BLOCK) {
            if(state.is(tagKey)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        AtomicBoolean isTransferLeash = new AtomicBoolean(false);
        List<Entity> entities = LeashDataImpl.leashableInArea(player, ing -> true);
        for(Entity entity : entities) {
            if (LeashDataImpl.isLeashHolder(entity, player.getUUID()))
                entity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(i -> {
                    i.transferLeash(player.getUUID(), this);
                    isTransferLeash.set(true);
                });

            }


        if (isTransferLeash.get() ) {
            this.gameEvent(GameEvent.BLOCK_ATTACH, player);
        }
        return InteractionResult.CONSUME;
    }
}
