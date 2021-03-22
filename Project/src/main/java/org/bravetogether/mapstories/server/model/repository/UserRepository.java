package org.bravetogether.mapstories.server.model.repository;

import org.bravetogether.mapstories.server.model.bean.user.UserDBImpl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Exposes CRUD operations implemented by spring.<br/>
 * Do not use a repository directly. Instead, auto wire a reference of {@link org.bravetogether.mapstories.server.model.service.UserService}
 *
 * @author Haim Adrian
 * @since 21-Mar-21
 */
public interface UserRepository extends CrudRepository<UserDBImpl, String> {
   Optional<UserDBImpl> findByIdIgnoreCase(String id);
   boolean existsByIdIgnoreCase(String id);

   @Transactional
   @Modifying
   @Query("UPDATE ms_user SET coins = :coins WHERE id = :id")
   void updateCoins(@Param(value = "id") String id, @Param(value = "coins") long coins);
}

