package com.example.websocket_app_test.repository;

import com.example.websocket_app_test.model.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    Optional<ChatUser> findByUsername(String username);

    @Query(value="SELECT * FROM chat_user WHERE username like concat('%', :username, '%')",
            nativeQuery=true)
    List<ChatUser> findAllByUsername(@Param("username") String username);
}
