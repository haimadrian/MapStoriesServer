package org.bravetogether.mapstories.server.controller;

import org.bravetogether.mapstories.server.model.bean.story.Coordinate;
import org.bravetogether.mapstories.server.model.bean.story.Story;
import org.bravetogether.mapstories.server.model.bean.user.User;
import org.bravetogether.mapstories.server.model.bean.user.UserDBImpl;
import org.bravetogether.mapstories.server.model.service.CoordinateService;
import org.bravetogether.mapstories.server.model.service.StoryService;
import org.bravetogether.mapstories.server.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * All story RESTful web services are in this controller class.
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@RestController
@RequestMapping("/story")
public class StoryController {

   @Autowired
   private StoryService storyService;

   @Autowired
   private UserService userService;

   @Autowired
   private CoordinateService coordinateService;

   @PostMapping
   public ResponseEntity<?> uploadStory(@RequestBody Story story) {
      try {
         if ((story == null) || (story.getCoordinate() == null) || (story.getCoordinate().getCoordinateId() == null) || (story.getUser() == null) || (story.getUser().getId() == null)) {
            return ResponseEntity.badRequest().body("Story must refer to coordinate and user identifiers. Was: " + story);
         }

         if (story.getStoryId() != null) {
            return updateStory(story.getStoryId(), story);
         }

         try {
            // Do not let clients to affect users/coordinates through this API. Get the references here.
            ResponseEntity<String> responseEntity = fillInUserAndCoordinateRefs(story);
            if (responseEntity != null) return responseEntity;

            // The response will contain story identifier
            return ResponseEntity.ok(storyService.save(story));
         } catch (IllegalArgumentException e) {
            // IllegalArgument means client sent wrong identifiers for user/coordinate
            return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
         }
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @PostMapping("/{storyId}")
   public ResponseEntity<?> updateStory(@PathVariable Long storyId, @RequestBody Story story) {
      try {
         if ((storyId == null) || (story == null) || (story.getCoordinate() == null) || (story.getCoordinate().getCoordinateId() == null) || (story.getUser() == null) || (story.getUser().getId() == null)) {
            return ResponseEntity.badRequest().body("Story must refer to coordinate and user identifiers. Was: " + story);
         }

         if (!storyId.equals(story.getStoryId())) {
            return ResponseEntity.badRequest().body("Unable to update story identifier. [storyId=" + storyId + ", story.storyId=" + story.getStoryId() + "]");
         }

         Optional<Story> existingStory = storyService.findById(storyId);
         if (existingStory.isEmpty()) {
            return ResponseEntity.notFound().build();
         }

         if (!existingStory.get().getUser().getId().equals(story.getUser().getId())) {
            return ResponseEntity.badRequest().body("Unable to update story user. [specified=" + story.getUser().getId() + ", existing=" + existingStory.get().getUser().getId() + "]");
         }

         // Do not let clients to affect users/coordinates through this API. Get the references here.
         ResponseEntity<String> responseEntity = fillInUserAndCoordinateRefs(story);
         if (responseEntity != null) return responseEntity;

         try {
            return ResponseEntity.ok(storyService.save(story));
         } catch (IllegalArgumentException e) {
            // IllegalArgument means client sent wrong identifiers for user/coordinate
            return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
         }
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/{storyId}")
   public ResponseEntity<?> getStory(@PathVariable Long storyId) {
      try {
         if (storyId == null) {
            return ResponseEntity.badRequest().body("Story identifier is mandatory for finding a story");
         }

         Optional<Story> story = storyService.findById(storyId);
         if (story.isEmpty()) {
            return ResponseEntity.notFound().build();
         }

         return ResponseEntity.ok(story.get());
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/hero/{heroName}")
   public ResponseEntity<?> getStoryByHeroName(@PathVariable String heroName) {
      try {
         if (heroName == null) {
            return ResponseEntity.badRequest().body("Hero name is mandatory for finding a story by hero name");
         }

         Collection<Story> stories = storyService.findByHeroName(heroName);
         return ResponseEntity.ok(copyStoriesRemovingContent(stories));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/title/{title}")
   public ResponseEntity<?> getStoryByTitle(@PathVariable String title) {
      try {
         if (title == null) {
            return ResponseEntity.badRequest().body("Title is mandatory for finding a story by title");
         }

         Collection<Story> stories = storyService.findByTitle(title);
         return ResponseEntity.ok(copyStoriesRemovingContent(stories));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/user/{userId}")
   public ResponseEntity<?> getStoryByUserId(@PathVariable String userId) {
      try {
         if (userId == null) {
            return ResponseEntity.badRequest().body("User identifier is mandatory for finding a story by user identifier");
         }

         Collection<Story> stories = storyService.findByUserId(userId);
         return ResponseEntity.ok(copyStoriesRemovingContent(stories));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/location/{locationName}")
   public ResponseEntity<?> getStoryByLocationName(@PathVariable String locationName) {
      try {
         if (locationName == null) {
            return ResponseEntity.badRequest().body("Location name is mandatory for finding a story by location");
         }

         Collection<Story> stories = storyService.findByLocationName(locationName);
         return ResponseEntity.ok(copyStoriesRemovingContent(stories));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/coordinate/{coordinateId}")
   public ResponseEntity<?> getStoryByCoordinateId(@PathVariable Long coordinateId) {
      try {
         if (coordinateId == null) {
            return ResponseEntity.badRequest().body("Coordinate identifier is mandatory for finding a story by coordinate");
         }

         Collection<Story> stories = storyService.findByCoordinateId(coordinateId);
         return ResponseEntity.ok(copyStoriesRemovingContent(stories));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   /**
    * A helper method used to copy stories without their content, in order to reduce response weight.
    * @param stories The stories to copy without their content
    * @return The copy
    */
   private static Collection<Story> copyStoriesRemovingContent(Collection<Story> stories) {
      return stories.stream().map(story -> new Story(story.getStoryId(), story.getUser(), story.getCoordinate(), story.getSince(), story.getHeroName(), story.getTitle(), "", story.getLinkToVideo(), story.getImage())).collect(Collectors.toList());
   }

   /**
    * A helper method used to fetch coordinate and user references from the service layer, so we will protect updating them
    * when client upload stories.
    * @param story The story to fill references in
    * @return Null if we've succeeded and badRequest if we've failed finding user or coordinate.
    */
   private ResponseEntity<String> fillInUserAndCoordinateRefs(Story story) {
      Optional<? extends User> user = userService.findById(story.getUser().getId());
      if (user.isEmpty()) {
         return ResponseEntity.badRequest().body("User with identifier " + story.getUser().getId() + " does not exist");
      }

      story.setUser((UserDBImpl) user.get());

      Optional<Coordinate> coordinate = coordinateService.findById(story.getCoordinate().getCoordinateId());
      if (coordinate.isEmpty()) {
         return ResponseEntity.badRequest().body("Coordinate with identifier " + story.getCoordinate().getCoordinateId() + " does not exist");
      }

      story.setCoordinate(coordinate.get());
      return null;
   }
}

