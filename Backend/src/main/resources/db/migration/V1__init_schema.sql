-- Life Console 初始表结构（MySQL 8 / InnoDB / utf8mb4）

CREATE TABLE users (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username            VARCHAR(64)     NOT NULL,
  display_name        VARCHAR(64)     NOT NULL,
  avatar_char         VARCHAR(8)      NULL,
  signature           VARCHAR(200)    NULL,
  target_year         SMALLINT UNSIGNED NOT NULL,
  theme               VARCHAR(16)     NOT NULL DEFAULT 'light',
  timezone            VARCHAR(64)     NOT NULL DEFAULT 'Asia/Shanghai',
  current_streak_days SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  longest_streak_days SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  last_active_on      DATE            NULL,
  created_at          DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at          DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE data_sources (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id       BIGINT UNSIGNED NOT NULL,
  source_type   VARCHAR(32)     NOT NULL,
  name          VARCHAR(64)     NOT NULL,
  description   VARCHAR(255)    NULL,
  status        VARCHAR(16)     NOT NULL,
  config_json   JSON            NULL,
  secret_cipher VARBINARY(1024) NULL,
  last_synced_at DATETIME(3)    NULL,
  created_at    DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at    DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_data_sources_user_type (user_id, source_type),
  CONSTRAINT fk_data_sources_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE annual_goals (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id      BIGINT UNSIGNED NOT NULL,
  year         SMALLINT UNSIGNED NOT NULL,
  name         VARCHAR(32)     NOT NULL,
  metric_key   VARCHAR(32)     NOT NULL,
  unit         VARCHAR(16)     NOT NULL,
  target_value DECIMAL(14,2)   NOT NULL,
  color        VARCHAR(16)     NOT NULL,
  sort_order   TINYINT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_annual_goals_user_year_name (user_id, year, name),
  CONSTRAINT fk_annual_goals_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE tasks (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id        BIGINT UNSIGNED NOT NULL,
  title          VARCHAR(255)    NOT NULL,
  note           VARCHAR(1000)   NULL,
  area           VARCHAR(16)     NOT NULL DEFAULT 'general',
  priority       VARCHAR(8)      NOT NULL DEFAULT 'mid',
  is_done        TINYINT(1)      NOT NULL DEFAULT 0,
  scheduled_date DATE            NULL,
  due_at         DATETIME(3)     NULL,
  completed_at   DATETIME(3)     NULL,
  sort_order     INT             NOT NULL DEFAULT 0,
  created_at     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at     DATETIME(3)     NULL,
  PRIMARY KEY (id),
  KEY idx_tasks_user_scheduled (user_id, scheduled_date, is_done),
  KEY idx_tasks_user_due (user_id, due_at),
  KEY idx_tasks_user_done_priority (user_id, is_done, priority),
  CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE projects (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id          BIGINT UNSIGNED NOT NULL,
  name             VARCHAR(128)    NOT NULL,
  stage            VARCHAR(64)     NULL,
  progress_percent DECIMAL(5,2)    NOT NULL DEFAULT 0,
  status           VARCHAR(16)     NOT NULL DEFAULT 'active',
  start_date       DATE            NULL,
  target_date      DATE            NULL,
  color            VARCHAR(16)     NOT NULL DEFAULT 'green',
  sort_order       INT             NOT NULL DEFAULT 0,
  created_at       DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at       DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_projects_user_status (user_id, status),
  CONSTRAINT fk_projects_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE sleep_records (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id          BIGINT UNSIGNED NOT NULL,
  sleep_date       DATE            NOT NULL,
  sleep_at         DATETIME(3)     NOT NULL,
  wake_at          DATETIME(3)     NOT NULL,
  duration_minutes SMALLINT UNSIGNED NOT NULL,
  deep_minutes     SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  light_minutes    SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  rem_minutes      SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  awake_minutes    SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  quality_score    TINYINT UNSIGNED NULL,
  source           VARCHAR(16)     NOT NULL DEFAULT 'manual',
  note             VARCHAR(255)    NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sleep_records_user_date (user_id, sleep_date),
  CONSTRAINT fk_sleep_records_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE daily_activities (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id             BIGINT UNSIGNED NOT NULL,
  activity_date       DATE            NOT NULL,
  steps               INT UNSIGNED    NOT NULL DEFAULT 0,
  step_goal           INT UNSIGNED    NOT NULL DEFAULT 10000,
  distance_km         DECIMAL(6,2)    NULL,
  active_calories_kcal INT UNSIGNED   NULL,
  source              VARCHAR(16)     NOT NULL DEFAULT 'manual',
  PRIMARY KEY (id),
  UNIQUE KEY uk_daily_activities_user_date (user_id, activity_date),
  CONSTRAINT fk_daily_activities_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE workouts (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id          BIGINT UNSIGNED NOT NULL,
  workout_type     VARCHAR(16)     NOT NULL,
  started_at       DATETIME(3)     NOT NULL,
  duration_minutes SMALLINT UNSIGNED NOT NULL,
  distance_km      DECIMAL(6,2)    NULL,
  calories_kcal    INT UNSIGNED    NULL,
  note             VARCHAR(255)    NULL,
  source           VARCHAR(16)     NOT NULL DEFAULT 'manual',
  PRIMARY KEY (id),
  KEY idx_workouts_user_started (user_id, started_at),
  KEY idx_workouts_user_type_started (user_id, workout_type, started_at),
  CONSTRAINT fk_workouts_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE body_metrics (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id          BIGINT UNSIGNED NOT NULL,
  measured_on      DATE            NOT NULL,
  weight_kg        DECIMAL(5,2)    NOT NULL,
  body_fat_percent DECIMAL(5,2)    NULL,
  muscle_kg        DECIMAL(5,2)    NULL,
  source           VARCHAR(16)     NOT NULL DEFAULT 'manual',
  PRIMARY KEY (id),
  UNIQUE KEY uk_body_metrics_user_date (user_id, measured_on),
  CONSTRAINT fk_body_metrics_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE habits (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id      BIGINT UNSIGNED NOT NULL,
  icon         VARCHAR(16)     NOT NULL,
  name         VARCHAR(64)     NOT NULL,
  unit         VARCHAR(16)     NOT NULL,
  target_value DECIMAL(8,2)    NOT NULL,
  period       VARCHAR(8)      NOT NULL DEFAULT 'day',
  color        VARCHAR(16)     NOT NULL,
  sort_order   TINYINT UNSIGNED NOT NULL DEFAULT 0,
  is_active    TINYINT(1)      NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uk_habits_user_name (user_id, name),
  CONSTRAINT fk_habits_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE habit_logs (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id    BIGINT UNSIGNED NOT NULL,
  habit_id   BIGINT UNSIGNED NOT NULL,
  log_date   DATE            NOT NULL,
  value      DECIMAL(8,2)    NOT NULL DEFAULT 0,
  is_done    TINYINT(1)      NOT NULL DEFAULT 0,
  created_at DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_habit_logs_habit_date (habit_id, log_date),
  KEY idx_habit_logs_user_date (user_id, log_date),
  CONSTRAINT fk_habit_logs_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_habit_logs_habit FOREIGN KEY (habit_id) REFERENCES habits (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE accounts (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id      BIGINT UNSIGNED NOT NULL,
  name         VARCHAR(64)     NOT NULL,
  account_type VARCHAR(16)     NOT NULL,
  balance      DECIMAL(14,2)   NOT NULL DEFAULT 0,
  currency     CHAR(3)         NOT NULL DEFAULT 'CNY',
  is_active    TINYINT(1)      NOT NULL DEFAULT 1,
  sort_order   INT             NOT NULL DEFAULT 0,
  updated_at   DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_accounts_user_active (user_id, is_active),
  CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE finance_categories (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id    BIGINT UNSIGNED NOT NULL,
  name       VARCHAR(32)     NOT NULL,
  type       VARCHAR(8)      NOT NULL,
  color      VARCHAR(16)     NOT NULL,
  sort_order TINYINT UNSIGNED NOT NULL DEFAULT 0,
  is_active  TINYINT(1)      NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uk_finance_categories_user_type_name (user_id, type, name),
  CONSTRAINT fk_finance_categories_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE transactions (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id     BIGINT UNSIGNED NOT NULL,
  type        VARCHAR(8)      NOT NULL,
  category_id BIGINT UNSIGNED NOT NULL,
  title       VARCHAR(128)    NOT NULL,
  amount      DECIMAL(12,2)   NOT NULL,
  occurred_at DATETIME(3)     NOT NULL,
  note        VARCHAR(255)    NULL,
  source      VARCHAR(16)     NOT NULL DEFAULT 'manual',
  created_at  DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at  DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at  DATETIME(3)     NULL,
  PRIMARY KEY (id),
  KEY idx_transactions_user_occurred (user_id, occurred_at),
  KEY idx_transactions_user_category_occurred (user_id, category_id, occurred_at),
  KEY idx_transactions_user_type_occurred (user_id, type, occurred_at),
  CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES finance_categories (id),
  CONSTRAINT ck_transactions_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE budgets (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id      BIGINT UNSIGNED NOT NULL,
  budget_month DATE            NOT NULL,
  category_id  BIGINT UNSIGNED NOT NULL,
  limit_amount DECIMAL(12,2)   NOT NULL,
  created_at   DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at   DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_budgets_user_month_category (user_id, budget_month, category_id),
  CONSTRAINT fk_budgets_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES finance_categories (id),
  CONSTRAINT ck_budgets_limit CHECK (limit_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE savings_plans (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id        BIGINT UNSIGNED NOT NULL,
  year           SMALLINT UNSIGNED NOT NULL,
  annual_target  DECIMAL(14,2)   NOT NULL,
  monthly_target DECIMAL(12,2)   NOT NULL,
  created_at     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_savings_plans_user_year (user_id, year),
  CONSTRAINT fk_savings_plans_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE life_activities (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id          BIGINT UNSIGNED NOT NULL,
  activity_type    VARCHAR(16)     NOT NULL,
  title            VARCHAR(128)    NOT NULL,
  occurred_at      DATETIME(3)     NOT NULL,
  duration_minutes INT UNSIGNED    NULL,
  note             VARCHAR(255)    NULL,
  created_at       DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at       DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_life_activities_user_occurred (user_id, occurred_at),
  KEY idx_life_activities_user_type_occurred (user_id, activity_type, occurred_at),
  CONSTRAINT fk_life_activities_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE media_items (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id          BIGINT UNSIGNED NOT NULL,
  media_type       VARCHAR(16)     NOT NULL,
  title            VARCHAR(255)    NOT NULL,
  subtitle         VARCHAR(255)    NULL,
  creator          VARCHAR(128)    NULL,
  status           VARCHAR(16)     NOT NULL,
  progress_current DECIMAL(10,1)   NULL,
  progress_total   DECIMAL(10,1)   NULL,
  progress_unit    VARCHAR(8)      NULL,
  rating           TINYINT UNSIGNED NULL,
  started_on       DATE            NULL,
  finished_on      DATE            NULL,
  last_activity_at DATETIME(3)     NULL,
  cover_url        VARCHAR(512)    NULL,
  note             VARCHAR(1000)   NULL,
  created_at       DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at       DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at       DATETIME(3)     NULL,
  PRIMARY KEY (id),
  KEY idx_media_items_user_status (user_id, status, last_activity_at),
  KEY idx_media_items_user_type_finished (user_id, media_type, finished_on),
  CONSTRAINT fk_media_items_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT ck_media_items_rating CHECK (rating IS NULL OR rating BETWEEN 0 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


