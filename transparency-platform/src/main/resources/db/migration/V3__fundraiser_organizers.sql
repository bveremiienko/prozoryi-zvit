CREATE TABLE fundraiser_organizer (
    id              BIGSERIAL PRIMARY KEY,
    slug            VARCHAR(120) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    edrpou          VARCHAR(20),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    website         VARCHAR(1000),
    verified        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_fundraiser_organizer_slug ON fundraiser_organizer (slug);

INSERT INTO fundraiser_organizer (id, slug, name, description, edrpou, email, phone, website, verified, created_at) VALUES
(1, 'bf-zdorovya-ukrainy',
 'БФ Здоровя України',
 'Благодійний фонд, що підтримує медичні заклади та закупівлю обладнання для лікарень України.',
 '12345678', 'info@zdorovya-demo.org.ua', '+380441234567', 'https://example.org/health', TRUE, '2024-01-10 10:00:00'),
(2, 'osbb-sonyachny-kyiv',
 'ОСББ Сонячний',
 'Об''єднання співвласників будинку в Києві. Організовує колективні ініціативи для стійкості будинку.',
 NULL, 'board@osbb-sonyachny.demo', NULL, NULL, FALSE, '2025-06-01 09:00:00'),
(3, 'go-shlyakh-veterana',
 'ГО Шлях ветерана',
 'Громадська організація: перекваліфікація ветеранів, IT-навчання та соціальна підтримка.',
 '87654321', 'contact@veteran-path.demo', '+380501112233', 'https://example.org/veterans', TRUE, '2023-03-15 08:00:00');

ALTER TABLE campaign ADD COLUMN organizer_id BIGINT REFERENCES fundraiser_organizer (id);

UPDATE campaign SET organizer_id = 1 WHERE id = 1;
UPDATE campaign SET organizer_id = 2 WHERE id = 2;
UPDATE campaign SET organizer_id = 3 WHERE id = 3;

ALTER TABLE campaign ALTER COLUMN organizer_id SET NOT NULL;

ALTER TABLE app_user ADD COLUMN fundraiser_organizer_id BIGINT REFERENCES fundraiser_organizer (id);
