-- 种子数据：真实优先，只写入代表性明细，页面总量由明细实时聚合

INSERT INTO users (id, username, display_name, avatar_char, signature, target_year, theme, timezone,
                   current_streak_days, longest_streak_days, last_active_on, created_at)
VALUES (1, 'lin', 'Lin', 'L', '慢慢来，比较快。', YEAR(CURDATE()), 'light', 'Asia/Shanghai',
        128, 128, CURDATE(), DATE_SUB(NOW(3), INTERVAL 127 DAY));

INSERT INTO data_sources (id, user_id, source_type, name, description, status, config_json)
VALUES (1, 1, 'finance_api', '财务 API', '用于同步账单与账户余额，需配置授权 Token', 'disconnected', NULL),
       (2, 1, 'health_api', '健康 API', '同步睡眠、步数、运动与体重数据', 'disconnected', NULL),
       (3, 1, 'obsidian', 'Obsidian 路径', '本地 Vault 目录：notes-vault', 'connected', JSON_OBJECT('vaultPath', 'notes-vault'));

INSERT INTO annual_goals (id, user_id, year, name, metric_key, unit, target_value, color, sort_order)
VALUES (1, 1, YEAR(CURDATE()), '读书', 'books_finished', '本', 24, 'green', 0),
       (2, 1, YEAR(CURDATE()), '跑步', 'running_km', 'km', 600, 'blue', 1),
       (3, 1, YEAR(CURDATE()), '储蓄', 'savings_amount', 'CNY', 80000, 'yellow', 2),
       (4, 1, YEAR(CURDATE()), '旅行', 'travel_count', '次', 4, 'pink', 3);

INSERT INTO savings_plans (id, user_id, year, annual_target, monthly_target)
VALUES (1, 1, YEAR(CURDATE()), 80000, 6000);

INSERT INTO finance_categories (id, user_id, name, type, color, sort_order)
VALUES (1, 1, '餐饮', 'expense', 'green', 0),
       (2, 1, '购物', 'expense', 'yellow', 1),
       (3, 1, '交通', 'expense', 'blue', 2),
       (4, 1, '娱乐', 'expense', 'pink', 3),
       (5, 1, '其他', 'expense', 'gray', 4),
       (6, 1, '收入', 'income', 'green', 5);

INSERT INTO accounts (id, user_id, name, account_type, balance, currency, is_active, sort_order)
VALUES (1, 1, '招商银行储蓄卡', 'bank', 150000, 'CNY', 1, 0),
       (2, 1, '支付宝', 'cash', 25780, 'CNY', 1, 1),
       (3, 1, '现金', 'cash', 3000, 'CNY', 1, 2),
       (4, 1, '基金账户', 'investment', 42000, 'CNY', 1, 3);

INSERT INTO budgets (id, user_id, budget_month, category_id, limit_amount)
VALUES (1, 1, DATE_FORMAT(CURDATE(), '%Y-%m-01'), 1, 3000),
       (2, 1, DATE_FORMAT(CURDATE(), '%Y-%m-01'), 2, 4000),
       (3, 1, DATE_FORMAT(CURDATE(), '%Y-%m-01'), 3, 1500),
       (4, 1, DATE_FORMAT(CURDATE(), '%Y-%m-01'), 4, 4000),
       (5, 1, DATE_FORMAT(CURDATE(), '%Y-%m-01'), 5, 8000);

