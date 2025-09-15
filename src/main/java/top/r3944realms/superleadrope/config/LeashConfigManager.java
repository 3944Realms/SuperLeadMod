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
import java.util.regex.Matcher;

import static top.r3944realms.superleadrope.config.LeashCommonConfig.Common.OFFSET_PATTERN;

public class LeashConfigManager {
    private final Map<String, double[]> entityHolderOffsetMap = new ConcurrentHashMap<>(), entityLeashOffsetMap = new ConcurrentHashMap<>();
    private final Map<String, double[]> tagHolderOffsetMap = new ConcurrentHashMap<>(), tagLeashOffsetMap = new ConcurrentHashMap<>();
    private final Map<String, double[]> modHolderOffsetMap = new ConcurrentHashMap<>(), modLeashOffsetMap = new ConcurrentHashMap<>();

    // 缓存常用配置值以提高性能
    private volatile List<String> teleportWhitelistCache;
    private volatile String commandPrefixCache;
    private volatile boolean enableCommandPrefixCache;
    private volatile double maxLeashLengthCache;
    private volatile double elasticDistanceCache;
    private volatile double extremeSnapFactorCache;
    private volatile double springDampeningCache;
    private volatile List<Double> axisSpecificElasticityCache;
    private volatile int maxLeashesPerEntityCache;

    public LeashConfigManager() {
        this.reloadAll();
    }

    /**
     * 解析偏移配置（线程安全）
     */
    public void parseOffsetConfig() {
        // --- Holder ---
        Map<String, double[]> holderEntityMap = new HashMap<>();
        Map<String, double[]> holderTagMap = new HashMap<>();
        Map<String, double[]> holderModMap = new HashMap<>();

        List<? extends String> holderOffsets = LeashCommonConfig.COMMON.defaultHolderLocationOffset.get();
        for (String offsetConfig : holderOffsets) {
            Matcher matcher = OFFSET_PATTERN.matcher(offsetConfig);
            if (!matcher.matches()) continue;

            try {
                double x = Double.parseDouble(matcher.group(1).trim());
                double y = Double.parseDouble(matcher.group(2).trim());
                double z = Double.parseDouble(matcher.group(3).trim());
                double[] offset = new double[]{x, y, z};

                String entityList = matcher.group(4);
                for (String entity : entityList.split(",")) {
                    String trimmed = entity.trim();
                    if (trimmed.equals("*")) {
                        // special case: apply to all entities
                        holderModMap.put("*", offset);
                    } else if (trimmed.startsWith("#")) {
                        String body = trimmed.substring(1).trim();
                        if (body.contains(":")) {
                            holderTagMap.put(body, offset);
                        } else {
                            holderModMap.put(body, offset);
                        }
                    } else {
                        holderEntityMap.put(trimmed, offset);
                    }
                }
            } catch (NumberFormatException e) {
                SuperLeadRope.logger.error("Invalid holder offset config: {}", offsetConfig);
            }
        }

        entityHolderOffsetMap.clear();
        entityHolderOffsetMap.putAll(holderEntityMap);
        tagHolderOffsetMap.clear();
        tagHolderOffsetMap.putAll(holderTagMap);
        modHolderOffsetMap.clear();
        modHolderOffsetMap.putAll(holderModMap);

        // --- Leash ---
        Map<String, double[]> leashEntityMap = new HashMap<>();
        Map<String, double[]> leashTagMap = new HashMap<>();
        Map<String, double[]> leashModMap = new HashMap<>();

        List<? extends String> leashOffsets = LeashCommonConfig.COMMON.defaultApplyEntityLocationOffset.get();
        for (String offsetConfig : leashOffsets) {
            Matcher matcher = OFFSET_PATTERN.matcher(offsetConfig);
            if (!matcher.matches()) continue;

            try {
                double x = Double.parseDouble(matcher.group(1).trim());
                double y = Double.parseDouble(matcher.group(2).trim());
                double z = Double.parseDouble(matcher.group(3).trim());
                double[] offset = new double[]{x, y, z};

                String entityList = matcher.group(4);
                for (String entity : entityList.split(",")) {
                    String trimmed = entity.trim();
                    if (trimmed.equals("*")) {
                        leashModMap.put("*", offset);
                    } else if (trimmed.startsWith("#")) {
                        String body = trimmed.substring(1).trim();
                        if (body.contains(":")) {
                            leashTagMap.put(body, offset);
                        } else {
                            leashModMap.put(body, offset);
                        }
                    } else {
                        leashEntityMap.put(trimmed, offset);
                    }
                }
            } catch (NumberFormatException e) {
                SuperLeadRope.logger.error("Invalid leash offset config: {}", offsetConfig);
            }
        }

        entityLeashOffsetMap.clear();
        entityLeashOffsetMap.putAll(leashEntityMap);
        tagLeashOffsetMap.clear();
        tagLeashOffsetMap.putAll(leashTagMap);
        modLeashOffsetMap.clear();
        modLeashOffsetMap.putAll(leashModMap);
    }


