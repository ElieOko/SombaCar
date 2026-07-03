package t3digitalgroup.vehnixauto.server.bootstrap.command

interface SeedCommand {
    val order: Int get() = 0
    suspend fun execute()
}
