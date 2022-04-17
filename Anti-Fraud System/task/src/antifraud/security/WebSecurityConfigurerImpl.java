package antifraud.security;

import antifraud.database.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailedService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailedService)
                .passwordEncoder(getEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() // manage access
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/**")
                    .hasRole(Role.ADMINISTRATOR.toString())

                .mvcMatchers(HttpMethod.GET, "/api/auth/list")
                    .hasAnyRole(Role.ADMINISTRATOR.toString(), Role.SUPPORT.toString())

                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction")
                    .hasRole(Role.MERCHANT.toString())

                .mvcMatchers(HttpMethod.PUT, "/api/auth/access")
                    .hasRole(Role.ADMINISTRATOR.toString())

                .mvcMatchers(HttpMethod.PUT, "/api/auth/role")
                    .hasRole(Role.ADMINISTRATOR.toString())

                .mvcMatchers("api/antifraud/suspicious-ip/**")
                    .hasRole(Role.SUPPORT.toString())

                .mvcMatchers("api/antifraud/stolencard/**")
                    .hasRole(Role.SUPPORT.toString())

                .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .mvcMatchers( "/actuator/shutdown").permitAll()
                .mvcMatchers("/h2").permitAll()
                //.anyRequest().authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handles auth error
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                .and()
                .csrf().disable().headers().frameOptions().disable(); // for Postman, the H2 console
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
