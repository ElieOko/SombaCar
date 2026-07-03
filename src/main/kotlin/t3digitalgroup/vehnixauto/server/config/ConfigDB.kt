package t3digitalgroup.vehnixauto.server.config

import org.flywaydb.core.Flyway
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.flyway.autoconfigure.FlywayProperties
import org.springframework.boot.r2dbc.autoconfigure.R2dbcProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.util.StringUtils

@Configuration
@EnableConfigurationProperties(
    FlywayProperties::class,
    R2dbcProperties::class,
)
class DatabaseConfig {

//    @Bean(initMethod = "migrate")
//    @Profile("dev")
//    fun flyway(
//        flywayProperties: FlywayProperties,
//        r2dbcProperties: R2dbcProperties,
//    ): Flyway {
//        val jdbcUrl = resolveJdbcUrl(flywayProperties, r2dbcProperties)
//        return Flyway.configure()
//            .dataSource(
//                jdbcUrl,
//                flywayProperties.user ?: r2dbcProperties.username,
//                flywayProperties.password ?: r2dbcProperties.password,
//            )
//            .locations(*flywayProperties.locations.toTypedArray())
//            .baselineOnMigrate(flywayProperties.isBaselineOnMigrate)
//            .load()
//    }
//
//    private fun resolveJdbcUrl(
//        flywayProperties: FlywayProperties,
//        r2dbcProperties: R2dbcProperties,
//    ): String {
//        flywayProperties.url?.takeIf { StringUtils.hasText(it) }?.let { return it }
//        val r2dbcUrl = r2dbcProperties.url
//            ?: throw IllegalStateException("Configure spring.flyway.url or spring.r2dbc.url for migrations.")
//        return r2dbcUrl.replace("r2dbc:", "jdbc:")
//    }
}
