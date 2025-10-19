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

package top.r3944realms.superleadrope.util.file;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.SuperLeadRope;

import java.io.File;
import java.util.Optional;

/**
 * The type Config util.
 */
public class ConfigUtil {
    /**
     * Create file.
     *
     * @param children the children
     */
    public static void createFile(String[] children) {//初始化配置文件目录
        File configFile = new File(FMLPaths.CONFIGDIR.get().toFile(), SuperLeadRope.MOD_ID);
        if (!configFile.exists()) {
            boolean mkdirSuccess = configFile.mkdirs();
            if (!mkdirSuccess) {
                SuperLeadRope.logger.error("failed to create config directory for whimsicality");
                throw new RuntimeException("failed to create config directory for " + SuperLeadRope.MOD_ID);
            } else {
                for (String child : children) {
                    File file = new File(configFile, child);
                    if (!file.exists()) {
                        boolean mkdirChildrenSuccess = file.mkdirs();
                        if (!mkdirChildrenSuccess) {
                            SuperLeadRope.logger.error("failed to create {} directory for +" + SuperLeadRope.MOD_ID, child);
                            throw new RuntimeException("failed to create " + child + " directory for" +SuperLeadRope.MOD_ID);
                        }
                    }
                }
            }
        }
    }

    /**
     * Register config.
     *
     * @param context    the context
     * @param type       the type
     * @param configSpec the config spec
     * @param folderName the folder name
     * @param fileName   the file name
     */
    public static void registerConfig (
            @NotNull ModLoadingContext context,
            ModConfig.Type type,
            ForgeConfigSpec configSpec,
            String folderName,
            String fileName
    ) {
        context.registerConfig(
                type,
                configSpec,
                SuperLeadRope.MOD_ID + "/" + Optional.ofNullable(folderName).map(i-> i + "/").orElse("") + fileName + ".toml"
        );
    }
}
