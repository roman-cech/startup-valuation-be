package cz.utb.fai.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

private const val REST_MAPPING = "/rest/**"

@Configuration
@EnableWebMvc
open class RestConfig : WebMvcConfigurer {

    @Value("\${frontend.host}") private lateinit var host: String
    @Value("\${frontend.port}") private lateinit var port: String

    private val restObjectMapper: ObjectMapper by lazy {
        jacksonObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
            .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    }

    @Bean
    open fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder
        .additionalMessageConverters(MappingJackson2HttpMessageConverter().apply { this.objectMapper = restObjectMapper })
        .build()

    @Bean
    open fun restExceptionHandler(): CustomRestExceptionHandler = CustomRestExceptionHandler()

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON).mediaType("json", MediaType.APPLICATION_JSON)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping(REST_MAPPING)
            .allowedOrigins("$host:$port")
            .allowedMethods("GET", "POST", "PUT")
            .allowedHeaders("*")
    }
}