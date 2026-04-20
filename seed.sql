INSERT INTO theory_pages (page_order, title, body) VALUES
(
    1,
    'What Is Social Engineering?',
    'Social engineering is a type of cyber attack that targets people rather than software. Instead of breaking into a system through technical vulnerabilities, attackers manipulate users into revealing sensitive information, clicking dangerous links, downloading malicious files, or granting access they should not give.

These attacks succeed by exploiting human trust, error, curiosity, fear, and urgency. In many cases, the victim believes the message, website, or request is legitimate. Because of this, even well-protected systems can still be compromised when a user is deceived into helping the attacker.

In simple terms: social engineering is psychological manipulation used to make people perform unsafe actions.'
),
(
    2,
    'Why Social Engineering Works',
    'Social engineering works because people often make quick decisions based on emotion, routine, or limited information. Attackers take advantage of this by creating messages that feel urgent, important, or familiar.

Common psychological triggers include:
- Urgency: "Act now or your account will be locked"
- Authority: pretending to be a manager, bank, or official organisation
- Fear: warning about a problem or threat
- Curiosity: tempting the victim with surprising information
- Reward: offering something free or beneficial

These attacks do not rely only on technical skill. They rely on human behaviour. This is why users are often described as the weakest link in cybersecurity, and why awareness is an important part of defence.'
),
(
    3,
    'Common Types of Social Engineering Attacks',
    'There are several common forms of social engineering:

Phishing:
Fake emails designed to trick users into clicking a malicious link, opening an attachment, or entering login details into a fake website.

Smishing:
The same idea as phishing, but delivered through SMS or text messages.

Vishing:
Voice phishing. The attacker uses a phone call and pretends to be from a trusted source, such as a bank, company, or support team.

Spear phishing:
A more targeted phishing attack aimed at a specific person. These messages are often more convincing because they are personalised.

Baiting:
The attacker offers something attractive, such as free content, a reward, or a useful file, in order to get the victim to interact with malicious content.

Pretexting:
The attacker invents a believable story or identity to gain trust and obtain information.

Although the method may change, the goal is usually the same: get the victim to trust the attacker and make a poor security decision.'
),
(
    4,
    'Warning Signs to Look Out For',
    'Many social engineering attacks can be detected if the user slows down and checks for warning signs. Common red flags include:

- messages that create panic or urgency
- requests for passwords, codes, or personal information
- suspicious links or unexpected attachments
- unusual sender addresses or phone numbers
- poor spelling, strange wording, or awkward formatting
- offers that seem too good to be true
- messages pretending to be from trusted organisations but asking you to act immediately
- websites that look slightly wrong or have unusual URLs

A useful habit is to stop and ask:
Who sent this?
Why are they asking me to act so quickly?
Does this request make sense?
Can I verify it in another way?

Taking a few extra seconds to question a message can prevent a serious mistake.'
),
(
    5,
    'How To Stay Safe',
    'The best defence against social engineering is careful behaviour. Good security habits can reduce the chance of becoming a victim.

Follow these basic rules:
- do not click links or open attachments from suspicious messages
- verify requests through a trusted channel before responding
- never share passwords or verification codes
- check email addresses, phone numbers, and website URLs carefully
- use strong passwords and enable multi-factor authentication
- be cautious with messages that use fear, pressure, or rewards
- report suspicious messages instead of interacting with them

Remember: attackers want quick reactions. A safe user pauses, checks, and verifies first.

Think before you click. Verify before you trust. Report when something feels wrong.'
);

-- EMAIL PHISHING SCENARIO
INSERT INTO scenarios (scenario_order, type, title, intro_text, learning_outcomes)
VALUES (
           1,
           'EMAIL',
           'Email Phishing',
           'You receive an urgent email claiming to be from your bank.',
           'Suspicious sender
       Urgency language
       Generic greeting
       Fake links'
       );

-- EMAIL PHISHING SCENARIO STAGES
INSERT INTO scenario_stages
(scenario_id, stage_order, stage_title, prompt, is_terminal, is_success, feedback_title, feedback_text)
VALUES
    (1, 1, 'Inbox', 'Review the email and decide what to do.', 0, NULL, NULL, NULL),

    (1, 2, 'Inspection', 'You check the sender details more carefully.', 0, NULL, NULL, NULL),

    (1, 3, 'Result', 'You clicked the link.', 1, 0,
     'Unsafe choice',
     'This was a phishing email. Warning signs included a suspicious sender address, urgency, a generic greeting, and a fake-looking link.'),

    (1, 4, 'Result', 'You deleted the email.', 1, 1,
     'Safe choice',
     'Deleting the email avoided the immediate risk. However, reporting suspicious emails is even better because it helps protect other users.'),

    (1, 5, 'Result', 'You reported the email.', 1, 1,
     'Best choice',
     'Correct. Reporting the email is the strongest response. The message showed multiple phishing indicators, including suspicious sender details, urgency, a generic greeting, and a fake link.');

