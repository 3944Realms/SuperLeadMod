package top.r3944realms.superleadrope.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Leash common config.
 */
public class LeashCommonConfig {
    /**
     * The constant BUILDER.
     */
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    /**
     * The constant SPEC.
     */
    public static final ForgeConfigSpec SPEC;
    /**
     * The constant COMMON.
     */
    public static final Common COMMON;

    static {
        BUILDER.comment("Leash Common Config");
        COMMON = new Common(BUILDER);
        SPEC = BUILDER.build();
    }

    /**
     * The type Common.
     */
    public static class Common {
        /**
         * The Enable slp mod command prefix.
         */
// Command
        public final ForgeConfigSpec.BooleanValue enableSLPModCommandPrefix;
        /**
         * The Slp mod command prefix.
         */
        public final ForgeConfigSpec.ConfigValue<String> SLPModCommandPrefix;

        /**
         * The Teleport whitelist.
         */
// Entity
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> teleportWhitelist;

        /**
         * The Max leash length.
         */
// Leash settings
        public final ForgeConfigSpec.DoubleValue maxLeashLength;
        public final ForgeConfigSpec.DoubleValue maxMovement;
        /**
         * The Elastic distance scale.
         */
        public final ForgeConfigSpec.DoubleValue elasticDistanceScale;
        /**
         * The Extreme snap factor.
         */
        public final ForgeConfigSpec.DoubleValue extremeSnapFactor;
        /**
         * The Spring dampening.
         */
        public final ForgeConfigSpec.DoubleValue springDampening;
        /**
         * The Axis specific elasticity.
         */
        public final ForgeConfigSpec.ConfigValue<List<? extends Double>> axisSpecificElasticity;
        /**
         * The Max leashes per entity.
         */
        public final ForgeConfigSpec.IntValue maxLeashesPerEntity;

        /**
         * The Enable true damping.
         */
// True damping
        public final ForgeConfigSpec.BooleanValue enableTrueDamping;
        /**
         * The Damping factor.
         */
        public final ForgeConfigSpec.DoubleValue dampingFactor;
        /**
         * The Max force.
         */
        public final ForgeConfigSpec.DoubleValue maxForce;
        /**
         * The Player spring factor.
         */
        public final ForgeConfigSpec.DoubleValue playerSpringFactor;
        /**
         * The Mob spring factor.
         */
        public final ForgeConfigSpec.DoubleValue mobSpringFactor;

        /**
         * The Default apply entity location offset.
         */
// Leash state offsets
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> defaultApplyEntityLocationOffset;
        /**
         * The Default holder location offset.
         */
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> defaultHolderLocationOffset;

        /**
         * The constant OFFSET_PATTERN.
         */
// 正则表达式模式
        static final Pattern OFFSET_PATTERN = Pattern.compile(
                "(?i)(?:vec3|vec3d|vector3|offset)\\s*\\(\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*\\)\\s*:\\s*\\[\\s*([^]]+?)\\s*]\\s*"
        );

        /**
         * Instantiates a new Common.
         *
         * @param builder the builder
         */
        public Common(ForgeConfigSpec.@NotNull Builder builder) {
            // ===== Command =====
            builder.push("Command");
            enableSLPModCommandPrefix = builder
                    .comment("Enable or disable the SLP mod command prefix")
                    .define("enableSLPModCommandPrefix", true);

            SLPModCommandPrefix = builder
                    .comment("The prefix of this mod's commands", " [ Default:'slp'] ")
                    .define("SLPModCommandPrefix", "slp");
            builder.pop();

            // ===== Entity =====
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
                            List.of("teleportWhitelist"),
                            List.of("#minecraft", "modernlife:bicycle", "modernlife:motorboat"),
                            o -> o instanceof String s && isValidEntityRefFormat(s)
                    );
            builder.pop();

