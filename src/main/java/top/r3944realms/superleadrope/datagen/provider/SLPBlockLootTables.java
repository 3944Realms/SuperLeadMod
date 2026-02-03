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

package top.r3944realms.superleadrope.datagen.provider;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.core.register.SLPBlocks;

import java.util.Set;


/**
 * The type Slp block loot tables.
 */
public class SLPBlockLootTables extends BlockLootSubProvider {
    /**
     * Instantiates a new Slp block loot tables.
     */
    public SLPBlockLootTables() {
        super(Set.of(),  FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

    }
    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return SLPBlocks.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
