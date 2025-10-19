/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
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

package top.r3944realms.superleadrope.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;

/**
 * The type Slp sound definitions provider.
 */
public class SLPSoundDefinitionsProvider extends SoundDefinitionsProvider {
    /**
     * Instantiates a new Slp sound definitions provider.
     *
     * @param output the output
     * @param helper the helper
     */
    public SLPSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, SuperLeadRope.MOD_ID, helper);
    }

    /**
     * Gets sound definition.
     *
     * @param subTitle the sub title
     * @param sounds   the sounds
     * @return the sound definition
     */
    public SoundDefinition getSoundDefinition(String subTitle, SoundDefinition.Sound... sounds) {
        return SoundDefinition.definition().subtitle(subTitle).with(sounds);
    }

    @Override
    public void registerSounds() {
        add(
                SLPSoundEvents.LEAD_UNTIED,
                getSoundDefinition(
                        SLPSoundEvents.getSubTitleTranslateKey("lead_untied"),
                        sound(SLPSoundEvents.RL_LEAD_UNTIED.withSuffix("1"), SoundDefinition.SoundType.SOUND),
                        sound(SLPSoundEvents.RL_LEAD_UNTIED.withSuffix("2"), SoundDefinition.SoundType.SOUND),
                        sound(SLPSoundEvents.RL_LEAD_UNTIED.withSuffix("3"), SoundDefinition.SoundType.SOUND)
                )
        );
        add(
                SLPSoundEvents.LEAD_TIED,
                getSoundDefinition(
                        SLPSoundEvents.getSubTitleTranslateKey("lead_tied"),
                        sound(SLPSoundEvents.RL_LEAD_TIED.withSuffix("1"), SoundDefinition.SoundType.SOUND),
                        sound(SLPSoundEvents.RL_LEAD_TIED.withSuffix("2"), SoundDefinition.SoundType.SOUND),
                        sound(SLPSoundEvents.RL_LEAD_TIED.withSuffix("3"), SoundDefinition.SoundType.SOUND)

                        )
        );
        add(
                SLPSoundEvents.LEAD_BREAK,
                getSoundDefinition(
                        SLPSoundEvents.getSubTitleTranslateKey("lead_break"),
                        sound(SLPSoundEvents.RL_LEAD_BREAK, SoundDefinition.SoundType.SOUND)
                )
        );
    }
}
