package cz.utb.fai.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "redis")
class RedisProperties {
    lateinit var host: String
    lateinit var port: String
    lateinit var password: String
    lateinit var topic: String
}