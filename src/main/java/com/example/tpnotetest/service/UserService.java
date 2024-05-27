package com.example.tpnotetest.service;

import com.example.tpnotetest.dto.UserDTO;
import com.example.tpnotetest.model.User;

import java.util.List;

public interface UserService {
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO createUser(User user);
    UserDTO updateUser(Long id, User user);
    void deleteUser(Long id);
}
