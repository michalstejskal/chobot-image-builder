package cz.chobot.image_builder.service

import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.bo.User
import java.util.*

interface IImageService {
    fun createComposeImage(module: Module, user: User)
    fun updateComposeImage(module: Module, user: User): Optional<String>
    fun deleteComposeImage(module: Module, user: User): Boolean
    fun restoreComposeImage(module: Module, user: User): Optional<String>
}