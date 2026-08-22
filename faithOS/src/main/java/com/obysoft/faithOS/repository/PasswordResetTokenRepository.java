package com.obysoft.faithOS.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.obysoft.faithOS.entity.PasswordResetToken;
import com.obysoft.faithOS.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteAllByUser(User user);
}
