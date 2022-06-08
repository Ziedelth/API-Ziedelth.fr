package fr.ziedelth.commands

import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.utils.ICommand

class AnimeCleanCommand : ICommand("animeclean") {
    override fun run(args: List<String>) {
        println("Cleaning animes...")
        AnimeController.clean()
        println("Cleaned!")
    }
}