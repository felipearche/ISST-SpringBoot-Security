package es.upm.dit.isst.lab5.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> {csrf.disable();})
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/").permitAll()
            .requestMatchers("/h2/**").permitAll()
            .requestMatchers("/alumnos").hasRole("ALUM")
            .requestMatchers("/profesores").hasRole("PROF")
            .requestMatchers("/todos").authenticated()
        )
        .formLogin(Customizer.withDefaults())
        .logout(Customizer.withDefaults());      

        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

        return http.build();
    }

    //Configuración de usuarios en base de datos
     @Bean
     public UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
       String usersByUsernameQuery = "select username, password, enabled from users where username = ?";
       String authsByUserQuery = "select username, authority from authorities where username = ?";          
       JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);    
       users.setUsersByUsernameQuery(usersByUsernameQuery);
       users.setAuthoritiesByUsernameQuery(authsByUserQuery);
       return users;
     }

    /* 
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails alum = User.builder()
            .username("alum")
            .password(passwordEncoder().encode("alum")) // Plain text password
            .roles("ALUM")
            .build();

        UserDetails profe = User.builder()
            .username("profe")
            .password(passwordEncoder().encode("profe")) // Plain text password
            .roles("PROF")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin")) // Plain text password
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(admin, alum, profe);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    */
}