-- EMAIL PHISHING SCENARIO STAGE 1 ELEMENTS
INSERT INTO scenario_elements (stage_id, element_order, element_type, label, value, extra_value)
VALUES
    (1, 1, 'EMAIL_FROM', 'From', 'Bank Security Team <security-alerts@secure-bankverify.com>', NULL),
    (1, 2, 'EMAIL_SUBJECT', 'Subject', 'Urgent: Verify your account now', NULL),
    (1, 3, 'EMAIL_BODY', NULL,
     'Dear Customer,

     We detected unusual activity on your bank account.
     To avoid temporary suspension, verify your account immediately using the secure link below:

     https://online-secure-bankverify.com/login

     Failure to act within 24 hours may result in restricted access.

     Regards,
     Bank Security Team',
     NULL);

-- EMAIL PHISHING SCENARIO STAGE 2 ELEMENTS
INSERT INTO scenario_elements (stage_id, element_order, element_type, label, value, extra_value)
VALUES
    (2, 1, 'EMAIL_FROM', 'From', 'Bank Security Team <security-alerts@secure-bankverify.com>', NULL),
    (2, 2, 'EMAIL_SUBJECT', 'Subject', 'Urgent: Verify your account now', NULL),
    (2, 3, 'EMAIL_BODY', NULL,
     'Dear Customer,

     We detected unusual activity on your bank account.
     To avoid temporary suspension, verify your account immediately using the secure link below:

     https://online-secure-bankverify.com/login

     Failure to act within 24 hours may result in restricted access.

     Regards,
     Bank Security Team',
     NULL),
    (2, 4, 'WARNING_BOX', 'Warning signs found',
     'Suspicious sender domain
     Generic greeting
     Pressure to act quickly
     Unusual bank link',
     NULL);

-- EMAIL PHISHING SCENARIO OPTIONS
INSERT INTO scenario_options (stage_id, option_order, option_text, next_stage_order, immediate_feedback)
VALUES
    (1, 1, 'Click link', 3, NULL),
    (1, 2, 'Check sender address', 2, 'Good step. Inspecting the sender can reveal phishing attempts.'),
    (1, 3, 'Delete', 4, NULL),
    (1, 4, 'Report email', 5, NULL),

    (2, 1, 'Click link', 3, NULL),
    (2, 2, 'Delete', 4, NULL),
    (2, 3, 'Report email', 5, NULL);

-- SMISHING SCENARIO
INSERT INTO scenarios (scenario_order, type, title, intro_text, learning_outcomes)
VALUES (
           2,
           'SMS',
           'SMS Smishing',
           'You receive a text message about a delayed parcel delivery.',
           'Mobile attacks
       Shortened links
       Emotional triggers
       Verification through official channels'
       );

-- SMISHING SCENARIO STAGES
INSERT INTO scenario_stages
(scenario_id, stage_order, stage_title, prompt, is_terminal, is_success, feedback_title, feedback_text)
VALUES
    (2, 1, 'Messages', 'A delivery text arrives on your phone.', 0, NULL, NULL, NULL),

    (2, 2, 'Result', 'You tapped the link.', 1, 0,
     'Unsafe choice',
     'This message used a delivery problem to trigger a quick reaction. Shortened or unusual links in SMS messages are a common smishing tactic.'),

    (2, 3, 'Result', 'You ignored the message.', 1, 1,
     'Safe choice',
     'Ignoring the message avoided immediate risk. A stronger response is to verify the delivery using the courier’s official app or website.'),

    (2, 4, 'Result', 'You checked the official courier service separately.', 1, 1,
     'Best choice',
     'Correct. Verifying through the official app or website is the safest approach. It avoids interacting with a potentially malicious link.');

-- SMISHING SCENARIO ELEMENTS
INSERT INTO scenario_elements (stage_id, element_order, element_type, label, value, extra_value)
VALUES
    (6, 1, 'SMS_SENDER', 'From', 'ParcelTrack Alerts', NULL),
    (6, 2, 'SMS_MESSAGE', NULL,
     'Your parcel delivery has been delayed. Confirm your details now to avoid return: http://trk-package-help.com',
     NULL);

