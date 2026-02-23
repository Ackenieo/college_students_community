package org.example.userserver.service;

import org.example.userserver.dto.*;
import org.example.userserver.vo.*;
import org.example.userserver.entity.User;
import org.example.userserver.repository.UserRepository;
import org.example.common.util.JwtUtil;
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
    
    @Autowired
    private EmailService emailService;
    
    private static final String USER_CACHE_PREFIX = "user:";

    
    private void cacheUser(User user) {
        // 将 User 转换为 UserDTO 再缓存，避免 LocalDateTime 序列化问题
        UserDTO userDTO = UserDTO.fromEntity(user);
        redisTemplate.opsForValue().set(USER_CACHE_PREFIX + user.getId(), userDTO);
    }
    
    // 认证相关方法
    public AuthResponseVO login(LoginRequestVO request) {
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
        
        return AuthResponseVO.success(
                token, 
                user.getId(), 
                user.getUsername(), 
                user.getEmail(), 
                user.getRole().name(), 
                expiresAt
        );
    }
    
    
    public UserDTO register(RegisterRequestVO request, VerifyCodeRequestVO verifyRequest) {
        // 验证邮箱验证码
        if (!verifyCodeService.verifyCode(request.getEmail(), verifyRequest.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.STUDENT);
        user.setStatus(User.UserStatus.ACTIVE);
        
        // 保存到数据库
        User savedUser = userRepository.save(user);
        
        // 缓存用户信息
        cacheUser(savedUser);
        
        // 清除验证码
        verifyCodeService.clearCode(request.getEmail());
        
        return UserDTO.fromEntity(savedUser);
    }
    
    public void sendRegisterVerificationCode(String email) {
        // 检查邮箱是否已注册
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("该邮箱已被注册");
        }
        
        // 生成并存储验证码
        String code = verifyCodeService.generateAndStoreCode(email, Duration.ofMinutes(5));
        
        // 发送验证码邮件
        boolean emailSent = emailService.sendVerificationCode(email, code);
        if (!emailSent) {
            throw new RuntimeException("验证码邮件发送失败，请稍后重试");
        }
    }
    
    public void sendPasswordResetCode(String email) {
        // 检查邮箱是否存在
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("该邮箱未注册");
        }
        
        // 生成并存储验证码
        String code = verifyCodeService.generateAndStoreCode(email, Duration.ofMinutes(5));
        
        // 发送验证码邮件
        boolean emailSent = emailService.sendVerificationCode(email, code);
        if (!emailSent) {
            throw new RuntimeException("验证码邮件发送失败，请稍后重试");
        }
    }
    
    public void resetPassword(PasswordResetRequestVO request) {
        // 验证邮箱验证码
        if (!verifyCodeService.verifyCode(request.getVerifyInfo(), request.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }

        User user = null;
        // 查找用户
        if (request.getMode().equals(0)) {

            user = userRepository.findByEmail(request.getVerifyInfo())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
        }else if (request.getMode().equals(1)) {
            user = userRepository.findByPhone(request.getVerifyInfo())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        cacheUser(user);
        
        // 清除验证码
        verifyCodeService.clearCode(request.getVerifyInfo());
    }

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    //TODO: 待测
    public UserDTO getUserById(Long userId) {
        // 从缓存中获取用户
        UserDTO cachedUser = (UserDTO) redisTemplate.opsForValue().get(USER_CACHE_PREFIX + userId);
        if (cachedUser != null) {
            return cachedUser;
        }
        // 从数据库中获取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 缓存用户信息
        cacheUser(user);
        // 返回用户信息
        return UserDTO.fromEntity(user);
    }
}
