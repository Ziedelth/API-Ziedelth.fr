package fr.ziedelth.commands

import fr.ziedelth.controllers.PlatformController
import fr.ziedelth.utils.ICommand

class ShowPlatformsCommand : ICommand("showplatforms") {
    override fun run(args: List<String>) {
        println("List of platforms:")

        PlatformController.getPlatforms()?.forEach {
            println("\t- ${it.id}: ${it.name}")
        }
    }
}