package org.bravetogether.mapstories.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bravetogether.mapstories.server.TestUtils;
import org.bravetogether.mapstories.server.model.bean.story.Coordinate;
import org.bravetogether.mapstories.server.model.bean.story.Story;
import org.bravetogether.mapstories.server.model.bean.user.UserDBImpl;
import org.bravetogether.mapstories.server.model.service.CoordinateService;
import org.bravetogether.mapstories.server.model.service.StoryService;
import org.bravetogether.mapstories.server.model.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.bravetogether.mapstories.server.config.JwtAuthenticationFilter.AUTHORIZATION_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StoryControllerTest {
   private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
   private final ObjectMapper mapper = new ObjectMapper();

   @Autowired
   private StoryService storyService;

   @Autowired
   private CoordinateService coordinateService;

   @Autowired
   private UserService userService;

   @Autowired
   private MockMvc mockMvc;

   private String authHeader;
   private String authHeader2;
   private UserDBImpl user;
   private UserDBImpl user2;
   private Long coordId;
   private Long secondStoryId;
   private Long lastStoryId;
   private Coordinate coordinate3;

   @BeforeEach
   void setUp() throws Exception {
      // Create a user so we can use its hash key for performing operations
      user = new UserDBImpl();
      user.setId("charizard@pokemon.com");
      user.setName("Charizard");
      user.setPwd("Roarrr".toCharArray());
      user.setDateOfBirth(LocalDate.of(1993, 7, 15));
      user = (UserDBImpl)userService.save(user);

      user2 = new UserDBImpl();
      user2.setId("charmander@pokemon.com");
      user2.setName("Charmander");
      user2.setPwd("Charrr".toCharArray());
      user2.setDateOfBirth(LocalDate.of(1995, 8, 30));
      user2 = (UserDBImpl)userService.save(user2);

      // Sign in so we will have Authorization header to use
      String json = "{ \"id\": \"charizard@pokemon.com\", \"pwd\": \"Roarrr\" }";
      MvcResult response = mockMvc.perform(post("/user/signin").secure(true).contentType(APPLICATION_JSON_UTF8).content(json))
            .andExpect(status().isOk())
            .andReturn();
      authHeader = TestUtils.getJwtTokenFromMvcResult(response);
      json = "{ \"id\": \"charmander@pokemon.com\", \"pwd\": \"Charrr\" }";
      response = mockMvc.perform(post("/user/signin").secure(true).contentType(APPLICATION_JSON_UTF8).content(json))
            .andExpect(status().isOk())
            .andReturn();
      authHeader2 = TestUtils.getJwtTokenFromMvcResult(response);

      Coordinate coordinate = new Coordinate(Double.valueOf(32.01623990507656), Double.valueOf(34.773109201554945), "Holon Institute of Technology", null);
      Coordinate coordinate2 = new Coordinate(Double.valueOf(32.015343027689276), Double.valueOf(34.770769562549276), "Israeli Cartoon Museum", null);
      coordinate3 = new Coordinate(Double.valueOf(80.015343027689276), Double.valueOf(84.770769562549276), "Far away location", null);

      coordinate = coordinateService.save(coordinate);
      coordinate2 = coordinateService.save(coordinate2);
      coordId = coordinate.getCoordinateId();
      coordinateService.save(coordinate3);

      Story story = new Story(user,
            coordinate,
            LocalDate.of(2020, 10, 22),
            "Ariana Grande",
            "Positions",
            "Heaven sent you to me\n" +
               "I'm just hopin' I don't repeat history",
            "https://www.youtube.com/watch?v=tcYodQoapMg",
            null);
      Story story2 = new Story(user,
            coordinate,
            LocalDate.of(2020, 11, 17),
            "Ariana Grande", "34+35",
            "Can you stay up all night?\n" +
                  "Fuck me 'til the daylight\n" +
                  "Thirty-four, thirty-five",
            "https://www.youtube.com/watch?v=B6_iQvaIjXw",
            null);
      Story story3 = new Story(user,
            coordinate2,
            LocalDate.of(2019, 11, 10),
            "Chrissy Costanza",
            "Phoenix",
            "So are you gonna die today or make it out alive?\n" +
               "You gotta conquer the monster in your head and then you'll fly\n" +
               "Fly, phoenix, fly\n" +
               "It's time for a new empire\n" +
               "Go bury your demons then tear down the ceiling\n" +
               "Phoenix, fly",
            "https://www.youtube.com/watch?v=dpdWuM4SZdc&ab_channel=LeagueofLegends",
            null);
      Story story4 = new Story(user,
            coordinate2,
            LocalDate.of(2019, 11, 10),
            "toBeUpdated",
            "someTitle",
            "contentHere",
            null,
            null);

      storyService.save(story);
      secondStoryId = storyService.save(story2).getStoryId();
      storyService.save(story3);
      lastStoryId = storyService.save(story4).getStoryId();
   }

   @AfterEach
   void tearDown() {
      coordinateService.deleteAll();
      storyService.deleteAll();
      userService.deleteAll();
   }

   @Test
   void testGetById_findStory_success() throws Exception {
      mockMvc.perform(get("/story/" + secondStoryId).secure(true).header(AUTHORIZATION_HEADER, authHeader))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.storyId").value(secondStoryId));
   }

   @Test
   void testGetByHeroName_arianaGrande_twoStoriesShouldBeFound() throws Exception {
      mockMvc.perform(get("/story/hero/Ariana").secure(true).header(AUTHORIZATION_HEADER, authHeader))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.length()").value(Integer.valueOf(2))); // 2 songs
   }

   @Test
   void testGetByTitle_phoenix_success() throws Exception {
      mockMvc.perform(get("/story/title/phoenix").secure(true).header(AUTHORIZATION_HEADER, authHeader))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.length()").value(Integer.valueOf(1))); // 1 song
   }

   @Test
   void testGetByUserId_charizard_fourStories() throws Exception {
      mockMvc.perform(get("/story/user/charizard@pokemon.com").secure(true).header(AUTHORIZATION_HEADER, authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(Integer.valueOf(4))); // 4 stories
   }

   @Test
   void testGetByLocation_holon_twoStoriesAtHit() throws Exception {
      mockMvc.perform(get("/story/location/holon").secure(true).header(AUTHORIZATION_HEADER, authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(Integer.valueOf(2))); // 2 stories at HIT
   }

   @Test
   void testGetByLocation_noLocation_successEmpty() throws Exception {
      mockMvc.perform(get("/story/location/nonExisting").secure(true).header(AUTHORIZATION_HEADER, authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(Integer.valueOf(0)));
   }

   @Test
   void testGetByCoordinate_holonId_twoStoriesAtHit() throws Exception {
      mockMvc.perform(get("/story/coordinate/" + coordId).secure(true).header(AUTHORIZATION_HEADER, authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(Integer.valueOf(2))); // 2 stories at HIT
   }

   @Test
   void testUploadStory_illegalCoordinate_badRequest() throws Exception {
      Coordinate coordinate = new Coordinate(Long.valueOf(0), Double.valueOf(90.015343027689276), Double.valueOf(34.770769562549276), "somewhere", null);
      Story story = new Story(user2,
            coordinate,
            LocalDate.of(2019, 11, 10),
            "A",
            "A",
            "someContent",
            null,
            null);
      String json = mapper.writeValueAsString(story);

      mockMvc.perform(post("/story").secure(true).header(AUTHORIZATION_HEADER, authHeader2).contentType(APPLICATION_JSON_UTF8).content(json))
             .andExpect(status().isBadRequest()); // Coordinate does not exist
   }

   @Test
   void testUploadStory_legalStory_success() throws Exception {
      Story story = new Story(user2,
            coordinate3,
            LocalDate.of(2019, 11, 10),
            "A",
            "A",
            "someContent",
            null,
            null);
      String json = mapper.writeValueAsString(story);

      mockMvc.perform(post("/story").secure(true).header(AUTHORIZATION_HEADER, authHeader2).contentType(APPLICATION_JSON_UTF8).content(json))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.storyId").value(Long.valueOf(lastStoryId.longValue() + 1))) // new identifier for upload
             .andExpect(jsonPath("$.title").value("A"));
   }

   @Test
   void testUpdateStory_legalStory_success() throws Exception {
      Story story = new Story(lastStoryId,
            user,
            coordinate3,
            LocalDate.of(2019, 11, 10),
            "updatedName",
            "A",
            "someContent",
            null,
            null);
      String json = mapper.writeValueAsString(story);

      mockMvc.perform(post("/story/" + lastStoryId).secure(true).header(AUTHORIZATION_HEADER, authHeader2).contentType(APPLICATION_JSON_UTF8).content(json))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.storyId").value(lastStoryId)) // same identifier for update
             .andExpect(jsonPath("$.heroName").value("updatedName"))
             .andExpect(jsonPath("$.title").value("A"));
   }

   @Test
   void testUpdateStory_wrongUser_badRequest() throws Exception {
      Story story = new Story(lastStoryId,
            user2,
            coordinate3,
            LocalDate.of(2019, 11, 10),
            "updatedName",
            "A",
            "someContent",
            null,
            null);
      String json = mapper.writeValueAsString(story);

      mockMvc.perform(post("/story/" + lastStoryId).secure(true).header(AUTHORIZATION_HEADER, authHeader2).contentType(APPLICATION_JSON_UTF8).content(json))
            .andExpect(status().isBadRequest());
   }

   @Test
   void testUpdateStory_wrongAuthorization_fail() throws Exception {
      // First, sign in so we will have Authorization header to use
      String json = "{ \"id\": \"charizard@pokemon.com\", \"pwd\": \"Roarrr\" }";
      mockMvc.perform(post("/user/signin").secure(true).contentType(APPLICATION_JSON_UTF8).content(json))
            .andExpect(status().isOk())
            .andReturn();

      // Second, test.
      mockMvc.perform(get("/story/" + secondStoryId).secure(true).header(AUTHORIZATION_HEADER, "wrongAuth"))
            .andExpect(status().isUnauthorized());
   }
}
