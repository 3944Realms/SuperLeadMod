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
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");

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
                var instructions = method.instructions;
                var found = false;

                // 查找所有 return 语句
                var returnInstructions = [];
                for (var i = 0; i < instructions.size(); i++) {
                    var insn = instructions.get(i);
                    if (insn.getOpcode() === Opcodes.IRETURN) {
                        returnInstructions.push({
                            index: i,
                            insn: insn
                        });
                    }
                }

                if (returnInstructions.length > 0) {
                    // 在最后一个 return 语句前插入检查（通常这是返回 false 的地方）
                    var lastReturn = returnInstructions[returnInstructions.length - 1];

                    // 在 return 前插入我们的检查
                    var hookLabel = new LabelNode();
                    var returnLabel = new LabelNode();

                    var checkInstructions = ASMAPI.listOf(
                        // 检查钩子
                        new VarInsnNode(Opcodes.ALOAD, 1),  // 加载 Mob 参数
                        new VarInsnNode(Opcodes.ALOAD, 2),  // 加载 Frustum 参数
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'top/r3944realms/superleadrope/core/hook/LeashRenderHook',
                            'shouldRenderExtra',
                            '(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/culling/Frustum;)Z',
                            false
                        ),
                        // 如果钩子返回 true，跳转到返回 true
                        new JumpInsnNode(Opcodes.IFNE, hookLabel),
                        // 否则执行原有的 return（可能是 false）
                        new JumpInsnNode(Opcodes.GOTO, returnLabel),

                        // 钩子返回 true 的情况
                        hookLabel,
                        new InsnNode(Opcodes.ICONST_1),
                        new InsnNode(Opcodes.IRETURN),

                        // 原有 return 的标签
                        returnLabel
                    );

                    instructions.insertBefore(lastReturn.insn, checkInstructions);
                    found = true;
                    ASMAPI.log("INFO", "成功在 return 前插入钩子检查");
                }

                if (!found) {
                    // 更简单的方案：直接在方法末尾添加
                    var hookLabel = new LabelNode();
                    var endLabel = new LabelNode();

                    // 在方法开始处添加跳转到检查的标签
                    var startInstructions = ASMAPI.listOf(
                        new JumpInsnNode(Opcodes.GOTO, endLabel),

                        // 钩子检查部分
                        hookLabel,
                        new VarInsnNode(Opcodes.ALOAD, 1),
                        new VarInsnNode(Opcodes.ALOAD, 2),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'top/r3944realms/superleadrope/core/hook/LeashRenderHook',
                            'shouldRenderExtra',
                            '(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/culling/Frustum;)Z',
                            false
                        ),
                        new InsnNode(Opcodes.IRETURN),

                        endLabel
                    );

                    instructions.insert(instructions.getFirst(), startInstructions);

                    // 在所有 return false 前跳转到钩子检查
                    for (var i = 0; i < instructions.size(); i++) {
                        var insn = instructions.get(i);
                        if (insn.getOpcode() === Opcodes.ICONST_0) {
                            var next = instructions.get(i + 1);
                            if (next && next.getOpcode() === Opcodes.IRETURN) {
                                // 将 return false 改为跳转到钩子检查
                                instructions.set(insn, new JumpInsnNode(Opcodes.GOTO, hookLabel));
                            }
                        }
                    }
                }

                return method;
            }
        }
    };
}