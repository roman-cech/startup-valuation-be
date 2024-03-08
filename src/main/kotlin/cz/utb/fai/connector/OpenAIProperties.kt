package cz.utb.fai.connector

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "open-ai")
class OpenAIProperties {
    lateinit var model: String
    lateinit var scheme: String
    lateinit var key: String
}