    /**
     * 重新加载所有配置值到缓存
     */
    public void reloadAll() {
        // 加载偏移配置
        parseOffsetConfig();

        // 加载其他配置到缓存
        teleportWhitelistCache = new ArrayList<>(LeashCommonConfig.COMMON.teleportWhitelist.get());
        commandPrefixCache = LeashCommonConfig.COMMON.SLPModCommandPrefix.get();
        enableCommandPrefixCache = LeashCommonConfig.COMMON.EnableSLPModCommandPrefix.get();
        maxLeashLengthCache = LeashCommonConfig.COMMON.maxLeashLength.get();
        elasticDistanceCache = LeashCommonConfig.COMMON.elasticDistance.get();
        extremeSnapFactorCache = LeashCommonConfig.COMMON.extremeSnapFactor.get();
        springDampeningCache = LeashCommonConfig.COMMON.springDampening.get();
        axisSpecificElasticityCache = new ArrayList<>(LeashCommonConfig.COMMON.axisSpecificElasticity.get());
        maxLeashesPerEntityCache = LeashCommonConfig.COMMON.maxLeashesPerEntity.get();

        SuperLeadRope.logger.debug("All configs reloaded: {}", getStats());
    }

    // ========== 偏移配置相关方法 ==========

    /**
     * 获取实体类型的偏移量
     */
    @SuppressWarnings("deprecation")
    public Vec3 getDefaultEntityOffset(EntityType<?> entityType) {
        String entityId = entityType.builtInRegistryHolder().key().location().toString();
        String modId = entityId.split(":")[0]; // 从实体ID提取modId

        // 获取实体的标签
        List<String> tagStrings = new ArrayList<>();
        for (var tag : entityType.builtInRegistryHolder().tags().toList()) {
            tagStrings.add(tag.location().toString());
        }

        double[] offset = getDefaultEntityOffset(entityId, modId, tagStrings);
        return offset != null ? new Vec3(offset[0], offset[1], offset[2]) : Vec3.ZERO;
    }

    /**
     * 获取实体对象的偏移量（便捷方法）
     */
    public Vec3 getDefaultEntityOffset(Entity entity) {
        return getDefaultEntityOffset(entity.getType());
    }


    /**
     * 获取实体偏移量（原始数据）
     */
    public double[] getDefaultEntityOffset(String entityId, String modId, List<String> tags) {
        // 1. 首先检查特定实体
        if (entityLeashOffsetMap.containsKey(entityId)) {
            return entityLeashOffsetMap.get(entityId);
        }

        // 2. 检查标签
        for (String tag : tags) {
            if (tagLeashOffsetMap.containsKey(tag)) {
                return tagHolderOffsetMap.get(tag);
            }
        }

        // 3. 检查模组
        if (modLeashOffsetMap.containsKey(modId)) {
            return modLeashOffsetMap.get(modId);
        }

        //4. 通配符
        if (modLeashOffsetMap.containsKey("*")) {
            return modLeashOffsetMap.get("*");
        }
        return null;
    }
    /**
     * 获取实体类型的偏移量
     */
    @SuppressWarnings("deprecation")
    public Vec3 getDefaultHolderOffset(EntityType<?> entityType) {
        String entityId = entityType.builtInRegistryHolder().key().location().toString();
        String modId = entityId.split(":")[0]; // 从实体ID提取modId

        // 获取实体的标签
        List<String> tagStrings = new ArrayList<>();
        for (var tag : entityType.builtInRegistryHolder().tags().toList()) {
            tagStrings.add(tag.location().toString());
        }

        double[] offset = getDefaultHolderOffset(entityId, modId, tagStrings);
        return offset != null ? new Vec3(offset[0], offset[1], offset[2]) : Vec3.ZERO;
    }

    /**
     * 获取实体对象的偏移量（便捷方法）
     */
    public Vec3 getDefaultHolderOffset(Entity entity) {
        return getDefaultHolderOffset(entity.getType());
    }


    /**
     * 获取实体偏移量（原始数据）
     */
    public double[] getDefaultHolderOffset(String entityId, String modId, List<String> tags) {
        // 1. 首先检查特定实体
        if (entityHolderOffsetMap.containsKey(entityId)) {
            return entityHolderOffsetMap.get(entityId);
        }

        // 2. 检查标签
        for (String tag : tags) {
            if (tagHolderOffsetMap.containsKey(tag)) {
                return tagHolderOffsetMap.get(tag);
            }
        }

        // 3. 检查模组
        if (modHolderOffsetMap.containsKey(modId)) {
            return modHolderOffsetMap.get(modId);
        }

        //4. 通配符
        if (modLeashOffsetMap.containsKey("*")) {
            return modHolderOffsetMap.get("*");
        }

        return null;
    }

    // ========== 传送白名单相关方法 ==========

    public List<String> getTeleportWhitelist() {
        return Collections.unmodifiableList(teleportWhitelistCache);
    }

