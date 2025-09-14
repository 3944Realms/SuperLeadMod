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

package top.r3944realms.superleadrope;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.r3944realms.superleadrope.config.LeashCommonConfig;
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.util.file.ConfigUtil;

@Mod(value = SuperLeadRope.MOD_ID)
public class SuperLeadRope {
    public static final Logger logger = LoggerFactory.getLogger(SuperLeadRope.class);

    public static final String MOD_ID = "superleadrope";
    public SuperLeadRope() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SLPItems.register(eventBus);
        SLPEntityTypes.register(eventBus);
        SLPSoundEvents.register(eventBus);
        NetworkHandler.register();
        initialize();

    }
    public static void initialize() {
        logger.info("Initializing SuperLeadRope");
        String c = "common";
        ConfigUtil.createFile(new String[]{c});
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        ConfigUtil.registerConfig(modLoadingContext, ModConfig.Type.COMMON, LeashCommonConfig.SPEC, c, "leash");
    }

    public static class ModInfo {
        public static final String VERSION;
        static {
            // 从 ModList 获取当前 ModContainer 的元数据
            VERSION = ModList.get()
                    .getModContainerById(MOD_ID)
                    .map(c -> c.getModInfo().getVersion().toString())
                    .orElse("UNKNOWN");
        }
    }
}
