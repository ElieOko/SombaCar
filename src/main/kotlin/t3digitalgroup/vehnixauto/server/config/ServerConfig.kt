package t3digitalgroup.vehnixauto.server.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import t3digitalgroup.vehnixauto.server.exception.CustomAccessDeniedHandler
import t3digitalgroup.vehnixauto.server.exception.CustomAuthEntryPoint
import t3digitalgroup.vehnixauto.server.security.JwtAuthFilter
import t3digitalgroup.vehnixauto.server.utils.Mode


@Profile(Mode.DEV)
@EnableWebSecurity
@Configuration
class ServerConfig(
    private val customAuthEntryPoint: CustomAuthEntryPoint,
    private val  customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val jwtAuthFilter: JwtAuthFilter
) : WebMvcConfigurer {
    private val log = LoggerFactory.getLogger(this::class.java)
    @PostConstruct
    fun init() {
        log.info("✅ ServerConfig is active")
    }
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
      return httpSecurity
            .csrf { csrf -> csrf.disable() }
          .cors { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling { configurer ->
                configurer
                    .authenticationEntryPoint(customAuthEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            }
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
          .httpBasic { it.disable() }
          .formLogin { it.disable() }
            .build()
    }
    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/property/**")
            .addResourceLocations(
                "file:opt/backend/casa/property/",
//                "file:casa/property/bedroom/",
//                "file:casa/property/room/",
//                "file:casa/property/kitchen/",
//                "file:casa/profil/",
//                "file:casa/realisation/",
            )
    }
}
