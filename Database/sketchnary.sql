DROP TABLE IF EXISTS Friend;
DROP TABLE IF EXISTS Player;

--- PLAYER ---
CREATE TABLE Player(
    id integer NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    name text NOT NULL,
    email text NOT NULL,
    birthdate date NOT NULL,
    country text,
    points integer NOT NULL,
    CONSTRAINT "valid points" CHECK (points > 0)
);

--- PLAYER SEQUENCE ---
CREATE SEQUENCE Player_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--- PLAYER SEQUENCE CONSTRAINTS---
ALTER SEQUENCE Player_id_seq OWNED BY Player.id;
ALTER TABLE ONLY Player ALTER COLUMN id SET DEFAULT nextval('Player_id_seq'::regclass);
SELECT pg_catalog.setval('Player_id_seq', 1, false);

--- FRIEND ---
CREATE TABLE Friend(
	id_player integer NOT NULL,
	id_friend integer NOT NULL
);

--- CONSTRAINTS ---
ALTER TABLE ONLY Player
       ADD CONSTRAINT "username unique" UNIQUE (username);
ALTER TABLE ONLY Player
	ADD CONSTRAINT "email unique" UNIQUE (email);
ALTER TABLE ONLY Player
	ADD CONSTRAINT "Player key" PRIMARY KEY (id);
ALTER TABLE ONLY Friend
	ADD CONSTRAINT id_player FOREIGN KEY (id_player) REFERENCES Player(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY Friend
	ADD CONSTRAINT id_friend FOREIGN KEY (id_friend) REFERENCES Player(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

--- INDEX ---
CREATE INDEX index_player_username ON Player USING hash(username);
CREATE INDEX index_join_player_friend ON Friend USING btree(id_friend);