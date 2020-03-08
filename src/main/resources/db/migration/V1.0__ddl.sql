CREATE TABLE teams
(
    team_id SERIAL PRIMARY KEY,
    name    TEXT NOT NULL,
    code    TEXT NOT NULL
        CONSTRAINT unique_teams_code UNIQUE
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
        CONSTRAINT unique_volunteer_email UNIQUE,
    phone_number  TEXT NOT NULL
        CONSTRAINT unique_volunteer_phone_number UNIQUE,
    team_code     TEXT REFERENCES teams (code),
    referent      BOOLEAN DEFAULT FALSE
);

-- depending of the current edition, we calculate the datetimes for each dayOfWeek
CREATE TABLE edition
(
    day_of_week TEXT NOT NULL
        CONSTRAINT unique_edition_day_of_week UNIQUE,
    day_date    DATE NULL
);

CREATE TABLE timetables
(
    timetable_id      SERIAL PRIMARY KEY,
    timetable_ref     TEXT    NOT NULL
        CONSTRAINT unique_timetable_ref UNIQUE,
    day_of_week       TEXT    NOT NULL,
    start_time        TIME    NOT NULL,
    end_time          TIME    NOT NULL,
    expected_bracelet NUMERIC NULL,
    expected_fouille  NUMERIC NULL,
    expected_litiges  NUMERIC NULL,
    description       TEXT
);

CREATE TABLE schedules
(
    schedule_id   SERIAL PRIMARY KEY,
    location      TEXT NOT NULL,
    timetable_ref TEXT NOT NULL REFERENCES timetables (timetable_ref),
    volunteer_ref TEXT NULL REFERENCES volunteers (volunteer_ref)
);
