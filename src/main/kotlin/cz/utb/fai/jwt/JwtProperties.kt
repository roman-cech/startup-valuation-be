package cz.utb.fai.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    lateinit var key: String
    lateinit var adminPassword: String
    lateinit var accessTokenExpiration: String
    lateinit var refreshTokenExpiration: String
}