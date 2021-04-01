package org.bravetogether.mapstories.server.model.repository;

import org.bravetogether.mapstories.server.model.bean.story.Story;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

/**
 * Exposes CRUD operations implemented by spring.<br/>
 * Do not use a repository directly. Instead, auto wire a reference of {@link org.bravetogether.mapstories.server.model.service.StoryService}
 *
 * @author Haim Adrian
 * @since 21-Mar-21
 */
public interface StoryRepository extends CrudRepository<Story, Long> {
   @Query(value = "SELECT story_id, user_id, coordinate_id, since, hero_name, title, content, link_to_video, image " +
                  "FROM ms_story " +
                  "WHERE lower(hero_name) LIKE lower(concat('%', :heroName,'%'))",
         nativeQuery = true)
   Collection<Story> findByHeroNameContainingIgnoreCase(@Param("heroName") String heroName);

   @Query(value = "SELECT story_id, user_id, coordinate_id, since, hero_name, title, content, link_to_video, image " +
                  "FROM ms_story " +
                  "WHERE lower(title) LIKE lower(concat('%', :_title,'%'))",
         nativeQuery = true)
   Collection<Story> findByTitleContainingIgnoreCase(@Param("_title") String _title);

   @Query(value = "SELECT story_id, user_id, coordinate_id, since, hero_name, title, content, link_to_video, image " +
                  "FROM ms_story " +
                  "WHERE lower(user_id) = lower(:userId)",
         nativeQuery = true)
   Collection<Story> findByUserId(@Param("userId") String userId);

   @Query(value = "SELECT story_id, user_id, ms_story.coordinate_id, since, hero_name, title, content, link_to_video, image " +
                  "FROM ms_story JOIN ms_coordinate ON ms_story.coordinate_id = ms_coordinate.coordinate_id " +
                  "WHERE lower(ms_coordinate.location_name) LIKE lower(concat('%', :location,'%'))",
         nativeQuery = true)
   Collection<Story> findByLocationName(@Param("location") String location);

   @Query(value = "SELECT story_id, user_id, ms_story.coordinate_id, since, hero_name, title, content, link_to_video, image " +
                  "FROM ms_story JOIN ms_coordinate ON ms_story.coordinate_id = ms_coordinate.coordinate_id " +
                  "WHERE ms_story.coordinate_id = :coordinateId",
         nativeQuery = true)
   Collection<Story> findByCoordinateId(@Param("coordinateId") Long coordinateId);
}

