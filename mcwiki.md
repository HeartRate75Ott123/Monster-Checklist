# 怪物清单 / Monster Checklist

> ⚠ **注意**：MC百科 已于 2024 年 11 月起限制 AIGC 内容进入公共编辑区（[公告](https://bbs.mcmod.cn/thread-20183-1-1.html)）。以下文案为参考草稿，**请人工复核修改后**再提交，否则会被编辑员退回。

---

## 概述

怪物清单（Monster Checklist）是一个与 Field Guide 联动的 NeoForge 客户端/服务器兼容模组。它会自动读取 Field Guide 中已解锁的怪物条目，以清单形式分页展示，并在达到指定里程碑阈值时自动发放属性增益奖励。

## 详细内容

### 怪物进度追踪

打开怪物清单 GUI 后，模组通过 Field Guide 的 `ClientCategoryManager.getResolvedCategoryEntries()` 接口获取所有已注册条目，过滤出分类路径包含 `monster` 且核心实体类型为 `EntityType` 的条目。条目按**未解锁 → 已解锁**排序，同状态内按名称字母序排列，每页显示 8 条。

### 里程碑奖励

内置里程碑系统，默认阈值为 5 / 10 / 20 / 35 / 50。当玩家解锁的怪物数量达到任一阈值时，自动触发奖励发放：

- 从属性增益池中随机选取 2 项（若某属性已达最大等级则排除）
- 每项属性提升固定数值（如最大生命值 +2、移动速度 +0.02）
- 通过聊天栏播报里程碑达成消息与具体增益内容

已领取的里程碑通过 `CLAIMED_MILESTONES` 数据附加（Data Attachment）以位掩码形式持久化存储，防止重复发放。

### 属性增益池

| 属性 | 每级提升 | 最大等级 | 说明 |
|------|---------|---------|------|
| 最大生命值 | +2.0 | 10 | |
| 移动速度 | +0.02 | 5 | |
| 幸运 | +1.0 | 5 | |
| 击退抗性 | +0.1 | 5 | |
| 护甲值 | +1.0 | 10 | |
| 护甲韧性 | +1.0 | 10 | |
| 实体交互距离 | +0.5 | 5 | |
| 方块交互距离 | +0.5 | 5 | |
| 挖掘效率 | +2.0 | 5 | |
| 闪避率 | +0.02 | 15 | 有概率完全闪避伤害 |
| 暴击率 | +0.02 | 15 | 暴击时伤害 ×1.5 |
| 精力上限 | +1 | 10 | 需安装 Paraglider |

闪避率与暴击率为模组自定义属性，不依赖原版属性系统。精力上限依赖 Paraglider 的 `VesselContainer` API，未安装 Paraglider 时该增益项自动跳过。

### 触发器

- **自动触发**：通过 Mixin 注入 `PlayerFieldGuideProgress.unlock()` 方法尾部，在 Field Guide 条目解锁瞬间同步检查里程碑
- **登录/重生/切维**：这三种事件也会重新检查并补发错过的里程碑

### 配置

里程碑阈值列表可在游戏内通过 Esc → 模组列表 → Monster Checklist → 配置 界面修改，类型为 `ModConfig.Type.CLIENT`，配置文件位于 `run/config/monster_checklist_mod_1784790200-client.toml`。

### 命令

- `/monsterchecklist add gift`（权限等级 2）—— 测试发放一次属性奖励

## 前置/联动

- **Field Guide**（必要前置）—— 提供怪物条目数据与解锁状态
- **Paraglider**（可选联动）—— 安装后启用精力上限增益项

## 其他信息

- **作者**：Plume Jade
- **协议**：MIT
- **开源地址**：https://github.com/HeartRate75Ott123/Monster-Checklist
- **AI 辅助声明**：本项目使用 AI 工具辅助开发
