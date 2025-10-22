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

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import top.r3944realms.superleadrope.SuperLeadRope;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import static top.r3944realms.superleadrope.config.LeashCommonConfig.Common.OFFSET_PATTERN;

/**
 * The type Leash config manager.
 */
@SuppressWarnings("unused")
public class LeashConfigManager {
    // ========== 最值检测 ==========
    /**
     * The constant MAX_DISTANCE_CHECK.
     */
    public static final Double MAX_DISTANCE_MAX_VALUE = 256.0;
    /**
     * The constant MAX_DISTANCE_MIN_VALUE.
     */
    public static final Double MAX_DISTANCE_MIN_VALUE = 1.0;
    /**
     * The constant MAX_DISTANCE_CHECK.
     */
    public static final Predicate<Double> MAX_DISTANCE_CHECK = distance -> distance == null || (distance >= MAX_DISTANCE_MIN_VALUE && distance <= MAX_DISTANCE_MAX_VALUE);
    /**
     * The constant ELASTIC_DISTANCE_CHECK.
     */
    public static final Double ELASTIC_DISTANCE_MAX_VALUE = 4.0;
    /**
     * The constant ELASTIC_DISTANCE_MIN_VALUE.
     */
    public static final Double ELASTIC_DISTANCE_MIN_VALUE = 0.2;
    /**
     * The constant ELASTIC_DISTANCE_CHECK.
     */
    public static final Predicate<Double> ELASTIC_DISTANCE_CHECK = distance -> distance == null || (distance >= ELASTIC_DISTANCE_MIN_VALUE && distance <= ELASTIC_DISTANCE_MAX_VALUE);
    // ========== 偏移映射 ==========
    private final Map<String, double[]> entityHolderMap = new ConcurrentHashMap<>();
    private final Map<String, double[]> tagHolderMap = new ConcurrentHashMap<>();
    private final Map<String, double[]> modHolderMap = new ConcurrentHashMap<>();

    private final Map<String, double[]> entityLeashMap = new ConcurrentHashMap<>();
    private final Map<String, double[]> tagLeashMap = new ConcurrentHashMap<>();
    private final Map<String, double[]> modLeashMap = new ConcurrentHashMap<>();

    // ========== 缓存配置 ==========
    private volatile List<String> teleportWhitelistCache = Collections.emptyList();
    private volatile String commandPrefixCache = "slp";
    private volatile boolean commandPrefixEnabledCache = true;

    private volatile boolean enableTrueDamping = true;
    private volatile double maxForce = 1.0;
    private volatile double playerSpringFactor = 0.3;
    private volatile double mobSpringFactor = 0.5;

    private volatile double maxLeashLength = 12.0;
    private volatile double elasticDistanceScale = 1.0;
    private volatile double extremeSnapFactor = 2.0;
    private volatile double springDampening = 0.7;
    private volatile List<Double> axisElasticity = List.of(0.8, 0.2, 0.8);
    private volatile int maxLeashesPerEntity = 6;

    /**
     * Instantiates a new Leash config manager.
     */
    public LeashConfigManager() {
        reloadAll();
    }

    // ================== 偏移解析 ==================
    private @NotNull @Unmodifiable Map<String, Map<String, double[]>> parseOffsetList(@NotNull List<? extends String> offsetConfigs) {
        Map<String, double[]> entityMap = new HashMap<>();
        Map<String, double[]> tagMap = new HashMap<>();
        Map<String, double[]> modMap = new HashMap<>();

        for (String config : offsetConfigs) {
            Matcher matcher = OFFSET_PATTERN.matcher(config);
            if (!matcher.matches()) continue;

            try {
                double x = Double.parseDouble(matcher.group(1).trim());
                double y = Double.parseDouble(matcher.group(2).trim());
                double z = Double.parseDouble(matcher.group(3).trim());
                double[] offset = new double[]{x, y, z};

                String[] entities = matcher.group(4).split(",");
                for (String e : entities) {
                    String trimmed = e.trim();
                    if (trimmed.equals("*")) modMap.put("*", offset);
                    else if (trimmed.startsWith("#")) {
                        String body = trimmed.substring(1).trim();
                        if (body.contains(":")) tagMap.put(body, offset);
                        else modMap.put(body, offset);
                    } else entityMap.put(trimmed, offset);
                }
            } catch (NumberFormatException ex) {
                SuperLeadRope.logger.error("Invalid offset config: {}", config);
            }
        }

        return Map.of(
                "entity", entityMap,
                "tag", tagMap,
                "mod", modMap
        );
    }

    /**
     * Parse offset config.
     */
    public void parseOffsetConfig() {
        Map<String, Map<String, double[]>> holder = parseOffsetList(LeashCommonConfig.COMMON.defaultHolderLocationOffset.get());
        entityHolderMap.clear(); entityHolderMap.putAll(holder.get("entity"));
        tagHolderMap.clear(); tagHolderMap.putAll(holder.get("tag"));
        modHolderMap.clear(); modHolderMap.putAll(holder.get("mod"));

        Map<String, Map<String, double[]>> leash = parseOffsetList(LeashCommonConfig.COMMON.defaultApplyEntityLocationOffset.get());
        entityLeashMap.clear(); entityLeashMap.putAll(leash.get("entity"));
        tagLeashMap.clear(); tagLeashMap.putAll(leash.get("tag"));
        modLeashMap.clear(); modLeashMap.putAll(leash.get("mod"));
    }

