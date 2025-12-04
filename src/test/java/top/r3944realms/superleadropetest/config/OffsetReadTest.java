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

package top.r3944realms.superleadropetest.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OffsetReadTest {
    private static final Pattern OFFSET_PATTERN = Pattern.compile(
            "(?i)(?:vec3|vec3d|vector3|offset)\\s*\\(\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*,\\s*([-+]?[0-9]*\\.?[0-9]+)\\s*\\)\\s*:\\s*\\[\\s*([^]]+?)\\s*]\\s*"
    );
    private static boolean isValidEntityRefFormat(String s) {
        if (s.startsWith("#")) {
            String body = s.substring(1);
            // 支持 #modid （整个模组）或 #modid:tag_name （标签）
            return body.matches("[a-z0-9_]+(:[a-z0-9_/]+)?");
        }
        // 普通实体 ID: modid:entity_id
        return s.matches("[a-z0-9_]+:[a-z0-9_/]+");
    }
    private static boolean isValidOffsetRefFormat(String s) {
        // 匹配格式: function_name(x,y,z) : [entity_list]
        Matcher matcher = OFFSET_PATTERN.matcher(s);
        if (!matcher.matches()) {
            return false;
        }

        // 检查坐标值是否有效
        try {
            // 组索引 现在坐标在组1、2、3，实体列表在组4
            Double.parseDouble(matcher.group(1));
            Double.parseDouble(matcher.group(2));
            Double.parseDouble(matcher.group(3));

            // 检查实体列表格式
            String entityList = matcher.group(4);
            String[] entities = entityList.split(",");
            for (String entity : entities) {
                if (!isValidEntityRefFormat(entity.trim())) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private static void testCase(String testString, boolean expected) {
        boolean result = isValidOffsetRefFormat(testString);
        String status = result == expected ? "✓ PASS" : "✗ FAIL";
        System.out.printf("%s: %s -> %s (expected: %s)%n",
                status, testString, result, expected);

        if (result) {
            Matcher matcher = OFFSET_PATTERN.matcher(testString);
            if (matcher.matches()) {
                System.out.printf("  解析结果: X=%s, Y=%s, Z=%s, Entities=%s%n%n",
                        matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
            }
        } else {
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 有效测试用例 ===");

        // 基本格式测试
        testCase("vec3(0,0.2,0) : [minecraft:bee]", true);
        testCase("Vec3(0, 0.2, 0) : [minecraft:bee]", true);
        testCase("VEC3(0,0.2,0) : [minecraft:bee]", true);

        // 不同函数名测试
        testCase("vec3d(0,0.2,0) : [minecraft:bee]", true);
        testCase("Vector3(0,0.2,0) : [minecraft:bee]", true);
        testCase("offset(0,0.2,0) : [minecraft:bee]", true);

        // 空格兼容测试
        testCase("vec3( 0 , 0.2 , 0 ) : [ minecraft:bee ]", true);
        testCase("vec3(0,0.2,0) : [minecraft:bee]   ", true);
        testCase("vec3(0, 0.2, 0) : [  minecraft:bee  ]  ", true);

        // 多实体测试
        testCase("vec3(0,1.0,0) : [minecraft:horse, minecraft:donkey]", true);
        testCase("vec3(0,0.5,0) : [#minecraft:boats, #minecraft:minecarts]", true);

        // 标签和模组测试
        testCase("vec3(0,0.5,0) : [#minecraft:boats]", true);
        testCase("vec3(0,0.3,0) : [#minecraft]", true);

        // 负数和小数测试
        testCase("vec3(-1, 1.5, 2.8) : [minecraft:horse]", true);
        testCase("vec3(0.0, -0.5, 1.23) : [minecraft:bee]", true);

        System.out.println("=== 无效测试用例 ===");

        // 错误函数名
        testCase("vector(0,0.2,0) : [minecraft:bee]", false);
        testCase("pos(0,0.2,0) : [minecraft:bee]", false);

        // 格式错误
        testCase("vec3(0,0.2,0) : minecraft:bee]", false); // 缺少左括号
        testCase("vec3(0,0.2,0) : [minecraft:bee", false); // 缺少右括号
        testCase("vec3(0,0.2) : [minecraft:bee]", false);  // 缺少Z坐标

        // 无效坐标
        testCase("vec3(a,b,c) : [minecraft:bee]", false);
        testCase("vec3(0,0.2,invalid) : [minecraft:bee]", false);

        // 无效实体引用
        testCase("vec3(0,0.2,0) : [invalid_entity]", false);
        testCase("vec3(0,0.2,0) : [minecraft:bee, invalid]", false);

        // 缺少冒号
        testCase("vec3(0,0.2,0) [minecraft:bee]", false);

        System.out.println("=== 测试完成 ===");
    }
}
