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

package top.r3944realms.superleadrope.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class LeashCommonConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static final Common COMMON;
    static {
        BUILDER.comment("Leash Common Config");
        COMMON = new Common(BUILDER);
        SPEC = BUILDER.build();
    }
    public static class Common {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> teleportWhitelist;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("leash");

            teleportWhitelist = builder
                    .comment("Entity teleport whitelist.",
                            "Use `#modid` to allow teleporting to all entities from a mod.",
                            "Use `modid:entity_name` to allow teleporting to a specific entity.")
                    .defineListAllowEmpty(
                            List.of("allowedTeleportEntities"),
                            List.of("#minecraft", "modernlife:bicycle", "modernlife:motorboat"),
                            o -> o instanceof String s && isValidFormat(s)
                    );

            builder.pop();
        }

        private static boolean isValidFormat(String s) {
            if (s.startsWith("#")) {
                return s.length() > 1 && s.substring(1).matches("[a-z0-9_]+");
            }
            return s.matches("[a-z0-9_]+:[a-z0-9_/]+");
        }
    }
}