-- 近 6 个月交易：当月明细 + 前 5 个月分类汇总
INSERT INTO transactions (user_id, type, category_id, title, amount, occurred_at, note)
VALUES
-- 当月收入
(1, 'income', 6, '工资入账', 18200, DATE_FORMAT(CURDATE(), '%Y-%m-10 09:00:00'), NULL),
(1, 'income', 6, '副业收入', 1200, DATE_FORMAT(CURDATE(), '%Y-%m-18 20:00:00'), NULL),
-- 当月餐饮
(1, 'expense', 1, '午餐 · 公司楼下', 38, DATE_FORMAT(CURDATE(), '%Y-%m-%d 12:30:00'), NULL),
(1, 'expense', 1, '咖啡', 32, DATE_FORMAT(CURDATE(), '%Y-%m-03 10:15:00'), NULL),
(1, 'expense', 1, '早餐', 18, DATE_FORMAT(CURDATE(), '%Y-%m-05 08:10:00'), NULL),
(1, 'expense', 1, '周末聚餐', 428, DATE_FORMAT(CURDATE(), '%Y-%m-08 19:30:00'), NULL),
(1, 'expense', 1, '超市采购', 486, DATE_FORMAT(CURDATE(), '%Y-%m-12 18:40:00'), NULL),
(1, 'expense', 1, '午餐', 42, DATE_FORMAT(CURDATE(), '%Y-%m-15 12:10:00'), NULL),
(1, 'expense', 1, '晚餐', 168, DATE_FORMAT(CURDATE(), '%Y-%m-19 19:20:00'), NULL),
-- 当月购物
(1, 'expense', 2, '独立书店 · 两本书', 128, DATE_FORMAT(CURDATE(), '%Y-%m-04 16:00:00'), NULL),
(1, 'expense', 2, '日用品', 267, DATE_FORMAT(CURDATE(), '%Y-%m-11 21:10:00'), NULL),
(1, 'expense', 2, '外套', 599, DATE_FORMAT(CURDATE(), '%Y-%m-16 15:30:00'), NULL),
(1, 'expense', 2, '家电', 1500, DATE_FORMAT(CURDATE(), '%Y-%m-22 14:00:00'), NULL),
-- 当月交通
(1, 'expense', 3, '地铁通勤', 6, DATE_FORMAT(CURDATE(), '%Y-%m-01 08:45:00'), NULL),
(1, 'expense', 3, '打车', 46, DATE_FORMAT(CURDATE(), '%Y-%m-07 22:10:00'), NULL),
(1, 'expense', 3, '高铁票', 320, DATE_FORMAT(CURDATE(), '%Y-%m-13 09:00:00'), NULL),
(1, 'expense', 3, '加油', 400, DATE_FORMAT(CURDATE(), '%Y-%m-21 17:40:00'), NULL),
-- 当月娱乐
(1, 'expense', 4, '电影票', 68, DATE_FORMAT(CURDATE(), '%Y-%m-06 20:30:00'), NULL),
(1, 'expense', 4, '视频会员', 25, DATE_FORMAT(CURDATE(), '%Y-%m-09 09:30:00'), NULL),
(1, 'expense', 4, '演出票', 380, DATE_FORMAT(CURDATE(), '%Y-%m-17 19:00:00'), NULL),
(1, 'expense', 4, '健身年卡', 1200, DATE_FORMAT(CURDATE(), '%Y-%m-23 11:00:00'), NULL),
-- 当月其他
(1, 'expense', 5, '房租', 4200, DATE_FORMAT(CURDATE(), '%Y-%m-01 10:00:00'), NULL),
(1, 'expense', 5, '水电燃气', 320, DATE_FORMAT(CURDATE(), '%Y-%m-02 09:00:00'), NULL),
(1, 'expense', 5, '手机话费', 99, DATE_FORMAT(CURDATE(), '%Y-%m-14 08:00:00'), NULL),
(1, 'expense', 5, '猫粮', 260, DATE_FORMAT(CURDATE(), '%Y-%m-20 18:20:00'), NULL),
(1, 'expense', 5, '保险', 800, DATE_FORMAT(CURDATE(), '%Y-%m-24 10:30:00'), NULL),
(1, 'expense', 5, '周末短途旅行', 2200, DATE_FORMAT(CURDATE(), '%Y-%m-25 08:00:00'), NULL),
-- 前 5 个月：每月工资 + 分类汇总
(1, 'income', 6, '工资入账', 18200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-10 09:00:00'), NULL),
(1, 'expense', 1, '餐饮合计', 2300, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-05 12:00:00'), NULL),
(1, 'expense', 2, '购物合计', 1700, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-06 12:00:00'), NULL),
(1, 'expense', 3, '交通合计', 1200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-07 12:00:00'), NULL),
(1, 'expense', 4, '娱乐合计', 750, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-08 12:00:00'), NULL),
(1, 'expense', 5, '其他合计', 2050, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-09 12:00:00'), NULL),

(1, 'income', 6, '工资入账', 18200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-10 09:00:00'), NULL),
(1, 'expense', 1, '餐饮合计', 2500, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-05 12:00:00'), NULL),
(1, 'expense', 2, '购物合计', 1900, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-06 12:00:00'), NULL),
(1, 'expense', 3, '交通合计', 1000, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-07 12:00:00'), NULL),
(1, 'expense', 4, '娱乐合计', 850, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-08 12:00:00'), NULL),
(1, 'expense', 5, '其他合计', 2400, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 MONTH), '%Y-%m-09 12:00:00'), NULL),

(1, 'income', 6, '工资入账', 18200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), '%Y-%m-10 09:00:00'), NULL),
(1, 'expense', 1, '餐饮合计', 2200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), '%Y-%m-05 12:00:00'), NULL),
(1, 'expense', 2, '购物合计', 1500, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), '%Y-%m-06 12:00:00'), NULL),
(1, 'expense', 3, '交通合计', 1300, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), '%Y-%m-07 12:00:00'), NULL),
(1, 'expense', 4, '娱乐合计', 700, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), '%Y-%m-08 12:00:00'), NULL),
(1, 'expense', 5, '其他合计', 1900, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), '%Y-%m-09 12:00:00'), NULL),

