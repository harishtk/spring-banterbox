-- Insert 10 users
INSERT INTO users (id, username, password, role)
VALUES
    (gen_random_uuid(), 'harry', 'hashed_pwd1', 'USER'),
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
WITH user_data AS (
    SELECT id, username FROM users WHERE username IN
                                         ('harry','hermione','ron','draco','luna','neville','ginny','fred','george','dobby')
)
INSERT INTO profiles (id, display_name, bio, profile_picture_id)
SELECT
    id,
    INITCAP(username) || ' the Wizard',
    'Welcome to the magical world of Banterbox!',
    NULL
FROM user_data;

INSERT INTO posts (id, author_id, content, created_at)
VALUES (gen_random_uuid(), '1b6ae16f-30d2-421a-af62-dcd841bcc94b', 'Practiced Patronus today! I think it’s working!',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '38e46b22-cafd-4e59-86b2-934aa25c32ae', 'Books are better than spells, prove me wrong.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '92ff744c-f232-44a7-8c00-6ba92b5353d9', 'I swear I saw a house-elf in the common room.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'af494264-c4d8-4ca3-aa4b-9c9d1f9466cd', 'Father will hear about this app.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '73fd30f6-1551-43f9-8590-6cbc89a957f1', 'Nargles are real and they follow me here too.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '1d4cf4c7-9d1b-4988-8028-2ed50fca5a13',
        'I finally remembered the password. Feeling like a hero.', CURRENT_TIMESTAMP),
       (gen_random_uuid(), '0b9d2122-73d7-4858-860d-0e69dd33b423', 'Quidditch training has begun!', CURRENT_TIMESTAMP),
       (gen_random_uuid(), '013f3b83-02cb-4048-866c-699fe225d6a2', 'Fred & George’s joke posts dropping soon.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '7f77c130-edd2-40ec-b233-a13662ea0589', 'Launching WizardWeasleys 2.0', CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'a0e68238-c53c-49ca-9059-b4231b579bfd', 'Dobby is free. Dobby is also posting memes now.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '1b6ae16f-30d2-421a-af62-dcd841bcc94b', 'Dark arts defense tutorial coming soon.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '38e46b22-cafd-4e59-86b2-934aa25c32ae', 'I love rewriting Banterbox documentation.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '92ff744c-f232-44a7-8c00-6ba92b5353d9', 'Banterbox is better than WizardTwitter.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'af494264-c4d8-4ca3-aa4b-9c9d1f9466cd', 'Muggle technology is suspicious.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '73fd30f6-1551-43f9-8590-6cbc89a957f1', 'I hear whispers through the post feed.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '1d4cf4c7-9d1b-4988-8028-2ed50fca5a13', 'I made a post. Hope it didn’t explode.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '0b9d2122-73d7-4858-860d-0e69dd33b423', 'This is my 5th post today. Fight me.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '013f3b83-02cb-4048-866c-699fe225d6a2', 'Joke post: Why did the hippogriff cross the road?',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), '7f77c130-edd2-40ec-b233-a13662ea0589', 'Answer: Because Buckbeak dared him.',
        CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'a0e68238-c53c-49ca-9059-b4231b579bfd', 'Dobby likes your post. Dobby is crying with joy.',
        CURRENT_TIMESTAMP);

-- Follows (follower_id, following_id)
INSERT INTO users_followers (follower_id, following_id)
VALUES
    ('1b6ae16f-30d2-421a-af62-dcd841bcc94b', '38e46b22-cafd-4e59-86b2-934aa25c32ae'), -- Harry -> Hermione
    ('1b6ae16f-30d2-421a-af62-dcd841bcc94b', '92ff744c-f232-44a7-8c00-6ba92b5353d9'), -- Harry -> Ron
    ('1b6ae16f-30d2-421a-af62-dcd841bcc94b', 'af494264-c4d8-4ca3-aa4b-9c9d1f9466cd'), -- Harry -> Draco
    ('38e46b22-cafd-4e59-86b2-934aa25c32ae', '1b6ae16f-30d2-421a-af62-dcd841bcc94b'), -- Hermione -> Harry
    ('38e46b22-cafd-4e59-86b2-934aa25c32ae', '73fd30f6-1551-43f9-8590-6cbc89a957f1'), -- Hermione -> Luna
    ('92ff744c-f232-44a7-8c00-6ba92b5353d9', '1b6ae16f-30d2-421a-af62-dcd841bcc94b'), -- Ron -> Harry
    ('92ff744c-f232-44a7-8c00-6ba92b5353d9', '7f77c130-edd2-40ec-b233-a13662ea0589'), -- Ron -> George
    ('013f3b83-02cb-4048-866c-699fe225d6a2', '7f77c130-edd2-40ec-b233-a13662ea0589'), -- Fred -> George
    ('7f77c130-edd2-40ec-b233-a13662ea0589', '013f3b83-02cb-4048-866c-699fe225d6a2'), -- George -> Fred
    ('1d4cf4c7-9d1b-4988-8028-2ed50fca5a13', 'a0e68238-c53c-49ca-9059-b4231b579bfd'); -- Neville -> Dobby

INSERT INTO post_likes (post_id, user_id, created_at) VALUES
-- Post: First post in banterbox
('38585285-1c61-4bbf-8a22-394c9bbaf077', '1b6ae16f-30d2-421a-af62-dcd841bcc94b', CURRENT_TIMESTAMP),
('38585285-1c61-4bbf-8a22-394c9bbaf077', '38e46b22-cafd-4e59-86b2-934aa25c32ae', CURRENT_TIMESTAMP),

