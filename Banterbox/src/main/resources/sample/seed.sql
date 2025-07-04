-- Insert 10 users
INSERT INTO users (id, username, password, role)
VALUES (gen_random_uuid(), 'harry', 'hashed_pwd1', 'USER'),
       (gen_random_uuid(), 'hermione', 'hashed_pwd2', 'USER'),
       (gen_random_uuid(), 'ron', 'hashed_pwd3', 'USER'),
       (gen_random_uuid(), 'draco', 'hashed_pwd4', 'USER'),
       (gen_random_uuid(), 'luna', 'hashed_pwd5', 'USER'),
       (gen_random_uuid(), 'neville', 'hashed_pwd6', 'USER'),
       (gen_random_uuid(), 'ginny', 'hashed_pwd7', 'USER'),
       (gen_random_uuid(), 'fred', 'hashed_pwd8', 'USER'),
       (gen_random_uuid(), 'george', 'hashed_pwd9', 'USER'),
       (gen_random_uuid(), 'dobby', 'hashed_pwd10', 'USER');

-- Match IDs using CTE to insert corresponding profiles
WITH user_data AS (SELECT id, username
                   FROM users
                   WHERE username IN
                         ('harry', 'hermione', 'ron', 'draco', 'luna', 'neville', 'ginny', 'fred', 'george', 'dobby'))
INSERT
INTO profiles (id, display_name, bio, profile_picture_id)
SELECT id,
       INITCAP(username) || ' the Wizard',
       'Welcome to the magical world of Banterbox!',
       NULL
FROM user_data;

-- Store post information with author references
WITH user_ids AS (SELECT username, id
                  FROM users),
     inserted_posts AS (
         INSERT INTO posts (id, author_id, content, created_at)
             SELECT gen_random_uuid(),
                    u.id,
                    p.content,
                    CURRENT_TIMESTAMP
             FROM (VALUES ('harry', 'Practiced Patronus today! I think it''s working!'),
                          ('hermione', 'Books are better than spells, prove me wrong.'),
                          ('ron', 'I swear I saw a house-elf in the common room.'),
                          ('draco', 'Father will hear about this app.'),
                          ('luna', 'Nargles are real and they follow me here too.'),
                          ('neville', 'I finally remembered the password. Feeling like a hero.'),
                          ('ginny', 'Quidditch training has begun!'),
                          ('fred', 'Fred & George''s joke posts dropping soon.'),
                          ('george', 'Launching WizardWeasleys 2.0'),
                          ('dobby', 'Dobby is free. Dobby is also posting memes now.'),
                          ('harry', 'Dark arts defense tutorial coming soon.'),
                          ('hermione', 'I love rewriting Banterbox documentation.'),
                          ('ron', 'Banterbox is better than WizardTwitter.'),
                          ('draco', 'Muggle technology is suspicious.'),
                          ('luna', 'I hear whispers through the post feed.'),
                          ('neville', 'I made a post. Hope it didn''t explode.'),
                          ('ginny', 'This is my 5th post today. Fight me.'),
                          ('fred', 'Joke post: Why did the hippogriff cross the road?'),
                          ('george', 'Answer: Because Buckbeak dared him.'),
                          ('dobby', 'Dobby likes your post. Dobby is crying with joy.')) AS p(username, content)
                      JOIN user_ids u ON p.username = u.username
             RETURNING id, author_id, content)
-- Insert follows using the actual user IDs
INSERT
INTO users_followers (follower_id, following_id)
SELECT f.id, t.id
FROM (VALUES ('harry', 'hermione'),
             ('harry', 'ron'),
             ('harry', 'draco'),
             ('hermione', 'harry'),
             ('hermione', 'luna'),
             ('ron', 'harry'),
             ('ron', 'george'),
             ('fred', 'george'),
             ('george', 'fred'),
             ('neville', 'dobby')) AS follows(from_user, to_user)
         JOIN user_ids f ON follows.from_user = f.username
         JOIN user_ids t ON follows.to_user = t.username;

-- Insert likes using post IDs and user IDs
WITH post_content_ids AS (SELECT id, content
                          FROM posts),
     user_ids AS (SELECT id, username
                  FROM users)
INSERT
INTO post_likes (post_id, user_id, created_at)
SELECT p.id, u.id, CURRENT_TIMESTAMP
FROM (VALUES ('Practiced Patronus today! I think it''s working!', 'ginny'),
             ('Practiced Patronus today! I think it''s working!', 'draco'),
             ('Books are better than spells, prove me wrong.', 'harry'),
             ('Books are better than spells, prove me wrong.', 'luna'),
             ('I swear I saw a house-elf in the common room.', 'fred'),
             ('Father will hear about this app.', 'george'),
             ('Nargles are real and they follow me here too.', 'dobby'),
             ('Nargles are real and they follow me here too.', 'neville'),
             ('I finally remembered the password. Feeling like a hero.', 'hermione'),
             ('Quidditch training has begun!', 'harry'),
             ('Fred & George''s joke posts dropping soon.', 'george'),
             ('Fred & George''s joke posts dropping soon.', 'ginny'),
             ('Launching WizardWeasleys 2.0', 'fred'),
             ('Dobby is free. Dobby is also posting memes now.', 'ron'),
             ('Dobby is free. Dobby is also posting memes now.', 'hermione'),
             ('Dark arts defense tutorial coming soon.', 'dobby'),
             ('I love rewriting Banterbox documentation.', 'draco'),
             ('Banterbox is better than WizardTwitter.', 'neville'),
             ('Muggle technology is suspicious.', 'ginny'),
             ('I hear whispers through the post feed.', 'ron'),
             ('I made a post. Hope it didn''t explode.', 'harry'),
             ('This is my 5th post today. Fight me.', 'dobby'),
             ('Joke post: Why did the hippogriff cross the road?', 'neville'),
             ('Answer: Because Buckbeak dared him.', 'fred')) AS likes(post_content, username)
         JOIN post_content_ids p ON p.content = likes.post_content
         JOIN user_ids u ON u.username = likes.username;