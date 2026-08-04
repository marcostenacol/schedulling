package com.scheduling.modules.auth.repository;

import com.scheduling.modules.auth.model.RefreshToken;
import com.scheduling.modules.auth.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(String token);

  /**
   * {@code @Modifying} força um bulk DELETE executado de imediato — sem isso, o Hibernate agenda a
   * remoção via entityManager.remove() no flush, que roda DEPOIS dos inserts pendentes na mesma
   * transação e violava a constraint unique(user_id) ao logar duas vezes seguidas com o mesmo
   * usuário (o insert do novo refresh token corria antes do delete do antigo).
   */
  @Modifying
  @Query("delete from RefreshToken r where r.user = :user")
  void deleteByUser(User user);
}
