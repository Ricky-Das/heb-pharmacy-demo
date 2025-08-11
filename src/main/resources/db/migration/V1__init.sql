CREATE TABLE patient (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  dob DATE NOT NULL
);

CREATE TABLE prescriber (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  dea_number TEXT
);

CREATE TABLE drug (
  id BIGSERIAL PRIMARY KEY,
  ndc TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL,
  days_supply_default INT NOT NULL,
  is_controlled BOOLEAN NOT NULL DEFAULT FALSE,
  schedule TEXT
);

CREATE TABLE store (
  id BIGSERIAL PRIMARY KEY,
  code TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL
);

CREATE TABLE prescription (
  id BIGSERIAL PRIMARY KEY,
  rx_number TEXT NOT NULL UNIQUE,
  patient_id BIGINT NOT NULL REFERENCES patient(id),
  prescriber_id BIGINT REFERENCES prescriber(id),
  drug_id BIGINT NOT NULL REFERENCES drug(id),
  quantity INT NOT NULL,
  refills_authorized INT NOT NULL,
  refills_used INT NOT NULL DEFAULT 0,
  written_date DATE NOT NULL,
  expires_at DATE NOT NULL,
  last_fill_at DATE,
  directions TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE inventory (
  store_id BIGINT NOT NULL REFERENCES store(id),
  drug_id BIGINT NOT NULL REFERENCES drug(id),
  on_hand INT NOT NULL,
  reserved INT NOT NULL DEFAULT 0,
  PRIMARY KEY (store_id, drug_id)
);

CREATE TABLE refill_request (
  id BIGSERIAL PRIMARY KEY,
  prescription_id BIGINT NOT NULL REFERENCES prescription(id),
  store_id BIGINT NOT NULL REFERENCES store(id),
  status TEXT NOT NULL,         -- REQUESTED, DENIED, RESERVED
  reason_code TEXT,             -- TOO_SOON, NO_REFILLS, RX_EXPIRED, NO_INVENTORY, CONTROLLED_NO_REFILL
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE audit_log (
  id BIGSERIAL PRIMARY KEY,
  entity TEXT NOT NULL,
  entity_id BIGINT NOT NULL,
  event TEXT NOT NULL,
  details JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE outbox (
  id BIGSERIAL PRIMARY KEY,
  aggregate_type TEXT NOT NULL,
  aggregate_id BIGINT NOT NULL,
  event_type TEXT NOT NULL,
  payload_json TEXT NOT NULL,
  published_at TIMESTAMP
);

CREATE UNIQUE INDEX uq_refill_req_per_rx_per_day
  ON refill_request (prescription_id, (date_trunc('day', created_at))); 