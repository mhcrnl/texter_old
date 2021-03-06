package posidon.texter

import posidon.texter.backend.InstanceManager
import posidon.texter.backend.Settings
import java.io.File

fun main(args: Array<String>) {
    InstanceManager.start(args)
    Settings.init()
    Window.init()
    for (string in args) {
        val file = File(string)
        if (file.isDirectory) Window.folder = string
        else Window.openFile(string)
    }
}

object AppInfo {
    const val NAME = "texter"
    const val INIT_WIDTH = 820
    const val INIT_HEIGHT = 620
    const val MIN_WIDTH = 620
    const val MIN_HEIGHT = 480
}