(1, 'income', 6, '工资入账', 18200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m-10 09:00:00'), NULL),
(1, 'income', 6, '项目奖金', 1200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m-20 20:00:00'), NULL),
(1, 'expense', 1, '餐饮合计', 2400, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m-05 12:00:00'), NULL),
(1, 'expense', 2, '购物合计', 1600, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m-06 12:00:00'), NULL),
(1, 'expense', 3, '交通合计', 1100, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m-07 12:00:00'), NULL),
(1, 'expense', 4, '娱乐合计', 900, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m-08 12:00:00'), NULL),
(1, 'expense', 5, '其他合计', 2300, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 4 MONTH), '%Y-%m-09 12:00:00'), NULL),

(1, 'income', 6, '工资入账', 18200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-10 09:00:00'), NULL),
(1, 'expense', 1, '餐饮合计', 2600, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-05 12:00:00'), NULL),
(1, 'expense', 2, '购物合计', 1800, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-06 12:00:00'), NULL),
(1, 'expense', 3, '交通合计', 1200, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-07 12:00:00'), NULL),
(1, 'expense', 4, '娱乐合计', 800, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-08 12:00:00'), NULL),
(1, 'expense', 5, '其他合计', 2100, DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-09 12:00:00'), NULL);

-- 任务：首页与工作页共用，同一文案仅保留一条
INSERT INTO tasks (id, user_id, title, area, priority, is_done, scheduled_date, due_at, completed_at, sort_order)
VALUES (1, 1, '提交本周周报', 'work', 'high', 1, CURDATE(), NULL, NOW(3), 0),
       (2, 1, '晨跑 5km', 'health', 'mid', 1, CURDATE(), NULL, NOW(3), 1),
       (3, 1, '回复 3 封邮件', 'general', 'low', 1, CURDATE(), NULL, NOW(3), 2),
       (4, 1, '写 Q2 产品复盘文档', 'work', 'high', 0, CURDATE(), DATE_FORMAT(CURDATE(), '%Y-%m-%d 18:00:00'), NULL, 3),
       (5, 1, '和设计对齐新版本交互稿', 'work', 'high', 0, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 10 HOUR, NULL, 4),
       (6, 1, '整理桌面文件夹', 'general', 'low', 0, CURDATE(), NULL, NULL, 5),
       (7, 1, '读《慢思考》20 页', 'life', 'low', 0, CURDATE(), NULL, NULL, 6),
       (8, 1, '整理 6 份用户访谈记录', 'work', 'mid', 0, CURDATE(), NULL, NULL, 7),
       (9, 1, '更新项目周报看板', 'work', 'mid', 0, CURDATE(), NULL, NULL, 8),
       (10, 1, '看一集设计课', 'general', 'low', 0, CURDATE(), NULL, NULL, 9),
       (11, 1, '用户访谈整理', 'work', 'mid', 1, DATE_FORMAT(CURDATE(), '%Y-%m-03'), NULL, NOW(3), 10),
       (12, 1, '周报草稿', 'work', 'low', 1, DATE_FORMAT(CURDATE(), '%Y-%m-05'), NULL, NOW(3), 11),
       (13, 1, 'A/B 实验启动', 'work', 'mid', 1, DATE_FORMAT(CURDATE(), '%Y-%m-09'), NULL, NOW(3), 12),
       (14, 1, '知识库同步', 'work', 'low', 1, DATE_FORMAT(CURDATE(), '%Y-%m-10'), NULL, NOW(3), 13),
       (15, 1, '设计稿评审', 'work', 'mid', 1, DATE_FORMAT(CURDATE(), '%Y-%m-11'), NULL, NOW(3), 14),
       (16, 1, '月度数据报告', 'work', 'mid', 0, DATE_FORMAT(CURDATE(), '%Y-%m-16'), DATE_FORMAT(CURDATE(), '%Y-%m-16 10:00:00'), NULL, 15),
       (17, 1, '团队分享', 'work', 'low', 0, DATE_FORMAT(CURDATE(), '%Y-%m-18'), NULL, NULL, 16),
       (18, 1, '季度 OKR 复盘', 'work', 'low', 0, DATE_FORMAT(CURDATE(), '%Y-%m-20'), DATE_FORMAT(CURDATE(), '%Y-%m-20 18:00:00'), NULL, 17),
       (19, 1, '用户增长复盘', 'work', 'mid', 0, DATE_FORMAT(CURDATE(), '%Y-%m-24'), NULL, NULL, 18),
       (20, 1, '知识库整理', 'work', 'low', 0, DATE_FORMAT(CURDATE(), '%Y-%m-26'), NULL, NULL, 19);

INSERT INTO projects (id, user_id, name, stage, progress_percent, status, start_date, target_date, color, sort_order)
VALUES (1, 1, '「生活台」v2 改版', '交互稿', 68, 'active', DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'green', 0),
       (2, 1, '用户增长实验', 'A/B 中', 42, 'active', DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'blue', 1),
       (3, 1, '知识库整理计划', '42/168', 25, 'active', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 'yellow', 2);

-- 近 14 天睡眠记录（睡眠归属起床日）
INSERT INTO sleep_records (user_id, sleep_date, sleep_at, wake_at, duration_minutes, deep_minutes, light_minutes, rem_minutes, awake_minutes, quality_score, source)
VALUES
(1, CURDATE(), DATE_SUB(CURDATE(), INTERVAL 1 DAY) + INTERVAL 23 HOUR + INTERVAL 40 MINUTE, CURDATE() + INTERVAL 7 HOUR + INTERVAL 12 MINUTE, 452, 110, 280, 62, 0, 82, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY) + INTERVAL 0 HOUR + INTERVAL 10 MINUTE, DATE_SUB(CURDATE(), INTERVAL 1 DAY) + INTERVAL 6 HOUR + INTERVAL 15 MINUTE, 365, 84, 220, 61, 0, 70, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY) + INTERVAL 23 HOUR + INTERVAL 20 MINUTE, DATE_SUB(CURDATE(), INTERVAL 2 DAY) + INTERVAL 7 HOUR + INTERVAL 8 MINUTE, 468, 120, 286, 62, 0, 85, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY) + INTERVAL 23 HOUR + INTERVAL 55 MINUTE, DATE_SUB(CURDATE(), INTERVAL 3 DAY) + INTERVAL 6 HOUR + INTERVAL 35 MINUTE, 400, 96, 240, 64, 0, 76, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY) + INTERVAL 0 HOUR + INTERVAL 30 MINUTE, DATE_SUB(CURDATE(), INTERVAL 4 DAY) + INTERVAL 6 HOUR + INTERVAL 50 MINUTE, 380, 80, 238, 62, 0, 68, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY) + INTERVAL 23 HOUR + INTERVAL 15 MINUTE, DATE_SUB(CURDATE(), INTERVAL 5 DAY) + INTERVAL 7 HOUR + INTERVAL 25 MINUTE, 490, 126, 300, 64, 0, 88, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(CURDATE(), INTERVAL 7 DAY) + INTERVAL 23 HOUR + INTERVAL 50 MINUTE, DATE_SUB(CURDATE(), INTERVAL 6 DAY) + INTERVAL 6 HOUR + INTERVAL 20 MINUTE, 390, 92, 238, 60, 0, 72, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 7 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY) + INTERVAL 0 HOUR + INTERVAL 5 MINUTE, DATE_SUB(CURDATE(), INTERVAL 7 DAY) + INTERVAL 6 HOUR + INTERVAL 55 MINUTE, 410, 98, 250, 62, 0, 74, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(CURDATE(), INTERVAL 9 DAY) + INTERVAL 23 HOUR + INTERVAL 35 MINUTE, DATE_SUB(CURDATE(), INTERVAL 8 DAY) + INTERVAL 7 HOUR + INTERVAL 18 MINUTE, 463, 112, 288, 63, 0, 84, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 9 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY) + INTERVAL 0 HOUR + INTERVAL 20 MINUTE, DATE_SUB(CURDATE(), INTERVAL 9 DAY) + INTERVAL 6 HOUR + INTERVAL 5 MINUTE, 345, 70, 214, 61, 0, 64, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 11 DAY) + INTERVAL 23 HOUR + INTERVAL 25 MINUTE, DATE_SUB(CURDATE(), INTERVAL 10 DAY) + INTERVAL 7 HOUR + INTERVAL 32 MINUTE, 487, 125, 298, 64, 0, 86, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 11 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY) + INTERVAL 23 HOUR + INTERVAL 45 MINUTE, DATE_SUB(CURDATE(), INTERVAL 11 DAY) + INTERVAL 6 HOUR + INTERVAL 40 MINUTE, 415, 90, 262, 63, 0, 75, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 13 DAY) + INTERVAL 0 HOUR + INTERVAL 15 MINUTE, DATE_SUB(CURDATE(), INTERVAL 12 DAY) + INTERVAL 6 HOUR + INTERVAL 30 MINUTE, 375, 78, 234, 63, 0, 67, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 13 DAY), DATE_SUB(CURDATE(), INTERVAL 14 DAY) + INTERVAL 23 HOUR + INTERVAL 30 MINUTE, DATE_SUB(CURDATE(), INTERVAL 13 DAY) + INTERVAL 7 HOUR + INTERVAL 22 MINUTE, 472, 116, 292, 64, 0, 83, 'manual');

