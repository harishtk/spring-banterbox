-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

BEGIN;

-- Insert 10 users with profile information
WITH inserted_users AS (
    INSERT INTO users (id, username, password, role, display_name, bio, profile_picture_id)
        VALUES
            (gen_random_uuid(), 'harry', '$2a$10$h1A.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuO', 'USER', 'Harry the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'hermione', '$2a$10$k2B.dX4RTSa9.EjxU.RKe.gzFVsG2JjzXIRzDS1RDZ9.UuX9j8yuP', 'USER', 'Hermione the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'ron', '$2a$10$m3C.zX4RTQz9.FjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuQ', 'USER', 'Ron the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'draco', '$2a$10$p4A.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuR', 'USER', 'Draco the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'luna', '$2a$10$q5B.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuS', 'USER', 'Luna the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'neville', '$2a$10$r6C.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuT', 'USER', 'Neville the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'ginny', '$2a$10$s7D.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuU', 'USER', 'Ginny the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'fred', '$2a$10$t8E.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuV', 'USER', 'Fred the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'george', '$2a$10$u9F.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuW', 'USER', 'George the Wizard', 'Welcome to the magical world of Banterbox!', NULL),
            (gen_random_uuid(), 'dobby', '$2a$10$v0G.zX3RTQz9.DjxT.QJd.fzEVsF1JjzWIRzCS0QDZ8.TtW8i7xuX', 'USER', 'Dobby the Wizard', 'Welcome to the magical world of Banterbox!', NULL)
        RETURNING id, username
)
-- Insert posts
INSERT INTO posts (id, author_id, content, created_at)
SELECT gen_random_uuid(),
       u.id,
       p.content,
       CURRENT_TIMESTAMP
FROM (VALUES
          ('harry', 'Practiced Patronus today! I think it''s working!'),
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
          ('dobby', 'Dobby likes your post. Dobby is crying with joy.')
     ) AS p(username, content)
         JOIN inserted_users u ON p.username = u.username;

-- Insert follows
INSERT INTO users_followers (follower_id, following_id)
SELECT f.id, t.id
FROM (VALUES
          ('harry', 'hermione'),
          ('harry', 'ron'),
          ('harry', 'draco'),
          ('hermione', 'harry'),
          ('hermione', 'luna'),
          ('ron', 'harry'),
          ('ron', 'george'),
          ('fred', 'george'),
          ('george', 'fred'),
          ('neville', 'dobby')
     ) AS follows(from_user, to_user)
         JOIN users f ON f.username = follows.from_user
         JOIN users t ON t.username = follows.to_user;

-- Insert likes
INSERT INTO post_likes (post_id, user_id, created_at)
SELECT p.id, u.id, CURRENT_TIMESTAMP
FROM (VALUES
          ('Practiced Patronus today! I think it''s working!', 'ginny'),
          ('Practiced Patronus today! I think it''s working!', 'draco'),
          ('Books are better than spells, prove me wrong.', 'harry'),
          ('Books are better than spells, prove me wrong.', 'luna'),
          ('I swear I saw a house-elf in the common room.', 'fred'),
          ('Father will hear about this app.', 'george'),
          ('Nargles are real and they follow me here too.', 'dobby'),
          ('Nargles are real and they follow me here too.', 'neville'),
          ('I finally remembered the password. Feeling like a hero.', 'hermione'),
          ('Quidditch training has begun!', 'harry')
     ) AS likes(post_content, username)
         JOIN posts p ON p.content = likes.post_content
         JOIN users u ON u.username = likes.username;

COMMIT;