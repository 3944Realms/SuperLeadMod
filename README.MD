# 🪢 Super Lead Rope / 超级拴绳

## ⚠️ 注意 / Warning
**提示：目前属于早期测试版，如在服务器上使用不保证后续版本兼容，请慎重考虑。**  
**Notice:** This is an **early test release**. So, **compatibility with future versions on servers is not guaranteed — use with caution.**  
建议在正式服务器部署前先在备份或测试服中验证。  
We recommend testing on backups or a development server before deploying to production.

---
**Super Lead Rope** 是一个对原版拴绳进行增强的模组，让玩家能够更灵活地管理和牵引生物。  
相比原版，它不仅更强大，还具备跨维度与跨服的拓展特性。

**Super Lead Rope** is a mod that enhances the vanilla lead, allowing players to manage and leash entities more flexibly.  
Compared to the original, it is not only more powerful but also supports cross-dimension and even cross-server features.

---

## ✨ 功能特色 / Features

### 🔗 多实体拴绳 / Multi-Entity Leash
- 一根绳子可以同时拴多个实体。
- 一个实体也可以被多根绳子拴住，支持复杂连接。

- A single rope can leash multiple entities at the same time.
- One entity can also be leashed by multiple ropes, supporting complex connections.

![The bee is leashed by 3 knots](https://cdn.modrinth.com/data/cached_images/04ab8197835a07376197207539b370eb5fff5921_0.webp)
![More](https://cdn.modrinth.com/data/cached_images/8bef5a7d7129d98acd119e0b11f5f740f86b2907.png)
---

### 📏 远距离牵引 / Long-Distance Tethering
- 原版拴绳在一定距离会断裂。
- 超级拴绳支持更远距离（待做）。
- 像橡皮筋一样拉回目标。在Y轴会有一定的弹性阻尼，保证在生物在Y轴下被牵引不会异常的频繁上下蹦极。

- Vanilla leads break after a certain distance.
- Super Lead Rope supports extended leash distance (WIP).
- Pulls entities back like a rubber band. There is a certain amount of damping on the Y axis to ensure that leashed entities do not bounce up and down excessively when being pulled vertically.

---

### 💾 持久拴绑 / Persistent Leash
- 支持保存拴绳状态，退出/重进游戏不会断开。
- 即使 **holder**（拴绳端点）暂时消失，回来时也能恢复连接。

- Leash states are persistent and won’t break after rejoining the game.
- Even if the **holder** (anchor point) disappears temporarily, connections will be restored when it comes back.

---

### 🌌 跨维度 / 传送 / Cross-Dimension & Teleport
- 目前支持 **传送跟随**：当拴绳实体或玩家通过传送（如下界传送门、指令）时，拴绳目标会一同跟随。

- Currently, supports **teleport-follow**: when a leashed entity or player teleports (e.g. Nether portal, commands), the tethered entities follow along.

---

### 🎨 可视化与材质 / Visuals & Materials
- 比原版拴绳更粗壮，或具有特殊颜色。
- 未来可能支持多种材质（铁链、魔法绳、能量链等）。

- Thicker or colored ropes compared to vanilla leads.
- Future plans include multiple leash materials (iron chains, magical ropes, energy cords, etc.).

---

## 💻 开发 / Develop

### Maven 仓库配置 / Maven Repository Configuration

在您的 `build.gradle` 中添加以下仓库配置：

Add the following repository configuration in your build.gradle:

```gradle
repositories {
    maven {
        name = "LTD Maven"
        url = "https://nexus.bot.leisuretimedock.top/repository/maven-public/"
    }
}
```

### 依赖导入 / Dependency Import

在 `dependencies` 区块中添加 Super Lead Rope 依赖：

Add Super Lead Rope dependency in the dependencies block:

```gradle
dependencies {
    compileOnly 'top.r3944realms.superleadrope:superleadrope:版本号:api@jar'
    runtimeOnly 'top.r3944realms.superleadrope:superleadrope:版本号'
}
```

#### 版本号示例 / Version Example
```groovy
compileOnly 'top.r3944realms.superleadrope:superleadrope:1.20.1-0.0.0.7:api@jar'
runtimeOnly 'top.r3944realms.superleadrope:superleadrope:1.20.1-0.0.0.7'
```
### 快速开始 / Quick Start
```groovy
repositories {
    maven {
        name = "LTD Maven"
        url = "https://nexus.bot.leisuretimedock.top/repository/maven-public/"
    }
}

dependencies {
    compileOnly 'top.r3944realms.superleadrope:superleadrope:1.20.1-0.0.0.7:api@jar'
    runtimeOnly 'top.r3944realms.superleadrope:superleadrope:1.20.1-0.0.0.7'
}
```

---
# 📜 License / 许可证

This project is licensed under the **GNU GPL v3** open source license.  
本项目采用 **GNU GPL v3** 开源协议。

## You are free to: / 您可以自由地：

- **Use** - Use the mod in personal or commercial projects  
  **使用** - 在个人或商业项目中使用本模组
- **Learn** - Study and learn from the source code  
  **学习** - 研究和学习模组的源代码
- **Modify** - Modify the code to suit your needs  
  **修改** - 修改模组代码以满足您的需求
- **Distribute** - Distribute original or modified versions  
  **分发** - 分发原始或修改后的版本

## Under the following conditions: / 在遵循以下条件的前提下：

1. **Open Source Requirement** - If you distribute modified versions, you must open source them under the same GPL v3 license  
   **开源要求** - 如果您分发修改后的版本，必须同样使用GPL v3协议开源
2. **Copyright Notice** - Preserve original copyright and license notices  
   **版权声明** - 保留原始的版权和许可证声明
3. **Source Code Availability** - Provide access to the modified source code  
   **源代码可用** - 提供修改后的源代码访问方式
4. **Same License** - Use the same GPL v3 license  
   **相同许可证** - 使用相同的GPL v3协议

**Full Terms:** [View LICENSE file](LICENSE.GPL3) | [GPL v3 Details](https://www.gnu.org/licenses/gpl-3.0.html)  
**完整条款：** [查看LICENSE文件](LICENSE.GPL3) | [GPL v3详情](https://www.gnu.org/licenses/gpl-3.0.html)
# 🎵 Audio Resources / 音效资源声明

This mod includes supplemental audio assets from **Minecraft (by Mojang Studios / Microsoft)**,
which are **not** covered by the GPL license of this project.  
本模组中包含部分来自 **Minecraft（Mojang Studios / Microsoft）** 的补充音效资源，  
这些资源 **不属于** 本项目 GPL 协议授权的范围。

- Copyright of these audio files belongs to **Mojang Studios / Microsoft**.  
  这些音效文件的版权归 **Mojang Studios / Microsoft** 所有。
- They are provided here solely for compatibility and gameplay purposes.  
  这些音效仅为兼容性和游戏功能提供。
- The **GPL v3 license applies only to the original code and assets of this mod**,  
  and does **not** apply to the bundled Minecraft audio files.  
  **GPL v3 协议仅适用于本模组的原创代码与资源**，不适用于其中包含的 Minecraft 音效文件。