-- 近 14 天步数
INSERT INTO daily_activities (user_id, activity_date, steps, step_goal, distance_km, active_calories_kcal, source)
VALUES
(1, CURDATE(), 8420, 10000, 5.89, 268, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 10240, 10000, 7.17, 320, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 9630, 10000, 6.74, 305, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 11050, 10000, 7.74, 352, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 7300, 10000, 5.11, 240, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 12100, 10000, 8.47, 380, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 10120, 10000, 7.08, 318, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 7 DAY), 8860, 10000, 6.20, 276, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 8 DAY), 10440, 10000, 7.31, 328, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 9 DAY), 9250, 10000, 6.48, 290, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 11200, 10000, 7.84, 356, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 11 DAY), 6800, 10000, 4.76, 224, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 10030, 10000, 7.02, 314, 'manual'),
(1, DATE_SUB(CURDATE(), INTERVAL 13 DAY), 9750, 10000, 6.83, 302, 'manual');

-- 近 26 周运动记录（跑步累计约 260km）
INSERT INTO workouts (user_id, workout_type, started_at, duration_minutes, distance_km, calories_kcal, source)
WITH RECURSIVE seq AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 25)
SELECT 1, 'run', DATE_SUB(CURDATE(), INTERVAL (n * 7 + 2) DAY) + INTERVAL 7 HOUR, 34, 6.0, 380, 'manual' FROM seq;

