package org.example.userserver.service;

import org.example.userserver.dto.CreateUserRequest;
import org.example.userserver.dto.UserDTO;
import org.example.userserver.entity.User;
import org.example.userserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_CACHE_PREFIX = "user:";
    
    public UserDTO createUser(CreateUserRequest request) {
        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.valueOf(request.getRole()));
        user.setStatus(User.UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // 缓存用户信息
        cacheUser(savedUser);
        
        return UserDTO.fromEntity(savedUser);
    }
    
    public Optional<UserDTO> getUserById(Long id) {
        // 先从缓存获取
        User cachedUser = (User) redisTemplate.opsForValue().get(USER_CACHE_PREFIX + id);
        if (cachedUser != null) {
            return Optional.of(UserDTO.fromEntity(cachedUser));
        }
        
        // 缓存未命中，从数据库获取
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            cacheUser(user);
            return Optional.of(UserDTO.fromEntity(user));
        }
        
        return Optional.empty();
    }
    
    public Optional<UserDTO> getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(UserDTO::fromEntity);
    }
    
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<UserDTO> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(User.UserRole.valueOf(role));
        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<UserDTO> searchUsers(String keyword) {
        List<User> users = userRepository.findByKeyword(keyword);
        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public UserDTO updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户名和邮箱是否被其他用户使用
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.valueOf(request.getRole()));
        
        User updatedUser = userRepository.save(user);
        
        // 更新缓存
        cacheUser(updatedUser);
        
        return UserDTO.fromEntity(updatedUser);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        
        userRepository.deleteById(id);
        
        // 删除缓存
        redisTemplate.delete(USER_CACHE_PREFIX + id);
    }
    
    public void updateLastLogin(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            cacheUser(user);
        }
    }
    
    private void cacheUser(User user) {
        redisTemplate.opsForValue().set(USER_CACHE_PREFIX + user.getId(), user);
    }
}
