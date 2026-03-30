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

package top.r3944realms.superleadrope.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type Fake player judge.
 */
public class FakePlayerJudge {
    private final static Set<Class<?>> fakePlayerClasses = new HashSet<>();

    private static void check(final @NotNull Class<?> fakePlayerClass) throws IllegalArgumentException {
        if(fakePlayerClass.equals(Player.class) || fakePlayerClass.equals(ServerPlayer.class))
            throw new IllegalArgumentException("Player or ServerPlayer class cannot be used as FakePlayer");
    }

    /**
     * Registers fake player.
     *
     * @param fakePlayerClass the fake player class
     */
    public static void registersFakePlayer(final Class<?> @NotNull ...fakePlayerClass) {
        for (Class<?> playerClass : fakePlayerClass) {
            check(playerClass);
        }
        fakePlayerClasses.addAll(List.of(fakePlayerClass));
    }

    /**
     * Is fake player boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    public static boolean isNotFakePlayer(@NotNull Entity entity) {
        if (!(entity instanceof Player)) return true;
        return !fakePlayerClasses.contains(entity.getClass());
    }
}
