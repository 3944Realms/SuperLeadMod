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

package top.r3944realms.superleadrope.core.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.item.EternalPotatoItem;
import top.r3944realms.superleadrope.content.item.SuperLeadRopeItem;

/**
 * The type Slp items.
 */
public class SLPItems {
    /**
     * The constant ITEMS.
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SuperLeadRope.MOD_ID);
    /**
     * The constant SUPER_LEAD_ROPE.
     */
    public static final RegistryObject<Item> SUPER_LEAD_ROPE = ITEMS.register(
            "super_lead_rope",
            () -> new SuperLeadRopeItem(new Item.Properties())
    );
    /**
     * The constant ETERNAL_POTATO.
     */
    public static final RegistryObject<Item> ETERNAL_POTATO =
            ITEMS.register("eternal_potato",
                    () -> new EternalPotatoItem(
                            new Item.Properties()
                                    .stacksTo(1) // 只能有一颗
                                    .fireResistant() // 防火
                    ));

    /**
     * Register.
     *
     * @param bus the bus
     */
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
