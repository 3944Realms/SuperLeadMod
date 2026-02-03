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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


/**
 * The type Slp potion recipe registry.
 */
public class SLPPotionRecipeRegistry {
    /**
     * The type Proper brewing recipe.
     */
    public static class ProperBrewingRecipe extends BrewingRecipe {
        private final Ingredient input;

        /**
         * Instantiates a new Proper brewing recipe.
         *
         * @param input      the input
         * @param ingredient the ingredient
         * @param output     the output
         */
        public ProperBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
            super(input, ingredient, output);
            this.input = input;
        }


        @Override
        public boolean isInput(@NotNull ItemStack stack) {
            ItemStack[] matchingStacks = input.getItems();
            if (matchingStacks.length == 0) {
                return stack.isEmpty();
            } else {
                for (ItemStack itemstack : matchingStacks) {
                    if (ItemStack.isSameItem(stack, itemstack) && ItemStack.isSameItemSameTags(itemstack, stack)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * Create potion item stack.
     *
     * @param potion the potion
     * @return the item stack
     */
    @Contract("_ -> new")
    public static @NotNull ItemStack createPotion(@NotNull RegistryObject<Potion> potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get());
    }

    /**
     * Create potion item stack.
     *
     * @param potion the potion
     * @return the item stack
     */
    @Contract("_ -> new")
    public static @NotNull ItemStack createPotion(Potion potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

    /**
     * Init.
     */
    public static void init() {
        BrewingRecipeRegistry.addRecipe(
                new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.INVISIBILITY)), Ingredient.of(Items.SLIME_BALL), createPotion(SLPPotions.NO_SUPER_LEASH)));
        BrewingRecipeRegistry.addRecipe(
                new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.LONG_INVISIBILITY)), Ingredient.of(Items.SLIME_BALL), createPotion(SLPPotions.LONG_NO_SUPER_LEASH)));
        BrewingRecipeRegistry.addRecipe(
                new ProperBrewingRecipe(Ingredient.of(createPotion(SLPPotions.NO_SUPER_LEASH)), Ingredient.of(Items.REDSTONE), createPotion(SLPPotions.LONG_NO_SUPER_LEASH)));
    }

}
