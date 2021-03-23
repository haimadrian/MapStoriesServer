CREATE TABLE ms_user (
  id VARCHAR(255) NOT NULL,
  coins BIGINT NOT NULL,
  date_of_birth DATE NOT NULL,
  name varchar(255) NOT NULL,
  pwd varchar(255) NOT NULL,
  CONSTRAINT ms_user_pk 		PRIMARY KEY (id)
);