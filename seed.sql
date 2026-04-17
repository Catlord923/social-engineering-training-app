INSERT INTO theory_pages (page_order, title, body)
VALUES (1, 'What is Social Engineering?',
        'Social engineering is the manipulation of users to perform unsafe actions...');

INSERT INTO scenarios (scenario_order, type, title, prompt, explanation, interaction_mode)
VALUES (1, 'EMAIL', 'Suspicious Bank Email',
        'What is the safest action?',
        'This email uses urgency and a fake link to trick users.',
        'MULTIPLE_CHOICE');

INSERT INTO scenario_elements (scenario_id, element_order, element_type, label, value, extra_value)
VALUES
    (1, 1, 'SENDER', 'From', 'support@bank-secure.com', NULL),
    (1, 2, 'SUBJECT', 'Subject', 'Urgent: Verify your account', NULL),
    (1, 3, 'BODY', NULL, 'Your account will be locked if you do not act now.', NULL),
    (1, 4, 'LINK', NULL, 'Verify Account', 'http://fake-bank-login.com');

INSERT INTO scenario_options (scenario_id, option_order, option_text, is_correct, feedback)
VALUES
    (1, 1, 'Click the link immediately', 0, 'This is unsafe. The link is suspicious.'),
    (1, 2, 'Ignore and delete the email', 1, 'Correct. Avoid interacting with suspicious emails.'),
    (1, 3, 'Reply to the sender', 0, 'Do not engage with attackers.');

INSERT INTO quiz_questions (question_order, question_text, explanation)
VALUES (1, 'What is a common sign of phishing?', 'Phishing often uses urgency and fake links.');

INSERT INTO quiz_options (question_id, option_order, option_text, is_correct)
VALUES
    (1, 1, 'Urgent messages asking for action', 1),
    (1, 2, 'Normal company emails', 0),
    (1, 3, 'Messages from known contacts', 0);