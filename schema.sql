-- THEORY
CREATE TABLE theory_pages (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              page_order INTEGER NOT NULL UNIQUE,
                              title TEXT NOT NULL,
                              body TEXT NOT NULL
);

-- SCENARIOS
CREATE TABLE scenarios (
                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                           scenario_order INTEGER NOT NULL UNIQUE,
                           type TEXT NOT NULL CHECK (type IN ('EMAIL', 'SMS', 'WEBSITE', 'BONUS')),
                           title TEXT NOT NULL,
                           prompt TEXT NOT NULL,
                           explanation TEXT NOT NULL,
                           interaction_mode TEXT NOT NULL CHECK (interaction_mode IN ('MULTIPLE_CHOICE', 'BONUS_FORM'))
);

-- SCENARIO UI ELEMENTS
CREATE TABLE scenario_elements (
                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                   scenario_id INTEGER NOT NULL,
                                   element_order INTEGER NOT NULL,
                                   element_type TEXT NOT NULL,
                                   label TEXT,
                                   value TEXT NOT NULL,
                                   extra_value TEXT,
                                   FOREIGN KEY (scenario_id) REFERENCES scenarios(id)
);

-- SCENARIO ANSWERS
CREATE TABLE scenario_options (
                                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                                  scenario_id INTEGER NOT NULL,
                                  option_order INTEGER NOT NULL,
                                  option_text TEXT NOT NULL,
                                  is_correct INTEGER NOT NULL CHECK (is_correct IN (0,1)),
                                  feedback TEXT NOT NULL,
                                  FOREIGN KEY (scenario_id) REFERENCES scenarios(id)
);

-- QUIZ
CREATE TABLE quiz_questions (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                question_order INTEGER NOT NULL UNIQUE,
                                question_text TEXT NOT NULL,
                                explanation TEXT
);

-- QUIZ ANSWERS
CREATE TABLE quiz_options (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              question_id INTEGER NOT NULL,
                              option_order INTEGER NOT NULL,
                              option_text TEXT NOT NULL,
                              is_correct INTEGER NOT NULL CHECK (is_correct IN (0,1)),
                              FOREIGN KEY (question_id) REFERENCES quiz_questions(id)
);