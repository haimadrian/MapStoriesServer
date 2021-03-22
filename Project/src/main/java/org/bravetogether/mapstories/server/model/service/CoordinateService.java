package org.bravetogether.mapstories.server.model.service;

import org.bravetogether.mapstories.server.model.bean.story.Coordinate;
import org.bravetogether.mapstories.server.model.repository.CoordinateRepository;
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
public class CoordinateService {
   @Autowired
   private CoordinateRepository coordinateRepository;

   /**
    * See {@link CoordinateRepository#findAll(Object)}
    */
   public Iterable<Coordinate> findAll() {
      return coordinateRepository.findAll();
   }

   /**
    * See {@link CoordinateRepository#findById(Object)}
    */
   public Optional<Coordinate> findById(Long id) {
      return coordinateRepository.findById(id);
   }

   /**
    * See {@link CoordinateRepository#findByDistance(double, double, int)}
    */
   public Collection<Coordinate> findByDistance(double latitude, double longitude, int distance) {
      return coordinateRepository.findByDistance(latitude, longitude, distance);
   }

   /**
    * See {@link CoordinateRepository#save(Object)}
    */
   public Coordinate save(Coordinate coordinate) {
      return coordinateRepository.save(coordinate);
   }

   /**
    * See {@link CoordinateRepository#existsById(Object)}
    */
   public boolean existsById(Long id) {
      return coordinateRepository.existsById(id);
   }

   /**
    * See {@link CoordinateRepository#deleteAll()}
    */
   public void deleteAll() {
      coordinateRepository.deleteAll();
   }
}

