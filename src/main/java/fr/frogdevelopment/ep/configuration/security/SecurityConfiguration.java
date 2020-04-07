package fr.frogdevelopment.ep.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form</li>
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()

                // Register our CustomRequestCache, that saves unauthorized access attempts, so
                // the user is redirected after login.
//                .requestCache().requestCache(new CustomRequestCache())

                // Restrict access to our application.
               /* .and()*/.authorizeRequests()

                // Allow all flow internal requests.
//                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

                // Allow all requests by logged in users.
//                .anyRequest().authenticated()
                .anyRequest().permitAll();

                // Configure the login page.
//                .and().formLogin().loginPage("/" + LoginView.ROUTE).permitAll().permitAll()
//                .failureUrl(LOGIN_FAILURE_URL)
//
//                // Configure logout
//                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        // typical logged in user with some privileges
        var normalUser =
                User.withUsername("user")
                        .password("{noop}password")
                        .roles("User")
                        .build();

        // admin user with all privileges
        var adminUser =
                User.withUsername("admin")
                        .password("{noop}password")
                        .roles("User", "Admin")
                        .build();

        return new InMemoryUserDetailsManager(normalUser, adminUser);
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt"
        );
    }
}
