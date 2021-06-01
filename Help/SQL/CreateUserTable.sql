CREATE TABLE ms_user (
  id VARCHAR(255) NOT NULL,
  coins BIGINT NOT NULL,
  date_of_birth DATE NOT NULL,
  name VARCHAR(255) NOT NULL,
  pwd VARCHAR(255) NOT NULL,
  image MEDIUMBLOB,
  CONSTRAINT ms_user_pk 		PRIMARY KEY (id)
);
