## Basic Command Structure

### Main Command
```
/slp leashdata [subcommand]
```

### Command Permissions
Requires OP permissions (permission level 2)

## Subcommand Categories

### 1. GET Commands - Retrieve Information

**Get leash data for entities:**
```
/slp leashdata get data <targets>
```
- `<targets>`: Target entities (selector)

**Get detailed info for a single entity:**
```
/slp leashdata get info <target>
```
- `<target>`: Target entity (selector)

### 2. ADD Commands - Add Leashes

**Add entity leash:**
```
/slp leashdata add <targets> <holder> [maxDistance] [elasticDistanceScale] [keepTicks] [reserved]
```
- `<targets>`: Entities to be leashed
- `<holder>`: Holder entity
- `[maxDistance]`: Maximum distance (default value range)
- `[elasticDistanceScale]`: Elastic distance scale (default value range)
- `[keepTicks]`: Keep duration (game ticks, ≥0)
- `[reserved]`: Reserved field (string)

**Add block leash:**
```
/slp leashdata add <targets> block <pos> [maxDistance] [elasticDistanceScale] [keepTicks] [reserved]
```
- `<pos>`: Block position
- Other parameters same as above

### 3. REMOVE Commands - Remove Leashes

**Remove specific entity leash:**
```
/slp leashdata remove <targets> <holder>
```
- `<targets>`: Target entities
- `<holder>`: Holder entity

**Remove block leash:**
```
/slp leashdata remove <targets> block <pos>
```
- `<pos>`: Block position

**Batch removal:**
```
/slp leashdata remove <targets> all               # Remove all leashes
/slp leashdata remove <targets> holders          # Remove all entity leashes
/slp leashdata remove <targets> knots            # Remove all block leashes
```

### 4. TRANSFER Commands - Transfer Leashes

**Transfer from entity to entity:**
```
/slp leashdata transfer <targets> <from> <to> [reserved]
```
- `<from>`: Original holder
- `<to>`: New holder

**Transfer from block to entity:**
```
/slp leashdata transfer <targets> fromBlock <fromPos> <to> [reserved]
```
- `<fromPos>`: Original block position
- `<to>`: New holder entity

**Transfer from block to block:**
```
/slp leashdata transfer <targets> fromBlock <fromPos> toBlock <toPos> [reserved]
```
- `<fromPos>`: Original block position
- `<toPos>`: New block position

### 5. SET Commands - Set Properties

**Set static properties (applies to all leashes):**
```
/slp leashdata set <targets> static maxDistance reset          # Reset max distance
/slp leashdata set <targets> static maxDistance <value>        # Set max distance
/slp leashdata set <targets> static elasticDistanceScale reset # Reset elastic scale
/slp leashdata set <targets> static elasticDistanceScale <value> # Set elastic scale
```

**Set entity leash properties:**
```
# Set max distance
/slp leashdata set <targets> entity maxDistance <holder> [distance] [keepTicks] [reserved]
/slp leashdata set <targets> entity maxDistance [distance] [keepTicks] [reserved]  # All holders

# Set elastic distance scale
/slp leashdata set <targets> entity elasticDistanceScale <holder> [scale] [keepTicks] [reserved]
/slp leashdata set <targets> entity elasticDistanceScale [scale] [keepTicks] [reserved]  # All holders
```

**Set block leash properties:**
```
# Set max distance
/slp leashdata set <targets> block <pos> maxDistance [distance] [keepTicks] [reserved]
/slp leashdata set <targets> block maxDistance [distance] [keepTicks] [reserved]  # All knots

# Set elastic distance scale
/slp leashdata set <targets> block <pos> elasticDistanceScale [scale] [keepTicks] [reserved]
/slp leashdata set <targets> block elasticDistanceScale [scale] [keepTicks] [reserved]  # All knots
```

### 6. APPLY FORCES Command - Apply Physics Forces

**Apply leash forces to entities:**
```
/slp leashdata applyForces <targets>
```

## Parameter Descriptions

### Selector Parameters:
- `<targets>`: Entity selector (e.g., `@e[type=!player]`, `@a`, `@p`)
- `<holder>`, `<from>`, `<to>`: Single entity selector

### Numerical Parameters:
- `maxDistance`: Maximum distance (`Double`, range: LeashConfigManager.MAX_DISTANCE_MIN_VALUE to MAX_DISTANCE_MAX_VALUE)
- `elasticDistanceScale`: Elastic distance scale (`Double`, range: LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE to ELASTIC_DISTANCE_MAX_VALUE)
- `keepTicks`: Keep duration (`Integer`, ≥0)

### Other Parameters:
- `reserved`: Reserved field (`String`)
- `<pos>`, `<fromPos>`, `<toPos>`: Block position coordinates

## Usage Examples

1. **Leash all sheep to player:**
   ```
   /slp leashdata add @e[type=sheep] @p 10.0 0.8 600 "farm"
   ```

2. **Check player's leash status:**
   ```
   /slp leashdata get info @p
   ```

3. **Transfer leash from fence post to player:**
   ```
   /slp leashdata transfer @e[type=sheep] fromBlock 100 64 100 @p
   ```

4. **Set max distance for all leashes:**
   ```
   /slp leashdata set @e[type=sheep] static maxDistance 15.0
   ```

## Configuration Defaults
- Max distance range: `LeashConfigManager.MAX_DISTANCE_MIN_VALUE` to `MAX_DISTANCE_MAX_VALUE`
- Elastic scale range: `LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE` to `ELASTIC_DISTANCE_MAX_VALUE`

## Important Notes
1. All commands require OP permissions (permission level 2)
2. Block leashes require SuperLeashKnotEntity to exist at the target position
3. Transfer operations do not affect the source entity itself (to prevent loops)
4. Display results are limited to showing up to 4 entities (shows ellipsis for more)