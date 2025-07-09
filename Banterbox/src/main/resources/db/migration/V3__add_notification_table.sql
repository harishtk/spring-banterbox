CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id UUID NOT NULL,
    actor_id UUID DEFAULT NULl,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(32) NOT NUll,
    sub_type VARCHAR(64) DEFAULT NULL,
    reference_id UUID DEFAULT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE
)