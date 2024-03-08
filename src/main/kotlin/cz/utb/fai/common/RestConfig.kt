package cz.utb.fai.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebMvc
open class RestConfig : WebMvcConfigurer {

    private val restObjectMapper: ObjectMapper by lazy { jacksonObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
        .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    }

    @Bean
    open fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        val converter = MappingJackson2HttpMessageConverter().apply { this.objectMapper = restObjectMapper }

        return builder
            .additionalMessageConverters(converter)
            .build()
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON).mediaType("json", MediaType.APPLICATION_JSON)
    }
}