    @SuppressWarnings("deprecation")
    public boolean isEntityTeleportAllowed(EntityType<?> entityType) {
        String entityId = entityType.builtInRegistryHolder().key().location().toString();
        String modid = entityId.split(":")[0];

        for (String entry : teleportWhitelistCache) {
            if (entry.startsWith("#")) {
                String body = entry.substring(1);

                // Case 1: #modid → allow all entities from this mod
                if (!body.contains(":")) {
                    if (modid.equals(body)) {
                        return true;
                    }
                }
                // Case 2: #modid:tag_name → allow all entities under this tag
                else {
                    ResourceLocation tagId = new ResourceLocation(body);
                    TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
                    if (entityType.builtInRegistryHolder().is(tag)) {
                        return true;
                    }
                }
            } else {
                // Case 3: modid:entity_name → allow a specific entity
                if (entry.equals(entityId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEntityTeleportAllowed(String entityId) {
        // 对于字符串ID，我们无法检查标签，只能检查模组和特定实体
        String modid = entityId.contains(":") ? entityId.split(":")[0] : "minecraft";

        for (String entry : teleportWhitelistCache) {
            if (entry.startsWith("#")) {
                String body = entry.substring(1);

                // Case 1: #modid → allow all entities from this mod
                if (!body.contains(":")) {
                    if (modid.equals(body)) {
                        return true;
                    }
                }
                // Case 2: #modid:tag_name → 字符串ID无法检查标签，跳过
                // 如果需要支持标签检查，需要传入EntityType而不是String
            } else {
                // Case 3: modid:entity_name → allow a specific entity
                if (entry.equals(entityId)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 辅助方法：检查实体ID是否匹配模式（用于旧的匹配逻辑）
    private boolean matchesTeleportPattern(String pattern, String entityId) {
        if (pattern.startsWith("#")) {
            String body = pattern.substring(1);
            if (body.contains(":")) {
                // 标签格式: #modid:tag_name - 字符串ID无法准确匹配标签
                // 返回模组匹配作为近似
                String patternModId = body.split(":")[0];
                String entityModId = entityId.split(":")[0];
                return entityModId.equals(patternModId);
            } else {
                // 模组格式: #modid
                String entityModId = entityId.split(":")[0];
                return entityModId.equals(body);
            }
        } else {
            // 实体格式: modid:entity_id
            return entityId.equals(pattern);
        }
    }

    // 添加一个重载方法，方便使用Entity对象
    public boolean isEntityTeleportAllowed(Entity entity) {
        return isEntityTeleportAllowed(entity.getType());
    }

    // ========== 命令配置相关方法 ==========

    public String getCommandPrefix() {
        return commandPrefixCache;
    }

    public boolean isCommandPrefixEnabled() {
        return enableCommandPrefixCache;
    }

    public String getFullCommand(String subCommand) {
        return isCommandPrefixEnabled() ?
                getCommandPrefix() + " " + subCommand :
                subCommand;
    }

    // ========== 拴绳物理配置相关方法 ==========

    public double getMaxLeashLength() {
        return maxLeashLengthCache;
    }

    public double getElasticDistance() {
        return elasticDistanceCache;
    }

    public double getExtremeSnapFactor() {
        return extremeSnapFactorCache;
    }

    public double getBreakDistance() {
        return getMaxLeashLength() * getExtremeSnapFactor();
    }

    public double getSpringDampening() {
        return springDampeningCache;
    }

    public List<Double> getAxisSpecificElasticity() {
        return Collections.unmodifiableList(axisSpecificElasticityCache);
    }

    public double getXElasticity() {
        return !axisSpecificElasticityCache.isEmpty() ? axisSpecificElasticityCache.get(0) : 0.8;
    }

    public double getYElasticity() {
        return axisSpecificElasticityCache.size() > 1 ? axisSpecificElasticityCache.get(1) : 0.2;
    }

    public double getZElasticity() {
        return axisSpecificElasticityCache.size() > 2 ? axisSpecificElasticityCache.get(2) : 0.8;
    }

    // ========== 实体限制配置相关方法 ==========

    public int getMaxLeashesPerEntity() {
        return maxLeashesPerEntityCache;
    }

    public boolean canEntityAcceptMoreLeashes(Entity entity, int currentLeashCount) {
        return currentLeashCount < getMaxLeashesPerEntity();
    }

    // ========== 管理方法 ==========

    public void reload() {
        reloadAll();
    }

    public void clear() {
        entityHolderOffsetMap.clear();entityLeashOffsetMap.clear();
        tagHolderOffsetMap.clear();tagLeashOffsetMap.clear();
        modHolderOffsetMap.clear();modLeashOffsetMap.clear();
        teleportWhitelistCache = Collections.emptyList();
    }

    public String getStats() {
        return String.format("Holder: Entities: %d, Tags: %d, Mods: %d \n Leash: Entities: %d, Tags: %d, Mods: %d, TeleportWhitelist: %d",
                entityHolderOffsetMap.size(), tagHolderOffsetMap.size(), modHolderOffsetMap.size(),
                entityLeashOffsetMap.size(), tagLeashOffsetMap.size(), modLeashOffsetMap.size(),
                teleportWhitelistCache.size());
    }

    public static void loading(LeashConfigManager manager) {
        manager.reloadAll();
    }

    public static void reloading(LeashConfigManager manager) {
        manager.reloadAll();
    }

    public static void unloading(LeashConfigManager manager) {
        if(manager != null)
            manager.clear();
    }
}
