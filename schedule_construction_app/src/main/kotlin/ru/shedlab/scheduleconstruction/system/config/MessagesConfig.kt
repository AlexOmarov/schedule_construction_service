package ru.shedlab.scheduleconstruction.system.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class MessagesConfig {

    @Bean
    fun messageSource(props: AppProps): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasenames(props.messages.basename)
        messageSource.setDefaultEncoding(props.messages.encoding)
        return messageSource
    }

    /**
     * Connect existing message source to JSR-303 validator
     *
     * @param messageSource Configured message source
     * @see messageSource(props: AppProps)
     *
     */
    @Bean
    fun validatorFactoryBean(messageSource: MessageSource): LocalValidatorFactoryBean {
        val localValidatorFactoryBean = LocalValidatorFactoryBean()
        localValidatorFactoryBean.setValidationMessageSource(messageSource)
        return localValidatorFactoryBean
    }
}