INSERT INTO workouts (user_id, workout_type, started_at, duration_minutes, distance_km, calories_kcal, source)
WITH RECURSIVE seq AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 25)
SELECT 1, 'run', DATE_SUB(CURDATE(), INTERVAL (n * 7 + 5) DAY) + INTERVAL 7 HOUR, 24, 4.0, 260, 'manual' FROM seq;

INSERT INTO workouts (user_id, workout_type, started_at, duration_minutes, distance_km, calories_kcal, source)
WITH RECURSIVE seq AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 25)
SELECT 1, 'ride', DATE_SUB(CURDATE(), INTERVAL (n * 7 + 6) DAY) + INTERVAL 16 HOUR, 45, 12.0, 320, 'manual' FROM seq;

INSERT INTO workouts (user_id, workout_type, started_at, duration_minutes, distance_km, calories_kcal, source)
WITH RECURSIVE seq AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 25)
SELECT 1, 'gym', DATE_SUB(CURDATE(), INTERVAL (n * 7 + 3) DAY) + INTERVAL 19 HOUR, 60, NULL, 420, 'manual' FROM seq;

INSERT INTO workouts (user_id, workout_type, started_at, duration_minutes, distance_km, calories_kcal, source)
WITH RECURSIVE seq AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 12)
SELECT 1, 'other', DATE_SUB(CURDATE(), INTERVAL (n * 14 + 4) DAY) + INTERVAL 20 HOUR, 30, NULL, 160, 'manual' FROM seq;

