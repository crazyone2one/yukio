package cn.master.yukio.security;

import cn.master.yukio.handler.LogoutHandler;
import cn.master.yukio.handler.LogoutSuccessHandlerImpl;
import cn.master.yukio.handler.NoLoginHandler;
import cn.master.yukio.handler.NoPermissionHandler;
import cn.master.yukio.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LogoutSuccessHandlerImpl logoutSuccessHandler;
    private final LogoutHandler logoutHandler;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final NoLoginHandler noLoginHandler;
    private final NoPermissionHandler noPermissionHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorizeHttpRequests -> {
            authorizeHttpRequests
                    .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                    .anyRequest().authenticated();
        });
        // 禁用表单登录
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);
        // 设置Session生成策略为无状态，即不使用Session。使用Token需要配置这个
        httpSecurity.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 禁用CSRF
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.authenticationProvider(authenticationProvider());
        // 权限问题
        httpSecurity.exceptionHandling(e -> e.accessDeniedHandler(noPermissionHandler).authenticationEntryPoint(noLoginHandler));
        // 自定义退出登录操作和成功退出登录操作
        httpSecurity.logout(l -> l
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(logoutSuccessHandler));
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false); //设置为false时，loadUserByUsername中抛出的UsernameNotFoundException才能被全局捕获并处理
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
