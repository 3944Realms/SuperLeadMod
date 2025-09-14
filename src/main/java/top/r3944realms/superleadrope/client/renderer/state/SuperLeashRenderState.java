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

package top.r3944realms.superleadrope.client.renderer.state;

import net.minecraft.world.phys.Vec3;

public record SuperLeashRenderState(
        Vec3 startPos,          // 当前帧起点位置
        Vec3 endPos,            // 当前帧终点位置
        Vec3 lastStartPos,      // 上一帧起点位置(用于摆动计算)
        Vec3 lastEndPos,        // 上一帧终点位置(用于摆动计算)
        float tension,         // 张力强度(0.0-1.0)
        float stretchRatio,    // 拉伸比例(1.0=正常长度)
        boolean isCritical,    // 是否处于临界断裂状态
        int keepLeashTicks,    // 剩余缓冲Tick
        int color,             // 颜色(根据状态变化)
        float thickness,      // 线宽(根据张力变化)
        float swingAngle,     // 当前摆动角度(弧度)
        float swingSpeed,      // 摆动速度(弧度/tick)
        float maxDistance      // 最大距离
) {
    // 预定义颜色常量
    public static final int COLOR_NORMAL = 0xFF6B4E2E; // 深棕色（木绳色，温暖自然）
    public static final int COLOR_TENSION = 0xFFD9A066; // 黄色偏橙（张力稍高时微亮）
    public static final int COLOR_CRITICAL = 0xFFB03A2E; // 暗红色（即将断裂，警告色）

    /**
     * 计算当前帧的摆动偏移量(用于波浪效果)
     * @param progress 沿绳子的进度(0.0-1.0)
     * @param partialTicks 部分tick时间
     * @return 垂直于绳子方向的偏移向量
     */
    public Vec3 getSwingOffset(float progress, float partialTicks) {
        // 基于正弦波的摆动效果
        float angle = swingAngle + swingSpeed * partialTicks;
        float offsetMagnitude = (float) (Math.sin(angle + progress * Math.PI * 2) *
                thickness * 2.0 * tension);

        // 计算垂直于绳子方向的向量
        Vec3 ropeDir = endPos.subtract(startPos).normalize();
        Vec3 perpendicular = new Vec3(-ropeDir.z, 0, ropeDir.x).normalize();
        return perpendicular.scale(offsetMagnitude);
    }
}