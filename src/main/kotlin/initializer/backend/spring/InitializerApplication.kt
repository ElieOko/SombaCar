package initializer.backend.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InitializerApplication

fun main(args: Array<String>) {
	runApplication<InitializerApplication>(*args)
}
