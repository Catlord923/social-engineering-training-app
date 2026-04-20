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
                           type TEXT NOT NULL CHECK (type IN ('EMAIL', 'SMS', 'POPUP', 'SOCIAL', 'BONUS')),
                           title TEXT NOT NULL,
                           intro_text TEXT,
                           learning_outcomes TEXT NOT NULL
);

-- SCENARIO STAGES
CREATE TABLE scenario_stages (
                                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                                 scenario_id INTEGER NOT NULL,
                                 stage_order INTEGER NOT NULL,
                                 stage_title TEXT,
                                 prompt TEXT NOT NULL,
                                 is_terminal INTEGER NOT NULL CHECK (is_terminal IN (0,1)) DEFAULT 0,
                                 is_success INTEGER CHECK (is_success IN (0,1)),
                                 feedback_title TEXT,
                                 feedback_text TEXT,
                                 UNIQUE (scenario_id, stage_order),
                                 FOREIGN KEY (scenario_id) REFERENCES scenarios(id)
);

-- SCENARIO UI ELEMENTS
CREATE TABLE scenario_elements (
                                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                                   stage_id INTEGER NOT NULL,
                                   element_order INTEGER NOT NULL,
                                   element_type TEXT NOT NULL CHECK (
                                       element_type IN (
                                                        'EMAIL_FROM',
                                                        'EMAIL_SUBJECT',
                                                        'EMAIL_BODY',
                                                        'SMS_SENDER',
                                                        'SMS_MESSAGE',
                                                        'POPUP_TITLE',
                                                        'POPUP_BODY',
                                                        'CHAT_SENDER',
                                                        'CHAT_MESSAGE',
                                                        'INFO_BOX',
                                                        'WARNING_BOX',
                                                        'LINK'
                                           )
                                       ),
                                   label TEXT,
                                   value TEXT NOT NULL,
                                   extra_value TEXT,
                                   UNIQUE (stage_id, element_order),
                                   FOREIGN KEY (stage_id) REFERENCES scenario_stages(id)
);

-- SCENARIO OPTIONS
CREATE TABLE scenario_options (
                                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                                  stage_id INTEGER NOT NULL,
                                  option_order INTEGER NOT NULL,
                                  option_text TEXT NOT NULL,
                                  next_stage_order INTEGER,
                                  immediate_feedback TEXT,
                                  UNIQUE (stage_id, option_order),
                                  FOREIGN KEY (stage_id) REFERENCES scenario_stages(id)
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