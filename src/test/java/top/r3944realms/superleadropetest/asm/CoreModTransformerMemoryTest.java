/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR 阿 PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadropetest.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;

public class CoreModTransformerMemoryTest {

    // 1️⃣ 静态类替代 Minecraft 类
    public static class TestMob {}
    public static class TestFrustum {}

    // Hook 模拟
    public static class LeashRenderHook {
        public static boolean shouldRenderExtra(TestMob mob, TestFrustum frustum) {
            System.out.println("[Hook] shouldRenderExtra called with Mob=" + mob + ", Frustum=" + frustum);
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        // 2️⃣ 构造假的 shouldRender 方法
        MethodNode methodNode = new MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC, "shouldRender",
                "(Ltop/r3944realms/superleadrope/core/test/CoreModTransformerMemoryTest$TestMob;" +
                        "Ltop/r3944realms/superleadrope/core/test/CoreModTransformerMemoryTest$TestFrustum;)Z",
                null, null);

        InsnList insns = methodNode.instructions;
        insns.clear(); // 清空原有指令
        // 插入 Hook 调用
        InsnList patch = new InsnList();
        patch.add(new VarInsnNode(Opcodes.ALOAD, 1));
        patch.add(new VarInsnNode(Opcodes.ALOAD, 2));
        patch.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "top/r3944realms/superleadrope/core/test/CoreModTransformerMemoryTest$LeashRenderHook",
                "shouldRenderExtra",
                "(Ltop/r3944realms/superleadrope/core/test/CoreModTransformerMemoryTest$TestMob;" +
                        "Ltop/r3944realms/superleadrope/core/test/CoreModTransformerMemoryTest$TestFrustum;)Z",
                false
        ));
        patch.add(new InsnNode(Opcodes.IRETURN));
        insns.add(patch);

        // 4️⃣ 创建假的 ClassNode
        ClassNode classNode = new ClassNode();
        classNode.version = Opcodes.V1_8;
        classNode.access = Opcodes.ACC_PUBLIC;
        classNode.name = "FakeMobRenderer";
        classNode.superName = "java/lang/Object";
        classNode.methods.add(methodNode);

        // 添加默认构造器 <init>
        MethodNode constructor = new MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        InsnList initInsns = constructor.instructions;
        initInsns.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
        initInsns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
        initInsns.add(new InsnNode(Opcodes.RETURN));
        constructor.maxStack = 1;
        constructor.maxLocals = 1;
        classNode.methods.add(constructor);

        // 5️⃣ 写入字节码
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        byte[] bytes = cw.toByteArray();

        // 6️⃣ 自定义 ClassLoader，直接返回 Class 对象
        ClassLoader loader = new ClassLoader(CoreModTransformerMemoryTest.class.getClassLoader()) {
            public Class<?> defineClassFromBytes(byte[] b) {
                return defineClass("FakeMobRenderer", b, 0, b.length);
            }
        };
        Class<?> clazz = ((Class<?>) loader.getClass().getMethod("defineClassFromBytes", byte[].class).invoke(loader, bytes));

        // 7️⃣ 实例化
        Object rendererInstance = clazz.getDeclaredConstructor().newInstance();
        Method shouldRender = clazz.getMethod("shouldRender", TestMob.class, TestFrustum.class);

        boolean result = (boolean) shouldRender.invoke(rendererInstance, new TestMob(), new TestFrustum());
        System.out.println("[Test] Result of shouldRender (after patch): " + result);

    }
}