package org.example.communityserver.repository;

import org.example.communityserver.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    boolean existsByUserIdAndBlockedUserId(Long userId, Long blockedUserId);

    Optional<Blacklist> findByUserIdAndBlockedUserId(Long userId, Long blockedUserId);

    void deleteByUserIdAndBlockedUserId(Long userId, Long blockedUserId);
}
