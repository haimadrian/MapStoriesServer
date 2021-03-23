-- A table to store N.Z data. This is in separate table as there can be several stories at one location, and it's better to avoid of duplicating location name and image.
-- 'ms' stands for MapStory, to recognize the tables of our app. I don't think using the full name would be
-- wise, because we have a 'story' table, so we could end up having 'map_story_story' table..
CREATE TABLE ms_coordinate (
  coordinate_id	INT 			NOT NULL 		AUTO_INCREMENT,
  latitude 		DECIMAL(17, 15)	NOT NULL,					-- e.g. 32.01623990507656 (latitude range is [-90, 90], so there are 2 digits left to the dot, and 15 decimal digits)
  longitude 	DECIMAL(18, 15)	NOT NULL,					-- e.g. 34.773109201554945 (longitude range is (-180, 180], so there are 3 digits left to the dot, and 15 decimal digits)
  location_name	VARCHAR(64) 	DEFAULT NULL,
  image 		MEDIUMBLOB 		DEFAULT NULL, 				-- Up to 16MB for an image
  CONSTRAINT ms_coordinate_pk 	PRIMARY KEY (coordinate_id), 
  CONSTRAINT ms_coordinate_check CHECK ((latitude >= -90) AND (latitude <= 90) AND (longitude > -180) AND (longitude <= 180))
) AUTO_INCREMENT = 1;