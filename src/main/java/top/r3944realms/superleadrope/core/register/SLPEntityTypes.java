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

package top.r3944realms.superleadrope.core.register;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

/**
 * The type Slp entity types.
 */
public class SLPEntityTypes {
    /**
     * The Entity types.
     */
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SuperLeadRope.MOD_ID);
    /**
     * The Super lead knot.
     */
    public static RegistryObject<EntityType<SuperLeashKnotEntity>> SUPER_LEAD_KNOT =  ENTITY_TYPES.register(
            "super_lead_knot",
            () -> EntityType.Builder.<SuperLeashKnotEntity>of(SuperLeashKnotEntity::new, MobCategory.MISC)
                    .noSave()
                    .sized(0.375F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
                    .build("super_lead_knot")
    );

    /**
     * Gets entity name key.
     *
     * @param entityName the entity name
     * @return the entity name key
     */
    public static String getEntityNameKey(String entityName) {
        return "entity." + SuperLeadRope.MOD_ID + "." + entityName;
    }

    /**
     * Register.
     *
     * @param bus the bus
     */
    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
