package home.serg.chatik.dao.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static home.serg.chatik.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private final UserRepository userRepository = new UserRepository();

    @BeforeAll
    public static void initDb() {
        initDbWithOneUser();
    }

    @AfterAll
    public static void clearDb() {
        clearDatabase();
    }

    @Test
    void findById_shouldReturnExistingUser() {
        Optional<User> optionalUser = userRepository.findById(DEFAULT_USER_ID);

        assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();

        assertAll(
                ()-> assertEquals(DEFAULT_USER_ID, user.getId()),
                ()-> assertEquals(EXISTING_USER_NAME, user.getUsername()),
                ()-> assertEquals(EXISTING_USER_PASSWORD, user.getPassword()),
                ()-> assertEquals(EXISTING_USER_ROLE, user.getRole()),
                ()-> assertEquals(EXISTING_USER_BLOCKED, user.getBlocked())
        );
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenUserNotFound(){
        Optional<User> optionalUser = userRepository.findById(WRONG_ID);

        assertTrue(optionalUser.isEmpty());
    }


    @Test
    void save_shouldSaveUserToDbAndReturnUserWithId() {
        User user = userRepository.save(BLOCKED_USER);

        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    void update_shouldUpdateUserInDbAndReturnUser() {
        User userForUpdate = new User("update", "update", Role.USER, true);
        String newUsername = "new username";
        User user = userRepository.save(userForUpdate);
        user.setUsername(newUsername);
        user.setBlocked(false);

        userRepository.update(user);

        User savedUser = userRepository.findById(user.getId()).orElse(null);

        assertNotNull(savedUser);
        assertFalse(savedUser.getBlocked());
        assertEquals(newUsername, savedUser.getUsername());

    }

    @Test
    void deleteById_shouldDeleteUserFromDb() {
        User userForDelete = new User("update", "update", Role.USER, true);
        User user = userRepository.save(userForDelete);

        assertTrue(userRepository.deleteById(user.getId()));

        User deletedUser = userRepository.findById(user.getId()).orElse(null);

        assertNull(deletedUser);
    }

    @Test
    void findByUsername_shouldReturnExistingUser() {
        Optional<User> optionalUser = userRepository.findByUsername(EXISTING_USER_NAME);

        assertTrue(optionalUser.isPresent());

        User user = optionalUser.get();

        assertAll(
                ()-> assertEquals(DEFAULT_USER_ID, user.getId()),
                ()-> assertEquals(EXISTING_USER_NAME, user.getUsername()),
                ()-> assertEquals(EXISTING_USER_PASSWORD, user.getPassword()),
                ()-> assertEquals(EXISTING_USER_ROLE, user.getRole()),
                ()-> assertEquals(EXISTING_USER_BLOCKED, user.getBlocked())
        );
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenUserNotFound(){
        Optional<User> optionalUser = userRepository.findByUsername(WRONG_USER_NAME);

        assertTrue(optionalUser.isEmpty());
    }
}