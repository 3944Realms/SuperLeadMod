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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> defaultApplyEntityLocationOffset, defaultHolderLocationOffset;
        // 正则表达式模式
        static final Pattern OFFSET_PATTERN = Pattern.compile(
                "(?i)(?:vec3|vec3d|vector3|offset)\\s*\\(\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*\\)\\s*:\\s*\\[\\s*([^]]+?)\\s*]\\s*"        );

        public Common(ForgeConfigSpec.Builder builder) {
            BUILDER.push("Command");
            EnableSLPModCommandPrefix = builder
                    .comment("The prefix of this mod's commands")
                    .define("SLPModCommandPrefix", true);
            SLPModCommandPrefix = builder
                    .comment("The prefix of this mod's commands", " [ Default:'slp'] ")
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
                            o -> o instanceof String s && isValidEntityRefFormat(s)
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
            builder.push("LeashStateSettings");
            defaultApplyEntityLocationOffset = builder
                    .comment(
                            "Default leash attachment point offsets for entities.",
                            "Reference point: the entity's eyeHeight (eye / head position).",
                            "Format: vec3(x,y,z) : [entity_list]",
                            "Optional names: vector3, vec3d, offset",
                            "Entity list may contain:",
                            " - modid:entity_id       : specific entity (e.g. minecraft:bee)",
                            " - #modid:tag_name       : entity type tag (e.g. #minecraft:boats)",
                            " - #modid                : all entities from a mod (e.g. #minecraft)",
                            " - *                     : all entities",
                            "Multiple entries can be separated by commas",
                            "Example: vec3(0,0.2,0) : [minecraft:bee, minecraft:horse]",
                            "Priority order: specific entity > tag > mod > *"
                    )
                    .defineListAllowEmpty(
                            List.of("defaultApplyEntityLocationOffset"),
                            List.of(
                                    "vec3(0,-0.5,0) : [*]", "vec3(0,-0.42,0) : [minecraft:player]"
                            ),
                            o -> o instanceof String s && isValidOffsetRefFormat(s)
                    );

            defaultHolderLocationOffset = builder
                    .comment(
                            "Default leash holder attachment point offsets (where the leash attaches to the holder).",
                            "Reference point: the entity's eyeHeight (eye / head position).",
                            "Format: vec3(x,y,z) : [entity_list]",
                            "Optional names: vector3, vec3d, offset",
                            "Entity list may contain:",
                            " - modid:entity_id       : specific entity (e.g. minecraft:player)",
                            " - #modid:tag_name       : entity type tag (e.g. #minecraft:players)",
                            " - #modid                : all entities from a mod (e.g. #minecraft)",
                            " - *                     : all entities",
                            "Multiple entries can be separated by commas",
                            "Example: vec3(0,1.5,0) : [minecraft:player]",
                            "Priority order: specific entity > tag > mod > *"
                    )
                    .defineListAllowEmpty(
                            List.of("defaultHolderLocationOffset"),
                            List.of(
                                    "vec3(0,-0.5,0) : [*]"
                            ),
                            o -> o instanceof String s && isValidOffsetRefFormat(s)
                    );
            BUILDER.pop();
        }

        private static boolean isValidEntityRefFormat(String s) {
            if ("*".equals(s)) {
                return true; // 支持任意实体通配
            }
            if (s.startsWith("#")) {
                String body = s.substring(1);
                // 支持 #modid （整个模组）或 #modid:tag_name （标签）
                return body.matches("[a-z0-9_]+(:[a-z0-9_/]+)?");
            }
            // 普通实体 ID: modid:entity_id
            return s.matches("[a-z0-9_]+:[a-z0-9_/]+");
        }

        private static boolean isValidOffsetRefFormat(String s) {
            // 匹配格式: vec3(x,y,z) : [entity_list]
            Matcher matcher = Common.OFFSET_PATTERN.matcher(s);
            if (!matcher.matches()) {
                return false;
            }

            // 检查坐标值是否有效
            try {
                Double.parseDouble(matcher.group(1));
                Double.parseDouble(matcher.group(2));
                Double.parseDouble(matcher.group(3));

                // 检查实体列表格式
                String entityList = matcher.group(4);
                String[] entities = entityList.split(",");
                for (String entity : entities) {
                    if (!isValidEntityRefFormat(entity.trim())) {
                        return false;
                    }
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
