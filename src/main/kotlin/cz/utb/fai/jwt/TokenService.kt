package cz.utb.fai.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenService(
    private val jwtProperties: JwtProperties
) {
    // Use a ConcurrentHashMap to store blacklisted tokens for thread safety
    private val blacklist: MutableSet<String> = ConcurrentHashMap.newKeySet()

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String =
        Jwts.builder()
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .addClaims(additionalClaims)
            .signWith(secretKey)
            .compact()

    fun isValid(token: String, userDetails: UserDetails): Boolean =
        userDetails.username == extractEmail(token) && !isExpired(token)

    fun extractEmail(token: String): String? = getAllClaims(token)?.subject

    fun addToBlacklist(token: String) = blacklist.add(token)

    fun isBlacklisted(token: String): Boolean = blacklist.contains(token)

    private fun isExpired(token: String): Boolean =
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