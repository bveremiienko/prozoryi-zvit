-- Demo campaigns (fixed timestamps for H2/PostgreSQL compatibility)

INSERT INTO campaign (id, slug, title, description, organizer_name, edrpou, target_amount,
                      declared_collected_amount, currency, status, external_donation_url,
                      external_website_url, started_at, next_report_due, created_at, updated_at)
VALUES
(1, 'dopomoga-medychne-obladnannya',
 'Допомога лікарні: медичне обладнання',
 'Збір на закупівлю дефібриляторів та моніторів для міської лікарні. Усі витрати публікуються щотижня.',
 'БФ Здоровя України', '12345678', 500000.00, 387500.00, 'UAH', 'ACTIVE',
 'https://send.monobank.ua/jar/example1', 'https://example.org/health', '2025-11-01', '2026-06-01',
 '2025-11-01 10:00:00', '2026-05-01 12:00:00'),
(2, 'generator-osbb-kyiv',
 'Генератор для ОСББ (Київ)',
 'Колективний збір мешканців на резервне живлення під час відключень.',
 'ОСББ Сонячний', NULL, 180000.00, 92000.00, 'UAH', 'ACTIVE',
 'https://send.monobank.ua/jar/example2', NULL, '2026-01-15', '2026-02-01',
 '2026-01-15 09:00:00', '2026-05-01 12:00:00'),
(3, 'navchannya-veteraniv-it',
 'Навчання ветеранів IT-спеціальностям',
 'Оплата курсів з QA та backend для 15 ветеранів. Проєкт завершено — фінальний звіт опубліковано.',
 'ГО Шлях ветерана', '87654321', 250000.00, 250000.00, 'UAH', 'CLOSED',
 'https://send.monobank.ua/jar/example3', 'https://example.org/veterans', '2025-06-01', NULL,
 '2025-06-01 08:00:00', '2026-04-01 10:00:00');

INSERT INTO expense_plan_line (campaign_id, category, planned_amount, currency, note, sort_order) VALUES
(1, 'PROCUREMENT', 420000.00, 'UAH', 'Дефібрилятори та монітори', 0),
(1, 'LOGISTICS', 35000.00, 'UAH', 'Доставка та митне оформлення', 1),
(1, 'SERVICES', 45000.00, 'UAH', 'Монтаж та навчання персоналу', 2),
(2, 'PROCUREMENT', 150000.00, 'UAH', 'Генератор 8 кВт', 0),
(2, 'SERVICES', 30000.00, 'UAH', 'Підключення та ТО', 1),
(3, 'SERVICES', 200000.00, 'UAH', 'Оплата навчальних програм', 0),
(3, 'OTHER', 50000.00, 'UAH', 'Стипендії під час навчання', 1);

INSERT INTO campaign_report (id, campaign_id, period_from, period_to, summary, submitted_at, report_hash) VALUES
(1, 1, '2025-11-01', '2025-11-30',
 'Закуплено 2 дефібрилятори, оплачено доставку. Залишок направлено на монітори — очікується поставка.',
 '2025-12-05 14:00:00', '1111111111111111111111111111111111111111111111111111111111111111'),
(2, 1, '2025-12-01', '2025-12-31',
 'Отримано 4 монітори пацієнта, встановлено в відділенні терапії. Акти підписано лікарнею.',
 '2026-01-10 11:00:00', '2222222222222222222222222222222222222222222222222222222222222222'),
(3, 3, '2025-09-01', '2025-12-31',
 '15 ветеранів завершили курси, 12 працевлаштовано. Фінальний звіт передано донорам.',
 '2026-02-01 16:00:00', '3333333333333333333333333333333333333333333333333333333333333333');

INSERT INTO report_line (report_id, category, amount, currency, description) VALUES
(1, 'PROCUREMENT', 280000.00, 'UAH', '2 дефібрилятори'),
(1, 'LOGISTICS', 22000.00, 'UAH', 'Доставка'),
(2, 'PROCUREMENT', 195000.00, 'UAH', '4 монітори'),
(2, 'SERVICES', 38000.00, 'UAH', 'Монтаж'),
(3, 'SERVICES', 198000.00, 'UAH', 'Оплата курсів'),
(3, 'OTHER', 48000.00, 'UAH', 'Стипендії');

INSERT INTO audit_event (entity_type, entity_id, action, field_name, old_value, new_value, actor, created_at) VALUES
('CAMPAIGN', 1, 'CREATE', NULL, NULL, NULL, 'system', '2025-11-01 10:00:00'),
('CAMPAIGN', 1, 'UPDATE', 'declaredCollectedAmount', '350000', '387500', 'organizer@demo.local', '2026-03-01 09:00:00'),
('CAMPAIGN', 1, 'REPORT_ADD', 'reportId', NULL, '1', 'organizer@demo.local', '2025-12-05 14:00:00'),
('CAMPAIGN', 1, 'REPORT_ADD', 'reportId', NULL, '2', 'organizer@demo.local', '2026-01-10 11:00:00'),
('CAMPAIGN', 2, 'CREATE', NULL, NULL, NULL, 'system', '2026-01-15 09:00:00'),
('CAMPAIGN', 2, 'PLAN_ADD', 'category', NULL, 'PROCUREMENT', 'organizer@demo.local', '2026-01-20 10:00:00'),
('CAMPAIGN', 3, 'CREATE', NULL, NULL, NULL, 'system', '2025-06-01 08:00:00'),
('REPORT', 3, 'CREATE', NULL, NULL, NULL, 'organizer@demo.local', '2026-02-01 16:00:00');

