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

package top.r3944realms.superleadrope.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.entity.SuperLeashRopeEntity;

/**
 * The type Ring toss enchantment.
 */
public class RingTossEnchantment extends Enchantment {
    /**
     * Instantiates a new Ring toss enchantment.
     *
     * @param rarity          the rarity
     * @param category        the category
     * @param applicableSlots the applicable slots
     */
    public RingTossEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    /**
     * Work.
     *
     * @param level            the level
     * @param entity           the entity
     * @param stack            the stack
     * @param chargePower      the charge power
     * @param enchantmentLevel the enchantment level
     */
    public static void work(Level level, @NotNull LivingEntity entity, ItemStack stack, float chargePower, int enchantmentLevel) {
        SuperLeashRopeEntity superLeashRopeEntity = new SuperLeashRopeEntity(level, entity, stack);
        if (entity instanceof Player player) {
            if(!player.isCreative()) {
                player.getInventory().removeItem(stack);
            } else superLeashRopeEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            superLeashRopeEntity.setLevel(enchantmentLevel);
            superLeashRopeEntity.setChargePower(chargePower);
        }
        superLeashRopeEntity.startThrown();
        level.addFreshEntity(superLeashRopeEntity);
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }
}
