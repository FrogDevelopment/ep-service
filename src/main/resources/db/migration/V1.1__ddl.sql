CREATE TABLE IF NOT EXISTS export_schedules
(
    start_datetime TIMESTAMP NOT NULL,
    end_datetime   TIMESTAMP NOT NULL,
    location       TEXT      NOT NULL,
    volunteer_ref  TEXT      NULL REFERENCES volunteers (volunteer_ref)
);

CREATE TABLE IF NOT EXISTS export_version
(
    version INTEGER NOT NULL,
    date_export TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO export_version
VALUES (0);
