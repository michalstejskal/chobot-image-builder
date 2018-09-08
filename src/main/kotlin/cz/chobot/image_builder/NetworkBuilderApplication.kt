package cz.chobot.image_builder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class NetworkBuilderApplication

fun main(args: Array<String>) {
    runApplication<NetworkBuilderApplication>(*args)
}
