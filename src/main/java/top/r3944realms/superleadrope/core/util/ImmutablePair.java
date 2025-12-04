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

package top.r3944realms.superleadrope.core.util;

import java.util.Objects;

/**
 * The type Immutable pair.
 *
 * @param <F> the type parameter
 * @param <S> the type parameter
 */
public record ImmutablePair<F, S>(F first, S second) {

    /**
     * Of immutable pair.
     *
     * @param <F>    the type parameter
     * @param <S>    the type parameter
     * @param first  the first
     * @param second the second
     * @return the immutable pair
     */
    public static <F, S> ImmutablePair<F, S> of(F first, S second) {
        return new ImmutablePair<>(first, second);
    }

    /**
     * 重写equals方法
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutablePair<?, ?> immutablePair = (ImmutablePair<?, ?>) o;

        if (!Objects.equals(first, immutablePair.first)) return false;
        return Objects.equals(second, immutablePair.second);
    }

    /**
     * 重写toString方法便于调试
     */
    @Override
    public String toString() {
        return "Pair{" + first + ", " + second + "}";
    }
}