            // ===== Leash Settings =====
            builder.push("LeashSettings");
            maxLeashLength = builder
                    .comment("Maximum leash distance (in blocks) for any entity")
                    .defineInRange("maxLeashLength", 6.0, LeashConfigManager.MAX_DISTANCE_MIN_VALUE, LeashConfigManager.MAX_DISTANCE_MAX_VALUE);
            maxMovement = builder
                    .comment("Defines the maximum acceleration in standard coordinate directions (X/Y/Z axes)")
                    .defineInRange("maxMovement", 100.0, 10.0, Double.MAX_VALUE);
            elasticDistanceScale = builder
                    .comment("Default elastic distance for the Super Lead rope")
                    .defineInRange("elasticDistanceScale", 1.0, LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE, LeashConfigManager.ELASTIC_DISTANCE_MAX_VALUE);

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

            // ===== True Damping =====
            builder.push("TrueDamping");
            enableTrueDamping = builder
                    .comment("Enable true velocity-based damping force (adds -c*v term)")
                    .define("enableTrueDamping", true);

            dampingFactor = builder
                    .comment("Damping factor (resistance against entity velocity)")
                    .defineInRange("dampingFactor", 0.1, 0.0, 2.0);

            maxForce = builder
                    .comment("Maximum leash pulling force (to prevent over-aggressive pulling)")
                    .defineInRange("maxForce", 1.0, 0.1, 10.0);

            playerSpringFactor = builder
                    .comment("Spring stiffness multiplier for players")
                    .defineInRange("playerSpringFactor", 0.3, 0.05, 1.0);

            mobSpringFactor = builder
                    .comment("Spring stiffness multiplier for mobs")
                    .defineInRange("mobSpringFactor", 0.5, 0.05, 2.0);
            builder.pop();



            // ===== Leash State Offsets =====
            builder.push("LeashStateSettings");
            defaultApplyEntityLocationOffset = builder
                    .comment(
                            "Default leash attachment point offsets for entities.",
                            "Reference point: the entity's eyeHeight (eye / head position).",
                            "Format: vec3(x,y,z) : [entity_list]",
                            "Optional names: vector3, vec3d, offset",
                            "Example: vec3(0,0.2,0) : [minecraft:bee, minecraft:horse]",
                            "Priority order: specific entity > tag > mod > *"
                    )
                    .defineListAllowEmpty(
                            List.of("defaultApplyEntityLocationOffset"),
                            List.of(
                                    "vec3(0,-0.2,0) : [*]",
                                    "vec3(0,-0.22,-0.2) : [minecraft:player]"
                            ),
                            o -> o instanceof String s && isValidOffsetRefFormat(s)
                    );

            defaultHolderLocationOffset = builder
                    .comment(
                            "Default leash holder attachment point offsets (where the leash attaches to the holder).",
                            "Reference point: the entity's eyeHeight (eye / head position).",
                            "Format: vec3(x,y,z) : [entity_list]",
                            "Optional names: vector3, vec3d, offset",
                            "Example: vec3(0,-0.5,0) : [minecraft:player]",
                            "Priority order: specific entity > tag > mod > *"
                    )
                    .defineListAllowEmpty(
                            List.of("defaultHolderLocationOffset"),
                            List.of(
                                    "vec3(0,-0.2,0) : [*]",
                                    "vec3(0,-0.6,0) : [minecraft:player]"
                            ),
                            o -> o instanceof String s && isValidOffsetRefFormat(s)
                    );
            builder.pop();
        }

        private static boolean isValidEntityRefFormat(String s) {
            if ("*".equals(s)) return true; // 支持任意实体通配
            if (s.startsWith("#")) {
                String body = s.substring(1);
                return body.matches("[a-z0-9_]+(:[a-z0-9_/]+)?");
            }
            return s.matches("[a-z0-9_]+:[a-z0-9_/]+");
        }

        private static boolean isValidOffsetRefFormat(String s) {
            Matcher matcher = OFFSET_PATTERN.matcher(s);
            if (!matcher.matches()) return false;
            try {
                Double.parseDouble(matcher.group(1));
                Double.parseDouble(matcher.group(2));
                Double.parseDouble(matcher.group(3));
                String[] entities = matcher.group(4).split(",");
                for (String entity : entities) {
                    if (!isValidEntityRefFormat(entity.trim())) return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
