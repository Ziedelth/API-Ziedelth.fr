package fr.ziedelth.utils

abstract class ICommand(val command: String) {
    abstract fun run(args: List<String>)
}