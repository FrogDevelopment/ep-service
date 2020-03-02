CREATE TABLE planning
(
    planning_id       SERIAL PRIMARY KEY,
    start_datetime    TIMESTAMP NOT NULL,
    end_datetime      TIMESTAMP NOT NULL,
    expected_bracelet NUMERIC   NULL,
    expected_fouille  NUMERIC   NULL,
    expected_litiges  NUMERIC   NULL,
    description       TEXT
);

CREATE TABLE teams
(
    team_id SERIAL PRIMARY KEY,
    name    TEXT NOT NULL,
    code    TEXT NOT NULL
        CONSTRAINT unique_code UNIQUE
);

CREATE TABLE volunteers
(
    volunteer_id  SERIAL PRIMARY KEY,
    volunteer_ref TEXT NOT NULL
        CONSTRAINT unique_ref UNIQUE,
    last_name     TEXT NOT NULL,
    first_name    TEXT NOT NULL,
    friends_group TEXT NULL,
    email         TEXT NOT NULL
        CONSTRAINT unique_email UNIQUE,
    phone_number  TEXT NOT NULL
        CONSTRAINT unique_phone_number UNIQUE,
    team_code     TEXT REFERENCES teams (code),
    referent      BOOLEAN DEFAULT FALSE
);

CREATE TABLE schedules
(
    schedule_id   SERIAL PRIMARY KEY,
    from_datetime TIMESTAMP NOT NULL,
    to_datetime   TIMESTAMP NOT NULL,
    location      TEXT      NOT NULL,
    team_code     TEXT      NOT NULL REFERENCES teams (code),
    volunteer_ref TEXT      NULL REFERENCES volunteers (volunteer_ref)
);
