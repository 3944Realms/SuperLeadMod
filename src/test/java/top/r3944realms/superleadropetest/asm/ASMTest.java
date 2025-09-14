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

package top.r3944realms.superleadropetest.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ASMTest {
    public static void main(String[] args) throws Exception {
        Path jarPath = Paths.get("G:\\WhimsyMod\\SuperLeadRope\\build\\moddev\\artifacts\\forge-1.20.1-47.3.4-merged.jar");
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            JarEntry entry = jar.getJarEntry("net/minecraft/client/renderer/entity/MobRenderer.class");
            try (InputStream in = jar.getInputStream(entry)) {
                byte[] classBytes = in.readAllBytes();

                ClassReader reader = new ClassReader(classBytes);
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, 0);

                System.out.println("Methods in MobRenderer:");
                classNode.methods.forEach(m -> System.out.println(" - " + m.name + m.desc));
            }
        }
    }
}
