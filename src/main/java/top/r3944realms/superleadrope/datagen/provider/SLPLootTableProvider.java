package top.r3944realms.superleadrope.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The type Simple loot table provider.
 */
public class SLPLootTableProvider extends LootTableProvider {
    /**
     * Instantiates a new Simple loot table provider.
     *
     * @param output              the output
     * @param subProvidersWrapper the sub providers wrapper
     */
    public SLPLootTableProvider(PackOutput output) {
        super(output, Set.of(),  List.of(new LootTableProvider.SubProviderEntry(
                SLPBlockLootTables::new,
                LootContextParamSets.BLOCK
        )));
    }


    @Override
    protected void validate(@NotNull Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationcontext) {
        map.forEach((id, table) -> table.validate(validationcontext));
    }
}
