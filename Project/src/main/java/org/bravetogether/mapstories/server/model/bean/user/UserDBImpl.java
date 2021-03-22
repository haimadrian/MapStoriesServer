package org.bravetogether.mapstories.server.model.bean.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A user model that we persist to database.<br/>
 * In order to avoid of returning user db model sensitive information from the server, we exclude pwd and key from json.
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@JsonDeserialize(as = UserDBImpl.class)
@Entity(name = "ms_user")
@NoArgsConstructor
@ToString(exclude = {"pwd"})
public class UserDBImpl implements User {
   /**
    * See {@link User#getId()}
    */
   @Id
   @Getter
   @Setter
   @NonNull
   private String id;

   /**
    * The password of a user. We use it when signing/logging in a user.
    */
   @Getter(AccessLevel.PACKAGE)
   @Setter
   @NonNull
   @JsonIgnore
   private char[] pwd;

   /**
    * See {@link User#getName()}
    */
   @Getter
   @Setter
   @NonNull
   private String name;

   /**
    * See {@link User#getDateOfBirth()}
    */
   @Getter
   @Setter
   @NonNull
   @JsonDeserialize(using = LocalDateDeserializer.class) // Date format is: yyyy-MM-dd. e.g. 1995-08-30
   @JsonSerialize(using = LocalDateSerializer.class)
   private LocalDate dateOfBirth;

   /**
    * See {@link User#getCoins()}
    */
   @Getter
   @Setter
   private long coins;

   /**
    * Constructs a new {@link UserDBImpl}, copying all fields out of another user.<br/>
    * We use a copy constructor in order to convert client model to server model and vice versa
    * @param user The user to get fields from
    */
   public UserDBImpl(User user) {
      this.id = user.getId();
      this.name = user.getName();
      this.coins = user.getCoins();
      this.dateOfBirth = user.getDateOfBirth();

      if (user instanceof UserImpl) {
         this.pwd = ((UserImpl)user).getPwd();
      } else if (user instanceof UserDBImpl) {
         this.pwd = ((UserDBImpl)user).getPwd();
      }
   }

   public void encodePassword(PasswordEncoder passwordEncoder) {
      if (pwd != null) {
         pwd = passwordEncoder.encode(new String(pwd)).toCharArray();
      }
   }

   /**
    * Converts this user into {@link UserDetails} reference to be used for authenticating this user
    * @return A user details reference to use for authentication
    */
   public UserDetails toUserDetails() {
      return new UserDetails() {
         @Override
         public Collection<? extends GrantedAuthority> getAuthorities() {
            return new ArrayList<>();
         }

         @Override
         public String getPassword() {
            return new String(pwd);
         }

         @Override
         public String getUsername() {
            return id;
         }

         @Override
         public boolean isAccountNonExpired() {
            return true;
         }

         @Override
         public boolean isAccountNonLocked() {
            return true;
         }

         @Override
         public boolean isCredentialsNonExpired() {
            return true;
         }

         @Override
         public boolean isEnabled() {
            return true;
         }
      };
   }
}

