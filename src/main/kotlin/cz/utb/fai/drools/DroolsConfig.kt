package cz.utb.fai.drools

import org.drools.core.io.impl.ClassPathResource
import org.kie.api.KieServices
import org.kie.api.runtime.KieContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

private const val RULES_VALUATION_DRL = "rules/startup-valuation.drl"

@Configuration
@ComponentScan
open class DroolsConfig {

    private val kieServices: KieServices by lazy { KieServices.Factory.get() }

    @Bean
    open fun kieContainer(): KieContainer = kieServices.run {
        val kieFileSystem = newKieFileSystem().apply {
            this.write(ClassPathResource(RULES_VALUATION_DRL))
        }

        val kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll()

        newKieContainer(kieBuilder.kieModule.releaseId)
    }
}