#   Super Lead rope mod
#   Copyright (C)  2025  R3944Realms
#   This program is free software: you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation, either version 3 of the License, or
#   (at your option) any later version.
#   This program is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
#   You should have received a copy of the GNU General Public License
#   along with this program.  If not, see <https://www.gnu.org/licenses/>.
import os
import random

# 确保输出目录存在
os.makedirs("dict", exist_ok=True)

# 全局已使用名字集合，保证三类字典互不重复
used_names = set()

def gen_dict(count: int, length: int, filename: str, charset: str):
    """
    生成字典文件：
    - 首字符必须是字母
    - 剩余字符可以是 charset 中任意字符
    - 保证全局唯一
    """
    if length < 1:
        raise ValueError("长度必须至少为 1")

    # 首字符必须是字母
    first_chars = [c for c in charset if c.isalpha()]
    if not first_chars:
        raise ValueError("字符集必须包含至少一个字母作为首字符")

    chars = list(charset)
    max_combinations = len(first_chars) * (len(chars) ** (length - 1))
    if count > max_combinations:
        raise ValueError(f"要求数量 {count} 超过最大可能组合数 {max_combinations}")

    generated = set()
    while len(generated) < count:
        s = random.choice(first_chars)
        s += ''.join(random.choice(chars) for _ in range(length - 1))
        if s in used_names:
            continue  # 跳过已使用的名字
        generated.add(s)
        used_names.add(s)

    with open(filename, "w") as f:
        for s in generated:
            f.write(s + "\n")

    print(f"生成完成，共 {len(generated)} 个写入 {filename}")


if __name__ == "__main__":
    # 字符集定义
    charset_class = "o0OQ"
    charset_member = "i1lI"
    charset_package = "UvuV"

    # 生成类名字典
    gen_dict(count=1000, length=10, filename="dict/class_dict.txt", charset=charset_class)

    # 生成字段/方法字典
    gen_dict(count=1000, length=10, filename="dict/member_dict.txt", charset=charset_member)

    # 生成包名字典
    gen_dict(count=1000, length=10, filename="dict/package_dict.txt", charset=charset_package)
