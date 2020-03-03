CREATE TABLE schedules
(
    schedules_id      SERIAL PRIMARY KEY,
    schedules_ref     TEXT    NOT NULL
        CONSTRAINT unique_schedules_ref UNIQUE,
    day_of_week       TEXT    NOT NULL,
    start_time        TIME    NOT NULL,
    end_time          TIME    NOT NULL,
    expected_bracelet NUMERIC NULL,
    expected_fouille  NUMERIC NULL,
    expected_litiges  NUMERIC NULL,
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
        CONSTRAINT unique_volunteer_ref UNIQUE,
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

CREATE TABLE timetable
(
    timetable_id  SERIAL PRIMARY KEY,
    location      TEXT    NOT NULL,
    planning_ref  NUMERIC NOT NULL REFERENCES schedules (schedules_ref),
    volunteer_ref TEXT    NULL REFERENCES volunteers (volunteer_ref)
);
