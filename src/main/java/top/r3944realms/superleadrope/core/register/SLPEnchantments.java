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

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.enchantment.PrisonerCurseEnchantment;
import top.r3944realms.superleadrope.content.enchantment.RingTossEnchantment;
import top.r3944realms.superleadrope.content.enchantment.SLPEnchantmentCategory;

/**
 * The type Slp enchantments.
 */
public class SLPEnchantments {
    /**
     * The constant ENCHANTMENTS.
     */
    public static final DeferredRegister<Enchantment> ENCHANTMENTS= DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, SuperLeadRope.MOD_ID);

    /**
     * The constant RING_TOSS.
     */
    public static final RegistryObject<Enchantment> RING_TOSS = ENCHANTMENTS.register(
            "ring_toss", () -> new RingTossEnchantment(Enchantment.Rarity.COMMON, SLPEnchantmentCategory.SUPER_LEAD, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND})
    );

    /**
     * The constant SELF_PRISONER.
     */
    public static final RegistryObject<Enchantment> SELF_PRISONER = ENCHANTMENTS.register(
            "prisoner", () -> new PrisonerCurseEnchantment(Enchantment.Rarity.RARE, SLPEnchantmentCategory.SUPER_LEAD, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND})
    );


    /**
     * Register.
     *
     * @param eventBus the event bus
     */
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
