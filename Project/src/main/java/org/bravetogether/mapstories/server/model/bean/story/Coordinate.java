package org.bravetogether.mapstories.server.model.bean.story;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.util.Collection;

/**
 * Coordinate information.<br/>
 * A coordinate is built out of latitude value, longitude value, a unique identifier, name of the location this coordinate represents
 * and optional image stored as byte array.<br/>
 * In addition, we hold the stories that were uploaded for some coordinate, in Many (stories) to One (coordinate) relationship.<br/>
 * The reason we have a reference to the stories is to support updating the stories (set null) when a coordinate is deleted.
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@Entity(name = "ms_coordinate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "(latitude >= -90) AND (latitude <= 90) AND (longitude > -180) AND (longitude <= 180)")
@ToString(exclude = {"stories"}) // Do not include stories or we would end up in stack overflow when printing a story
public class Coordinate {
   // A coordinate identifier might refer to null when we create a new coordinate, before we have the id generated by DB.
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "coordinate_id")
   private Long coordinateId;

   @NonNull
   private Double latitude;

   @NonNull
   private Double longitude;

   @Column(name = "location_name")
   private String locationName;

   @Column(name = "image")
   private byte[] image;

   @JsonIgnore
   @OneToMany(mappedBy="coordinate", cascade={CascadeType.PERSIST})
   Collection<Story> stories;

   public Coordinate(@NonNull Double latitude, @NonNull Double longitude, String locationName, byte[] image, Collection<Story> stories) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.locationName = locationName;
      this.image = image;
      this.stories = stories;
   }

   // To implement ON DELETE SET NULL
   @PreRemove
   private void preRemove() {
      stories.forEach(story -> story.setCoordinate(null));
   }
}

