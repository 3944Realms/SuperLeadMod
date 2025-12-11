package top.r3944realms.superleadrope.compat;

import com.ibm.icu.impl.Pair;
import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.event.SuperLeadRopeEvent;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;
import top.r3944realms.superleadrope.api.type.util.ILeashHelper;
import top.r3944realms.superleadrope.api.workspace.Services;
import top.r3944realms.superleadrope.content.gamerule.server.TeleportWithLeashedEntities;
import top.r3944realms.superleadrope.core.leash.LeashSyncManager;
import top.r3944realms.superleadrope.core.register.SLPGameruleRegistry;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;
import top.r3944realms.superleadrope.util.capability.LeashStateInnerAPI;
import top.r3944realms.superleadrope.util.entity.TeleportUtil;
import top.r3944realms.superleadrope.util.model.RidingRelationship;
import top.r3944realms.superleadrope.util.riding.RidingApplier;
import top.r3944realms.superleadrope.util.riding.RidingDismounts;
import top.r3944realms.superleadrope.util.riding.RidingFinder;
import top.r3944realms.superleadrope.util.riding.RidingSaver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class WayStoneCompat {
    public final static boolean isModLoaded = ModList.get().isLoaded("waystones");
    public final static Map<UUID, Set<Pair<Entity, OriginalState>>> tempLeashMap = new ConcurrentHashMap<>();
    public final static Map<UUID, UUID> uuidMap = new ConcurrentHashMap<>();
    public record OriginalState(Pose pose, boolean isSprinting, float yaw, float pitch, Vec3 deltaMovement, LeashInfo leashInfo, RidingRelationship ridingRelationship) {}
    public static void init() {
        if (isModLoaded) {
            MinecraftForge.EVENT_BUS.addListener(WayStoneCompat::onWayStoneTeleport$Pre);
            MinecraftForge.EVENT_BUS.addListener(WayStoneCompat::onWayStoneTeleport$Post);
        }
    }
    public static void onWayStoneTeleport$Pre(WaystoneTeleportEvent.@NotNull Pre event) {
        Entity telEntity = event.getContext().getEntity();
        ILeashHelper.IHolder holderHelper = Services.WORK_SPACE.getLeashHelper().getHolderHelper(telEntity);
        Set<Entity> allLeashedEntities = holderHelper.getAllLeashedEntities();
        if(!SLPGameruleRegistry.getGameruleBoolValue(telEntity.level(), TeleportWithLeashedEntities.ID)) {
            holderHelper.getAllLeashedEntities();
            return;
        }
        ServerLevel level = event.getContext().getDestination().getLevel();
        Vec3 destination = event.getContext().getDestination().getLocation();
        Set<Pair<Entity, OriginalState>> set = new HashSet<>();
        for (Entity beLeashedEntity : allLeashedEntities) {
            // --- 保存状态快照 ---
            if (MinecraftForge.EVENT_BUS.post(new SuperLeadRopeEvent.teleportWithHolder(beLeashedEntity, telEntity, beLeashedEntity.level(), level, beLeashedEntity.position(), destination)))
                continue;
            Pose originalPose = beLeashedEntity.getPose();
            boolean originalIsSprinting = beLeashedEntity.isSprinting();
            float originalYaw = beLeashedEntity.getYRot();
            float originalPitch = beLeashedEntity.getXRot();
            Vec3 originalDeltaMovement = beLeashedEntity.getDeltaMovement();

            AtomicReference<LeashInfo> originalLeashInfo = new AtomicReference<>();
            LeashDataInnerAPI.getLeashData(beLeashedEntity).ifPresent(data -> {
                originalLeashInfo.set(data.getLeashInfo(telEntity).orElse(null));
                data.removeLeash(telEntity);
            });


            // --- 保存骑乘关系（可修改列表） ---
            RidingRelationship originalRidingRelationship = RidingSaver.save(beLeashedEntity, true);

            // --- 解除骑乘 ---
            List<Entity> allPassengers = RidingFinder.getEntityFromRidingShip(originalRidingRelationship, level::getEntity);
            RidingDismounts.dismountEntities(allPassengers);
            set.add(Pair.of(beLeashedEntity, new OriginalState(
                            originalPose,
                            originalIsSprinting,
                            originalYaw,
                            originalPitch,
                            originalDeltaMovement,
                            originalLeashInfo.get(),
                            originalRidingRelationship
                    )
                )
            );
        }
        tempLeashMap.put(telEntity.getUUID(), set);
    }
    public static void onWayStoneTeleport$Post(WaystoneTeleportEvent.@NotNull Post event) {
        Entity telEntity = event.getContext().getEntity();
        ServerLevel serverLevel = event.getContext().getDestination().getLevel();
        Vec3 destination = event.getContext().getDestination().getLocation();
        Set<Pair<Entity, OriginalState>> set = tempLeashMap.get(telEntity.getUUID());
        Set<UUID> shouldBeRemoved = new HashSet<>();
        if (set != null) {
            HashSet<Pair<Entity, OriginalState>> newSet = new HashSet<>();
            // --- 传送实体及乘客 ---
            for (Pair<Entity, OriginalState> entityPair : set) {
                Entity beLeashedEntity = entityPair.first;
                Entity newEntity = TeleportUtil.teleportEntity(beLeashedEntity, serverLevel, destination, beLeashedEntity.getDirection());
                if (!beLeashedEntity.getUUID().equals(newEntity.getUUID())){
                    uuidMap.put(beLeashedEntity.getUUID(), newEntity.getUUID());
                }
                newSet.add(Pair.of(newEntity, entityPair.second));
            }
            for (Pair<Entity, OriginalState> entityPair : newSet) {
                // --- 恢复状态 ---
                Entity beLeashedEntity = entityPair.first;
                OriginalState originalState = entityPair.second;
                LeashStateInnerAPI.getLeashState(beLeashedEntity).ifPresent(LeashSyncManager.State::track);
                LeashDataInnerAPI.getLeashData(beLeashedEntity).ifPresent(LeashSyncManager.Data::track);
                beLeashedEntity.setDeltaMovement(originalState.deltaMovement);
                beLeashedEntity.setPose(originalState.pose);
                beLeashedEntity.setSprinting(originalState.isSprinting);
                // --- 将holder替换 ---
                LeashInfo leashInfo = Optional.ofNullable(originalState.leashInfo)
                        .orElse(LeashInfo.EMPTY);
                if (leashInfo.holderUUIDOpt().isPresent() && uuidMap.containsKey(leashInfo.holderUUIDOpt().get())) {
                    leashInfo.transferHolder(beLeashedEntity);
                    shouldBeRemoved.add(leashInfo.holderUUIDOpt().get());
                }
                LeashDataInnerAPI.LeashOperations.attachWithInfo(beLeashedEntity, telEntity, leashInfo);
                // --- 重新应用骑乘关系，仅保留白名单根载具 ---
                uuidMap.forEach((oldUUID, newUUID) -> {
                    int andReplaceAll = originalState.ridingRelationship.findAndReplaceAll(oldUUID, newUUID);
                    if (andReplaceAll != 0) shouldBeRemoved.add(oldUUID);
                });
                RidingRelationship filteredRelationship = RidingSaver.filterByWhitelistRoot(originalState.ridingRelationship);
                RidingApplier.applyRidingRelationship(filteredRelationship, serverLevel::getEntity);
            }
            shouldBeRemoved.forEach(uuidMap::remove);
            tempLeashMap.remove(telEntity.getUUID());
        }
    }
}
