/*
 *  Super Lead rope mod
 *  Copyright (C)  2026  R3944Realms
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
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.CommonConfigHashInformPacket;

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
    /**
     * The constant cacheTag.
     */
// ========= 缓存 ========
    public volatile static CompoundTag cacheTag = null;
    /**
     * The constant cacheHash.
     */
    public volatile static int cacheHash = -1;
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
    private volatile double maxMovement = 100.0;
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
     * Gets max movement.
     *
     * @return the max movement
     */
    public double getMaxMovement() { return maxMovement; }

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
        try {
            parseOffsetConfig();

            teleportWhitelistCache = new ArrayList<>(LeashCommonConfig.COMMON.teleportWhitelist.get());
            commandPrefixCache = LeashCommonConfig.COMMON.SLPModCommandPrefix.get();
            commandPrefixEnabledCache = LeashCommonConfig.COMMON.enableSLPModCommandPrefix.get();

            maxLeashLength = LeashCommonConfig.COMMON.maxLeashLength.get();
            maxMovement = LeashCommonConfig.COMMON.maxMovement.get();
            elasticDistanceScale = LeashCommonConfig.COMMON.elasticDistanceScale.get();
            extremeSnapFactor = LeashCommonConfig.COMMON.extremeSnapFactor.get();
            springDampening = LeashCommonConfig.COMMON.springDampening.get();
            axisElasticity = new ArrayList<>(LeashCommonConfig.COMMON.axisSpecificElasticity.get());
            maxLeashesPerEntity = LeashCommonConfig.COMMON.maxLeashesPerEntity.get();

            enableTrueDamping = LeashCommonConfig.COMMON.enableTrueDamping.get();
            maxForce = LeashCommonConfig.COMMON.maxForce.get();
            playerSpringFactor = LeashCommonConfig.COMMON.playerSpringFactor.get();
            mobSpringFactor = LeashCommonConfig.COMMON.mobSpringFactor.get();
            cacheHash = calculateConfigHash();
            cacheTag = serializeToNBT();
            SuperLeadRope.logger.debug("Configs reloaded: {}", getStats());
        } catch (Exception e) {
            SuperLeadRope.logger.error("Failed to reload configs", e);
            cacheHash = -1;
            cacheTag = null;
        }

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
     * 将配置管理器状态序列化为NBT
     *
     * @return the compound tag
     */
    public synchronized CompoundTag serializeToNBT() {
        if (cacheHash == calculateConfigHash() && cacheTag != null) return cacheTag;
        CompoundTag tag = new CompoundTag();

        // 序列化偏移映射
        CompoundTag offsets = new CompoundTag();
        serializeOffsetMap(offsets, "holder_entity", entityHolderMap);
        serializeOffsetMap(offsets, "holder_tag", tagHolderMap);
        serializeOffsetMap(offsets, "holder_mod", modHolderMap);
        serializeOffsetMap(offsets, "leash_entity", entityLeashMap);
        serializeOffsetMap(offsets, "leash_tag", tagLeashMap);
        serializeOffsetMap(offsets, "leash_mod", modLeashMap);
        tag.put("offsets", offsets);

        // 序列化缓存配置
        ListTag whitelistTag = new ListTag();
        for (String entry : teleportWhitelistCache) {
            whitelistTag.add(StringTag.valueOf(entry));
        }
        tag.put("teleport_whitelist", whitelistTag);

        tag.putString("command_prefix", commandPrefixCache);
        tag.putBoolean("command_prefix_enabled", commandPrefixEnabledCache);

        // 序列化物理参数
        tag.putBoolean("enable_true_damping", enableTrueDamping);
        tag.putDouble("max_force", maxForce);
        tag.putDouble("player_spring_factor", playerSpringFactor);
        tag.putDouble("mob_spring_factor", mobSpringFactor);

        tag.putDouble("max_leash_length", maxLeashLength);
        tag.putDouble("max_movement", maxMovement);
        tag.putDouble("elastic_distance_scale", elasticDistanceScale);
        tag.putDouble("extreme_snap_factor", extremeSnapFactor);
        tag.putDouble("spring_dampening", springDampening);

        ListTag elasticityTag = new ListTag();
        for (double value : axisElasticity) {
            elasticityTag.add(DoubleTag.valueOf(value));
        }
        tag.put("axis_elasticity", elasticityTag);

        tag.putInt("max_leashes_per_entity", maxLeashesPerEntity);
        cacheHash = calculateConfigHash();
        cacheTag = tag;

        return tag;
    }
    private void update() {
        // ========== 更新偏移配置 ==========
        // 将holder偏移写回配置
        List<String> holderOffsetConfigs = convertMapsToOffsetList(entityHolderMap, tagHolderMap, modHolderMap);
        LeashCommonConfig.COMMON.defaultHolderLocationOffset.set(holderOffsetConfigs);

        // 将leash偏移写回配置
        List<String> leashOffsetConfigs = convertMapsToOffsetList(entityLeashMap, tagLeashMap, modLeashMap);
        LeashCommonConfig.COMMON.defaultApplyEntityLocationOffset.set(leashOffsetConfigs);

        // ========== 更新白名单 ==========
        LeashCommonConfig.COMMON.teleportWhitelist.set(new ArrayList<>(teleportWhitelistCache));

        // ========== 更新命令配置 ==========
        LeashCommonConfig.COMMON.SLPModCommandPrefix.set(commandPrefixCache);
        LeashCommonConfig.COMMON.enableSLPModCommandPrefix.set(commandPrefixEnabledCache);

        // ========== 更新物理参数 ==========
        LeashCommonConfig.COMMON.maxLeashLength.set(maxLeashLength);
        LeashCommonConfig.COMMON.maxMovement.set(maxMovement);
        LeashCommonConfig.COMMON.elasticDistanceScale.set(elasticDistanceScale);
        LeashCommonConfig.COMMON.extremeSnapFactor.set(extremeSnapFactor);
        LeashCommonConfig.COMMON.springDampening.set(springDampening);
        LeashCommonConfig.COMMON.axisSpecificElasticity.set(new ArrayList<>(axisElasticity));
        LeashCommonConfig.COMMON.maxLeashesPerEntity.set(maxLeashesPerEntity);

        // ========== 更新阻尼参数 ==========
        LeashCommonConfig.COMMON.enableTrueDamping.set(enableTrueDamping);
        LeashCommonConfig.COMMON.maxForce.set(maxForce);
        LeashCommonConfig.COMMON.playerSpringFactor.set(playerSpringFactor);
        LeashCommonConfig.COMMON.mobSpringFactor.set(mobSpringFactor);

    }

    /**
     * 将偏移映射转换为配置列表格式
     */
    private List<String> convertMapsToOffsetList(
            Map<String, double[]> entityMap,
            Map<String, double[]> tagMap,
            Map<String, double[]> modMap) {

        Map<double[], Set<String>> offsetToTargets = new HashMap<>();

        // 收集entity映射
        for (Map.Entry<String, double[]> entry : entityMap.entrySet()) {
            offsetToTargets.computeIfAbsent(entry.getValue(), k -> new LinkedHashSet<>())
                    .add(entry.getKey());
        }

        // 收集tag映射
        for (Map.Entry<String, double[]> entry : tagMap.entrySet()) {
            offsetToTargets.computeIfAbsent(entry.getValue(), k -> new LinkedHashSet<>())
                    .add("#" + entry.getKey());
        }

        // 收集mod映射
        for (Map.Entry<String, double[]> entry : modMap.entrySet()) {
            if (entry.getKey().equals("*")) {
                offsetToTargets.computeIfAbsent(entry.getValue(), k -> new LinkedHashSet<>())
                        .add("*");
            } else {
                offsetToTargets.computeIfAbsent(entry.getValue(), k -> new LinkedHashSet<>())
                        .add("#" + entry.getKey());
            }
        }

        // 转换为配置字符串列表
        List<String> configs = new ArrayList<>();
        for (Map.Entry<double[], Set<String>> entry : offsetToTargets.entrySet()) {
            double[] offset = entry.getKey();
            Set<String> targets = entry.getValue();

            if (targets.isEmpty() || offset.length != 3) continue;

            String config = String.format(Locale.ROOT,
                    "%.2f,%.2f,%.2f=%s",
                    offset[0], offset[1], offset[2],
                    String.join(",", targets)
            );
            configs.add(config);
        }

        return configs;
    }

    /**
     * 从NBT反序列化配置管理器状态
     *
     * @param tag the tag
     */
    public void deserializeFromNBT(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return;

        // 反序列化偏移映射
        if (tag.contains("offsets", Tag.TAG_COMPOUND)) {
            CompoundTag offsets = tag.getCompound("offsets");
            deserializeOffsetMap(offsets, "holder_entity", entityHolderMap);
            deserializeOffsetMap(offsets, "holder_tag", tagHolderMap);
            deserializeOffsetMap(offsets, "holder_mod", modHolderMap);
            deserializeOffsetMap(offsets, "leash_entity", entityLeashMap);
            deserializeOffsetMap(offsets, "leash_tag", tagLeashMap);
            deserializeOffsetMap(offsets, "leash_mod", modLeashMap);
        }

        // 反序列化缓存配置
        if (tag.contains("teleport_whitelist", Tag.TAG_LIST)) {
            ListTag whitelistTag = tag.getList("teleport_whitelist", Tag.TAG_STRING);
            List<String> whitelist = new ArrayList<>();
            for (int i = 0; i < whitelistTag.size(); i++) {
                whitelist.add(whitelistTag.getString(i));
            }
            teleportWhitelistCache = Collections.unmodifiableList(whitelist);
        }

        if (tag.contains("command_prefix", Tag.TAG_STRING)) {
            commandPrefixCache = tag.getString("command_prefix");
        }
        if (tag.contains("command_prefix_enabled", Tag.TAG_BYTE)) {
            commandPrefixEnabledCache = tag.getBoolean("command_prefix_enabled");
        }

        // 反序列化物理参数
        if (tag.contains("enable_true_damping", Tag.TAG_BYTE)) {
            enableTrueDamping = tag.getBoolean("enable_true_damping");
        }
        if (tag.contains("max_force", Tag.TAG_DOUBLE)) {
            maxForce = tag.getDouble("max_force");
        }
        if (tag.contains("player_spring_factor", Tag.TAG_DOUBLE)) {
            playerSpringFactor = tag.getDouble("player_spring_factor");
        }
        if (tag.contains("mob_spring_factor", Tag.TAG_DOUBLE)) {
            mobSpringFactor = tag.getDouble("mob_spring_factor");
        }

        if (tag.contains("max_leash_length", Tag.TAG_DOUBLE)) {
            maxLeashLength = tag.getDouble("max_leash_length");
        }
        if (tag.contains("max_movement", Tag.TAG_DOUBLE)) {
            maxLeashLength = tag.getDouble("max_movement");
        }
        if (tag.contains("elastic_distance_scale", Tag.TAG_DOUBLE)) {
            elasticDistanceScale = tag.getDouble("elastic_distance_scale");
        }
        if (tag.contains("extreme_snap_factor", Tag.TAG_DOUBLE)) {
            extremeSnapFactor = tag.getDouble("extreme_snap_factor");
        }
        if (tag.contains("spring_dampening", Tag.TAG_DOUBLE)) {
            springDampening = tag.getDouble("spring_dampening");
        }

        if (tag.contains("axis_elasticity", Tag.TAG_LIST)) {
            ListTag elasticityTag = tag.getList("axis_elasticity", Tag.TAG_DOUBLE);
            List<Double> elasticity = new ArrayList<>();
            for (int i = 0; i < elasticityTag.size(); i++) {
                elasticity.add(elasticityTag.getDouble(i));
            }
            axisElasticity = Collections.unmodifiableList(elasticity);
        }

        if (tag.contains("max_leashes_per_entity", Tag.TAG_INT)) {
            maxLeashesPerEntity = tag.getInt("max_leashes_per_entity");
        }
        update();
    }

    /**
     * 计算配置哈希值（用于快速比较配置是否变化）
     *
     * @return the int
     */
    public int calculateConfigHash() {
        // 使用FNV-1a哈希算法
        int hash = 0x811c9dc5; // FNV偏移基础值

        // 哈希偏移映射
        hash = fnv1aHashMap(hash, entityHolderMap);
        hash = fnv1aHashMap(hash, tagHolderMap);
        hash = fnv1aHashMap(hash, modHolderMap);
        hash = fnv1aHashMap(hash, entityLeashMap);
        hash = fnv1aHashMap(hash, tagLeashMap);
        hash = fnv1aHashMap(hash, modLeashMap);

        // 哈希白名单
        for (String entry : teleportWhitelistCache) {
            hash = fnv1aHashString(hash, entry);
        }

        // 哈希字符串参数
        hash = fnv1aHashString(hash, commandPrefixCache);
        hash ^= commandPrefixEnabledCache ? 0x55555555 : 0xAAAAAAAA;

        // 哈希双精度参数（转换为长整型位表示进行哈希）
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(enableTrueDamping ? 1.0 : 0.0));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(maxForce));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(playerSpringFactor));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(mobSpringFactor));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(maxLeashLength));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(maxMovement));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(elasticDistanceScale));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(extremeSnapFactor));
        hash = fnv1aHashLong(hash, Double.doubleToLongBits(springDampening));

        // 哈希轴弹性列表
        for (double value : axisElasticity) {
            hash = fnv1aHashLong(hash, Double.doubleToLongBits(value));
        }

        // 哈希整数参数
        hash = fnv1aHashInt(hash, maxLeashesPerEntity);

        return hash;
    }

    // ================== 辅助方法 ==================

    private void serializeOffsetMap(CompoundTag parent, String key, @NotNull Map<String, double[]> map) {
        CompoundTag mapTag = new CompoundTag();
        for (Map.Entry<String, double[]> entry : map.entrySet()) {
            String entryKey = entry.getKey().replace(':', '_'); // 避免NBT键中的冒号问题
            ListTag offsetList = new ListTag();
            for (double value : entry.getValue()) {
                offsetList.add(DoubleTag.valueOf(value));
            }
            mapTag.put(entryKey, offsetList);
        }
        parent.put(key, mapTag);
    }

    private void deserializeOffsetMap(@NotNull CompoundTag parent, String key, Map<String, double[]> map) {
        if (parent.contains(key, Tag.TAG_COMPOUND)) {
            map.clear();
            CompoundTag mapTag = parent.getCompound(key);
            for (String entryKey : mapTag.getAllKeys()) {
                if (mapTag.contains(entryKey, Tag.TAG_LIST)) {
                    ListTag offsetList = mapTag.getList(entryKey, Tag.TAG_DOUBLE);
                    double[] offset = new double[offsetList.size()];
                    for (int i = 0; i < offsetList.size(); i++) {
                        offset[i] = offsetList.getDouble(i);
                    }
                    map.put(entryKey.replace('_', ':'), offset); // 恢复原始键名
                }
            }
        }
    }

    // FNV-1a哈希辅助方法
    private int fnv1aHashInt(int hash, int value) {
        hash ^= (value & 0xFF);
        hash *= 0x01000193;
        hash ^= ((value >> 8) & 0xFF);
        hash *= 0x01000193;
        hash ^= ((value >> 16) & 0xFF);
        hash *= 0x01000193;
        hash ^= ((value >> 24) & 0xFF);
        hash *= 0x01000193;
        return hash;
    }

    private int fnv1aHashLong(int hash, long value) {
        hash = fnv1aHashInt(hash, (int)(value & 0xFFFFFFFFL));
        return fnv1aHashInt(hash, (int)(value >> 32));
    }

    private int fnv1aHashString(int hash, @NotNull String str) {
        for (int i = 0; i < str.length(); i++) {
            hash ^= str.charAt(i);
            hash *= 0x01000193;
        }
        return hash;
    }

    private int fnv1aHashMap(int hash, @NotNull Map<String, double[]> map) {
        for (Map.Entry<String, double[]> entry : map.entrySet()) {
            hash = fnv1aHashString(hash, entry.getKey());
            for (double value : entry.getValue()) {
                hash = fnv1aHashLong(hash, Double.doubleToLongBits(value));
            }
        }
        return hash;
    }

    /**
     * Broad hash packet.
     */
    public void broadHashPacket() {
        if (cacheHash != -1){
            NetworkHandler.sendToAllPlayer(new CommonConfigHashInformPacket(cacheHash));
        }
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