-- 近 30 天体重
INSERT INTO body_metrics (user_id, measured_on, weight_kg, body_fat_percent, muscle_kg, source)
WITH RECURSIVE seq AS (SELECT 0 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 29)
SELECT 1, DATE_SUB(CURDATE(), INTERVAL n DAY), ROUND(57.4 + n * 0.04, 2), 22.5, 25.8, 'manual' FROM seq;

-- 本月 30 次生活活动
INSERT INTO life_activities (user_id, activity_type, title, occurred_at, duration_minutes, note)
WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 30)
SELECT 1,
       CASE WHEN n <= 12 THEN 'sport' WHEN n <= 21 THEN 'solitude' WHEN n <= 27 THEN 'gathering' ELSE 'travel' END,
       CASE WHEN n <= 12 THEN '运动记录' WHEN n <= 21 THEN '独处时光' WHEN n <= 27 THEN '朋友聚餐' ELSE '周末短途旅行' END,
       DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL MOD(n - 1, 27) DAY) + INTERVAL 18 HOUR,
       CASE WHEN n <= 12 THEN 60 ELSE NULL END,
       NULL
FROM seq;

-- 媒体条目：12 本已读完 + 4 本在读 + 影视与动画
INSERT INTO media_items (id, user_id, media_type, title, subtitle, creator, status, progress_current, progress_total, progress_unit,
                         rating, started_on, finished_on, last_activity_at)