-- Post: This is next postdd
('2b4301ef-b33d-4cda-b01a-dc5f7d9bc0e8', '92ff744c-f232-44a7-8c00-6ba92b5353d9', CURRENT_TIMESTAMP),

-- Practiced Patronus...
('195f8633-066e-42cc-af7a-8e2ac217ea41', '0b9d2122-73d7-4858-860d-0e69dd33b423', CURRENT_TIMESTAMP),
('195f8633-066e-42cc-af7a-8e2ac217ea41', 'af494264-c4d8-4ca3-aa4b-9c9d1f9466cd', CURRENT_TIMESTAMP),

-- Books are better...
('653bb2a4-92f4-4a89-a00f-46bb5e539ff9', '1b6ae16f-30d2-421a-af62-dcd841bcc94b', CURRENT_TIMESTAMP),
('653bb2a4-92f4-4a89-a00f-46bb5e539ff9', '73fd30f6-1551-43f9-8590-6cbc89a957f1', CURRENT_TIMESTAMP),

-- House elf...
('7580d042-6601-48a8-91fb-846035b773e2', '013f3b83-02cb-4048-866c-699fe225d6a2', CURRENT_TIMESTAMP),

-- Father will hear...
('9ffbc2e9-8ec4-4e4f-953c-66c96fd7ab78', '7f77c130-edd2-40ec-b233-a13662ea0589', CURRENT_TIMESTAMP),

-- Nargles...
('22f93f37-d218-4200-b5c9-aeda8c9f2b63', 'a0e68238-c53c-49ca-9059-b4231b579bfd', CURRENT_TIMESTAMP),
('22f93f37-d218-4200-b5c9-aeda8c9f2b63', '1d4cf4c7-9d1b-4988-8028-2ed50fca5a13', CURRENT_TIMESTAMP),

-- Password remembered...
('2a81fca4-fe98-41df-8f0e-b4017dda6474', '38e46b22-cafd-4e59-86b2-934aa25c32ae', CURRENT_TIMESTAMP),

-- Quidditch...
('5ca59962-8aaa-4e01-82cc-dba80c166469', '1b6ae16f-30d2-421a-af62-dcd841bcc94b', CURRENT_TIMESTAMP),

-- Fred & George...
('f215a738-9b31-4eb1-83cb-e791e5d4ab20', '7f77c130-edd2-40ec-b233-a13662ea0589', CURRENT_TIMESTAMP),
('f215a738-9b31-4eb1-83cb-e791e5d4ab20', '0b9d2122-73d7-4858-860d-0e69dd33b423', CURRENT_TIMESTAMP),

-- WizardWeasleys...
('2d925090-23c6-41af-87f1-fb61f3963eec', '013f3b83-02cb-4048-866c-699fe225d6a2', CURRENT_TIMESTAMP),

-- Dobby meme...
('e01ce884-4043-469a-be30-a60465d57e6f', '92ff744c-f232-44a7-8c00-6ba92b5353d9', CURRENT_TIMESTAMP),
('e01ce884-4043-469a-be30-a60465d57e6f', '38e46b22-cafd-4e59-86b2-934aa25c32ae', CURRENT_TIMESTAMP),

-- Dark arts defense...
('cf3bbb4d-e0c8-4425-bb95-9e5076041965', 'a0e68238-c53c-49ca-9059-b4231b579bfd', CURRENT_TIMESTAMP),

-- Rewriting docs...
('652db777-ed59-4e74-90aa-538c73e3213f', 'af494264-c4d8-4ca3-aa4b-9c9d1f9466cd', CURRENT_TIMESTAMP),

-- WizardTwitter...
('420efc95-e9aa-45fb-b9ea-718acc1ded0c', '1d4cf4c7-9d1b-4988-8028-2ed50fca5a13', CURRENT_TIMESTAMP),

-- Muggle tech...
('b98088a7-0c0b-46b8-93cc-e52b6b219262', '0b9d2122-73d7-4858-860d-0e69dd33b423', CURRENT_TIMESTAMP),

-- Whispers...
('68b96f30-e4ef-4ce2-ba3f-2ff10173a8ac', '92ff744c-f232-44a7-8c00-6ba92b5353d9', CURRENT_TIMESTAMP),

-- Hope it didn’t explode...
('f1cdc10a-cd8f-4441-9ce5-38cc3f9d9e6a', '1b6ae16f-30d2-421a-af62-dcd841bcc94b', CURRENT_TIMESTAMP),

-- 5th post today...
('36fbac19-6a06-4ca8-885c-340e50c8d146', 'a0e68238-c53c-49ca-9059-b4231b579bfd', CURRENT_TIMESTAMP),

-- Hippogriff joke...
('b2ee5364-a582-4e49-b6b9-a3b66a7176cc', '1d4cf4c7-9d1b-4988-8028-2ed50fca5a13', CURRENT_TIMESTAMP),

-- Buckbeak dared him...
('8e81618e-7171-4cb2-a0f9-bbc0ceceabfa', '013f3b83-02cb-4048-866c-699fe225d6a2', CURRENT_TIMESTAMP),

-- Dobby likes your post...
('7b46a861-6edb-4b7d-9785-7376c0b98733', 'dc3adde9-5ba4-4ceb-8f6c-41529884707b', CURRENT_TIMESTAMP);

