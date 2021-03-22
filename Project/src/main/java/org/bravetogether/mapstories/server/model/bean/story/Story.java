package org.bravetogether.mapstories.server.model.bean.story;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bravetogether.mapstories.server.model.bean.user.UserDBImpl;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Story information.<br/>
 * Each story got a unique identifier, the user who uploaded that story, a coordinate to tell where this story was told,
 * so we can show it over map, 'since' date so we can tell from which date the story is, name of the hero that the story
 * is about, a title and content. In addition, there is an optional link to video, to connect stories to videos.
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@Entity(name = "ms_story")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Story {
   // A story identifier might refer to null when we create a new story, before we have the id generated by DB.
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "story_id")
   private Long storyId;

   @ManyToOne
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   @NonNull
   private UserDBImpl user;

   @ManyToOne
   @JoinColumn(name = "coordinate_id", referencedColumnName = "coordinate_id")
   private Coordinate coordinate;

   @JsonDeserialize(using = LocalDateDeserializer.class) // Date format is: yyyy-MM-dd. e.g. 1995-08-30
   @JsonSerialize(using = LocalDateSerializer.class)
   private LocalDate since;

   @Column(name = "hero_name")
   private String heroName;

   private String title;

   private String content;

   @Column(name = "link_to_video")
   private String linkToVideo;

   public Story(@NonNull UserDBImpl user, Coordinate coordinate, LocalDate since, String heroName, String title, String content, String linkToVideo) {
      this.user = user;
      this.coordinate = coordinate;
      this.since = since;
      this.heroName = heroName;
      this.title = title;
      this.content = content;
      this.linkToVideo = linkToVideo;
   }
}
