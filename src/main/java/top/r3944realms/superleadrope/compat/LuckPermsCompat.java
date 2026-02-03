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

package top.r3944realms.superleadrope.compat;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.node.Node;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.Objects;

/**
 * The type Luck perms compat.
 */
public class LuckPermsCompat {
    /**
     * The constant isModLoaded.
     */
    public final static boolean isModLoaded = ModList.get().isLoaded("luckperms");
    /**
     * The constant instance.
     */
    public static volatile ILPC instance;

    /**
     * The interface Ilpc.
     */
    public interface ILPC {
        /**
         * Init.
         */
        void init();

        /**
         * Is leashed bypass boolean.
         *
         * @param player the player
         * @return the boolean
         */
        default boolean isLeashedBypass(Entity player) { return false; }
    }

    /**
     * Gets or create lpc.
     *
     * @return the or create lpc
     */
    @Contract(" -> new")
    public static @NotNull ILPC getOrCreateLPC() {

        if (instance == null) {
            synchronized (LuckPermsCompat.class) {
                if (instance == null) {
                    if (!isModLoaded) {
                        instance = new DummyLPC();
                    } else instance = new RealLPC();
                }
            }
        }
       return instance;
    }

    // 空实现
    private static class DummyLPC implements ILPC {
        @Override
        public void init() {}
    }

    // 真实实现（只有在模组加载时才被初始化）
    private static class RealLPC implements ILPC {
        private boolean isInitialized;
        private LuckPerms luckPerms ;
        private final Node LeashBypass = Node.builder(SuperLeadRope.MOD_ID + ".leash.bypass").build();

        /**
         * Instantiates a new Real lpc.
         */
        public RealLPC() {
            isInitialized = false;
            init();
        }

        @Override
        public void init() {
            try {
                luckPerms = LuckPermsProvider.get();
                luckPerms.getContextManager().registerCalculator(new LeashCalculator());
                isInitialized = true;
            } catch (IllegalStateException e) {
                SuperLeadRope.logger.error("LuckPermsCompat failed to initialize", e);
            }
        }

        @Override
        public boolean isLeashedBypass(Entity player) {
            if (!(player instanceof Player)) return false;
            return isInitialized && luckPerms.getUserManager().isLoaded(player.getUUID()) &&
                    Objects.requireNonNull(luckPerms.getUserManager().getUser(player.getUUID()))
                            .getNodes()
                            .stream()
                            .filter(i -> i.equals(LeashBypass))
                            .findFirst()
                            .map(Node::getValue)
                            .orElse(false);
        }

        /**
         * The type Leash calculator.
         */
        public static class LeashCalculator implements ContextCalculator<Player> {
            @Override
            public void calculate(@NotNull Player target, ContextConsumer contextConsumer) {
                contextConsumer.accept("isLeashed", String.valueOf(LeashDataInnerAPI.QueryOperations.hasLeash(target)));
            }

            @Override
            public @NotNull ContextSet estimatePotentialContexts() {
                ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
                builder.add("isLeashed", "false");
                builder.add("isLeashed", "true");
                return builder.build();
            }
        }
    }
}
