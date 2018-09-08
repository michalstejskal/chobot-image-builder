package cz.chobot.image_builder.service

import cz.chobot.image_builder.bo.Module

interface IDockerService {
    fun buildImage(workdir: String, username: String, module: Module): String
    fun deleteImage(module: Module)
}