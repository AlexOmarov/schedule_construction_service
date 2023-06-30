package ru.shedlab.scheduleconstruction.infrastructure.config

import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.jsr107.Eh107Configuration
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import java.time.Duration
import javax.cache.CacheManager

@Configuration
@EnableCaching
class CacheConfig(private val props: Props) : JCacheManagerCustomizer {

    override fun customize(cacheManager: CacheManager) {
        cacheManager.createCache("stubs", getConfig(props.cache.defaultTime, props.cache.defaultHeapSize))
    }

    private fun getConfig(duration: Duration, heap: Long): javax.cache.configuration.Configuration<Any, Any> {
        return Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Any::class.java,
                Any::class.java,
                ResourcePoolsBuilder.heap(heap)
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(duration))
                .build()
        )
    }
}
