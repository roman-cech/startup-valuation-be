package cz.utb.fai.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory


@Configuration
open class RedisConfig(
    private val properties: RedisProperties,
) {

    @Bean
    @Primary
    open fun redisConnection(): ReactiveRedisConnectionFactory =
        LettuceConnectionFactory(RedisStandaloneConfiguration(properties.host, properties.port.toInt()))

}