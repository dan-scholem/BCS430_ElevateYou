PRAGMA foreign_keys=ON;

-- Notification Queue: items waiting to be delivered
CREATE TABLE IF NOT EXISTS notification_queue (
  nq_id         INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id       INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  title         TEXT NOT NULL,                      -- notification title
  body          TEXT,                               -- optional content
  scheduled_at  TEXT NOT NULL,                      -- ISO8601 local time is OK
  status        TEXT NOT NULL DEFAULT 'PENDING',    -- PENDING | SENT
  created_at    TEXT NOT NULL DEFAULT (datetime('now'))
);

-- Speed up dispatcher lookups
CREATE INDEX IF NOT EXISTS idx_nq_due
  ON notification_queue(status, scheduled_at);

-- Notification Log: delivery audit trail
CREATE TABLE IF NOT EXISTS notification_log (
  log_id       INTEGER PRIMARY KEY AUTOINCREMENT,
  nq_id        INTEGER REFERENCES notification_queue(nq_id) ON DELETE SET NULL,
  user_id      INTEGER NOT NULL,
  delivered_at TEXT NOT NULL,                       -- actual delivery time
  result       TEXT NOT NULL                        -- e.g., 'SENT'
);
