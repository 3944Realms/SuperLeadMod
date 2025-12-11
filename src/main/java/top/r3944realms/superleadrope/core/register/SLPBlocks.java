package top.r3944realms.superleadrope.core.register;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.block.DollBlock;

import java.util.Collection;

public class SLPBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SuperLeadRope.MOD_ID);
    public static final RegistryObject<Block> DOLL = BLOCKS.register("doll", DollBlock::new);
    public static Collection<RegistryObject<Block>> getEntries() {
        return BLOCKS.getEntries();
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
