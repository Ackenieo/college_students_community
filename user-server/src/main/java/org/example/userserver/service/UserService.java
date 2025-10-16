package org.example.userserver.service;

import org.example.userserver.dto.*;
import org.example.userserver.entity.User;
import org.example.userserver.repository.UserRepository;
import org.example.userserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private VerifyCodeService verifyCodeService;
    
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
    
    // 认证相关方法
    public AuthResponse login(LoginRequest request) {
        // 根据邮箱查找用户
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("用户账户已被禁用");
        }
        
        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        cacheUser(user);
        
        // 生成JWT token
        String token = jwtUtil.generateToken(
                user.getId(), 
                user.getUsername(), 
                user.getEmail(), 
                user.getRole().name()
        );
        
        // 计算token过期时间
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        
        return AuthResponse.success(
                token, 
                user.getId(), 
                user.getUsername(), 
                user.getEmail(), 
                user.getRole().name(), 
                expiresAt
        );
    }
    
    public UserDTO register(RegisterRequest request) {
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
        user.setRole(User.UserRole.STUDENT);
        user.setStatus(User.UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        cacheUser(savedUser);
        
        return UserDTO.fromEntity(savedUser);
    }
    
    public UserDTO registerWithVerification(RegisterRequest request, VerifyCodeRequest verifyRequest) {
        // 验证邮箱验证码
        if (!verifyCodeService.verifyCode(request.getEmail(), verifyRequest.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 注册用户
        UserDTO user = register(request);
        
        // 清除验证码
        verifyCodeService.clearCode(request.getEmail());
        
        return user;
    }
    
    public void sendRegisterVerificationCode(String email) {
        // 检查邮箱是否已注册
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("该邮箱已被注册");
        }
        
        // 生成并存储验证码
        String code = verifyCodeService.generateAndStoreCode(email, Duration.ofMinutes(10));
        
        // TODO: 发送邮件通知
        // 这里应该调用通知服务发送验证码邮件
        System.out.println("注册验证码: " + code + " (邮箱: " + email + ")");
    }
    
    public void sendPasswordResetCode(String email) {
        // 检查邮箱是否存在
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("该邮箱未注册");
        }
        
        // 生成并存储验证码
        String code = verifyCodeService.generateAndStoreCode(email, Duration.ofMinutes(10));
        
        // TODO: 发送邮件通知
        // 这里应该调用通知服务发送密码重置验证码邮件
        System.out.println("密码重置验证码: " + code + " (邮箱: " + email + ")");
    }
    
    public void resetPassword(PasswordResetRequest request) {
        // 验证邮箱验证码
        if (!verifyCodeService.verifyCode(request.getEmail(), request.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 查找用户
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        cacheUser(user);
        
        // 清除验证码
        verifyCodeService.clearCode(request.getEmail());
    }
    
    public Optional<UserDTO> getUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(UserDTO::fromEntity);
    }
}
