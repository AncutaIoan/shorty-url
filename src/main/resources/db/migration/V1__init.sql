CREATE TABLE IF NOT EXISTS short_links (
                                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           short_code TEXT NOT NULL UNIQUE,
                                           original_url TEXT NOT NULL,
                                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                           expires_at TIMESTAMPTZ,
                                           clicks INTEGER NOT NULL DEFAULT 0,
                                           user_id UUID,
                                           is_active BOOLEAN NOT NULL DEFAULT TRUE
);