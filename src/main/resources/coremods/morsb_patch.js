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

var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

function initializeCoreMod() {
    return {
        'leash_render': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.entity.MobRenderer',
                'methodName': ASMAPI.mapMethod('m_5523_'), // shouldRender
                'methodDesc': '(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z'
            },
            'transformer': function(method) {
                var insns = method.instructions;

                // 寻找具体的 ICONST_0 位置
                for (var i = 0; i < insns.size(); i++) {
                    var insn = insns.get(i);

                    // 寻找 L4 标签后的 ICONST_0 -> IRETURN 序列
                    if (insn.getOpcode() === Opcodes.ICONST_0) {
                        var nextInsn = insns.get(i + 1);
                        if (nextInsn && nextInsn.getOpcode() === Opcodes.IRETURN) {
                            // 找到目标位置，插入我们的钩子调用
                            var newInstructions = ASMAPI.listOf(
                                new VarInsnNode(Opcodes.ALOAD, 1),  // 加载 Mob 参数 (livingEntity)
                                new VarInsnNode(Opcodes.ALOAD, 2),  // 加载 Frustum 参数 (camera)
                                new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    'top/r3944realms/superleadrope/core/hook/LeashRenderHook',
                                    'shouldRenderExtra',
                                    '(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/culling/Frustum;)Z',
                                    false
                                )
                            );

                            // 在 ICONST_0 之前插入新指令，然后移除 ICONST_0
                            method.instructions.insertBefore(insn, newInstructions);
                            method.instructions.remove(insn);

                            // 只需要修改这一个地方
                            break;
                        }
                    }
                }

                return method;
            }
        }
    };
}