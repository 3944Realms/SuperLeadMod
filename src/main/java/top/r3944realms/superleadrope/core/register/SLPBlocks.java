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

package top.r3944realms.superleadrope.core.register;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;

import java.util.Collection;

/**
 * The type Slp blocks.
 */
public class SLPBlocks {
    /**
     * The constant BLOCKS.
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SuperLeadRope.MOD_ID);

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public static Collection<RegistryObject<Block>> getEntries() {
        return BLOCKS.getEntries();
    }

    /**
     * Register.
     *
     * @param eventBus the event bus
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
