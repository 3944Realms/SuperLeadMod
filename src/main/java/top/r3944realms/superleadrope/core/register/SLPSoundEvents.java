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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;

/**
 * The type Slp sound events.
 */
public class SLPSoundEvents {
    /**
     * The constant SOUND_EVENTS.
     */
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SuperLeadRope.MOD_ID);
    /**
     * The constant RL_LEAD_UNTIED.
     */
    public static final ResourceLocation RL_LEAD_UNTIED = new ResourceLocation(SuperLeadRope.MOD_ID,"item/superlead/lead_untied");
    /**
     * The constant RL_LEAD_TIED.
     */
    public static final ResourceLocation RL_LEAD_TIED = new ResourceLocation(SuperLeadRope.MOD_ID,"item/superlead/lead_tied");
    /**
     * The constant RL_LEAD_BREAK.
     */
    public static final ResourceLocation RL_LEAD_BREAK = new ResourceLocation(SuperLeadRope.MOD_ID,"item/superlead/lead_break");
    /**
     * The constant LEAD_UNTIED.
     */
    public static final RegistryObject<SoundEvent> LEAD_UNTIED = registerSound("lead_untied");
    /**
     * The constant LEAD_TIED.
     */
    public static final RegistryObject<SoundEvent> LEAD_TIED = registerSound("lead_tied");
    /**
     * The constant LEAD_BREAK.
     */
    public static final RegistryObject<SoundEvent> LEAD_BREAK = registerSound("lead_break");
    private static RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation location = new ResourceLocation(SuperLeadRope.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }

    /**
     * Register.
     *
     * @param bus the bus
     */
    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    /**
     * Gets sub title translate key.
     *
     * @param name the name
     * @return the sub title translate key
     */
    public static String getSubTitleTranslateKey(String name) {
        return "sound." + SuperLeadRope.MOD_ID + ".subtitle." + name;
    }
}
