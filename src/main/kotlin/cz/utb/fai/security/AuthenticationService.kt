package cz.utb.fai.security

import cz.utb.fai.dto.controller.AuthenticationResponse
import cz.utb.fai.dto.controller.Token
import cz.utb.fai.repository.AccessTokenRepository
import cz.utb.fai.repository.RefreshTokenRepository
import cz.utb.fai.repository.UserRepository
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

        accessTokenRepository.save(accessToken.accessToken, userDetails)
        refreshTokenRepository.save(refreshToken, userDetails)

        return AuthenticationResponse(
            token = Token(
                accessToken = accessToken.accessToken,
                refreshToken = refreshToken,
                expirationDate = accessToken.expirationDate
            ),
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

    fun refreshAccessToken(token: String): Token? {
        val extractedEmail = tokenService.extractEmail(token)

        return extractedEmail?.let { email ->
            val currentUserDetails = userDetailsService.loadUserByUsername(email)
            val refreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(token)

            if (!tokenService.isExpired(token) && refreshTokenUserDetails?.username == currentUserDetails.username) {
                val accessToken = createAccessToken(currentUserDetails)
                val refreshToken = createRefreshToken(currentUserDetails)

                accessTokenRepository.updateToken(accessToken.accessToken, currentUserDetails)
                refreshTokenRepository.updateToken(refreshToken, currentUserDetails)

                Token(
                    accessToken = accessToken.accessToken,
                    refreshToken = refreshToken,
                    expirationDate = accessToken.expirationDate
                )
            }
            else null
        }
    }

    private fun createAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration.toLong())
    )

    private fun createRefreshToken(user: UserDetails): String = tokenService.generate(
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration.toLong())
    ).accessToken
}