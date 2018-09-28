package cz.chobot.image_builder.controller

import cz.chobot.image_builder.repository.ModuleRepository
import cz.chobot.image_builder.repository.NetworkRepository
import cz.chobot.image_builder.repository.UserRepository
import cz.chobot.image_builder.service.IImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.concurrent.thread


@RestController
@RequestMapping("/api/v1/")
class ImageController {

    @Autowired
    private lateinit var imageService: IImageService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var moduleRepository: ModuleRepository

    @Autowired
    private lateinit var networkRepository: NetworkRepository

    @PostMapping("network/{idNetwork}/user/{idUser}/module/{idModule}")
    fun createHandlerImage(@PathVariable("idNetwork") idNetwork: Long, @PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<String> {
        val network = networkRepository.findById(idNetwork)
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)

        if (network.isPresent && user.isPresent && module.isPresent) {
            imageService.createComposeImage(module.get(), user.get())
            return ResponseEntity.accepted().build()
        }
        return ResponseEntity.notFound().build()
    }

    @PutMapping("network/{idNetwork}/user/{idUser}/module/{idModule}")
    fun updateHandlerImage(@PathVariable("idNetwork") idNetwork: Long, @PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<String> {
        val network = networkRepository.findById(idNetwork)
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)

        if (network.isPresent && user.isPresent && module.isPresent) {
            imageService.updateComposeImage(module.get(), user.get())
            return ResponseEntity.accepted().build()

        }
        return ResponseEntity.notFound().build()
    }


    @PutMapping("network/{idNetwork}/user/{idUser}/module/{idModule}/restore")
    fun restoreHandlerImage(@PathVariable("idNetwork") idNetwork: Long, @PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<String> {
        val network = networkRepository.findById(idNetwork)
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)

        if (network.isPresent && user.isPresent && module.isPresent) {
            imageService.restoreComposeImage(module.get(), user.get())
            return ResponseEntity.accepted().build()
        }

        return ResponseEntity.notFound().build()
    }

    @DeleteMapping("network/{idNetwork}/user/{idUser}/module/{idModule}")
    fun deleteHandlerImage(@PathVariable("idNetwork") idNetwork: Long, @PathVariable("idUser") idUser: Long, @PathVariable("idModule") idModule: Long): ResponseEntity<Void> {
        val network = networkRepository.findById(idNetwork)
        val user = userRepository.findById(idUser)
        val module = moduleRepository.findById(idModule)

        if (network.isPresent && user.isPresent && module.isPresent) {
            imageService.deleteComposeImage(module.get(), user.get())
            return ResponseEntity.accepted().build()
        }

        return ResponseEntity.notFound().build()
    }
}