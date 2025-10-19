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

package top.r3944realms.superleadrope.content;

import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

/**
 * The type Slp damage types.
 */
public class SLPDamageTypes {
    /**
     * The constant ETERNAL_POTATO_NOT_OWNER.
     */
// 非绑定主人使用惩罚
    public static final DamageType ETERNAL_POTATO_NOT_OWNER =
            new DamageType(
                    "eternal_potato_not_owner",
                    DamageScaling.ALWAYS,
                    0f,
                    DamageEffects.HURT,
                    DeathMessageType.DEFAULT
            );
    /**
     * The constant ETERNAL_POTATO_NOT_COMPLETE.
     */
    public static final DamageType ETERNAL_POTATO_NOT_COMPLETE =
            new DamageType(
                    "eternal_potato_not_complete",
                    DamageScaling.ALWAYS,
                    0f,
                    DamageEffects.HURT,
                    DeathMessageType.DEFAULT
            );
}
