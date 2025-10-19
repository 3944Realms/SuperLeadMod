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
import net.minecraftforge.common.data.LanguageProvider;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.datagen.data.SLPLangKeyValue;
import top.r3944realms.superleadrope.util.lang.LanguageEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.r3944realms.superleadrope.datagen.data.SLPLangKeyValue.getLan;


/**
 * The type Slp language provider.
 */
public class SLPLanguageProvider extends LanguageProvider {
    private final LanguageEnum Language;
    private final Map<String, String> LanKeyMap;
    private static final List<String> objects = new ArrayList<>();

    /**
     * Instantiates a new Slp language provider.
     *
     * @param output the output
     * @param Lan    the lan
     */
    public SLPLanguageProvider(PackOutput output, LanguageEnum Lan) {
        super(output, SuperLeadRope.MOD_ID, Lan.local);
        this.Language = Lan;
        LanKeyMap = new HashMap<>();
        init();
    }
    private void init() {
        for (SLPLangKeyValue key : SLPLangKeyValue.values()) {
            addLang(key.getKey(), getLan(Language, key));
        }
    }
    private void addLang(String Key, String value) {
        if(!objects.contains(Key)) objects.add(Key);
        LanKeyMap.put(Key, value);
    }

    @Override
    protected void addTranslations() {
        objects.forEach(key -> add(key,LanKeyMap.get(key)));
    }
}