    // ================== 获取偏移 ==================
    private double[] getOffset(String entityId, String modId, List<String> tags,
                               @NotNull Map<String,double[]> entityMap,
                               Map<String,double[]> tagMap,
                               Map<String,double[]> modMap) {

        if (entityMap.containsKey(entityId)) return entityMap.get(entityId);
        for (String tag : tags) if (tagMap.containsKey(tag)) return tagMap.get(tag);
        if (modMap.containsKey(modId)) return modMap.get(modId);
        return modMap.getOrDefault("*", null);
    }

    /**
     * Gets default entity offset.
     *
     * @param type the type
     * @return the default entity offset
     */
    @SuppressWarnings({"DuplicatedCode", "deprecation"})
    public Vec3 getDefaultEntityOffset(@NotNull EntityType<?> type) {
        String entityId = type.builtInRegistryHolder().key().location().toString();
        String modId = entityId.split(":")[0];
        List<String> tags = new ArrayList<>();
        for (var t : type.builtInRegistryHolder().tags().toList()) tags.add(t.location().toString());

        double[] offset = getOffset(entityId, modId, tags, entityLeashMap, tagLeashMap, modLeashMap);
        return offset != null ? new Vec3(offset[0], offset[1], offset[2]) : Vec3.ZERO;
    }

    /**
     * Gets default holder offset.
     *
     * @param type the type
     * @return the default holder offset
     */
    @SuppressWarnings({"DuplicatedCode", "deprecation"})
    public Vec3 getDefaultHolderOffset(@NotNull EntityType<?> type) {
        String entityId = type.builtInRegistryHolder().key().location().toString();
        String modId = entityId.split(":")[0];
        List<String> tags = new ArrayList<>();
        for (var t : type.builtInRegistryHolder().tags().toList()) tags.add(t.location().toString());

        double[] offset = getOffset(entityId, modId, tags, entityHolderMap, tagHolderMap, modHolderMap);
        return offset != null ? new Vec3(offset[0], offset[1], offset[2]) : Vec3.ZERO;
    }

    /**
     * Gets default entity offset.
     *
     * @param entity the entity
     * @return the default entity offset
     */
    public Vec3 getDefaultEntityOffset(@NotNull Entity entity) { return getDefaultEntityOffset(entity.getType()); }

    /**
     * Gets default holder offset.
     *
     * @param entity the entity
     * @return the default holder offset
     */
    public Vec3 getDefaultHolderOffset(@NotNull Entity entity) { return getDefaultHolderOffset(entity.getType()); }

    /**
     * Gets teleport whitelist.
     *
     * @return the teleport whitelist
     */
// ================== 白名单 ==================
    public List<String> getTeleportWhitelist() { return Collections.unmodifiableList(teleportWhitelistCache); }

    /**
     * Is entity teleport allowed boolean.
     *
     * @param type the type
     * @return the boolean
     */
    @SuppressWarnings({"DuplicatedCode", "deprecation"})
    public boolean isEntityTeleportAllowed(@NotNull EntityType<?> type) {
        String entityId = type.builtInRegistryHolder().key().location().toString();
        String modId = entityId.split(":")[0];

        for (String entry : teleportWhitelistCache) {
            if (entry.startsWith("#")) {
                String body = entry.substring(1);
                if (!body.contains(":") && body.equals(modId)) return true;

                if (body.contains(":")) {
                    ResourceLocation tagId = new ResourceLocation(body);
                    TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
                    if (type.builtInRegistryHolder().is(tag)) return true;
                }
            } else if (entry.equals(entityId)) return true;
        }
        return false;
    }

    /**
     * Is entity teleport allowed boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public boolean isEntityTeleportAllowed(@NotNull Entity entity) { return isEntityTeleportAllowed(entity.getType()); }

    /**
     * Gets command prefix.
     *
     * @return the command prefix
     */
// ================== 命令 ==================
    public String getCommandPrefix() { return commandPrefixCache; }

    /**
     * Is command prefix enabled boolean.
     *
     * @return the boolean
     */
    public boolean isCommandPrefixEnabled() { return commandPrefixEnabledCache; }

    /**
     * Gets full command.
     *
     * @param subCommand the sub command
     * @return the full command
     */
    public String getFullCommand(String subCommand) {
        return isCommandPrefixEnabled() ? getCommandPrefix() + " " + subCommand : subCommand;
    }

    /**
     * Is enable true damping boolean.
     *
     * @return the boolean
     */
// ================== 拴绳物理参数 ==================
    public boolean isEnableTrueDamping() { return enableTrueDamping; }

