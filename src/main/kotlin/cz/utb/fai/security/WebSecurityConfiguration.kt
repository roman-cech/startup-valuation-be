package cz.utb.fai.security

import cz.utb.fai.repository.type.Role
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
open class WebSecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .cors().and()
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .antMatchers(*URL_WHITE_LIST).permitAll()
                    .antMatchers(HttpMethod.POST, "/rest/v1/startups/evaluate").hasRole(Role.ADMIN.name)
                    .antMatchers(HttpMethod.GET, "/rest/v1/startups/evaluation/{jobId}").hasRole(Role.ADMIN.name)
                    .antMatchers(HttpMethod.POST, "/rest/v1/auth/logout").hasRole(Role.ADMIN.name)
                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    companion object {
        private val URL_WHITE_LIST = arrayOf(
            "/rest/v1/auth/login",
            "/rest/v1/auth/refresh",
            "/error"
        )
    }
}
