CREATE TABLE teams
(
    team_id SERIAL PRIMARY KEY,
    name    TEXT NOT NULL,
    code    TEXT NOT NULL
        CONSTRAINT unique_code UNIQUE
);

CREATE TABLE members
(
    member_id    SERIAL PRIMARY KEY,
    last_name    TEXT NOT NULL,
    first_name   TEXT NOT NULL,
    email        TEXT NOT NULL
        CONSTRAINT unique_email UNIQUE,
    phone_number TEXT NOT NULL,
    team_code    TEXT REFERENCES teams (code),
    referent     BOOLEAN DEFAULT FALSE
);

CREATE TABLE schedules
(
    schedule_id   SERIAL PRIMARY KEY,
    from_datetime TIMESTAMP NOT NULL,
    to_datetime   TIMESTAMP NOT NULL,
    location      TEXT      NOT NULL,
    team_code TEXT REFERENCES teams (code)
);
