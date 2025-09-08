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

package top.r3944realms.superleadrope.core.register;

import top.r3944realms.superleadrope.core.punishment.IObligationCompletion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * IObligationCompletion 注册与反序列化管理器
 */
public class ObligationCompletionRegistry {

    /** ID -> IObligationCompletion 实例 */
    private static final Map<String, IObligationCompletion> REGISTRY = new HashMap<>();

    /**
     * 注册一个 IObligationCompletion 实例
     * @param id 唯一 ID
     * @param completion 实例
     */
    public static void register(String id, IObligationCompletion completion) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("ID cannot be null or empty");
        if (completion == null) throw new IllegalArgumentException("Completion cannot be null");
        REGISTRY.put(id, completion);
    }

    /**
     * 根据 ID 获取 IObligationCompletion 实例
     * @param id ID
     * @return 实例，如果未注册则返回 NONE
     */
    public static IObligationCompletion byId(String id) {
        return REGISTRY.getOrDefault(id, IObligationCompletion.NONE);
    }

    /**
     * 获取只读注册表（用于调试或枚举）
     */
    public static Map<String, IObligationCompletion> getAll() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}