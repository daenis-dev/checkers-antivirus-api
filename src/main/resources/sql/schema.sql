CREATE SEQUENCE IF NOT EXISTS backups_id_seq;

CREATE TABLE backups (
	id INT NOT NULL DEFAULT nextval('backups_id_seq') PRIMARY KEY,
    file_path VARCHAR(255) NOT NULL,
    uncompressed_file_size_in_bytes BIGINT NOT NULL,
    file_extension VARCHAR(7) NOT NULL,
    user_id VARCHAR(255) NOT NULL
);

INSERT INTO backups (file_path, uncompressed_file_size_in_bytes, file_extension, user_id) VALUES ('src/test/resources/storage/123-abc/test.zip', 22, 'txt', '123-abc');

CREATE SEQUENCE IF NOT EXISTS user_friend_lists_id_seq;
CREATE SEQUENCE IF NOT EXISTS user_friends_id_seq;
CREATE SEQUENCE IF NOT EXISTS events_id_seq;

CREATE TABLE user_friend_lists (
	id INT NOT NULL DEFAULT nextval('user_friend_lists_id_seq') PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    created_date_time_in_utc TIMESTAMPTZ NOT NULL
);

INSERT INTO user_friend_lists (user_id, created_date_time_in_utc) VALUES ('456-def', NOW());

CREATE TABLE user_friends (
    id INT NOT NULL DEFAULT nextval('user_friends_id_seq') PRIMARY KEY,
    user_friend_list_id INT NOT NULL,
    friend_user_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_friend_list_id) REFERENCES user_friend_lists (id)
);

INSERT INTO user_friends (user_friend_list_id, friend_user_id) VALUES (1, '123-abc');

CREATE TABLE events (
	id INT NOT NULL DEFAULT nextval('events_id_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    start_date_time_in_utc TIMESTAMPTZ NOT NULL,
    end_date_time_in_utc TIMESTAMPTZ NOT NULL,
    created_date_time_in_utc TIMESTAMPTZ NOT NULL,
    modified_date_time_in_utc TIMESTAMPTZ NOT NULL
);

INSERT INTO events (
    name, user_id, start_date_time_in_utc, end_date_time_in_utc, created_date_time_in_utc, modified_date_time_in_utc)
VALUES (
    'Meeting One', '456-def', NOW() + INTERVAL '2 hours', NOW() + INTERVAL '3 hours', NOW(), NOW()
);

INSERT INTO events (
    name, user_id, start_date_time_in_utc, end_date_time_in_utc, created_date_time_in_utc, modified_date_time_in_utc)
VALUES (
    'Meeting Two', '456-def', NOW() + INTERVAL '4 hours', NOW() + INTERVAL '5 hours', NOW(), NOW()
);
