package com.example.WebsiteGiaDung.Security;

import com.example.WebsiteGiaDung.service.CustomOAuth2UserService;
import com.example.WebsiteGiaDung.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public UserDetailsService userDetailsService() {
        return userService; // Đảm bảo rằng UserService của bạn được sử dụng
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/css/**", "/js/**", "/", "/oauth/**", "/register", "/error",
//                                "/products", "/cart", "/cart/**")
//                        .permitAll()
//                        .requestMatchers("/products/edit/**", "/products/add", "/products/delete")
//                        .hasAnyAuthority("ADMIN")
//                        .requestMatchers("/api/**").permitAll()
//                        .requestMatchers("/products/edit/**", "/products/add", "/products/delete/users/**", "/users/**")
//                        .hasAuthority("MASTER")
//                        .anyRequest().authenticated()
//                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/logout"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/uploads/**", "/js/**", "/pluto-1.0.0/**", "/", "/oauth/**", "/register",
                                "/uploads", "/error", "/cart", "/cart/**", "/register", "/verify", "/userdetail")
                        .permitAll()
                        .requestMatchers("/products/{id}").permitAll() // Allow product detail pages
                        .requestMatchers("/products/**").hasAuthority("ADMIN") // Protect all other /products routes
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/user/update").authenticated()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/",true) // Trang chuyển hướng sau khi đăng nhập thành công
                        .failureUrl("/login?error=true") // Trang chuyển hướng khi đăng nhập thất bại
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Redirect to the logout page
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/",true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .build();
    }

}
