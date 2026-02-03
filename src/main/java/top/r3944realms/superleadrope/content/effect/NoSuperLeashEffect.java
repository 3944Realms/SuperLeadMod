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

/**
 * The type No super leash effect.
 */
public class NoSuperLeashEffect extends MobEffect {
    /**
     * Instantiates a new No super leash effect.
     *
     * @param category the category
     * @param color    the color
     */
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
