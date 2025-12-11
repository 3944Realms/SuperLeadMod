package top.r3944realms.superleadrope.core.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.effect.NoSuperLeashEffect;

import java.util.function.Supplier;

public class SLPEffects {
    public static DeferredRegister<MobEffect> MOB_EFFECT = DeferredRegister.create(Registries.MOB_EFFECT, SuperLeadRope.MOD_ID);
    public static RegistryObject<MobEffect> NO_SUPER_LEASH_EFFECT = register(
            "no_super_leash",
            () -> new NoSuperLeashEffect(MobEffectCategory.NEUTRAL, 12063764)
    );
    public static <T extends MobEffect> RegistryObject<MobEffect> register(String name, Supplier<T> effect) {
        return MOB_EFFECT.register(name, effect);
    }

    public static String getEffectKey(MobEffect effect) {
        return effect.getDescriptionId();
    }
    public static String getModEffectKey(RegistryObject<MobEffect> effect) {
        return getEffectKey(effect.get());
    }
    public static void register(IEventBus eventBus) {
        MOB_EFFECT.register(eventBus);
    }
}
