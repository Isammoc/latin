# --- !Ups
 
CREATE TABLE latin_text (
    id SERIAL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    comment TEXT,
    PRIMARY KEY (id)
);
 
# --- !Downs
 
DROP TABLE latin_text;
