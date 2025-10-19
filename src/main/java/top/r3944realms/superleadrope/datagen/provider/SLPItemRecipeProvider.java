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

package top.r3944realms.superleadrope.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.core.register.SLPItems;

import java.util.function.Consumer;

/**
 * The type Slp item recipe provider.
 */
public class SLPItemRecipeProvider extends RecipeProvider {
    /**
     * Instantiates a new Slp item recipe provider.
     *
     * @param output the output
     */
    public SLPItemRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SLPItems.SUPER_LEAD_ROPE.get())
                .pattern("SL ")
                .pattern("LE ")
                .pattern("  I")
                .define('S', Items.SLIME_BALL)
                .define('L', Items.LEAD)
                .define('E', Items.EXPERIENCE_BOTTLE)
                .define('I', Items.STRING)
                .unlockedBy("has_lead", has(Items.LEAD))
                .save(consumer);
    }
}
