package org.bravetogether.mapstories.server.controller;

import org.bravetogether.mapstories.server.model.bean.story.Coordinate;
import org.bravetogether.mapstories.server.model.service.CoordinateService;
import org.bravetogether.mapstories.server.model.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * All coordinate RESTful web services are in this controller class.
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@RestController
@RequestMapping("/coordinate")
public class CoordinateController {

   @Autowired
   private CoordinateService coordinateService;

   @Autowired
   private StoryService storyService;

   @PostMapping
   public ResponseEntity<?> uploadCoordinate(@RequestBody Coordinate coordinate) {
      try {
         if ((coordinate == null) || !rangeCheck(coordinate.getLatitude(), -90, 90) || !rangeCheck(coordinate.getLongitude(), -180, 180)) {
            return ResponseEntity.badRequest().body("Coordinate details are mandatory, where latitude ∈ [-90, 90] and longitude ∈ (-180, 180). Was: " + coordinate);
         }

         if (coordinate.getCoordinateId() != null) {
            return updateCoordinate(coordinate.getCoordinateId(), coordinate);
         }

         // The response will contain coordinate identifier
         return ResponseEntity.ok(coordinateService.save(coordinate));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @PostMapping("/{coordinateId}")
   public ResponseEntity<?> updateCoordinate(@PathVariable Long coordinateId, @RequestBody Coordinate coordinate) {
      try {
         if ((coordinateId == null) || (coordinate == null) || !rangeCheck(coordinate.getLatitude(), -90, 90) || !rangeCheck(coordinate.getLongitude(), -180, 180)) {
            return ResponseEntity.badRequest().body("Coordinate details are mandatory, where latitude ∈ [-90, 90] and longitude ∈ (-180, 180). Was: " + coordinate);
         }

         Optional<Coordinate> existingCoordinate = coordinateService.findById(coordinateId);
         if (existingCoordinate.isEmpty()) {
            return ResponseEntity.notFound().build();
         }

         // Fill in stories so we will not lose data
         coordinate.setStories(storyService.findByCoordinateId(coordinateId));

         // The response will contain coordinate identifier
         return ResponseEntity.ok(coordinateService.save(coordinate));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping
   public ResponseEntity<?> getAllCoordinates() {
      try {
         return ResponseEntity.ok(StreamSupport.stream(coordinateService.findAll().spliterator(), false));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/{coordinateId}")
   public ResponseEntity<?> getCoordinate(@PathVariable Long coordinateId) {
      try {
         if (coordinateId == null) {
            return ResponseEntity.badRequest().body("Coordinate details are mandatory");
         }

         Optional<Coordinate> coordinate = coordinateService.findById(coordinateId);
         if (coordinate.isEmpty()) {
            return ResponseEntity.notFound().build();
         }

         return ResponseEntity.ok(coordinate.get());
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   @GetMapping("/dist")
   public ResponseEntity<?> getByDistance(@RequestParam("lat") Double latitude, @RequestParam("lng") Double longitude, @RequestParam(name = "dist", required = false) Integer distanceInKm) {
      try {
         if (!rangeCheck(latitude, -90, 90) || !rangeCheck(longitude, -180, 180)) {
            return ResponseEntity.badRequest().body("Coordinate details are mandatory, where latitude ∈ [-90, 90] and longitude ∈ (-180, 180). Were: lat=" + latitude + ", lng=" + longitude);
         }

         // Use 1KM by default
         Integer distanceToUse = distanceInKm;
         if (distanceToUse == null) {
            distanceToUse = Integer.valueOf(1);
         }

         return ResponseEntity.ok(coordinateService.findByDistance(latitude.doubleValue(), longitude.doubleValue(), distanceToUse.intValue()));
      } catch (Throwable t) {
         return ControllerErrorHandler.returnInternalServerError(t);
      }
   }

   private static boolean rangeCheck(Double value, double min, double max) {
      return (value != null) && (value.doubleValue() >= min) && (value.doubleValue() <= max);
   }

}

