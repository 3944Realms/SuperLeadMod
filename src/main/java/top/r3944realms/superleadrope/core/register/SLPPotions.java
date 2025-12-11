package top.r3944realms.superleadrope.core.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;

import java.util.function.Supplier;

public class SLPPotions {
    public static DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, SuperLeadRope.MOD_ID);
    public static final RegistryObject<Potion> NO_SUPER_LEASH = register("no_super_leash",
            () -> new Potion("no_super_leash", new MobEffectInstance(SLPEffects.NO_SUPER_LEASH_EFFECT.get(), 1200, 0))
    );
    public static final RegistryObject<Potion> LONG_NO_SUPER_LEASH = register("long_no_super_leash",
            () -> new Potion("no_super_leash", new MobEffectInstance(SLPEffects.NO_SUPER_LEASH_EFFECT.get(), 3600, 0))
    );
    public static <T extends Potion>RegistryObject<Potion> register(String Name, Supplier<T> supplier) {
        return POTIONS.register(Name, supplier);
    }
    /**
     *
     * @param name the Name of Potion
     * @param type (char)<br/> [ <br/> 0 & 3 ~ 255 : potion <br/>1 : lingering_potion <br/>2 : splash_potion<br/>]
     * @return Language Key
     */
    public static String getPotionNameKey(String name, char type) {
        return "item.minecraft." +
                (type == 1 ? "lingering_potion" :
                        (type == 2 ? "splash_potion" : "potion")
                )
                + ".effect." + name;
    }
    public static String getTippedArrowNameKey(String Name) {
        return "item.minecraft.tipped_arrow.effect." + Name;
    }

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
