package cz.utb.fai.messaging

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
open class RedisConfig(
    private val properties: RedisProperties,
) {

    @Bean
    @Primary
    open fun redisConnection(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(properties.host, properties.port.toInt())
        config.password = RedisPassword.of(properties.password)
        return LettuceConnectionFactory(config)
    }

    @Bean
    @Primary
    open fun reactiveRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, String> = ReactiveRedisTemplate(
        reactiveRedisConnectionFactory, RedisSerializationContext.newSerializationContext<String, String>()
            .key(StringRedisSerializer())
            .value(StringRedisSerializer())
            .hashKey(StringRedisSerializer())
            .hashValue(StringRedisSerializer())
            .build()
    )
}