package org.bravetogether.mapstories.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bravetogether.mapstories.server.TestUtils;
import org.bravetogether.mapstories.server.model.bean.story.Coordinate;
import org.bravetogether.mapstories.server.model.bean.user.UserDBImpl;
import org.bravetogether.mapstories.server.model.service.CoordinateService;
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
public class CoordinateControllerTest {
   private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
   private final ObjectMapper mapper = new ObjectMapper();

   @Autowired
   private CoordinateService coordinateService;

   @Autowired
   private UserService userService;

   @Autowired
   private MockMvc mockMvc;

   private String authHeader;
   private Long secondId;
   private Long lastId;

   @BeforeEach
   void setUp() throws Exception {
      // Create a user so we can use its hash key for performing operations
      UserDBImpl user = new UserDBImpl();
      user.setId("charizard@pokemon.com");
      user.setName("Charizard");
      user.setPwd("Roarrr".toCharArray());
      user.setDateOfBirth(LocalDate.of(1995, 8, 30));
      userService.save(user);

      // Sign in so we will have Authorization header to use
      String json = "{ \"id\": \"charizard@pokemon.com\", \"pwd\": \"Roarrr\" }";
      MvcResult response = mockMvc.perform(post("/user/signin").secure(true).contentType(APPLICATION_JSON_UTF8).content(json))
            .andExpect(status().isOk())
            .andReturn();

      authHeader = TestUtils.getJwtTokenFromMvcResult(response);

      Coordinate coordinate = new Coordinate(Double.valueOf(32.01623990507656), Double.valueOf(34.773109201554945), "Holon Institute of Technology", null);
      Coordinate coordinate2 = new Coordinate(Double.valueOf(32.015343027689276), Double.valueOf(34.770769562549276), "Israeli Cartoon Museum", null);
      Coordinate coordinate3 = new Coordinate(Double.valueOf(80.015343027689276), Double.valueOf(84.770769562549276), "Far away location", null);

      coordinateService.save(coordinate);
      secondId = coordinateService.save(coordinate2).getCoordinateId();
      lastId = coordinateService.save(coordinate3).getCoordinateId();
   }

   @AfterEach
   void tearDown() {
      coordinateService.deleteAll();
      userService.deleteAll();
   }

   @Test
   void testGetAll_findAllCoordinates_threeCoordinatesShouldBeFound() throws Exception {
      mockMvc.perform(get("/coordinate").secure(true).header(AUTHORIZATION_HEADER, authHeader))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.length()").value(Integer.valueOf(3)));
   }

   @Test
   void testGetByDistance_locate1KmFromCartoonMuseum_findHIT() throws Exception {
      mockMvc.perform(get("/coordinate/dist?lat=32.015343027689276&lng=34.770769562549276").secure(true).header(AUTHORIZATION_HEADER, authHeader))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.length()").value(Integer.valueOf(2))); // HIT and Israeli Museum
   }

   @Test
   void testUploadCoordinate_illegalLat_badRequest() throws Exception {
      Coordinate coordinate = new Coordinate(Double.valueOf(90.015343027689276), Double.valueOf(34.770769562549276), "somewhere", null);
      String json = mapper.writeValueAsString(coordinate);

      mockMvc.perform(post("/coordinate").secure(true).header(AUTHORIZATION_HEADER, authHeader).contentType(APPLICATION_JSON_UTF8).content(json))
             .andExpect(status().isBadRequest());
   }

   @Test
   void testUploadCoordinate_illegalLng_badRequest() throws Exception {
      Coordinate coordinate = new Coordinate(Double.valueOf(32.015343027689276), Double.valueOf(180.770769562549276), "somewhere", null);
      String json = mapper.writeValueAsString(coordinate);

      mockMvc.perform(post("/coordinate").secure(true).header(AUTHORIZATION_HEADER, authHeader).contentType(APPLICATION_JSON_UTF8).content(json))
             .andExpect(status().isBadRequest());
   }

   @Test
   void testUploadCoordinate_legalCoordinate_success() throws Exception {
      Coordinate coordinate = new Coordinate(Double.valueOf(34.015343027689276), Double.valueOf(34.770769562549276), "somewhere", null);
      String json = mapper.writeValueAsString(coordinate);

      mockMvc.perform(post("/coordinate").secure(true).header(AUTHORIZATION_HEADER, authHeader).contentType(APPLICATION_JSON_UTF8).content(json))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.coordinateId").value(Long.valueOf(lastId.longValue() + 1))) // new identifier for upload
             .andExpect(jsonPath("$.locationName").value("somewhere"));
   }

   @Test
   void testUpdateCoordinate_legalCoordinate_success() throws Exception {
      Coordinate coordinate = new Coordinate(secondId, Double.valueOf(32.015343027689276), Double.valueOf(34.770769562549276), "updatedName", null);
      String json = mapper.writeValueAsString(coordinate);

      mockMvc.perform(post("/coordinate/" + secondId).secure(true).header(AUTHORIZATION_HEADER, authHeader).contentType(APPLICATION_JSON_UTF8).content(json))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.coordinateId").value(secondId)) // same identifier for update
             .andExpect(jsonPath("$.locationName").value("updatedName"));
   }

   @Test
   void testUpdateCoordinate_wrongAuthorization_fail() throws Exception {
      // First, sign in so we will have Authorization header to use
      String json = "{ \"id\": \"charizard@pokemon.com\", \"pwd\": \"Roarrr\" }";
      mockMvc.perform(post("/user/signin").secure(true).contentType(APPLICATION_JSON_UTF8).content(json))
            .andExpect(status().isOk())
            .andReturn();

      // Second, test.
      mockMvc.perform(get("/coordinate/" + secondId).secure(true).header(AUTHORIZATION_HEADER, "wrongAuth"))
            .andExpect(status().isUnauthorized());
   }
}
