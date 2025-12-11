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


public class SLPPotionRecipeRegistry {
    public static class ProperBrewingRecipe extends BrewingRecipe {
        private final Ingredient input;

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
    @Contract("_ -> new")
    public static @NotNull ItemStack createPotion(@NotNull RegistryObject<Potion> potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get());
    }

    @Contract("_ -> new")
    public static @NotNull ItemStack createPotion(Potion potion){
        return  PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }
    public static void init() {
        BrewingRecipeRegistry.addRecipe(
                new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.INVISIBILITY)), Ingredient.of(Items.SLIME_BALL), createPotion(SLPPotions.NO_SUPER_LEASH)));
        BrewingRecipeRegistry.addRecipe(
                new ProperBrewingRecipe(Ingredient.of(createPotion(Potions.LONG_INVISIBILITY)), Ingredient.of(Items.SLIME_BALL), createPotion(SLPPotions.LONG_NO_SUPER_LEASH)));
        BrewingRecipeRegistry.addRecipe(
                new ProperBrewingRecipe(Ingredient.of(createPotion(SLPPotions.NO_SUPER_LEASH)), Ingredient.of(Items.REDSTONE), createPotion(SLPPotions.LONG_NO_SUPER_LEASH)));
    }

}
