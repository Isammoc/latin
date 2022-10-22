-- !Ups
 
CREATE TABLE latin_text (
    id INTEGER PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    comment TEXT
);
 
-- !Downs
 
DROP TABLE latin_text;
