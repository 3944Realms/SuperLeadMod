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
        public final ForgeConfigSpec.ConfigValue<String> SLPModCommandPrefix;
        public final ForgeConfigSpec.BooleanValue EnableSLPModCommandPrefix;
        public final ForgeConfigSpec.DoubleValue maxLeashLength;
        public final ForgeConfigSpec.DoubleValue elasticDistance;
        public final ForgeConfigSpec.DoubleValue extremeSnapFactor;
        public final ForgeConfigSpec.DoubleValue springDampening;
        public final ForgeConfigSpec.ConfigValue<List<? extends Double>> axisSpecificElasticity;
        public final ForgeConfigSpec.IntValue maxLeashesPerEntity;
        public Common(ForgeConfigSpec.Builder builder) {
            BUILDER.push("Command");
            EnableSLPModCommandPrefix = builder
                    .comment("The prefix of this mod's commands")
                    .define("SLPModCommandPrefix", true);
            SLPModCommandPrefix = builder
                    .comment("The prefix of this mod's commands"," [ Default:'slp'] ")
                    .define("EnableSLPModCommandPrefix", "slp");
            BUILDER.pop();
            builder.push("Entity");
            teleportWhitelist = builder
                    .comment(
                            "Entity teleport whitelist.",
                            "Accepted formats:",
                            " - #modid                 : allow teleporting to all entities from a specific mod",
                            " - modid:entity_name      : allow teleporting to a specific entity",
                            " - #modid:tag_name        : allow teleporting to all entities under a given entity type tag"
                    )
                    .defineListAllowEmpty(
                            List.of("allowedTeleportEntities"),
                            List.of("#minecraft", "modernlife:bicycle", "modernlife:motorboat"),
                            o -> o instanceof String s && isValidFormat(s)
                    );
            builder.pop();
            builder.push("LeashSettings");
            maxLeashLength = builder
                    .comment("Maximum leash distance (in blocks) for any entity")
                    .defineInRange("maxLeashLength", 12.0, 6.0, 256.0);
            elasticDistance = builder
                    .comment("Default elastic distance for the Super Lead rope")
                    .defineInRange("elasticDistance", 6.0, 6.0, 128.0);

            extremeSnapFactor = builder
                    .comment("Leash break factor = maxDistance * factor")
                    .defineInRange("extremeSnapFactor", 2.0, 1.0, 4.0);

            springDampening = builder
                    .comment("Spring dampening coefficient")
                    .defineInRange("springDampening", 0.7, 0.0, 1.0);

            axisSpecificElasticity = builder
                    .comment("Axis-specific elasticity coefficients for X,Y,Z axes")
                    .defineList("axisSpecificElasticity", List.of(0.8, 0.2, 0.8), o -> o instanceof Double);

            maxLeashesPerEntity = builder
                    .comment("Maximum number of leashes per entity")
                    .defineInRange("maxLeashesPerEntity", 6, 1, 24);

            builder.pop();
        }

        private static boolean isValidFormat(String s) {
            if (s.startsWith("#")) {
                String body = s.substring(1);
                // 支持 #modid （整个模组）
                if (body.matches("[a-z0-9_]+")) {
                    return true;
                }
                // 支持 #modid:tag_name （标签）
                return body.matches("[a-z0-9_]+:[a-z0-9_/]+");
            }
            // 普通实体 ID
            return s.matches("[a-z0-9_]+:[a-z0-9_/]+");
        }
    }
}
