package fr.ziedelth.commands

import fr.ziedelth.controllers.PlatformController
import fr.ziedelth.utils.ICommand

class DeletePlatformCommand : ICommand("deleteplatform") {
    override fun run(args: List<String>) {
        if (args.size != 1) {
            println("Usage: deleteplatform <platform_id>")
            return
        }

        val platformId = args[0].toLongOrNull()

        if (platformId == null) {
            println("Invalid platform id")
            return
        }

        println("Deleting platform $platformId...")
        PlatformController.deletePlatform(platformId)
        println("Platform $platformId deleted")
    }
}