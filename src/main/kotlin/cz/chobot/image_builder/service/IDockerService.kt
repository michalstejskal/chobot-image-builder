package cz.chobot.image_builder.service

interface IDockerService {
    fun buildImage(workdir: String, username: String, applicationName: String, tag: String): String
}