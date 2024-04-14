package cz.utb.fai.repository

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class AccessTokenRepository {

    private val tokens: MutableMap<String, UserDetails> = ConcurrentHashMap()

    fun save(token: String, userDetails: UserDetails) { tokens[token] = userDetails }

    fun updateToken(token: String, userDetails: UserDetails) { tokens.putIfAbsent(token, userDetails) }

    fun existByToken(token: String): Boolean = tokens.containsKey(token)

    fun deleteByUserDetails(userDetails: UserDetails) {
        tokens.entries.find { it.value == userDetails }?.key.let { tokens.remove(it) }
    }
}