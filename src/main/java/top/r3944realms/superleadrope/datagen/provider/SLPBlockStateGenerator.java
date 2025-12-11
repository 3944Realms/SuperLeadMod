package top.r3944realms.superleadrope.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.core.register.SLPBlocks;

public class SLPBlockStateGenerator extends BlockStateProvider {
    public SLPBlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SuperLeadRope.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(SLPBlocks.DOLL.get(), models().getExistingFile(modLoc("block/doll")));
    }
}