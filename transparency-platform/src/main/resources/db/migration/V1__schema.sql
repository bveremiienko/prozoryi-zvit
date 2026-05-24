CREATE TABLE app_user (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(50) NOT NULL DEFAULT 'ORGANIZER',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE campaign (
    id                      BIGSERIAL PRIMARY KEY,
    slug                    VARCHAR(120) NOT NULL UNIQUE,
    title                   VARCHAR(500) NOT NULL,
    description             TEXT,
    organizer_name          VARCHAR(255) NOT NULL,
    edrpou                  VARCHAR(20),
    target_amount           NUMERIC(14, 2),
    declared_collected_amount NUMERIC(14, 2) NOT NULL DEFAULT 0,
    currency                VARCHAR(3) NOT NULL DEFAULT 'UAH',
    status                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    external_donation_url   VARCHAR(1000),
    external_website_url    VARCHAR(1000),
    started_at              DATE,
    next_report_due         DATE,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_campaign_status ON campaign (status);
CREATE INDEX idx_campaign_slug ON campaign (slug);

CREATE TABLE expense_plan_line (
    id              BIGSERIAL PRIMARY KEY,
    campaign_id     BIGINT NOT NULL REFERENCES campaign (id) ON DELETE CASCADE,
    category        VARCHAR(50) NOT NULL,
    planned_amount  NUMERIC(14, 2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'UAH',
    note            VARCHAR(500),
    sort_order      INT NOT NULL DEFAULT 0
);

CREATE TABLE campaign_report (
    id              BIGSERIAL PRIMARY KEY,
    campaign_id     BIGINT NOT NULL REFERENCES campaign (id) ON DELETE CASCADE,
    period_from     DATE NOT NULL,
    period_to       DATE NOT NULL,
    summary         TEXT NOT NULL,
    submitted_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    report_hash     VARCHAR(64)
);

CREATE INDEX idx_report_campaign ON campaign_report (campaign_id);

CREATE TABLE report_line (
    id              BIGSERIAL PRIMARY KEY,
    report_id       BIGINT NOT NULL REFERENCES campaign_report (id) ON DELETE CASCADE,
    category        VARCHAR(50) NOT NULL,
    amount          NUMERIC(14, 2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'UAH',
    description     VARCHAR(500)
);

CREATE TABLE report_attachment (
    id              BIGSERIAL PRIMARY KEY,
    report_id       BIGINT NOT NULL REFERENCES campaign_report (id) ON DELETE CASCADE,
    original_filename VARCHAR(500) NOT NULL,
    storage_path    VARCHAR(1000) NOT NULL,
    mime_type       VARCHAR(100),
    file_size       BIGINT NOT NULL,
    uploaded_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_event (
    id              BIGSERIAL PRIMARY KEY,
    entity_type     VARCHAR(50) NOT NULL,
    entity_id       BIGINT NOT NULL,
    action          VARCHAR(50) NOT NULL,
    field_name      VARCHAR(100),
    old_value       TEXT,
    new_value       TEXT,
    actor           VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_entity ON audit_event (entity_type, entity_id);
CREATE INDEX idx_audit_created ON audit_event (created_at DESC);
