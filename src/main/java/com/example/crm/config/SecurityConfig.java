package com.example.crm.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.entity.EmployeeUser;
import com.example.crm.mapper.EmployeeUserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/blog/**", "/api/blog/posts/**", "/login").permitAll()
                        .requestMatchers("/me/**", "/customers/**", "/mail/**", "/api/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/me/blog", true)
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
            return User.withUsername(user.getUsername())
                    .password(user.getPasswordHash())
                    .roles("EMPLOYEE")
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
