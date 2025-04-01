package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.OrderStatus;
import com.example.WebsiteGiaDung.Role;
import com.example.WebsiteGiaDung.model.Order;
import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.repository.IRoleRepository;
import com.example.WebsiteGiaDung.repository.OrderRepository;
import com.example.WebsiteGiaDung.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.bytebuddy.utility.RandomString;

import java.util.List;
import java.util.Optional;
@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderRepository orderRepository;
    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
  /*  public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }*/
    public void save(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String verificationCode = RandomString.make(64);
        user.setVerificationCode(verificationCode);
        user.setEnabled(false); // Tài khoản chưa được kích hoạt
        userRepository.save(user);
    }

    // Gán vai trò mặc định cho người dùng dựa trên tên người dùng.
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
                    userRepository.save(user);
                },
                () -> { throw new UsernameNotFoundException("User not found"); }
        );
    }
    // Tải thông tin chi tiết người dùng để xác thực.
  /*  @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("Account not activated yet. Please verify your email.");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getEnabled()) {
            throw new UsernameNotFoundException("User account is disabled");
        }

        return user; // Return the user as it implements UserDetails
    }
    // Tìm kiếm người dùng dựa trên tên đăng nhập.
    public Optional<User> findByUsername(String username) throws
            UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {

        return userRepository.findById(id);
    }
    public List<User> findAll() {

        return userRepository.findAll();
    }
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
    @Autowired
    @Lazy
    private EmailService emailService;

    public String getUsernameByVerificationCode(String code) {
        User user = userRepository.findByVerificationCode(code);
        return (user != null) ? user.getUsername() : null;
    }

    public void register(User user, String siteURL) throws MessagingException {
        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            String verificationCode = RandomString.make(64);
            user.setVerificationCode(verificationCode);
            user.setEnabled(false);

            userRepository.save(user);
            emailService.sendVerificationEmail(user, siteURL);
        } catch (Exception e) {
            log.error("Error registering user: ", e);
            throw e;
        }
    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if (user == null || user.getEnabled()) {
            return false;
        }
        user.setVerificationCode(null);
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    public void updateUser(Long id, User user) {
        // Fetch the existing user by ID
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update the user details
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        userRepository.save(existingUser);

        // Update related orders only if OrderStatus is not DELIVERED
        List<Order> orders = orderRepository.findByUserId(id);
        for (Order order : orders) {
            if (!OrderStatus.DELIVERED.equals(order.getOrderStatus())) {
                order.setPhone(user.getPhone()); // Sync phone number if needed
                orderRepository.save(order);
            }
        }
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
