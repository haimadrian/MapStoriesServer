package org.bravetogether.mapstories.server.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * A helper class to encode/decode user details.<br/>
 * This is a super basic mechanism...
 *
 * @deprecated I have configured Spring to require SSL, and used JWT authentication in order to authenticate users, instead of this
 * clumsy implementation here.
 *
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@Deprecated
public class AuthorizationHelper {
   /**
    * Make sure user hash key does not contain this character, cause we use it as a separator between user identifier and hash key.
    */
   public static final String USER_TOKEN_SEPARATOR = "##";
   private static final ThreadLocal<Base64.Decoder> decoder = ThreadLocal.withInitial(Base64::getDecoder);
   private static final ThreadLocal<Base64.Encoder> encoder = ThreadLocal.withInitial(Base64::getEncoder);
   public static final String BASE_64_REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";

   /**
    * Use this method to encode user details into a string that can be sent to client.<br/>
    * We use basic Authorization mechanism, using user identifier and some hash key in order to recognize the client
    * and protect the APIs of the server
    * @param userToken The user token to encode
    * @return Encoded user token
    */
   public static String encodeUser(UserToken userToken) {
      return encoder.get().encodeToString((userToken.getUserId() + USER_TOKEN_SEPARATOR + userToken.getUserHashKey()).getBytes(StandardCharsets.UTF_8));
   }

   /**
    * Use this method to decode user string and extract user details out of it.<br/>
    * This is a string that we've sent to client, so now we should decode it so we can compare it to server data
    * and verify if client got access to the APIs.
    * @param encodedString The string containing user token
    * @return A user token containing the decoded data, or null in case the input was fake.
    */
   public static UserToken decodeUser(String encodedString) {
      if (encodedString != null && encodedString.matches(BASE_64_REGEX)) {
         String[] userIdAndKey = new String(decoder.get().decode(encodedString)).split(USER_TOKEN_SEPARATOR);
         if (userIdAndKey.length == 2) {
            return new UserToken(userIdAndKey[0], userIdAndKey[1]);
         }
      }

      return null;
   }

   public static String generateUserHashKey() {
      return UUID.randomUUID().toString().replace(USER_TOKEN_SEPARATOR, "!!");
   }

   @AllArgsConstructor
   @Data
   public static class UserToken {
      private String userId;
      private String userHashKey;
   }
}

