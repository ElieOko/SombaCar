package initializer.backend.spring.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.ApiVersionConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebConfiguration : WebFluxConfigurer {

    override fun configureApiVersioning(configurer: ApiVersionConfigurer) {
        configurer
            .addSupportedVersions("1.0","2.0")
//            .useRequestHeader("API-Version")
            .useMediaTypeParameter(MediaType.APPLICATION_JSON, "version")
            .setDefaultVersion("1.0")
            .setVersionParser(ApiVersionParser())

    }

}


