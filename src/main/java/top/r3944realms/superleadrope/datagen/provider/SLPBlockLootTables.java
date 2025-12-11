/*
 * *
 *  * Copyright (c) 2025 R3944Realms. All rights reserved.
 *  *
 *  * This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *  * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/4.0/
 *  * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *  *
 *  * 本作品采用知识共享署名-非商业性使用-相同方式共享 4.0 国际许可协议进行许可。
 *
 */

package top.r3944realms.superleadrope.datagen.provider;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.core.register.SLPBlocks;

import java.util.Set;


public class SLPBlockLootTables extends BlockLootSubProvider {
    public SLPBlockLootTables() {
        super(Set.of(),  FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(SLPBlocks.DOLL.get());
    }
    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return SLPBlocks.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
