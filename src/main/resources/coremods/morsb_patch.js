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

var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");

function initializeCoreMod() {
    return {
        "leash_render_patch": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.renderer.entity.MobRenderer",
                "methodName": "m_5523_",
                "methodDesc": "(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z"
            },
            "transformer": function(method) {
                var insns = method.instructions;

                for (var i = 0; i < insns.size(); i++) {
                    var insn = insns.get(i);
                    if (insn.getOpcode && insn.getOpcode() === Opcodes.ICONST_0) {
                        var next = insns.get(i + 1);
                        if (next && next.getOpcode() === Opcodes.IRETURN) {
                            // 插入调试日志和方法调用
                            insns.insertBefore(insn, ASMAPI.listOf(
                                new VarInsnNode(Opcodes.ALOAD, 1), // Mob
                                new VarInsnNode(Opcodes.ALOAD, 2), // Frustum
                                ASMAPI.buildMethodCall(
                                    'top/r3944realms/superleadrope/core/hook/LeashRenderHook',
                                    null,
                                    ASMAPI.MethodType.STATIC,
                                    'shouldRenderExtra',
                                    '(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/culling/Frustum;)Z',
                                    ASMAPI.MethodCallMode.STATIC
                                )
                            ));
                            // 移除原来的 ICONST_0
                            insns.remove(insn);
                            break;
                        }
                    }
                }
                return method;
            }
        }
    };
}