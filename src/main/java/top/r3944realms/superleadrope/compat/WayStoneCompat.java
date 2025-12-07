package top.r3944realms.superleadrope.compat;

import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;
import top.r3944realms.superleadrope.api.workspace.Services;

import java.util.Set;

public class WayStoneCompat {
    public final static boolean isModLoaded = ModList.get().isLoaded("waystones");
    public static void init() {
        if (isModLoaded) {
            MinecraftForge.EVENT_BUS.addListener(WayStoneCompat::onWayStoneTeleport);
        }
    }
    public static void onWayStoneTeleport(WaystoneTeleportEvent.@NotNull Pre event) {
        Entity entity = event.getContext().getEntity();
        ILeashHelper.IHolder holderHelper = Services.WORK_SPACE.getLeashHelper().getHolderHelper(entity);
        Set<Entity> allLeashedEntities = holderHelper.getAllLeashedEntities();
        allLeashedEntities.forEach(event::addAdditionalEntity);
    }
}
