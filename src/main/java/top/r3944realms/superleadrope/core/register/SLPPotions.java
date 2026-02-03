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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;

import java.util.function.Supplier;

/**
 * The type Slp potions.
 */
public class SLPPotions {
    /**
     * The Potions.
     */
    public static DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, SuperLeadRope.MOD_ID);
    /**
     * The constant NO_SUPER_LEASH.
     */
    public static final RegistryObject<Potion> NO_SUPER_LEASH = register("no_super_leash",
            () -> new Potion("no_super_leash", new MobEffectInstance(SLPEffects.NO_SUPER_LEASH_EFFECT.get(), 1200, 0))
    );
    /**
     * The constant LONG_NO_SUPER_LEASH.
     */
    public static final RegistryObject<Potion> LONG_NO_SUPER_LEASH = register("long_no_super_leash",
            () -> new Potion("no_super_leash", new MobEffectInstance(SLPEffects.NO_SUPER_LEASH_EFFECT.get(), 3600, 0))
    );

    /**
     * Register registry object.
     *
     * @param <T>      the type parameter
     * @param Name     the name
     * @param supplier the supplier
     * @return the registry object
     */
    public static <T extends Potion>RegistryObject<Potion> register(String Name, Supplier<T> supplier) {
        return POTIONS.register(Name, supplier);
    }

    /**
     * Gets potion name key.
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

    /**
     * Gets tipped arrow name key.
     *
     * @param Name the name
     * @return the tipped arrow name key
     */
    public static String getTippedArrowNameKey(String Name) {
        return "item.minecraft.tipped_arrow.effect." + Name;
    }

    /**
     * Register.
     *
     * @param eventBus the event bus
     */
    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
