package cz.chobot.image_builder.controller

import cz.chobot.image_builder.repository.ModuleRepository
import cz.chobot.image_builder.repository.UserRepository
import cz.chobot.image_builder.service.IImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/network")
class ImageController {

    @Autowired
    private lateinit var imageService: IImageService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var moduleRepository: ModuleRepository

    @PostMapping("/user/{idUser}/module/{idModule}")
    fun createHandlerImage(@PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<String> {
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)
        if (user.isPresent && module.isPresent) {
            val imageId = imageService.createComposeImage(module.get(), user.get())
            if (imageId.isPresent) {
                return ResponseEntity.ok(imageId.get())
            }

            return ResponseEntity.status(500).build()
        }

        return ResponseEntity.notFound().build()
    }

    @PutMapping("/user/{idUser}/module/{idModule}")
    fun updateHandlerImage(@PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<String> {
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)

        if (user.isPresent && module.isPresent) {
            val imageId = imageService.updateComposeImage(module.get(), user.get())
            if (imageId.isPresent) {
                return ResponseEntity.ok(imageId.get())
            }

            return ResponseEntity.status(500).build()
        }

        return ResponseEntity.notFound().build()
    }


    @PutMapping("/restore/user/{idUser}/module/{idModule}")
    fun restoreHandlerImage(@PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<String> {
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)

        if (user.isPresent && module.isPresent) {
            val imageId = imageService.restoreComposeImage(module.get(), user.get())
            if (imageId.isPresent) {
                return ResponseEntity.ok(imageId.get())
            }

            return ResponseEntity.status(500).build()
        }

        return ResponseEntity.notFound().build()
    }

    @DeleteMapping("/user/{idUser}/module/{idModule}")
    fun deleteHandlerImage(@PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<Void> {
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)

        if (user.isPresent && module.isPresent) {
            if(imageService.deleteComposeImage(module.get(), user.get())){
                return ResponseEntity.ok().build()
            }

            return ResponseEntity.status(500).build()
        }

        return ResponseEntity.notFound().build()
    }
}