package cz.chobot.image_builder.controller

import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.service.IImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@RestController
@RequestMapping("/api/v1/network")
class ImageController {

    @Autowired
    private lateinit var imageService: IImageService

    @PostMapping
    fun createHandlerImage(@RequestBody module: Module): ResponseEntity<Void> {
        val newApplication = imageService.createHandlerImage(module)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newApplication.id)
                .toUri()
        return ResponseEntity.created(location).build()
    }
}