    /**
     * Gets max force.
     *
     * @return the max force
     */
    public double getMaxForce() { return maxForce; }

    /**
     * Gets player spring factor.
     *
     * @return the player spring factor
     */
    public double getPlayerSpringFactor() { return playerSpringFactor; }

    /**
     * Gets mob spring factor.
     *
     * @return the mob spring factor
     */
    public double getMobSpringFactor() { return mobSpringFactor; }

    /**
     * Gets max leash length.
     *
     * @return the max leash length
     */
    public double getMaxLeashLength() { return maxLeashLength; }

    /**
     * Gets elastic distance scale.
     *
     * @return the elastic distance scale
     */
    public double getElasticDistanceScale() { return elasticDistanceScale; }

    /**
     * Gets extreme snap factor.
     *
     * @return the extreme snap factor
     */
    public double getExtremeSnapFactor() { return extremeSnapFactor; }

    /**
     * Gets spring dampening.
     *
     * @return the spring dampening
     */
    public double getSpringDampening() { return springDampening; }

    /**
     * Gets axis elasticity.
     *
     * @return the axis elasticity
     */
    public List<Double> getAxisElasticity() { return Collections.unmodifiableList(axisElasticity); }

    /**
     * Gets x elasticity.
     *
     * @return the x elasticity
     */
    public double getXElasticity() { return !axisElasticity.isEmpty() ? axisElasticity.get(0) : 0.8; }

    /**
     * Gets y elasticity.
     *
     * @return the y elasticity
     */
    public double getYElasticity() { return axisElasticity.size() > 1 ? axisElasticity.get(1) : 0.2; }

    /**
     * Gets z elasticity.
     *
     * @return the z elasticity
     */
    public double getZElasticity() { return axisElasticity.size() > 2 ? axisElasticity.get(2) : 0.8; }

    /**
     * Gets max leashes per entity.
     *
     * @return the max leashes per entity
     */
    public int getMaxLeashesPerEntity() { return maxLeashesPerEntity; }

    /**
     * Can entity accept more leashes boolean.
     *
     * @param entity       the entity
     * @param currentCount the current count
     * @return the boolean
     */
    public boolean canEntityAcceptMoreLeashes(Entity entity, int currentCount) {
        return currentCount < maxLeashesPerEntity;
    }

    /**
     * Reload all.
     */
// ================== 管理 ==================
    public void reloadAll() {
        parseOffsetConfig();

        teleportWhitelistCache = new ArrayList<>(LeashCommonConfig.COMMON.teleportWhitelist.get());
        commandPrefixCache = LeashCommonConfig.COMMON.SLPModCommandPrefix.get();
        commandPrefixEnabledCache = LeashCommonConfig.COMMON.enableSLPModCommandPrefix.get();

        maxLeashLength = LeashCommonConfig.COMMON.maxLeashLength.get();
        elasticDistanceScale = LeashCommonConfig.COMMON.elasticDistanceScale.get();
        extremeSnapFactor = LeashCommonConfig.COMMON.extremeSnapFactor.get();
        springDampening = LeashCommonConfig.COMMON.springDampening.get();
        axisElasticity = new ArrayList<>(LeashCommonConfig.COMMON.axisSpecificElasticity.get());
        maxLeashesPerEntity = LeashCommonConfig.COMMON.maxLeashesPerEntity.get();

        enableTrueDamping = LeashCommonConfig.COMMON.enableTrueDamping.get();
        maxForce = LeashCommonConfig.COMMON.maxForce.get();
        playerSpringFactor = LeashCommonConfig.COMMON.playerSpringFactor.get();
        mobSpringFactor = LeashCommonConfig.COMMON.mobSpringFactor.get();

        SuperLeadRope.logger.debug("Configs reloaded: {}", getStats());
    }

    /**
     * Clear.
     */
    public void clear() {
        entityHolderMap.clear(); tagHolderMap.clear(); modHolderMap.clear();
        entityLeashMap.clear(); tagLeashMap.clear(); modLeashMap.clear();
        teleportWhitelistCache = Collections.emptyList();
    }

    /**
     * Loading.
     *
     * @param manager the manager
     */
    public static void loading(@NotNull LeashConfigManager manager) {
        manager.reloadAll();
    }

    /**
     * Reloading.
     *
     * @param manager the manager
     */
    public static void reloading(@NotNull LeashConfigManager manager) {
        manager.reloadAll();
    }

    /**
     * Unloading.
     *
     * @param manager the manager
     */
    public static void unloading(LeashConfigManager manager) {
        if(manager != null) manager.clear();
    }

    /**
     * Gets stats.
     *
     * @return the stats
     */
    public String getStats() {
        return String.format(
                "Holder: Entities: %d, Tags: %d, Mods: %d\nLeash: Entities: %d, Tags: %d, Mods: %d, TeleportWhitelist: %d",
                entityHolderMap.size(), tagHolderMap.size(), modHolderMap.size(),
                entityLeashMap.size(), tagLeashMap.size(), modLeashMap.size(),
                teleportWhitelistCache.size()
        );
    }
}