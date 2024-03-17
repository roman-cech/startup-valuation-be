package cz.utb.fai.jwt

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByEmail(username)?.let { user ->
            User.builder()
                .username(user.email)
                .password(user.password)
                .roles(user.role.name)
                .build()
        } ?: throw UsernameNotFoundException("Not found!")
}