package com.example.tpnotetest;

import com.example.tpnotetest.dto.UserDTO;
import com.example.tpnotetest.exception.DataClownException;
import com.example.tpnotetest.exception.ObjectNotFoundException;
import com.example.tpnotetest.model.User;
import com.example.tpnotetest.repository.UserRepository;
import com.example.tpnotetest.service.UserService;
import com.example.tpnotetest.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Lorsqu'on cherche par ID et qu'on ne trouve pas, retourner une exception")
    public void testGetUserById_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Lorsqu'on cherche par ID, retourner une instance d'utilisateur")
    public void testGetUserById_Success() {
        User user = new User(1L, "John Doe", "john@example.com", "password");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDTO userDto = userService.getUserById(1L);
        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("john@example.com", userDto.getEmail());
    }

    @Test
    @DisplayName("Lorsqu'on enregistre avec un email déjà enregistré, retourner une exception")
    public void testCreateUser_EmailAlreadyExists() {
        User user = new User(null, "John Doe", "john@example.com", "password");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(DataClownException.class, () -> userService.createUser(user));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Lorsqu'on enregistre l'utilisateur, retourner un succès")
    public void testCreateUser_Success() {
        User user = new User(null, "John Doe", "john@example.com", "password");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "John Doe", "john@example.com", "password"));
        UserDTO userDto = userService.createUser(user);
        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("john@example.com", userDto.getEmail());
    }

    @Test
    @DisplayName("Lorsqu'on met à jour l'utilisateur, retourner un succès")
    public void testUpdateUser_UserNotFound() {
        User user = new User(null, "John Doe", "john@example.com", "password");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(1L, user));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Lorsqu'on met à jour avec un email déjà enregistré, retourner une exception")
    public void testUpdateUser_EmailAlreadyExists() {
        User existingUser = new User(1L, "John Doe", "john@example.com", "password");
        User userToUpdate = new User(null, "Jane Doe", "jane@example.com", "password");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userToUpdate));
        Exception exception = assertThrows(DataClownException.class, () -> userService.updateUser(1L, userToUpdate));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Lorsqu'on met à jour l'utilisateur, retourner un succès")
    public void testUpdateUser_Success() {
        User existingUser = new User(1L, "John Doe", "john@example.com", "password");
        User userToUpdate = new User(null, "John Smith", "john.smith@example.com", "newpassword");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "John Smith", "john.smith@example.com", "newpassword"));
        UserDTO userDto = userService.updateUser(1L, userToUpdate);
        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("John Smith", userDto.getName());
        assertEquals("john.smith@example.com", userDto.getEmail());
    }

    @Test
    @DisplayName("Lorsqu'on supprime un utilisateur qui n'existe pas, retourner une exception")
    public void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Lorsqu'on supprime un utilisateur avec succès")
    public void testDeleteUser_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
