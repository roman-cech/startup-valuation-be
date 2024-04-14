package cz.utb.fai.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    private val jwtProperties: JwtProperties
) {

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): AccessToken = AccessToken(
        accessToken = Jwts.builder()
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .addClaims(additionalClaims)
            .signWith(secretKey)
            .compact(),
        expirationDate = expirationDate
    )

    fun isValid(token: String, userDetails: UserDetails): Boolean =
        userDetails.username == extractEmail(token) && !isExpired(token)

    fun extractEmail(token: String): String? = getAllClaims(token)?.subject

    fun isExpired(token: String): Boolean =
        getAllClaims(token)?.expiration?.before(Date(System.currentTimeMillis())) ?: true

    private fun getAllClaims(token: String): Claims? =
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
}

data class AccessToken(
    val accessToken: String,
    val expirationDate: Date
)