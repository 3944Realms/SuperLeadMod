/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR 阿 PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope.util.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 骑乘关系数据结构
 */
public class RidingRelationship {
    private UUID entityId;
    private UUID vehicleId;
    private List<RidingRelationship> passengers;

    /**
     * Instantiates a new Riding relationship.
     */
    public RidingRelationship() {
        this.passengers = new ArrayList<>();
    }

    /**
     * Instantiates a new Riding relationship.
     *
     * @param passengers the passengers
     * @param vehicleId  the vehicle id
     * @param entityId   the entity id
     */
    public RidingRelationship(List<RidingRelationship> passengers, UUID vehicleId, UUID entityId) {
        this.passengers = passengers != null ? passengers : new ArrayList<>();
        this.vehicleId = vehicleId;
        this.entityId = entityId;
    }

    /**
     * Gets entity id.
     *
     * @return the entity id
     */
    public UUID getEntityId() {
        return entityId;
    }

    /**
     * Sets entity id.
     *
     * @param entityId the entity id
     */
    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets passengers.
     *
     * @return the passengers
     */
    public List<RidingRelationship> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    /**
     * Sets passengers.
     *
     * @param passengers the passengers
     */
    public void setPassengers(List<RidingRelationship> passengers) {
        this.passengers = passengers != null ? passengers : new ArrayList<>();
    }

    /**
     * Add passenger.
     *
     * @param passenger the passenger
     */
    public void addPassenger(RidingRelationship passenger) {
        this.passengers.add(passenger);
    }

    /**
     * Gets vehicle id.
     *
     * @return the vehicle id
     */
    public UUID getVehicleId() {
        return vehicleId;
    }

    /**
     * Sets vehicle id.
     *
     * @param vehicleId the vehicle id
     */
    public void setVehicleId(UUID vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * 获取所有嵌套乘客的数量
     *
     * @return the total passenger count
     */
    public int getTotalPassengerCount() {
        int count = passengers.size();
        for (RidingRelationship passenger : passengers) {
            count += passenger.getTotalPassengerCount();
        }
        return count;
    }

    /**
     * 检查是否包含特定实体
     *
     * @param entityId the entity id
     * @return the boolean
     */
    public boolean containsEntity(UUID entityId) {
        if (Objects.equals(this.entityId, entityId)) {
            return true;
        }
        for (RidingRelationship passenger : passengers) {
            if (passenger.containsEntity(entityId)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 查找并替换所有匹配的UUID
     *
     * @param oldUuid 要查找的旧UUID
     * @param newUuid 要替换的新UUID
     * @return 替换的数量
     */
    public int findAndReplaceAll(UUID oldUuid, UUID newUuid) {
        int replacedCount = 0;

        // 替换当前节点的entityId
        if (Objects.equals(this.entityId, oldUuid)) {
            this.entityId = newUuid;
            replacedCount++;
        }

        // 替换当前节点的vehicleId
        if (Objects.equals(this.vehicleId, oldUuid)) {
            this.vehicleId = newUuid;
            replacedCount++;
        }

        // 递归替换所有乘客
        for (RidingRelationship passenger : passengers) {
            replacedCount += passenger.findAndReplaceAll(oldUuid, newUuid);
        }

        return replacedCount;
    }

    /**
     * 查找所有出现的UUID位置
     *
     * @param targetUuid 要查找的UUID
     * @return 包含位置的列表，格式为"角色(实体/载具)-索引"
     */
    public List<String> findAllOccurrences(UUID targetUuid) {
        List<String> occurrences = new ArrayList<>();
        findAllOccurrencesRecursive(targetUuid, this, occurrences, "");
        return occurrences;
    }

    private void findAllOccurrencesRecursive(UUID targetUuid, @NotNull RidingRelationship node,
                                             List<String> occurrences, String path) {
        // 检查当前节点的entityId
        if (Objects.equals(node.entityId, targetUuid)) {
            String fullPath = path.isEmpty() ? "根实体" : path + "->乘客";
            occurrences.add(fullPath + "(实体ID)");
        }

        // 检查当前节点的vehicleId
        if (Objects.equals(node.vehicleId, targetUuid)) {
            String fullPath = path.isEmpty() ? "根实体" : path;
            occurrences.add(fullPath + "(载具ID)");
        }

        // 递归检查乘客
        for (int i = 0; i < node.passengers.size(); i++) {
            String newPath = path.isEmpty() ? "乘客[" + i + "]" : path + "->乘客[" + i + "]";
            findAllOccurrencesRecursive(targetUuid, node.passengers.get(i), occurrences, newPath);
        }
    }

    /**
     * 查找并替换所有UUID（支持批量替换）
     *
     * @param replacements 替换映射表，key为旧UUID，value为新UUID
     * @return 总替换数量
     */
    public int batchFindAndReplace(Map<UUID, UUID> replacements) {
        int totalReplaced = 0;

        // 替换当前节点的entityId
        if (this.entityId != null && replacements.containsKey(this.entityId)) {
            this.entityId = replacements.get(this.entityId);
            totalReplaced++;
        }

        // 替换当前节点的vehicleId
        if (this.vehicleId != null && replacements.containsKey(this.vehicleId)) {
            this.vehicleId = replacements.get(this.vehicleId);
            totalReplaced++;
        }

        // 递归批量替换所有乘客
        for (RidingRelationship passenger : passengers) {
            totalReplaced += passenger.batchFindAndReplace(replacements);
        }

        return totalReplaced;
    }

    /**
     * 获取树中所有唯一的UUID集合
     *
     * @return 包含所有UUID的集合
     */
    public Set<UUID> getAllUniqueUUIDs() {
        Set<UUID> uuids = new HashSet<>();
        getAllUniqueUUIDsRecursive(this, uuids);
        return uuids;
    }

    private void getAllUniqueUUIDsRecursive(RidingRelationship node, Set<UUID> uuids) {
        if (node.entityId != null) {
            uuids.add(node.entityId);
        }
        if (node.vehicleId != null) {
            uuids.add(node.vehicleId);
        }

        for (RidingRelationship passenger : node.passengers) {
            getAllUniqueUUIDsRecursive(passenger, uuids);
        }
    }

    @Override
    public String toString() {
        return "RidingRelationship{" +
                "entityId=" + entityId +
                ", vehicleId=" + vehicleId +
                ", passengers=" + passengers.size() +
                '}';
    }
}