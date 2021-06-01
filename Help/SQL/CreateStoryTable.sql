-- Main table to store stories
CREATE TABLE ms_story (
  story_id 		INT 			NOT NULL 		AUTO_INCREMENT,
  user_id 		VARCHAR(30) 	NOT NULL, 					-- ??? Where do they store users and their coins? We need to make a foreign key with this column !!!
  coordinate_id	INT 			DEFAULT NULL,
  since 		DATE 			DEFAULT NULL,
  hero_name		VARCHAR(255)	NOT NULL,
  title 		VARCHAR(255)	NOT NULL,
  content 		TEXT 			DEFAULT NULL, 				-- 64kb should be more than enough for a story :)
  link_to_video	VARCHAR(2048) 	DEFAULT NULL, 				-- ??? If we are sitting in the same database with the videos, then it's better to create a foreign key to their table instead !!!
  image 		MEDIUMBLOB,
  CONSTRAINT ms_story_pk 		PRIMARY KEY (story_id),
  CONSTRAINT ms_coordinate_fk 	FOREIGN KEY (coordinate_id) -- Create a relation between our table and coordinate table
    REFERENCES ms_coordinate (coordinate_id)
    ON DELETE SET NULL 										-- So once a coordinate is deleted, we set null in our table and avoid of losing the story
    ON UPDATE NO ACTION,
  CONSTRAINT ms_user_fk 		FOREIGN KEY (user_id) 		-- Create a relation between our table and user table
    REFERENCES ms_user (id)
    ON DELETE CASCADE										-- So once a user is deleted, we delete all stories uploaded by that user
    ON UPDATE NO ACTION
) AUTO_INCREMENT = 1;