VALUES
(1, 1, 'book', '《百年孤独》', NULL, '加西亚·马尔克斯', 'finished', 100, 100, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 300 DAY), DATE_SUB(CURDATE(), INTERVAL 210 DAY), DATE_SUB(NOW(3), INTERVAL 210 DAY)),
(2, 1, 'book', '《小径分岔的花园》', NULL, '博尔赫斯', 'finished', 100, 100, 'percent', 5, DATE_SUB(CURDATE(), INTERVAL 200 DAY), DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_SUB(NOW(3), INTERVAL 160 DAY)),
(3, 1, 'book', '《月亮与六便士》', NULL, '毛姆', 'finished', 100, 100, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 150 DAY), DATE_SUB(CURDATE(), INTERVAL 120 DAY), DATE_SUB(NOW(3), INTERVAL 120 DAY)),
(4, 1, 'book', '《围城》', NULL, '钱钟书', 'finished', 100, 100, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 110 DAY), DATE_SUB(CURDATE(), INTERVAL 90 DAY), DATE_SUB(NOW(3), INTERVAL 90 DAY)),
(5, 1, 'book', '《瓦尔登湖》', NULL, '梭罗', 'finished', 100, 100, 'percent', 3, DATE_SUB(CURDATE(), INTERVAL 85 DAY), DATE_SUB(CURDATE(), INTERVAL 70 DAY), DATE_SUB(NOW(3), INTERVAL 70 DAY)),
(6, 1, 'book', '《局外人》', NULL, '加缪', 'finished', 100, 100, 'percent', 5, DATE_SUB(CURDATE(), INTERVAL 65 DAY), DATE_SUB(CURDATE(), INTERVAL 50 DAY), DATE_SUB(NOW(3), INTERVAL 50 DAY)),
(7, 1, 'book', '《蛤蟆先生去看心理医生》', NULL, '罗伯特·戴博德', 'finished', 100, 100, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(NOW(3), INTERVAL 30 DAY)),
(8, 1, 'book', '《秋园》', NULL, '杨本芬', 'finished', 100, 100, 'percent', 5, DATE_SUB(CURDATE(), INTERVAL 28 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(NOW(3), INTERVAL 20 DAY)),
(9, 1, 'book', '《夜晚的潜水艇》', NULL, '陈春成', 'finished', 100, 100, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 18 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(NOW(3), INTERVAL 12 DAY)),
(10, 1, 'book', '《人类简史》', NULL, '尤瓦尔·赫拉利', 'finished', 100, 100, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 9 DAY), DATE_SUB(NOW(3), INTERVAL 9 DAY)),
(11, 1, 'book', '《慢思考》', NULL, '特奥·康普诺利', 'finished', 100, 100, 'percent', 5, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY), DATE_SUB(NOW(3), INTERVAL 4 DAY)),
(12, 1, 'book', '《置身事内》', NULL, '兰小欢', 'finished', 100, 100, 'percent', 5, DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_SUB(NOW(3), INTERVAL 2 DAY)),
(13, 1, 'book', '《思考，快与慢》', NULL, '丹尼尔·卡尼曼', 'in_progress', 62, 100, 'percent', NULL, DATE_SUB(CURDATE(), INTERVAL 20 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 1 DAY)),
(14, 1, 'book', '《乡土中国》', NULL, '费孝通', 'in_progress', 28, 100, 'percent', NULL, DATE_SUB(CURDATE(), INTERVAL 15 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 2 DAY)),
(15, 1, 'book', '《枪炮、病菌与钢铁》', NULL, '贾雷德·戴蒙德', 'in_progress', 12, 100, 'percent', NULL, DATE_SUB(CURDATE(), INTERVAL 10 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 3 HOUR)),
(16, 1, 'book', '《万历十五年》', NULL, '黄仁宇', 'in_progress', 100, 100, 'percent', NULL, DATE_SUB(CURDATE(), INTERVAL 40 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 1 DAY)),
(17, 1, 'movie', '《海街日记》', '夏秋篇', '是枝裕和', 'finished', 1, 1, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(NOW(3), INTERVAL 3 DAY)),
(18, 1, 'movie', '《步履不停》', NULL, '是枝裕和', 'finished', 1, 1, 'percent', 5, DATE_SUB(CURDATE(), INTERVAL 25 DAY), DATE_SUB(CURDATE(), INTERVAL 22 DAY), DATE_SUB(NOW(3), INTERVAL 22 DAY)),
(19, 1, 'movie', '《花束般的恋爱》', NULL, '土井裕泰', 'finished', 1, 1, 'percent', 4, DATE_SUB(CURDATE(), INTERVAL 70 DAY), DATE_SUB(CURDATE(), INTERVAL 65 DAY), DATE_SUB(NOW(3), INTERVAL 65 DAY)),
(20, 1, 'series', '《我的解放日志》', NULL, '金锡允', 'finished', 16, 16, 'episode', 5, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(NOW(3), INTERVAL 8 DAY)),
(21, 1, 'series', '《四重奏》', NULL, '土井裕泰', 'finished', 10, 10, 'episode', 4, DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 50 DAY), DATE_SUB(NOW(3), INTERVAL 50 DAY)),
(22, 1, 'documentary', '《人生果实》', NULL, '伏原健之', 'finished', 1, 1, 'percent', 5, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(NOW(3), INTERVAL 12 DAY)),
(23, 1, 'anime', '《玲音》', NULL, '中村隆太郎', 'finished', 13, 13, 'episode', 4, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 18 DAY), DATE_SUB(NOW(3), INTERVAL 18 DAY)),
(24, 1, 'series', '《重启人生》', NULL, '水野格', 'in_progress', 4, 10, 'episode', NULL, DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 2 DAY)),
(25, 1, 'series', '《漫长的季节》', NULL, '辛爽', 'in_progress', 6, 12, 'episode', NULL, DATE_SUB(CURDATE(), INTERVAL 12 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 1 DAY)),
(26, 1, 'documentary', '《宇宙时空之旅》', NULL, '安·德鲁扬', 'in_progress', 2, 13, 'episode', NULL, DATE_SUB(CURDATE(), INTERVAL 9 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 3 DAY)),
(27, 1, 'anime', '《虫师》', NULL, '长滨博史', 'in_progress', 3, 26, 'episode', NULL, DATE_SUB(CURDATE(), INTERVAL 14 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 5 DAY)),
(28, 1, 'movie', '《小森林》', '夏秋篇', '森淳一', 'in_progress', 100, 100, 'percent', NULL, DATE_SUB(CURDATE(), INTERVAL 4 DAY), NULL, DATE_SUB(NOW(3), INTERVAL 1 DAY));


INSERT INTO habits (id, user_id, icon, name, unit, target_value, period, color, sort_order, is_active)
VALUES (1, 1, '💧', '喝水', '杯', 8, 'day', 'blue', 0, 1),
       (2, 1, '🧘', '冥想', '分钟', 20, 'day', 'green', 1, 1),
       (3, 1, '🤸', '拉伸', '次', 2, 'day', 'yellow', 2, 1),
       (4, 1, '🌙', '早睡', '天', 7, 'week', 'pink', 3, 1);

INSERT INTO habit_logs (user_id, habit_id, log_date, value, is_done)
VALUES (1, 1, CURDATE(), 6, 0),
       (1, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 8, 1),
       (1, 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 7, 0),
       (1, 2, CURDATE(), 15, 0),
       (1, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 20, 1),
       (1, 2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 20, 1),
       (1, 3, CURDATE(), 1, 0),
       (1, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 2, 1),
       (1, 3, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 2, 1),
       (1, 4, DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), 1, 1),
       (1, 4, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 1 DAY), 1, 1),
       (1, 4, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 2 DAY), 1, 1),
       (1, 4, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 3 DAY), 1, 1);
