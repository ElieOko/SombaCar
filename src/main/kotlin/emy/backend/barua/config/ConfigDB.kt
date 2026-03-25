package emy.backend.barua.config

import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.flyway.autoconfigure.FlywayProperties
import org.springframework.boot.r2dbc.autoconfigure.R2dbcProperties

@Configuration
@EnableConfigurationProperties(
    FlywayProperties::class,
    R2dbcProperties::class
)
class DatabaseConfig {

//    @Bean(initMethod = "migrate")
//    fun flyway(
//        flywayProperties: FlywayProperties,
//        r2dbcProperties: R2dbcProperties
//    ): Flyway {
//
//        return Flyway.configure()
//            .dataSource(
//                "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:5432/postgres",          // JDBC URL
//                r2dbcProperties.username,
//                r2dbcProperties.password
//            )
//            .locations(*flywayProperties.locations.toTypedArray())
//            .baselineOnMigrate(true)
//            .load()
//    }
}
