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

package top.r3944realms.superleadrope.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.datagen.provider.*;
import top.r3944realms.superleadrope.utils.lang.LanguageEnum;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SLPDataGenEvent {
    static Logger logger = LoggerFactory.getLogger(SLPDataGenEvent.class);
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) throws IOException {
        logger.info("GatherDataEvent thread: {}", Thread.currentThread().getName());
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        LanguageGenerator(event, LanguageEnum.English);
        LanguageGenerator(event, LanguageEnum.SimpleChinese);
        LanguageGenerator(event, LanguageEnum.TraditionalChinese);
        LanguageGenerator(event, LanguageEnum.LiteraryChinese);
        RecipeGenerator(event);
        ModelDataGenerate(event);
        TagsProvider(event, lookupProvider);
        SoundProvider(event);
    }
    private static void LanguageGenerator(GatherDataEvent event, LanguageEnum language) {
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<SLPLanguageProvider>) pOutput -> new SLPLanguageProvider(pOutput, language)
        );
    }

    private static void RecipeGenerator(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(
                true,
                new SLPItemRecipeProvider(packOutput)
        );
    }
    private static void TagsProvider(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> completableFuture) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<SLPBlockTagProvider>) pOutput -> new SLPBlockTagProvider(pOutput, completableFuture, event.getExistingFileHelper())

        );
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<SLPItemTagProvider>) pOutput ->
                        new SLPItemTagProvider(pOutput, completableFuture, event.getExistingFileHelper())
        );
    }
    private static void SoundProvider(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<SLPSoundDefinitionsProvider>) pOutput ->
                        new SLPSoundDefinitionsProvider(pOutput,event.getExistingFileHelper())
        );
    }

    private static void ModelDataGenerate(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<SLPItemModelProvider>) pOutput -> new SLPItemModelProvider(pOutput, event.getExistingFileHelper())
        );
    }
}
