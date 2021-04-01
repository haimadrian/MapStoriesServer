package org.bravetogether.mapstories.server.model.repository;

import org.bravetogether.mapstories.server.model.bean.story.Coordinate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

/**
 * Exposes CRUD operations implemented by spring.<br/>
 * Do not use a repository directly. Instead, auto wire a reference of {@link org.bravetogether.mapstories.server.model.service.CoordinateService}
 *
 * @author Haim Adrian
 * @since 21-Mar-21
 */
public interface CoordinateRepository extends CrudRepository<Coordinate, Long> {
   /**
    * Select all coordinates within some distance, relative to specified latitude and longitude.
    * @param latitude Latitude value
    * @param longitude Longitude value
    * @param distanceInKm Distance to use for finding coordinates in that circle
    * @return Coordinates in the circle around specified point
    */
   @Query(value = "SELECT coordinate_id, latitude, longitude, location_name,  (6371 * acos(cos(radians( :latitude )) * cos(radians( latitude )) * cos(radians( longitude ) - radians( :longitude )) + sin(radians( :latitude )) * sin(radians( latitude )))) AS distance " +
                  "FROM ms_coordinate " +
                  "HAVING distance <= :distanceInKm " +
                  "ORDER BY distance ASC",
         nativeQuery = true)
   Collection<Coordinate> findByDistance(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distanceInKm") int distanceInKm);
}

