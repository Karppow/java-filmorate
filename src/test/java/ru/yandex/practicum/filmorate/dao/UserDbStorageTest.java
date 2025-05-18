package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")  // профиль с настройками для H2
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        // Удаляем данные из user_friends и users, чтобы не было конфликта по уникальности email
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM users");
    }

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        user1.setLogin("alice123");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        user2 = new User();
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        user2.setLogin("bob123");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
    }

    @Test
    void addAndGetUserTest() {
        User added = userDbStorage.addUser(user1);
        assertThat(added.getId()).isPositive();

        User fetched = userDbStorage.getUser(added.getId());
        assertThat(fetched).isNotNull();
        assertThat(fetched.getEmail()).isEqualTo(user1.getEmail());
        assertThat(fetched.getFriends()).isEmpty();
    }

    @Test
    void updateUserTest() {
        User added = userDbStorage.addUser(user1);
        added.setName("Updated Alice");
        userDbStorage.updateUser(added);

        User updated = userDbStorage.getUser(added.getId());
        assertThat(updated.getName()).isEqualTo("Updated Alice");
    }

    @Test
    void addAndRemoveFriendTest() {
        User u1 = userDbStorage.addUser(user1);
        User u2 = userDbStorage.addUser(user2);

        userDbStorage.addFriend(u1.getId(), u2.getId());
        List<User> friends = userDbStorage.getFriends(u1.getId());
        assertThat(friends).extracting("id").contains(u2.getId());

        userDbStorage.removeFriend(u1.getId(), u2.getId());
        List<User> friendsAfterRemove = userDbStorage.getFriends(u1.getId());
        assertThat(friendsAfterRemove).doesNotContain(u2);
    }

    @Test
    void getCommonFriendsTest() {
        User u1 = userDbStorage.addUser(user1);
        User u2 = userDbStorage.addUser(user2);

        User common = new User();
        common.setName("Common Friend");
        common.setEmail("common@example.com");
        common.setLogin("common123");
        common.setBirthday(LocalDate.of(1995, 5, 5));
        User commonUser = userDbStorage.addUser(common);

        userDbStorage.addFriend(u1.getId(), commonUser.getId());
        userDbStorage.addFriend(u2.getId(), commonUser.getId());

        List<Integer> commonFriends = userDbStorage.getCommonFriends(u1.getId(), u2.getId());
        assertThat(commonFriends).contains(commonUser.getId());
    }

    @Test
    void userExistsTest() {
        User added = userDbStorage.addUser(user1);
        assertThat(userDbStorage.userExists(added.getId())).isTrue();
        assertThat(userDbStorage.userExists(99999)).isFalse();
    }

    @Test
    void getUsersByIdsTest() {
        User u1 = userDbStorage.addUser(user1);
        User u2 = userDbStorage.addUser(user2);

        Set<User> users = userDbStorage.getUsersByIds(Set.of(u1.getId(), u2.getId()));
        assertThat(users).extracting("id").containsExactlyInAnyOrder(u1.getId(), u2.getId());
    }
}
