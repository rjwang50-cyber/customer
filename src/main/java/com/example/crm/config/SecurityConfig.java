package com.example.crm.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.entity.EmployeeUser;
import com.example.crm.mapper.EmployeeUserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/blog/**", "/api/blog/posts/**", "/login").permitAll()
                        .requestMatchers("/customers/**", "/api/customers/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers("/me/**", "/api/me/blog/**").hasAnyRole("ADMIN", "MARKETING")
                        .requestMatchers("/mail/**", "/api/mail/**").hasAnyRole("ADMIN", "OPS")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(EmployeeUserMapper employeeUserMapper) {
        return username -> {
            EmployeeUser user = employeeUserMapper.selectOne(new LambdaQueryWrapper<EmployeeUser>()
                    .eq(EmployeeUser::getUsername, username)
                    .eq(EmployeeUser::getStatus, 1)
                    .last("limit 1"));
            if (user == null) {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found");
            }
            Collection<GrantedAuthority> authorities = resolveAuthorities(user.getRoleCodes());
            return User.withUsername(user.getUsername())
                    .password(user.getPasswordHash())
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = hasRole(authentication.getAuthorities(), "ROLE_ADMIN");
            boolean isMarketing = hasRole(authentication.getAuthorities(), "ROLE_MARKETING");
            boolean isSales = hasRole(authentication.getAuthorities(), "ROLE_SALES");
            boolean isOps = hasRole(authentication.getAuthorities(), "ROLE_OPS");
            if (isAdmin || isMarketing) {
                response.sendRedirect("/me/blog");
                return;
            }
            if (isSales) {
                response.sendRedirect("/customers");
                return;
            }
            if (isOps) {
                response.sendRedirect("/mail/logs");
                return;
            }
            response.sendRedirect("/blog");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private Collection<GrantedAuthority> resolveAuthorities(String roleCodes) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.hasText(roleCodes)) {
            for (String role : roleCodes.split(",")) {
                String code = role == null ? "" : role.trim().toUpperCase();
                if (!StringUtils.hasText(code)) {
                    continue;
                }
                authorities.add(new SimpleGrantedAuthority("ROLE_" + code));
            }
        }
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        }
        return authorities;
    }

    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String roleName) {
        return authorities.stream().anyMatch(a -> roleName.equals(a.getAuthority()));
    }
}
