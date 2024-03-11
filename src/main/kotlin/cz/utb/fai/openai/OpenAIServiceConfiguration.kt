package cz.utb.fai.openai

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate


@Configuration
open class OpenAIServiceConfiguration(
    private val properties: OpenAIProperties,
    private val restTemplate: RestTemplate
) {
    private val objectMapper = jacksonObjectMapper().apply {
        this.registerKotlinModule()
        this.setSerializationInclusion(Include.NON_NULL)
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    //TODO errorHandler

    private fun createTemplate(): RestTemplate =
        restTemplate.apply {
            messageConverters.filterIsInstance<MappingJackson2HttpMessageConverter>().forEach { jsonConverter ->
                jsonConverter.objectMapper = objectMapper
            }
        }

    @Bean
    open fun getExplanationConnector(): GetExplanationConnector =
        createTemplate().run { return GetExplanationImpl(this, properties) }
}