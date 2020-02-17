CREATE TABLE teams
(
    team_id      SERIAL PRIMARY KEY,
    name         TEXT NOT NULL,
    abbreviation TEXT NOT NULL
);

CREATE TABLE members
(
    member_id    SERIAL PRIMARY KEY,
    last_name    TEXT NOT NULL,
    first_name   TEXT NOT NULL,
    email        TEXT NOT NULL
        CONSTRAINT unique_email UNIQUE,
    phone_number TEXT NOT NULL,
    team_id      INTEGER REFERENCES teams (team_id),
    referent     BOOLEAN DEFAULT FALSE
);

CREATE TABLE schedules
(
    schedule_id   SERIAL PRIMARY KEY,
    from_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    to_datetime   TIMESTAMP WITH TIME ZONE NOT NULL,
    location      TEXT                     NOT NULL,
    team_id       INTEGER                  NOT NULL REFERENCES teams (team_id)
);
