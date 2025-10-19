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

package top.r3944realms.superleadrope.datagen.data;
import top.r3944realms.superleadrope.SuperLeadRope;

import javax.annotation.Nullable;

/**
 * The enum Slp advancement key.
 */
public enum SLPAdvancementKey {
    ;
    private final String Name;
    @Nullable
    private final SLPAdvancementKey Parent;
    SLPAdvancementKey(String name, @Nullable SLPAdvancementKey parent) {
        this.Name = name;
        this.Parent = parent;
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public @Nullable SLPAdvancementKey getParent() {
        return Parent;
    }

    /**
     * Gets name key.
     *
     * @return the name key
     */
    public String getNameKey() {
        return "advancement." + SuperLeadRope.MOD_ID + "." + Name;
    }

    /**
     * Gets desc key.
     *
     * @return the desc key
     */
    public String getDescKey() {
        return this.getNameKey() + ".desc";
    }

    /**
     * Gets name with name space.
     *
     * @return the name with name space
     */
    public String getNameWithNameSpace() {
        return SuperLeadRope.MOD_ID + ":" + this.Name;
    }
}
