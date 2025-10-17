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
import top.r3944realms.superleadrope.SuperLeadRope;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import static top.r3944realms.superleadrope.config.LeashCommonConfig.Common.OFFSET_PATTERN;

public class LeashConfigManager {
    // ========== 最值检测 ==========
    public static final Predicate<Double> MAX_DISTANCE_CHECK = distance -> distance == null || (distance >= 6.0 && distance <= 256.0);
    public static final Predicate<Double> ELASTIC_DISTANCE_CHECK = distance -> distance == null || (distance >= 0.2 && distance <= 4.0);
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

    public LeashConfigManager() {
        reloadAll();
    }

    // ================== 偏移解析 ==================
    private Map<String, Map<String, double[]>> parseOffsetList(List<? extends String> offsetConfigs) {
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
                               Map<String,double[]> entityMap,
                               Map<String,double[]> tagMap,
                               Map<String,double[]> modMap) {

        if (entityMap.containsKey(entityId)) return entityMap.get(entityId);
        for (String tag : tags) if (tagMap.containsKey(tag)) return tagMap.get(tag);
        if (modMap.containsKey(modId)) return modMap.get(modId);
        return modMap.getOrDefault("*", null);
    }

    @SuppressWarnings({"DuplicatedCode", "deprecation"})
    public Vec3 getDefaultEntityOffset(EntityType<?> type) {
        String entityId = type.builtInRegistryHolder().key().location().toString();
        String modId = entityId.split(":")[0];
        List<String> tags = new ArrayList<>();
        for (var t : type.builtInRegistryHolder().tags().toList()) tags.add(t.location().toString());

        double[] offset = getOffset(entityId, modId, tags, entityLeashMap, tagLeashMap, modLeashMap);
        return offset != null ? new Vec3(offset[0], offset[1], offset[2]) : Vec3.ZERO;
    }

    @SuppressWarnings({"DuplicatedCode", "deprecation"})
    public Vec3 getDefaultHolderOffset(EntityType<?> type) {
        String entityId = type.builtInRegistryHolder().key().location().toString();
        String modId = entityId.split(":")[0];
        List<String> tags = new ArrayList<>();
        for (var t : type.builtInRegistryHolder().tags().toList()) tags.add(t.location().toString());

        double[] offset = getOffset(entityId, modId, tags, entityHolderMap, tagHolderMap, modHolderMap);
        return offset != null ? new Vec3(offset[0], offset[1], offset[2]) : Vec3.ZERO;
    }

    public Vec3 getDefaultEntityOffset(Entity entity) { return getDefaultEntityOffset(entity.getType()); }
    public Vec3 getDefaultHolderOffset(Entity entity) { return getDefaultHolderOffset(entity.getType()); }

    // ================== 白名单 ==================
    public List<String> getTeleportWhitelist() { return Collections.unmodifiableList(teleportWhitelistCache); }
    @SuppressWarnings({"DuplicatedCode", "deprecation"})
    public boolean isEntityTeleportAllowed(EntityType<?> type) {
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

    public boolean isEntityTeleportAllowed(Entity entity) { return isEntityTeleportAllowed(entity.getType()); }

    // ================== 命令 ==================
    public String getCommandPrefix() { return commandPrefixCache; }
    public boolean isCommandPrefixEnabled() { return commandPrefixEnabledCache; }
    public String getFullCommand(String subCommand) {
        return isCommandPrefixEnabled() ? getCommandPrefix() + " " + subCommand : subCommand;
    }

    // ================== 拴绳物理参数 ==================
    public boolean isEnableTrueDamping() { return enableTrueDamping; }
    public double getMaxForce() { return maxForce; }
    public double getPlayerSpringFactor() { return playerSpringFactor; }
    public double getMobSpringFactor() { return mobSpringFactor; }

    public double getMaxLeashLength() { return maxLeashLength; }
    public double getElasticDistanceScale() { return elasticDistanceScale; }
    public double getExtremeSnapFactor() { return extremeSnapFactor; }
    public double getSpringDampening() { return springDampening; }
    public List<Double> getAxisElasticity() { return Collections.unmodifiableList(axisElasticity); }
    public double getXElasticity() { return !axisElasticity.isEmpty() ? axisElasticity.get(0) : 0.8; }
    public double getYElasticity() { return axisElasticity.size() > 1 ? axisElasticity.get(1) : 0.2; }
    public double getZElasticity() { return axisElasticity.size() > 2 ? axisElasticity.get(2) : 0.8; }

    public int getMaxLeashesPerEntity() { return maxLeashesPerEntity; }
    public boolean canEntityAcceptMoreLeashes(Entity entity, int currentCount) {
        return currentCount < maxLeashesPerEntity;
    }

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

    public void clear() {
        entityHolderMap.clear(); tagHolderMap.clear(); modHolderMap.clear();
        entityLeashMap.clear(); tagLeashMap.clear(); modLeashMap.clear();
        teleportWhitelistCache = Collections.emptyList();
    }

    public static void loading(LeashConfigManager manager) {
        manager.reloadAll();
    }

    public static void reloading(LeashConfigManager manager) {
        manager.reloadAll();
    }

    public static void unloading(LeashConfigManager manager) {
        if(manager != null) manager.clear();
    }

    public String getStats() {
        return String.format(
                "Holder: Entities: %d, Tags: %d, Mods: %d\nLeash: Entities: %d, Tags: %d, Mods: %d, TeleportWhitelist: %d",
                entityHolderMap.size(), tagHolderMap.size(), modHolderMap.size(),
                entityLeashMap.size(), tagLeashMap.size(), modLeashMap.size(),
                teleportWhitelistCache.size()
        );
    }
}