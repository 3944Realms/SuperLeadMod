package top.r3944realms.superleadrope.compat;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.*;
import net.luckperms.api.node.Node;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.Objects;

public class LuckPermsCompat {
    public final static boolean isModLoaded = ModList.get().isLoaded("luckperms");
    public static volatile ILPC instance;
    public interface ILPC {
        void init();
        default boolean isLeashedBypass(Entity player) { return false; }
    }

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
