package t3digitalgroup.vehnixauto.server.app.car.application.commands

import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.entities.CarModelEntity
import t3digitalgroup.vehnixauto.server.app.car.infrastructure.repositories.CarModelRepository
import t3digitalgroup.vehnixauto.server.utils.Mode

@Component
@Profile(Mode.DEV)
class SeedCarModelCommand(
    private val repository: CarModelRepository,
) : CommandLineRunner {
    private val defaults = listOf(
        CarModelEntity(brand = "Toyota", model = "Corolla", generation = "E210", bodyType = "Berline"),
        CarModelEntity(brand = "Toyota", model = "Hilux", generation = "AN120", bodyType = "Pick-up"),
        CarModelEntity(brand = "Toyota", model = "RAV4", generation = "XA50", bodyType = "SUV"),
        CarModelEntity(brand = "Toyota", model = "Land Cruiser", generation = "J300", bodyType = "SUV"),
        CarModelEntity(brand = "Nissan", model = "Patrol", generation = "Y62", bodyType = "SUV"),
        CarModelEntity(brand = "Nissan", model = "X-Trail", generation = "T33", bodyType = "SUV"),
        CarModelEntity(brand = "Mercedes-Benz", model = "Classe C", generation = "W206", bodyType = "Berline"),
        CarModelEntity(brand = "Mercedes-Benz", model = "Classe E", generation = "W214", bodyType = "Berline"),
        CarModelEntity(brand = "BMW", model = "Série 3", generation = "G20", bodyType = "Berline"),
        CarModelEntity(brand = "BMW", model = "X5", generation = "G05", bodyType = "SUV"),
        CarModelEntity(brand = "Hyundai", model = "Tucson", generation = "NX4", bodyType = "SUV"),
        CarModelEntity(brand = "Kia", model = "Sportage", generation = "NQ5", bodyType = "SUV"),
        CarModelEntity(brand = "Peugeot", model = "301", generation = "M33", bodyType = "Berline"),
        CarModelEntity(brand = "Peugeot", model = "508", generation = "R83", bodyType = "Berline"),
        CarModelEntity(brand = "Renault", model = "Duster", generation = "HJD", bodyType = "SUV"),
        CarModelEntity(brand = "Renault", model = "Logan", generation = "L90", bodyType = "Berline"),
    )

    override fun run(vararg args: String) {
        runBlocking {
//            defaults.forEach { model ->
//                if (repository.findByBrandAndModel(model.brand, model.model) == null) {
//                    repository.save(model)
//                }
//            }
        }
    }
}
