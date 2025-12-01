## 基础指令结构

### 主要指令
```
/slp leashdata [子命令]
```

### 指令权限
需要 OP 权限（权限等级 2）

## 子命令分类

### 1. GET 命令 - 获取信息

**获取实体拴绳数据：**
```
/slp leashdata get data <targets>
```
- `<targets>`: 目标实体（选择器）

**获取单个实体详细信息：**
```
/slp leashdata get info <target>
```
- `<target>`: 目标实体（选择器）

### 2. ADD 命令 - 添加拴绳

**添加实体拴绳：**
```
/slp leashdata add <targets> <holder> [maxDistance] [elasticDistanceScale] [keepTicks] [reserved]
```
- `<targets>`: 被拴绳实体
- `<holder>`: 持有者实体
- `[maxDistance]`: 最大距离（默认值范围）
- `[elasticDistanceScale]`: 弹性距离比例（默认值范围）
- `[keepTicks]`: 保持时间（游戏刻，≥0）
- `[reserved]`: 保留字段（字符串）

**添加方块拴绳：**
```
/slp leashdata add <targets> block <pos> [maxDistance] [elasticDistanceScale] [keepTicks] [reserved]
```
- `<pos>`: 方块位置
- 其他参数同上

### 3. REMOVE 命令 - 移除拴绳

**移除特定实体拴绳：**
```
/slp leashdata remove <targets> <holder>
```
- `<targets>`: 目标实体
- `<holder>`: 持有者实体

**移除方块拴绳：**
```
/slp leashdata remove <targets> block <pos>
```
- `<pos>`: 方块位置

**批量移除：**
```
/slp leashdata remove <targets> all               # 移除所有拴绳
/slp leashdata remove <targets> holders          # 移除所有实体拴绳
/slp leashdata remove <targets> knots            # 移除所有方块拴绳
```

### 4. TRANSFER 命令 - 转移拴绳

**实体到实体转移：**
```
/slp leashdata transfer <targets> <from> <to> [reserved]
```
- `<from>`: 原持有者
- `<to>`: 新持有者

**方块到实体转移：**
```
/slp leashdata transfer <targets> fromBlock <fromPos> <to> [reserved]
```
- `<fromPos>`: 原方块位置
- `<to>`: 新持有者实体

**方块到方块转移：**
```
/slp leashdata transfer <targets> fromBlock <fromPos> toBlock <toPos> [reserved]
```
- `<fromPos>`: 原方块位置
- `<toPos>`: 新方块位置

### 5. SET 命令 - 设置属性

**设置静态属性（适用于所有拴绳）：**
```
/slp leashdata set <targets> static maxDistance reset          # 重置最大距离
/slp leashdata set <targets> static maxDistance <value>        # 设置最大距离
/slp leashdata set <targets> static elasticDistanceScale reset # 重置弹性比例
/slp leashdata set <targets> static elasticDistanceScale <value> # 设置弹性比例
```

**设置实体拴绳属性：**
```
# 设置最大距离
/slp leashdata set <targets> entity maxDistance <holder> [distance] [keepTicks] [reserved]
/slp leashdata set <targets> entity maxDistance [distance] [keepTicks] [reserved]  # 所有持有者

# 设置弹性距离比例
/slp leashdata set <targets> entity elasticDistanceScale <holder> [scale] [keepTicks] [reserved]
/slp leashdata set <targets> entity elasticDistanceScale [scale] [keepTicks] [reserved]  # 所有持有者
```

**设置方块拴绳属性：**
```
# 设置最大距离
/slp leashdata set <targets> block <pos> maxDistance [distance] [keepTicks] [reserved]
/slp leashdata set <targets> block maxDistance [distance] [keepTicks] [reserved]  # 所有节点

# 设置弹性距离比例
/slp leashdata set <targets> block <pos> elasticDistanceScale [scale] [keepTicks] [reserved]
/slp leashdata set <targets> block elasticDistanceScale [scale] [keepTicks] [reserved]  # 所有节点
```

### 6. APPLY FORCES 命令 - 应用物理力

**对实体应用拴绳力：**
```
/slp leashdata applyForces <targets>
```

## 参数说明

### 选择器参数：
- `<targets>`: 实体选择器（如 `@e[type=!player]`, `@a`, `@p`）
- `<holder>`, `<from>`, `<to>`: 单个实体选择器

### 数值参数：
- `maxDistance`: 最大距离（`Double`，范围：LeashConfigManager.MAX_DISTANCE_MIN_VALUE 到 MAX_DISTANCE_MAX_VALUE）
- `elasticDistanceScale`: 弹性距离比例（`Double`，范围：LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE 到 ELASTIC_DISTANCE_MAX_VALUE）
- `keepTicks`: 保持时间（`Integer`，≥0）

### 其他参数：
- `reserved`: 保留字段（`String`）
- `<pos>`, `<fromPos>`, `<toPos>`: 方块位置坐标

## 使用示例

1. **拴住所有羊到玩家：**
   ```
   /slp leashdata add @e[type=sheep] @p 10.0 0.8 600 "farm"
   ```

2. **查看玩家拴绳状态：**
   ```
   /slp leashdata get info @p
   ```

3. **将拴绳从木桩转移到玩家：**
   ```
   /slp leashdata transfer @e[type=sheep] fromBlock 100 64 100 @p
   ```

4. **设置所有拴绳的最大距离：**
   ```
   /slp leashdata set @e[type=sheep] static maxDistance 15.0
   ```

## 配置默认值
- 最大距离范围：`LeashConfigManager.MAX_DISTANCE_MIN_VALUE` 到 `MAX_DISTANCE_MAX_VALUE`
- 弹性比例范围：`LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE` 到 `ELASTIC_DISTANCE_MAX_VALUE`

## 注意事项
1. 所有指令需要 OP 权限（权限等级 2）
2. 方块拴绳需要在对应位置存在 SuperLeashKnotEntity
3. 转移操作不会作用于来源实体自身（避免循环）
4. 显示结果会限制最多显示 4 个实体（超过会显示省略号）
