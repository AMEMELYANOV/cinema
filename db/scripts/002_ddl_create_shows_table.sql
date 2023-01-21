CREATE TABLE IF NOT EXISTS shows (
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  description TEXT,
  postername VARCHAR
);

COMMENT ON TABLE shows IS 'Сеансы';
COMMENT ON COLUMN shows.id IS 'Идентификатор сеанса';
COMMENT ON COLUMN shows.name IS 'Наименование сеанса';
COMMENT ON COLUMN shows.description IS 'Подробное описание сеанса';