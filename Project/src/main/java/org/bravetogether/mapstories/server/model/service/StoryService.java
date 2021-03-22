package org.bravetogether.mapstories.server.model.service;

import org.bravetogether.mapstories.server.model.bean.story.Coordinate;
import org.bravetogether.mapstories.server.model.bean.story.Story;
import org.bravetogether.mapstories.server.model.bean.user.User;
import org.bravetogether.mapstories.server.model.bean.user.UserDBImpl;
import org.bravetogether.mapstories.server.model.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * Wrap the access to the repository, so we can add additional logic between controller and repository.</br>
 * For example, we use a cache for better performance, so it is done in the service, rather than repository.
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@Component
public class StoryService {
   @Autowired
   private StoryRepository storyRepository;

   @Autowired
   private UserService userService;

   @Autowired
   private CoordinateService coordinateService;

   /**
    * See {@link StoryRepository#findById(Object)}
    */
   public Optional<Story> findById(Long id) {
      return storyRepository.findById(id);
   }

   /**
    * See {@link StoryRepository#findByHeroNameContainingIgnoreCase(String)}
    */
   public Collection<Story> findByHeroName(String heroName) {
      return storyRepository.findByHeroNameContainingIgnoreCase(heroName);
   }

   /**
    * See {@link StoryRepository#findByTitleContainingIgnoreCase(String)}
    */
   public Collection<Story> findByTitle(String title) {
      return storyRepository.findByTitleContainingIgnoreCase(title);
   }

   /**
    * See {@link StoryRepository#findByUserId(String)}
    */
   public Collection<Story> findByUserId(String userId) {
      return storyRepository.findByUserId(userId);
   }

   /**
    * See {@link StoryRepository#findByLocationName(String)}
    */
   public Collection<Story> findByLocationName(String location) {
      return storyRepository.findByLocationName(location);
   }

   /**
    * See {@link StoryRepository#findByCoordinateId(Long)}
    */
   public Collection<Story> findByCoordinateId(Long coordinateId) {
      return storyRepository.findByCoordinateId(coordinateId);
   }

   /**
    * See {@link StoryRepository#save(Object)}
    */
   public Story save(Story story) {
      Optional<Coordinate> coordinate = coordinateService.findById(story.getCoordinate().getCoordinateId());
      if (coordinate.isEmpty()) {
         throw new IllegalArgumentException("Coordinate with identifier: " + story.getCoordinate().getCoordinateId() + " does not exist.");
      }

      Optional<? extends User> user = userService.findById(story.getUser().getId());
      if (user.isEmpty()) {
         throw new IllegalArgumentException("User with identifier: " + story.getUser().getId() + " does not exist.");
      }

      // Add two coins to the user for uploading a story
      userService.addCoins(user.get(), 2L);

      // Update the reference to the one we have in the repository, so we will not lose data
      story.setCoordinate(coordinate.get());
      story.setUser((UserDBImpl)user.get());
      return storyRepository.save(story);
   }

   /**
    * See {@link StoryRepository#existsById(Object)}
    */
   public boolean existsById(Long id) {
      return storyRepository.existsById(id);
   }

   /**
    * See {@link StoryRepository#deleteAll()}
    */
   public void deleteAll() {
      storyRepository.deleteAll();
   }
}

