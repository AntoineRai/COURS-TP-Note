package com.example.tpnotetest.service.impl;

import com.example.tpnotetest.dto.UserDTO;
import com.example.tpnotetest.exception.DataClownException;
import com.example.tpnotetest.exception.ObjectNotFoundException;
import com.example.tpnotetest.model.User;
import com.example.tpnotetest.repository.UserRepository;
import com.example.tpnotetest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail())).collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DataClownException("Email already exists");
        }
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    @Override
    public UserDTO updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DataClownException("Email already exists");
        }
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        User updatedUser = userRepository.save(existingUser);
        return new UserDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
