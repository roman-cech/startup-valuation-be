package cz.utb.fai.jwt

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
    private val jwtProperties: JwtProperties
) {

    fun authentication(email: String, password: String): AuthenticationResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(email, password))

        val user = userDetailsService.loadUserByUsername(email)
        val accessToken = createAccessToken(user)
        val refreshToken = createRefreshToken(user)

        refreshTokenRepository.save(refreshToken, user)
        accessTokenRepository.save(accessToken, user)

        return AuthenticationResponse(accessToken = accessToken, refreshToken = refreshToken)
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