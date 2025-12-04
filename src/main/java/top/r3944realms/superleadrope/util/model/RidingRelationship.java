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

    @Override
    public String toString() {
        return "RidingRelationship{" +
                "entityId=" + entityId +
                ", vehicleId=" + vehicleId +
                ", passengers=" + passengers.size() +
                '}';
    }
}