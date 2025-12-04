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

package top.r3944realms.superleadrope.content.capability.provider;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;
import top.r3944realms.superleadrope.content.item.EternalPotatoItem;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;

import java.util.UUID;

/**
 * The type Eternal potato provider.
 */
public class EternalPotatoProvider implements ICapabilitySerializable<CompoundTag> {

    /**
     * The constant ETERNAL_POTATO_DATA_REL.
     */
    public static final ResourceLocation ETERNAL_POTATO_DATA_REL =
            new ResourceLocation(SuperLeadRope.MOD_ID, "eternal_potato_data");

    private final UUID uuid;
    private volatile IEternalPotato instance;
    private final LazyOptional<IEternalPotato> optional;

    // 新增引用 ItemStack
    private final ItemStack stack;

    /**
     * Instantiates a new Eternal potato provider.
     *
     * @param stack the stack
     */
    public EternalPotatoProvider(ItemStack stack) {
        this.stack = stack;
        this.uuid = EternalPotatoItem.getOrCreateItemUUID(stack);
        this.instance = null;
        this.optional = LazyOptional.of(this::resolveInstance);
    }

    private IEternalPotato resolveInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = EternalPotatoFacade.getOrCreate(uuid);

                    // 绑定写回回调，只绑定一次
                    instance.bindItemStackSync(() -> {
                        if (!stack.isEmpty()) {
                            stack.setTag(instance.serializeNBT());
                        }
                    });
                }
            }
        }
        return instance;

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CapabilityHandler.ETERNAL_POTATO_CAP.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return resolveInstance() != null ? resolveInstance().serializeNBT() : new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        if (compoundTag == null) return;

        // 只在首次 attach 时初始化 UUID
        if (instance == null) {
            UUID loaded = compoundTag.hasUUID("item_uuid")
                    ? compoundTag.getUUID("item_uuid")
                    : UUID.randomUUID();

            IEternalPotato shared = EternalPotatoFacade.getOrCreate(loaded);

            // 不再调用 shared.deserializeNBT(compoundTag)，由 Manager 统一负责持久化
            this.instance = shared;

            // 绑定写回回调
            shared.bindItemStackSync(() -> {
                if (!stack.isEmpty()) {
                    stack.setTag(shared.serializeNBT());
                }
            });
        }
    }
}