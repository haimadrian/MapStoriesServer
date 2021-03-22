package org.bravetogether.mapstories.server.model.service;

import org.bravetogether.mapstories.server.model.bean.user.User;
import org.bravetogether.mapstories.server.model.bean.user.UserDBImpl;
import org.bravetogether.mapstories.server.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Wrap the access to the repository, so we can add additional logic between controller and repository.</br>
 * For example, we use a cache for better performance, so it is done in the service, rather than repository.
 * @author Haim Adrian
 * @since 21-Mar-21
 */
@Component
public class UserService implements UserDetailsService {
   @Autowired
   private UserRepository userRepository;

   @Autowired
   private PasswordEncoder passwordEncoder;

   /**
    * See {@link UserRepository#findById(Object)}
    */
   @CachePut(value = "userCache", key = "#id") // Cache the results of this method because we call it from AuthorizationFilter, and we need it to be as fast as possible.
   public Optional<? extends User> findById(String id) {
      return userRepository.findByIdIgnoreCase(id);
   }

   /**
    * See {@link UserRepository#save(Object)}<br/>
    * <b>Note</b> that this method will encode user password, so you must not save a previously saved user reference,
    * to avoid of encoding an encoded password.
    */
   @CacheEvict(value = "userCache", key = "#user.id") // When we save, we want to remove item from cache, in order to have the up to date item in the cache.
   public User save(UserDBImpl user) {
      // Do not save passwords as clear text and achieve a higher security level this way.
      user.encodePassword(passwordEncoder);
      return userRepository.save(user);
   }

   /**
    * Add x coins to user and update the database with the calculated value.
    * @param user The user to get its current amount of coins, and add the specified amount
    * @param coins The amount of coins to add/remove
    */
   @CacheEvict(value = "userCache", key = "#user.id") // When we save, we want to remove item from cache, in order to have the up to date item in the cache.
   public void addCoins(User user, long coins) {
      long newAmountOfCoins = user.getCoins() + coins;
      ((UserDBImpl)user).setCoins(newAmountOfCoins);
      userRepository.updateCoins(user.getId(), newAmountOfCoins);
   }

   /**
    * See {@link UserRepository#existsById(Object)}
    */
   public boolean existsById(String id) {
      return userRepository.existsByIdIgnoreCase(id);
   }

   /**
    * See {@link UserRepository#deleteAll()}
    */
   public void deleteAll() {
      userRepository.deleteAll();
   }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Optional<? extends User> user = findById(username);
      if (user.isEmpty()) {
         throw new UsernameNotFoundException("User " + username + " is not registered. Please sign up");
      }

      return ((UserDBImpl)user.get()).toUserDetails();
   }
}

