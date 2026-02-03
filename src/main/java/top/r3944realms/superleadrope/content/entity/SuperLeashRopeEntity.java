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

package top.r3944realms.superleadrope.content.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.content.item.SuperLeadRopeItem;
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;
import top.r3944realms.superleadrope.util.nbt.NBTReader;
import top.r3944realms.superleadrope.util.nbt.NBTWriter;

import java.util.Objects;
import java.util.UUID;

/**
 * The type Super leash rope entity.
 */
public class SuperLeashRopeEntity extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_TARGET = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_THROWER_ID = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_RETURNING = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_ROTATION = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_IS_SHOOT_BY_DISPENSE = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> DATA_DISPENSE_POS = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> DATA_ENCHANTMENT_LEVEL = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_PRISONER_CURSE = SynchedEntityData.defineId(SuperLeashRopeEntity.class, EntityDataSerializers.BOOLEAN);
    // 常量
    private static final int MAX_LIFETIME = 200; // 10秒寿命
    private static final float MAX_RANGE = 24.0F; // 最大射程
    private static final float ROPE_SPEED = 0.8F; // 抛掷速度
    private static final float RETURN_SPEED = 2.0F; // 返回速度
    private static final float ROTATION_SPEED = 20.0F; // 旋转速度
    private float chargePower = 0.5F;
    // 实体状态
    private UUID throwerUUID;


    /**
     * Gets rope item.
     *
     * @return the rope item
     */
    public ItemStack getRopeItem() {
        return ropeItem;
    }

    /**
     * Sets level.
     *
     * @param level the level
     */
    public void setLevel(int level) {
        this.entityData.set(DATA_ENCHANTMENT_LEVEL, Math.max(1, level));
    }

    /**
     * Has prisoner curse boolean.
     *
     * @return the boolean
     */
    public boolean hasPrisonerCurse() {
        return this.entityData.get(DATA_HAS_PRISONER_CURSE);
    }

    /**
     * Is shoot by dispenser boolean.
     *
     * @return the boolean
     */
    public boolean isShootByDispenser() {
        return this.entityData.get(DATA_IS_SHOOT_BY_DISPENSE);
    }

    /**
     * Gets dispenser pos.
     *
     * @return the dispenser pos
     */
    public BlockPos getDispenserPos() {
        return this.entityData.get(DATA_DISPENSE_POS);
    }

    /**
     * Gets enchantment level.
     *
     * @return the enchantment level
     */
    public int getEnchantmentLevel() {
        return this.entityData.get(DATA_ENCHANTMENT_LEVEL);
    }

    /**
     * Sets charge power.
     *
     * @param chargePower the charge power
     */
    public void setChargePower(float chargePower) {
        this.chargePower = chargePower;
    }


    /**
     * Has target boolean.
     *
     * @return the boolean
     */
    public boolean hasTarget() {
        return this.entityData.get(DATA_HAS_TARGET);
    }

    /**
     * Is returning boolean.
     *
     * @return the boolean
     */
    public boolean isReturning() {
        return this.entityData.get(DATA_RETURNING);
    }

    /**
     * Gets rotation.
     *
     * @return the rotation
     */
    public float getRotation() {
        return this.entityData.get(DATA_ROTATION);
    }

    /**
     * The Pickup.
     */
    public AbstractArrow.Pickup pickup;
    private ItemStack ropeItem = ItemStack.EMPTY;
    private Vec3 initialPosition;
    private boolean stuckInBlock = false;
    private BlockPos stuckBlockPos;
    private Direction stuckDirection;

    /**
     * Instantiates a new Super leash rope entity.
     *
     * @param entityType the entity type
     * @param level      the level
     */
    public SuperLeashRopeEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.setNoGravity(true);
    }

    /**
     * Instantiates a new Super leash rope entity.
     *
     * @param level     the level
     * @param thrower   the thrower
     * @param itemStack the item stack
     */
    public SuperLeashRopeEntity(Level level, LivingEntity thrower, @NotNull ItemStack itemStack) {
        this(SLPEntityTypes.SUPER_LEASH_ROPE.get(), level);
        this.setNoGravity(true);
        this.setThrower(thrower);
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.throwerUUID = thrower.getUUID();
        this.ropeItem = itemStack.copy();
        this.initialPosition = thrower.position();
        this.entityData.set(DATA_HAS_PRISONER_CURSE, SuperLeadRopeItem.hasPrisonerCurse(itemStack));
    }

    /**
     * Instantiates a new Super leash rope entity.
     *
     * @param level        the level
     * @param dispenserPos the dispenser pos
     * @param itemStack    the item stack
     */
    public SuperLeashRopeEntity(Level level, BlockPos dispenserPos, @NotNull ItemStack itemStack) {
        this(SLPEntityTypes.SUPER_LEASH_ROPE.get(), level);
        this.setNoGravity(true);
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.throwerUUID = null;
        this.ropeItem = itemStack.copy();
        this.initialPosition = null;
        this.entityData.set(DATA_IS_SHOOT_BY_DISPENSE, true);
        this.entityData.set(DATA_DISPENSE_POS, dispenserPos);
        this.entityData.set(DATA_HAS_PRISONER_CURSE, SuperLeadRopeItem.hasPrisonerCurse(itemStack));
    }

    /**
     * Start thrown.
     */
    public void startThrown() {
        if (isShootByDispenser()) {
            BlockPos pos = getDispenserPos();
            if (pos != null) {
                BlockState state = level().getBlockState(pos);

                // 检查是否是发射器
                if (state.getBlock() instanceof DispenserBlock) {
                    // 获取发射方向
                    Direction direction = state.getValue(DispenserBlock.FACING);

                    // 设置发射器前方的位置
                    double spawnX = pos.getX() + 0.5 + direction.getStepX() * 0.7;
                    double spawnY = pos.getY() + 0.5 + direction.getStepY() * 0.7;
                    double spawnZ = pos.getZ() + 0.5 + direction.getStepZ() * 0.7;
                    this.setPos(spawnX, spawnY, spawnZ);

                    // 计算发射方向和速度
                    Vec3 shootDirection = new Vec3(
                            direction.getStepX(),
                            direction.getStepY(),
                            direction.getStepZ()
                    ).normalize();

                    // 使用发射器的默认速度
                    float dispenserSpeed = 1.5F;
                    this.setDeltaMovement(
                            shootDirection.x * dispenserSpeed,
                            shootDirection.y * dispenserSpeed,
                            shootDirection.z * dispenserSpeed
                    );

                    // 设置实体朝向
                    if (direction.getStepY() == 0) {
                        // 水平方向
                        float yaw = -direction.toYRot();
                        this.setYRot(yaw);
                        this.setXRot(0.0F);
                    } else {
                        // 垂直方向
                        this.setYRot(0.0F);
                        this.setXRot(direction == Direction.UP ? -90.0F : 90.0F);
                    }

                    // 播放发射器音效
                    level().playSound(null, pos, SoundEvents.DISPENSER_LAUNCH,
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        } else {
            Entity thrower = getThrower();
            if (thrower != null && tickCount == 0) {
                // 设置初始位置和旋转
                this.setPos(thrower.getX(), thrower.getEyeY() - 0.1, thrower.getZ());
                this.setYRot(thrower.getYRot());
                this.setXRot(thrower.getXRot());

                // 设置初始速度
                Vec3 look = thrower.getLookAngle();
                this.setDeltaMovement(look.x * ROPE_SPEED * chargePower, look.y * ROPE_SPEED * chargePower, look.z * ROPE_SPEED * chargePower);
                if (hasPrisonerCurse()) {
                    LeashDataInnerAPI.LeashOperations.attach(thrower, this, 10d, 1.0d, 200);
                }
            }
        }
    }

    /**
     * Gets thrower.
     *
     * @return the thrower
     */
    @Nullable
    public Entity getThrower() {
        if (this.throwerUUID != null && this.level() instanceof ServerLevel) {
            return ((ServerLevel) this.level()).getEntity(this.throwerUUID);
        }

        int throwerId = this.entityData.get(DATA_THROWER_ID);
        if (throwerId != -1) {
            return this.level().getEntity(throwerId);
        }

        return null;
    }

    /**
     * Sets thrower.
     *
     * @param thrower the thrower
     */
    public void setThrower(@NotNull Entity thrower) {
        this.throwerUUID = thrower.getUUID();
        this.entityData.set(DATA_THROWER_ID, thrower.getId());
    }

    @Override
    public void tick() {
        super.tick();

        // 更新生命周期
        int lifetime = this.entityData.get(DATA_LIFETIME);
        this.entityData.set(DATA_LIFETIME, lifetime + 1);

        // 更新旋转
        float rotation = this.entityData.get(DATA_ROTATION);
        this.entityData.set(DATA_ROTATION, (rotation + ROTATION_SPEED) % 360.0F);

        // 检查是否过期
        if (lifetime >= MAX_LIFETIME) {
            returnToThrower();
            return;
        }

        // 检查距离限制
        if (!hasPrisonerCurse() && this.initialPosition != null && this.position().distanceTo(this.initialPosition) > MAX_RANGE * getEnchantmentLevel() * chargePower) {
            this.startReturning();
        } else if (isShootByDispenser() && this.position().distanceTo(getDispenserPos().getCenter()) > MAX_RANGE * getEnchantmentLevel() * 0.5) {
            this.startReturning();
        } else if (hasPrisonerCurse() && lifetime == MAX_LIFETIME / 2) {
            this.startReturning();
        }

        // 处理不同状态
        if (this.entityData.get(DATA_RETURNING)) {
            this.tickReturning();
        } else if (this.stuckInBlock) {
            this.tickStuck();
        } else {
            this.tickFlying();
        }

        // 移动实体
        this.move(MoverType.SELF, this.getDeltaMovement());

        // 更新位置到客户端
        if (!this.level().isClientSide) {
            this.checkCollisions();
        }
    }

    private void tickFlying() {
        // 应用空气阻力
        Vec3 deltaMovement = this.getDeltaMovement();
        this.setDeltaMovement(deltaMovement.multiply(0.99, 0.99, 0.99));
        if (hasPrisonerCurse()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }
        // 检查是否击中实体
        if (!this.level().isClientSide) {
            this.checkEntityCollisions();
        }
    }

    private void tickReturning() {
        if (isShootByDispenser()) {
            BlockPos dispenser = this.getDispenserPos();

            // 计算返回方向
            Vec3 toDispenser = dispenser.getCenter()
                    .subtract(this.position());

            if (toDispenser.length() < 1.0) {
                // 到达发射器者位置，回收绳索
                this.returnToThrower();
                return;
            }

            // 归一化并设置速度
            Vec3 direction = toDispenser.normalize();
            this.setDeltaMovement(direction.scale(RETURN_SPEED));
        } else {
            Entity thrower = this.getThrower();
            if (thrower == null) {
                this.discard();
                return;
            }
            // 计算返回方向
            Vec3 toThrower = thrower.position().add(0, thrower.getEyeHeight() * 0.5, 0)
                    .subtract(this.position());

            if (toThrower.length() < 1.0) {
                // 到达投掷者位置，回收绳索
                this.returnToThrower();
                return;
            }

            // 归一化并设置速度
            Vec3 direction = toThrower.normalize();
            this.setDeltaMovement(direction.scale(RETURN_SPEED));
        }
        // 如果有目标，一起拉回来
        if (!hasTarget()) {
            if (!this.level().isClientSide) {
                this.checkEntityCollisions();
            }
        }
    }

    private void tickStuck() {
        // 检查是否仍然卡在方块中
        if (this.stuckBlockPos != null && !this.level().isEmptyBlock(this.stuckBlockPos)) {
            // 保持位置
            BlockPos pos = this.stuckBlockPos;
            this.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        } else {
            // 方块被破坏，开始返回
            this.stuckInBlock = false;
            this.startReturning(true);
        }
    }
    private void startReturning(boolean force) {
        if (isReturning() || (!force && (hasPrisonerCurse() && this.entityData.get(DATA_LIFETIME) < MAX_LIFETIME / 2))) {
            return;
        }
        this.entityData.set(DATA_RETURNING, true);
        this.stuckInBlock = false;
    }
    private void startReturning() {
        this.startReturning(false);
    }

    private void returnToThrower() {
        if (isShootByDispenser()) {
            BlockPos pos = getDispenserPos();
            if (level().getBlockEntity(pos) instanceof DispenserBlockEntity dispenserBlockEntity) {
                dispenserBlockEntity.addItem(this.ropeItem);
                if(hasTarget() && getTarget() != null)
                    LeashDataInnerAPI.LeashOperations.detach(getTarget(), this);
            } else {
                ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), this.ropeItem);
                level().addFreshEntity(itemEntity);
            }
        } else {
            Entity thrower = this.getThrower();
            if(hasPrisonerCurse() && thrower != null) {
                LeashDataInnerAPI.LeashOperations.detach(thrower, this);
            }
            if (thrower instanceof Player player) {
                // 如果是玩家，尝试将绳索物品还给他们
                if (pickup != AbstractArrow.Pickup.CREATIVE_ONLY) {
                    if (!player.getInventory().add(this.ropeItem)) {
                        // 如果背包满了，掉落物品
                        player.drop(this.ropeItem, false);
                    } else this.level().playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(),
                            SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 1.0F);
                }

                // 如果套住了实体，将其拉近
                if (hasTarget() && !hasPrisonerCurse()) {
                    Entity target = this.getTarget();
                    if (target != null) {
                        // 将目标传送到玩家附近
                        Vec3 pullPosition = thrower.position()
                                .add(thrower.getLookAngle().scale(2.0))
                                .add(0, 1.0, 0);
                        target.teleportTo(pullPosition.x, pullPosition.y, pullPosition.z);
                        LeashDataInnerAPI.TransferOperations.transfer(target, this, thrower);

                        // 播放拉回声音
                        this.level().playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(),
                                SoundEvents.LEASH_KNOT_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            } else {
                ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), this.ropeItem);
                level().addFreshEntity(itemEntity);
            }
        }


        // 销毁实体
        this.discard();
    }

    private void checkEntityCollisions() {
        // 获取抛掷者
        Entity thrower = this.getThrower();
        if (!isShootByDispenser() && thrower == null) return;

        // 检查与实体的碰撞
        Vec3 start = this.position();
        Vec3 end = start.add(this.getDeltaMovement());

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                this.level(),
                this,
                start,
                end,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0),
                entity -> !entity.isSpectator() && entity.isPickable() && (isShootByDispenser() || entity != thrower)
        );

        if (entityHitResult != null) {
            this.onHitEntity(entityHitResult);
        }
    }

    private void checkCollisions() {
        // 使用射线检测检查碰撞
        Vec3 start = this.position();
        Vec3 end = start.add(this.getDeltaMovement());

        HitResult hitResult = this.level().clip(new ClipContext(
                start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this
        ));

        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onHit(hitResult);
        }
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    @Nullable
    public Entity getTarget() {
        if (!hasTarget()) {
            return null;
        }

        int targetId = this.entityData.get(DATA_TARGET_ID);
        if (targetId != -1) {
            return this.level().getEntity(targetId);
        }

        return null;
    }


    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_LIFETIME, 0);
        this.entityData.define(DATA_HAS_TARGET, false);
        this.entityData.define(DATA_TARGET_ID, -1);
        this.entityData.define(DATA_THROWER_ID, -1);
        this.entityData.define(DATA_RETURNING, false);
        this.entityData.define(DATA_ROTATION, 0.0F);
        this.entityData.define(DATA_IS_SHOOT_BY_DISPENSE, false);
        this.entityData.define(DATA_DISPENSE_POS, BlockPos.ZERO);
        this.entityData.define(DATA_ENCHANTMENT_LEVEL, 1);
        this.entityData.define(DATA_HAS_PRISONER_CURSE, false);
    }
    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putInt("Lifetime", this.entityData.get(DATA_LIFETIME));
        compound.putBoolean("HasTarget", this.entityData.get(DATA_HAS_TARGET));
        compound.putBoolean("Returning", this.entityData.get(DATA_RETURNING));
        compound.putFloat("Rotation", this.entityData.get(DATA_ROTATION));

        if (this.throwerUUID != null) {
            compound.putUUID("Thrower", this.throwerUUID);
        }

        if (!this.ropeItem.isEmpty()) {
            compound.put("RopeItem", this.ropeItem.save(new CompoundTag()));
        }

        if (this.initialPosition != null) {
            compound.put("InitialPosition", NBTWriter.writeVec3(initialPosition));
        }

        compound.putBoolean("StuckInBlock", this.stuckInBlock);
        if (this.stuckBlockPos != null) {
            compound.put("StuckPos", NbtUtils.writeBlockPos(stuckBlockPos));
        }

        if (this.stuckDirection != null) {
            compound.putInt("StuckDirection", this.stuckDirection.get3DDataValue());
        }
        compound.putInt("EnchantmentLevel", getEnchantmentLevel());
        compound.putFloat("ChargePower", this.chargePower);
        compound.putBoolean("IsShootByDispenser", isShootByDispenser());
        if (isShootByDispenser()) {
            compound.put("DispenserPos", NbtUtils.writeBlockPos(this.entityData.get(DATA_DISPENSE_POS)));
        }
        compound.putBoolean("HasPrisonerCurse", hasPrisonerCurse());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.entityData.set(DATA_LIFETIME, compound.getInt("Lifetime"));
        this.entityData.set(DATA_HAS_TARGET, compound.getBoolean("HasTarget"));
        this.entityData.set(DATA_RETURNING, compound.getBoolean("Returning"));
        this.entityData.set(DATA_ROTATION, compound.getFloat("Rotation"));
        this.entityData.set(DATA_HAS_PRISONER_CURSE, compound.getBoolean("HasPrisonerCurse"));

        if (compound.hasUUID("Thrower")) {
            this.throwerUUID = compound.getUUID("Thrower");
        }

        if (compound.contains("RopeItem")) {
            this.ropeItem = ItemStack.of(compound.getCompound("RopeItem"));
        }

        if (compound.contains("InitialPosition")) {
            this.initialPosition = NBTReader.readVec3(compound.getCompound("InitialPosition"));
        }

        this.stuckInBlock = compound.getBoolean("StuckInBlock");
        if (compound.contains("StuckPos")) {
            this.stuckBlockPos = NbtUtils.readBlockPos(compound.getCompound("StuckPos"));
        }

        if (compound.contains("StuckDirection")) {
            this.stuckDirection = Direction.from3DDataValue(compound.getInt("StuckDirection"));
        }
        if (compound.contains("EnchantmentLevel")) {
            this.entityData.set(DATA_ENCHANTMENT_LEVEL, compound.getInt("EnchantmentLevel"));
        }
        if (compound.contains("ChargePower")) {
            this.chargePower = compound.getFloat("ChargePower");
        }
        if (compound.contains("IsShootByDispenser")) {
            this.entityData.set(DATA_IS_SHOOT_BY_DISPENSE, compound.getBoolean("IsShootByDispenser"));
        }
        if (compound.contains("DispenserPos")) {
            this.entityData.set(DATA_DISPENSE_POS, NbtUtils.readBlockPos(compound.getCompound("DispenserPos")));
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        this.startReturning();
        return false;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) result;
            BlockPos blockPos = blockHit.getBlockPos();
            if (SuperLeashKnotEntity.isSupportBlock(this.level().getBlockState(blockPos))) {
                Entity thrower = getThrower();
                if (!isShootByDispenser() && hasPrisonerCurse() && thrower != null) {
                    if (LeashDataInnerAPI.LeashOperations. detach(thrower, this)) {
                        LeashDataInnerAPI.LeashOperations. attach(thrower, SuperLeashKnotEntity.getOrCreateKnot(level(), blockPos));
                    }
                }
                if (hasTarget()) {
                    Entity target = getTarget();
                    if (target != null) {
                        if (LeashDataInnerAPI.LeashOperations. detach(target, this)) {
                            LeashDataInnerAPI.LeashOperations. attach(target, SuperLeashKnotEntity.getOrCreateKnot(level(), blockPos));
                        }
                        entityData.set(DATA_HAS_TARGET, false);
                        entityData.set(DATA_TARGET_ID, -1);
                    }
                }
                this.startReturning(true);
                return;
            }
            // 卡在方块中
            this.stuckInBlock = true;
            this.stuckBlockPos = blockPos;
            this.stuckDirection = blockHit.getDirection();

            // 停止移动
            this.setDeltaMovement(Vec3.ZERO);

            // 播放声音
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.LEASH_KNOT_PLACE, SoundSource.PLAYERS, 0.5F, 1.0F);

            // 一段时间后开始返回
            Objects.requireNonNull(this.level().getServer()).execute(() -> {
                if (this.isAlive()) {
                    this.startReturning(true);
                }
            });
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        Entity resultEntity = result.getEntity();

        Entity thrower = getThrower();
        if (resultEntity instanceof SuperLeashKnotEntity knot && hasTarget() ) {
            Entity target = getTarget();
            if(target != null) {
                if (LeashDataInnerAPI.LeashOperations. detach(target, this)) {
                    LeashDataInnerAPI.LeashOperations. attach(target, knot);
                }
                this.entityData.set(DATA_HAS_TARGET, false);
                this.entityData.set(DATA_TARGET_ID, -1);
                return;
            }
            return;
        }

        if ((!isShootByDispenser() && (resultEntity == thrower || LeashDataInnerAPI.QueryOperations.isLeashedBy(resultEntity, thrower))) || !LeashDataInnerAPI.QueryOperations.canBeLeashed(resultEntity)) {
            return;
        }
        if (!isShootByDispenser() && hasPrisonerCurse() && thrower != null) {
            if (LeashDataInnerAPI.LeashOperations. detach(thrower, this)) {
                LeashDataInnerAPI.LeashOperations. attach(thrower, resultEntity);
            }
        }
        // 套住实体
        this.entityData.set(DATA_HAS_TARGET, true);
        this.entityData.set(DATA_TARGET_ID, resultEntity.getId());
        if (LeashDataInnerAPI.QueryOperations.canBeLeashed(resultEntity)) {
            LeashDataInnerAPI.LeashOperations.attach(resultEntity, this, 10d, 1.0d, 200);
        }
        // 停止移动
        this.setDeltaMovement(Vec3.ZERO);

        // 播放套住的声音
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.LEASH_KNOT_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);

        // 立即开始返回
        this.startReturning();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * The type Super leash rope item behavior.
     */
    public static class SuperLeashRopeItemBehavior extends OptionalDispenseItemBehavior {
        /**
         * The constant INSTANCE.
         */
        public static final SuperLeashRopeItemBehavior INSTANCE = new SuperLeashRopeItemBehavior();
        @Override
        protected @NotNull ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
            ServerLevel serverLevel = source.getLevel();
            int enchantmentLevel = SuperLeadRopeItem.getRingTossEnchantmentLevel(stack);
            if (enchantmentLevel > 0) {
                stack.hurtAndBreak(10, FakePlayerFactory.getMinecraft(serverLevel), p -> {});
                SuperLeashRopeEntity leashRope = new SuperLeashRopeEntity(serverLevel, source.getPos(), stack);
                leashRope.setLevel(enchantmentLevel);
                leashRope.startThrown();
                serverLevel.addFreshEntity(leashRope);
                return ItemStack.EMPTY;
            } else return stack;
        }
    }
}
