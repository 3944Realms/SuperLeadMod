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

package top.r3944realms.superleadrope.core.exception;

import java.util.UUID;

/**
 * The type Riding cycle exception.
 */
public class RidingCycleException extends IllegalStateException {
    private final UUID entityId;
    private final UUID vehicleId;

    /**
     * Instantiates a new Riding cycle exception.
     *
     * @param entityId  the entity id
     * @param vehicleId the vehicle id
     */
    public RidingCycleException(UUID entityId, UUID vehicleId) {
        super(String.format("Cyclic riding reference detected. " +
                        "Entity %s cannot be added as passenger to vehicle %s " +
                        "as it would create a circular dependency.",
                entityId, vehicleId));
        this.entityId = entityId;
        this.vehicleId = vehicleId;
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
     * Gets vehicle id.
     *
     * @return the vehicle id
     */
    public UUID getVehicleId() {
        return vehicleId;
    }
}
