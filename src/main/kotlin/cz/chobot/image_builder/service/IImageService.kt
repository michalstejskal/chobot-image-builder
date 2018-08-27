package cz.chobot.image_builder.service

import cz.chobot.image_builder.bo.Module

interface IImageService {
    fun createHandlerImage(module: Module): Module
    fun updateHandlerImage(module: Module): Module
}