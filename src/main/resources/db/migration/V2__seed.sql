-- Stores
INSERT INTO store (code, name) VALUES
('410', 'H-E-B #410'), ('001', 'H-E-B #001');

-- People
INSERT INTO patient (name, dob) VALUES
('Jane Doe', '1989-04-12'),
('Mark Smith', '1978-11-03');

INSERT INTO prescriber (name, dea_number) VALUES
('Dr. Avery Chen', 'AC1234567');

-- Drugs
INSERT INTO drug (ndc, name, days_supply_default, is_controlled, schedule) VALUES
('0001-0001', 'Amoxicillin 500mg', 30, false, null),
('0001-0002', 'Metformin 500mg', 30, false, null),
('0001-0003', 'Oxycodone 5mg', 30, true, 'II');

-- Prescriptions
-- Eligible: 30-day supply, last fill 31 days ago, refills left
INSERT INTO prescription (rx_number, patient_id, prescriber_id, drug_id, quantity, refills_authorized, refills_used, written_date, expires_at, last_fill_at, directions, is_active)
VALUES ('RX1001', 1, 1, 1, 30, 3, 1, CURRENT_DATE - INTERVAL '100 days', CURRENT_DATE + INTERVAL '200 days', CURRENT_DATE - INTERVAL '31 days', 'Take 1 capsule twice daily', true);

-- Too soon: last fill 5 days ago
INSERT INTO prescription (rx_number, patient_id, prescriber_id, drug_id, quantity, refills_authorized, refills_used, written_date, expires_at, last_fill_at, directions, is_active)
VALUES ('RX2001', 1, 1, 1, 30, 3, 1, CURRENT_DATE - INTERVAL '100 days', CURRENT_DATE + INTERVAL '200 days', CURRENT_DATE - INTERVAL '5 days', 'Take 1 capsule twice daily', true);

-- No inventory: metformin
INSERT INTO prescription (rx_number, patient_id, prescriber_id, drug_id, quantity, refills_authorized, refills_used, written_date, expires_at, last_fill_at, directions, is_active)
VALUES ('RX3001', 2, 1, 2, 30, 2, 0, CURRENT_DATE - INTERVAL '50 days', CURRENT_DATE + INTERVAL '300 days', NULL, 'Take 1 tablet twice daily', true);

-- Controlled Schedule II: should be denied regardless of refills
INSERT INTO prescription (rx_number, patient_id, prescriber_id, drug_id, quantity, refills_authorized, refills_used, written_date, expires_at, last_fill_at, directions, is_active)
VALUES ('RX4001', 2, 1, 3, 30, 5, 0, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '200 days', NULL, 'Take 1 tablet every 6 hours PRN', true);

-- Inventory
-- Store 410 has plenty of Amoxicillin (eligible case)
INSERT INTO inventory (store_id, drug_id, on_hand, reserved)
SELECT id, 1, 100, 0 FROM store WHERE code='410';

-- Store 410 has zero Metformin (no inventory case)
INSERT INTO inventory (store_id, drug_id, on_hand, reserved)
SELECT id, 2, 0, 0 FROM store WHERE code='410';

-- Store 410 has Oxycodone on-hand (but controlled rule will deny anyway)
INSERT INTO inventory (store_id, drug_id, on_hand, reserved)
SELECT id, 3, 20, 0 FROM store WHERE code='410'; 