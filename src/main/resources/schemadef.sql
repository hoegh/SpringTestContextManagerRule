DROP TABLE test;

CREATE TABLE test(
    id INT NOT NULL,
    PRIMARY KEY ( id ));

ALTER TABLE test ENGINE=InnoDB;