-- SMISHING SCENARIO OPTIONS
INSERT INTO scenario_options (stage_id, option_order, option_text, next_stage_order, immediate_feedback)
VALUES
    (6, 1, 'Tap link', 2, NULL),
    (6, 2, 'Ignore', 3, NULL),
    (6, 3, 'Check official courier app/site', 4, NULL);

-- TECH SUPPORT SCENARIO
INSERT INTO scenarios (scenario_order, type, title, intro_text, learning_outcomes)
VALUES (
           3,
           'POPUP',
           'Tech Support Scam',
           'A pop-up warns that your device is infected and tells you to act immediately.',
           'Fear tactics
       Fake urgency
       Rogue software risk
       Do not trust alarming popups'
       );

-- TECH SUPPORT SCENARIO STAGES
INSERT INTO scenario_stages
(scenario_id, stage_order, stage_title, prompt, is_terminal, is_success, feedback_title, feedback_text)
VALUES
    (3, 1, 'Browser Popup', 'A frightening popup appears while browsing.', 0, NULL, NULL, NULL),

    (3, 2, 'Result', 'You called the number.', 1, 0,
     'Unsafe choice',
     'Tech support scams often use alarming popups and fake phone numbers to trick users into giving away money, personal data, or device access.'),

    (3, 3, 'Result', 'You closed the popup.', 1, 1,
     'Best choice',
     'Correct. Closing the popup without interacting is the safest response. Real security warnings do not usually demand that you call a random number immediately.'),

    (3, 4, 'Result', 'You installed the suggested software.', 1, 0,
     'Unsafe choice',
     'Installing software promoted by a suspicious popup can lead to malware or remote-access compromise.');

-- TECH SUPPORT SCENARIO ELEMENTS
INSERT INTO scenario_elements (stage_id, element_order, element_type, label, value, extra_value)
VALUES
    (10, 1, 'POPUP_TITLE', NULL, 'Windows Alert: Your device is infected!', NULL),
    (10, 2, 'POPUP_BODY', NULL,
     'Immediate action required.
     Call Microsoft Certified Support now at 0800-555-0199.
     Do not shut down your device.
     Install SecurityFix to remove detected threats.',
     NULL);

-- TECH SUPPORT SCENARIO OPTIONS
INSERT INTO scenario_options (stage_id, option_order, option_text, next_stage_order, immediate_feedback)
VALUES
    (10, 1, 'Call number', 2, NULL),
    (10, 2, 'Close popup', 3, NULL),
    (10, 3, 'Install suggested software', 4, NULL);

-- SOCIAL MEDIA SCENARIO
INSERT INTO scenarios (scenario_order, type, title, intro_text, learning_outcomes)
VALUES (
           4,
           'SOCIAL',
           'Social Media Impersonation',
           'A friend sends you a message asking whether a photo in a link is you.',
           'Compromised accounts
       Trust exploitation
       Verify identity elsewhere
       Be cautious with unexpected links'
       );

-- SOCIAL MEDIA SCENARIO STAGES
INSERT INTO scenario_stages
(scenario_id, stage_order, stage_title, prompt, is_terminal, is_success, feedback_title, feedback_text)
VALUES
    (4, 1, 'Chat Message', 'A message arrives from a friend on social media.', 0, NULL, NULL, NULL),

    (4, 2, 'Result', 'You opened the link.', 1, 0,
     'Unsafe choice',
     'Attackers often hijack real accounts to send malicious links. Because the message comes from someone familiar, users may trust it too quickly.'),

    (4, 3, 'Result', 'You asked your friend separately.', 1, 1,
     'Best choice',
     'Correct. Verifying through another channel is the strongest response because it confirms whether the account was compromised.'),

    (4, 4, 'Result', 'You ignored the message.', 1, 1,
     'Safe choice',
     'Ignoring the message avoided the risk. Verifying separately would give you even more confidence about whether the friend’s account was compromised.');

-- SOCIAL MEDIA SCENARIO ELEMENTS
INSERT INTO scenario_elements (stage_id, element_order, element_type, label, value, extra_value)
VALUES
    (14, 1, 'CHAT_SENDER', 'Friend', 'Alex', NULL),
    (14, 2, 'CHAT_MESSAGE', NULL,
     'Is this you in this photo? http://photo-tagged-you.net',
     NULL);

-- SOCIAL MEDIA SCENARIO OPTIONS
INSERT INTO scenario_options (stage_id, option_order, option_text, next_stage_order, immediate_feedback)
VALUES
    (14, 1, 'Open link', 2, NULL),
    (14, 2, 'Ask friend separately', 3, NULL),
    (14, 3, 'Ignore', 4, NULL);