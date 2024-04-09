package cz.utb.fai.jwt

import io.jsonwebtoken.Jwt
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val jwtProperties: JwtProperties
) {

    fun authentication(email: String, password: String): AuthenticationResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(email, password))

        val userDetails = userDetailsService.loadUserByUsername(email)
        val accessToken = createAccessToken(userDetails)
        val refreshToken = createRefreshToken(userDetails)

        val user = requireNotNull(userRepository.findByEmail(email))

        refreshTokenRepository.save(refreshToken, userDetails)
        accessTokenRepository.save(accessToken, userDetails)

        return AuthenticationResponse(
            token = AuthenticationResponse.Token(accessToken = accessToken, refreshToken = refreshToken),
            user = AuthenticationResponse.UserSession(
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email
            )
        )
    }

    fun logOut(token: String) = tokenService.extractEmail(token)?.let { email ->
        val userDetails = userDetailsService.loadUserByUsername(email)
        accessTokenRepository.deleteByUserDetails(userDetails)
        refreshTokenRepository.deleteByUserDetails(userDetails)
    }

    fun refreshAccessToken(refreshToken: String): String? {
        val extractedEmail = tokenService.extractEmail(refreshToken)

        return extractedEmail?.let { email ->
            val currentUserDetails = userDetailsService.loadUserByUsername(email)
            val refreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(refreshToken)

            if (!tokenService.isExpired(refreshToken) && refreshTokenUserDetails?.username == currentUserDetails.username)
                createAccessToken(currentUserDetails)
            else null
        }
    }

    private fun createAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration.toLong())
    )

    private fun createRefreshToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration.toLong())
    )
}