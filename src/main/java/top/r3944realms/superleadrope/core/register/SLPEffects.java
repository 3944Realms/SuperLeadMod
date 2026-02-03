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

/**
 * The type Slp effects.
 */
public class SLPEffects {
    /**
     * The Mob effect.
     */
    public static DeferredRegister<MobEffect> MOB_EFFECT = DeferredRegister.create(Registries.MOB_EFFECT, SuperLeadRope.MOD_ID);
    /**
     * The constant NO_SUPER_LEASH_EFFECT.
     */
    public static RegistryObject<MobEffect> NO_SUPER_LEASH_EFFECT = register(
            "no_super_leash",
            () -> new NoSuperLeashEffect(MobEffectCategory.NEUTRAL, 12063764)
    );

    /**
     * Register registry object.
     *
     * @param <T>    the type parameter
     * @param name   the name
     * @param effect the effect
     * @return the registry object
     */
    public static <T extends MobEffect> RegistryObject<MobEffect> register(String name, Supplier<T> effect) {
        return MOB_EFFECT.register(name, effect);
    }

    /**
     * Gets effect key.
     *
     * @param effect the effect
     * @return the effect key
     */
    public static String getEffectKey(MobEffect effect) {
        return effect.getDescriptionId();
    }

    /**
     * Gets mod effect key.
     *
     * @param effect the effect
     * @return the mod effect key
     */
    public static String getModEffectKey(RegistryObject<MobEffect> effect) {
        return getEffectKey(effect.get());
    }

    /**
     * Register.
     *
     * @param eventBus the event bus
     */
    public static void register(IEventBus eventBus) {
        MOB_EFFECT.register(eventBus);
    }
}
