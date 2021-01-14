package me.yh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
            csrf().disable()
            .authorizeRequests()
                .mvcMatchers("/").permitAll() //여기는 아무나 접근
                .anyRequest().authenticated() //인증된 사용자만 접근
            .and()
                .formLogin() //폼 로그인 사용
            .and()
                .httpBasic() // httpBasic 사용
            .and()
                .sessionManagement()
                .maximumSessions(1);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("yh").password("{noop}1234").roles("USER").and()
                .withUser("woo").password("{noop}1234").roles("USER").and()
                .withUser("aa").password("{noop}1234").roles("USER").and()
                .withUser("bb").password("{noop}1234").roles("USER").and()
                .withUser("cc").password("{noop}1234").roles("USER");
    }
}
