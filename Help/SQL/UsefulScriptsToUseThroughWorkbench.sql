-- *** INSERT INTO ms_coordinate using a PreparedStatement (to avoid sql injection)
-- Before inserting a new coordinate, we will select coordinates with radius of 1KM and suggest users existing coordinates instead.
-- Note that when we execute this statement programmatically, we can use getGeneratedKeys() in order to receive the automatically generated key. (coordinate_id)
/* Java example:
...
statement.executeUpdate(...);
ResultSet rs = statement.getGeneratedKeys();
if (rs.next()) {
	int coordinateIdToUseWhenInsertingNewStory = rs.getInt(1);
} else { error? }
*/
INSERT INTO ms_coordinate (latitude, longitude, location_name, image)
VALUES (?, ?, ?, ?);

-- Example:
INSERT INTO ms_coordinate (latitude, longitude, location_name, image)
VALUES (32.01623990507656, 34.773109201554945, 'HIT', NULL);


-- *** INSERT INTO ms_story using a PreparedStatement (to avoid sql injection)
INSERT INTO ms_story (user_id, coordinate_id, since, hero_name, title, content, link_to_video)
VALUES (?, ?, ?, ?, ?, ?, ?);

-- Example:
INSERT INTO ms_story (user_id, coordinate_id, since, hero_name, title, content, link_to_video)
VALUES ('gilam@hit.ac.il', 1, '1941-07-04', 'Ploni', 'The lonely island', 'some long text in here', 'optional URI here');


-- *** Update image of an existing coordinate
UPDATE ms_coordinate SET image = ? WHERE coordinate_id = ?;

-- *** Update a story. Before full update of a story, select it to find the differences and avoid of redundant updates. 
-- For xample, if content is a huge value, there is no need to mention it in an update in case there was no modification of it. In such a case, select another update statement
UPDATE ms_story SET since = ?, hero_name = ?, title = ?, content = ?, link_to_video = ? WHERE story_id = ?;
UPDATE ms_story SET since = ?, hero_name = ?, title = ?, link_to_video = ? WHERE story_id = ?;
UPDATE ms_story SET link_to_video = ? WHERE story_id = ?;

-- *** Select nearby coordinates (suggesting 1KM by default) to suggest them to users and avoid of duplicating coordinates when adding a story
-- Use Haversine formula: https://en.wikipedia.org/wiki/Haversine_formula
-- First ? = latitude, Second ? = longitude, Third ? = latitude, of the point we would like to find existing coordinates around. Last ? is the radius to search within
-- distance is in KM. Replace 1 in order to increase the radius. Replace 6371 with something else to change KM to another unit
SELECT coordinate_id, latitude, longitude, location_name, (6371 * acos(cos(radians( ? )) * cos(radians( latitude )) * cos(radians( longitude ) - radians( ? )) + sin(radians( ? )) * sin(radians( latitude )))) AS distance 
FROM ms_coordinate 
HAVING distance <= ? -- Use 1 by default to look 1KM around a selected point
ORDER BY distance ASC;

-- Example:
-- Try to locate HIT from example above, by searching around Israeli Cartoon Museum 32.015343027689276, 34.770769562549276
SELECT coordinate_id, latitude, longitude, location_name, (6371 * acos(cos(radians( 32.015343027689276 )) * cos(radians( latitude )) * cos(radians( longitude ) - radians( 34.770769562549276 )) + sin(radians( 32.015343027689276 )) * sin(radians( latitude )))) AS distance 
FROM ms_coordinate 
HAVING distance <= 1
ORDER BY distance ASC;

-- Find stories by title
SELECT story_id, user_id, coordinate_id, since, hero_name, title, link_to_video 
FROM ms_story
WHERE title like ?; -- This parameter should be replaced with setString("%" + titleToFind + "%")

-- Find stories by hero
SELECT story_id, user_id, coordinate_id, since, hero_name, title, link_to_video 
FROM ms_story
WHERE hero_name like ?; -- This parameter should be replaced with setString("%" + nameToFind + "%")

-- Find stories by user
SELECT story_id, user_id, coordinate_id, since, hero_name, title, link_to_video 
FROM ms_story
WHERE user_id = ?; 

-- Find stories by identifier
-- Use this query after filtering the stories you want, in order to retrieve their content
SELECT story_id, user_id, coordinate_id, since, hero_name, title, content, link_to_video 
FROM ms_story
WHERE story_id = ?; 

-- Programmatically calculate how many identifiers are in the list, and create enough question marks to contain them.
-- So assuming we want to select stories by four identifiers: [1, 3, 4, 5], we will replace "DYNAMIC_AMOUNT_OF_?" with ?, ?, ?, ? and then set 4 integer parameters
-- as in the list, in order to protect the query from sql injection.
SELECT story_id, user_id, coordinate_id, since, hero_name, title, content, link_to_video 
FROM ms_story
WHERE story_id = ANY ("DYNAMIC_AMOUNT_OF_?"); 

-- Find stories by location name. First find story identifiers to reduce network bandwidth, and second select the actual stories content
SELECT story_id, user_id, coordinate_id, since, hero_name, title, link_to_video, latitude, longitude, location_name
FROM ms_story JOIN ms_coordinate
ON ms_story.coordinate_id = ms_coordinate.coordinate_id
WHERE location_name like ?;  -- This parameter should be replaced with setString("%" + locationToFind + "%")

-- Find stories by coordinate_id. First find coordinates within some distance using the query above, and second select the actual stories content
SELECT story_id, user_id, coordinate_id, since, hero_name, title, link_to_video, latitude, longitude, location_name
FROM ms_story JOIN ms_coordinate
ON ms_story.coordinate_id = ms_coordinate.coordinate_id
WHERE coordinate_id = ?;

-- To be able to select many, rather than a single coordinate identifier
SELECT story_id, user_id, coordinate_id, since, hero_name, title, link_to_video, latitude, longitude, location_name
FROM ms_story JOIN ms_coordinate
ON ms_story.coordinate_id = ms_coordinate.coordinate_id
WHERE coordinate_id = ANY ("DYNAMIC_AMOUNT_OF_?");