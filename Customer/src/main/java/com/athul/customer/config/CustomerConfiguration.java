package com.athul.customer.config;


import com.athul.library.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class CustomerConfiguration {


    private final CustomerRepository customerRepository;

    private final CustomSuccessHandler customSuccessHandler;



    private final CustomLogoutHandler customLogoutHandler;

    private final AuthFilter authFilter;


    public CustomerConfiguration(CustomerRepository customerRepository, CustomSuccessHandler customSuccessHandler, CustomLogoutHandler customLogoutHandler, AuthFilter authFilter) {
        this.customerRepository = customerRepository;
        this.customSuccessHandler = customSuccessHandler;
        this.customLogoutHandler = customLogoutHandler;
        this.authFilter = authFilter;
    }

    @Bean
    public UserDetailsService userDetailsService(){

        return new CustomerServiceConfig(customerRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.addFilterBefore(authFilter, BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize->authorize
                        .requestMatchers("/css/**","/imgs/**","/js/**","/less/**","/pages/**","/scss/**","/vendor/**","/sass/**").permitAll()
                        .requestMatchers("/user/**").hasAuthority("CUSTOMER")
                        .requestMatchers("/**","/index/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form->form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .permitAll().successHandler(customSuccessHandler))
                .logout(logout-> logout
                        .addLogoutHandler(customLogoutHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                                .invalidSessionUrl("/login")
                                .maximumSessions(1)
                        .maxSessionsPreventsLogin(false));

        return http.build();
    }
}
