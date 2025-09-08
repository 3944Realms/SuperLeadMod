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

package top.r3944realms.superleadrope.core.punishment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import org.jetbrains.annotations.Nullable;

/**
 * 定义物品的惩罚方式
 *
 * @param strength     威力（爆炸用 / 效果用）
 * @param affectOthers 是否影响其他实体
 */
public record PunishmentDefinition(PunishmentDefinition.Type type, float strength,
                                   boolean affectOthers) {

    public static final PunishmentDefinition DEFAULT = new PunishmentDefinition(Type.LIGHTNING, 0, false);

    public enum Type {
        LIGHTNING,   // 雷劈
        EXPLOSION,   // 爆炸
        EFFECT       // 给予负面效果
    }
    /** 序列化到网络 */
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeEnum(this.type);
        buf.writeFloat(this.strength);
        buf.writeBoolean(this.affectOthers);
    }

    /** 从网络反序列化 */
    public static PunishmentDefinition fromNetwork(FriendlyByteBuf buf) {
        Type type = buf.readEnum(Type.class);
        float strength = buf.readFloat();
        boolean affectOthers = buf.readBoolean();
        return new PunishmentDefinition(type, strength, affectOthers);
    }
    /**
     * 执行惩罚
     */
    public void execute(ServerPlayer target, DamageSource cause) {
        execute(target, cause, null);
    }
    public void execute(ServerPlayer target, DamageSource cause,@Nullable Component actionMessage) {
        ServerLevel level = (ServerLevel) target.level();
        switch (type) {
            case LIGHTNING -> {
                LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                bolt.setPos(target.getX(), target.getY(), target.getZ());
                bolt.setVisualOnly(true);
                if(actionMessage != null) target.displayClientMessage(actionMessage, true);
                level.addFreshEntity(bolt);
                target.hurt(cause, Float.MAX_VALUE);
            }
            case EXPLOSION -> {

            }
            case EFFECT -> {

            }
        }
    }
}