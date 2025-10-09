PRAGMA foreign_keys=ON;


-- Notification Queue: items waiting to be delivered
CREATE TABLE IF NOT EXISTS notification_queue (
    nq_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    title         TEXT NOT NULL,                         -- notification title
    body          TEXT,                                  -- optional content
    scheduled_at  TEXT NOT NULL,                         -- when to deliver (ISO8601)
    status        TEXT NOT NULL DEFAULT 'PENDING',       -- PENDING | SENT
    created_at    TEXT NOT NULL DEFAULT (datetime('now'))
    );

-- Speed up dispatcher lookups (pending + due)
CREATE INDEX IF NOT EXISTS idx_nq_due
    ON notification_queue(status, scheduled_at);

-- Optional: speed up user-specific queries
CREATE INDEX IF NOT EXISTS idx_nq_user
    ON notification_queue(user_id);


-- Notification Log: delivery audit trail
CREATE TABLE IF NOT EXISTS notification_log (
    log_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nq_id        INTEGER REFERENCES notification_queue(nq_id) ON DELETE SET NULL,
    user_id      INTEGER NOT NULL,
    delivered_at TEXT NOT NULL,                          -- actual delivery time
    result       TEXT NOT NULL                           -- e.g., 'SENT'
    );

-- Optional: speed up log lookups by user/time
CREATE INDEX IF NOT EXISTS idx_nlog_user_time
    ON notification_log(user_id, delivered_at);

-- Inbox fields
ALTER TABLE notification_queue add column is_read      integer not null default 0;
ALTER TABLE notification_queue add column read_at      text;
ALTER TABLE notification_queue add column is_archived  integer not null default 0;
ALTER TABLE notification_queue add column delivered_at TEXT;

create index if not exists idx_nq_index_user_delivered
    on notification_queue(user_id, is_archived, delivered_at DESC);

create index if not exists idx_nq_unread_badge
    on notification_queue(user_id, is_archived, is_read);