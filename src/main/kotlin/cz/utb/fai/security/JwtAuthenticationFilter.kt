package cz.utb.fai.security

import cz.utb.fai.repository.AccessTokenRepository
import cz.utb.fai.repository.RefreshTokenRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private const val BEARER = "Bearer "
private const val NOT_AUTHORIZED_MESSAGE = "Not Authorized!"


@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val accessTokenRepository: AccessTokenRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()

        val email = tokenService.extractEmail(jwtToken)
        val isValidAccess = accessTokenRepository.existByToken(jwtToken)

        if (email != null && isValidAccess && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = userDetailsService.loadUserByUsername(email)

            if (tokenService.isValid(jwtToken, foundUser)) {
                updateContext(foundUser, request)
                filterChain.doFilter(request, response)
                return
            }
        }

        throw AccessDeniedException(NOT_AUTHORIZED_MESSAGE)
    }

    private fun String?.doesNotContainBearerToken() = isNullOrBlank() || !startsWith(BEARER)

    private fun String.extractTokenValue() = this.substringAfter(BEARER)

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}