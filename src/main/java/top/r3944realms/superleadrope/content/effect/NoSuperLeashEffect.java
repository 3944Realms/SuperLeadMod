package top.r3944realms.superleadrope.content.effect;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.core.register.SLPEffects;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.Optional;

public class NoSuperLeashEffect extends MobEffect {
    public NoSuperLeashEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        MobEffectInstance effect = pLivingEntity.getEffect(SLPEffects.NO_SUPER_LEASH_EFFECT.get());
        if(effect != null && effect.getDuration() != 0) {
            Optional<ILeashData> leashData = LeashDataInnerAPI.getLeashData(pLivingEntity);
            if (leashData.isPresent() && leashData.get().hasLeash()) {
                LeashDataInnerAPI.LeashOperations.detachAll(pLivingEntity);
                pLivingEntity.level().playSound(null,
                        pLivingEntity.getOnPos(),
                        SLPSoundEvents.LEAD_UNTIED.get(),
                        SoundSource.AMBIENT
                );
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration >= 1;
    }
}
