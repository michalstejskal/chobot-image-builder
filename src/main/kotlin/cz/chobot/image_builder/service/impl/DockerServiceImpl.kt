package cz.chobot.image_builder.service.impl

import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.ProgressHandler
import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.service.IDockerService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference

@Service
class DockerServiceImpl : IDockerService {
    private val logger = LoggerFactory.getLogger(DockerServiceImpl::class.java)

    /**
     * Create new docker image from Dockerfile in given path
     * Image name will be userLogin-moduleName-moduleVersion
     * workdir: path to directory where is code and Dockerfile
     * username: login of user
     * module: whole module with code
     * return: Docker image id
     */
    override fun buildImage(workdir: String, username: String, module: Module): String {
        val imageName = "$username-${module.name}-${module.actualVersion.name}".replace("\\s".toRegex(), "")
        logger.info("Creating new docker image for {} module {} with name {} from path {}", username, module.name, imageName, workdir)

        val docker: DockerClient = DefaultDockerClient.fromEnv().build()
        val imageIdFromMessage = AtomicReference<String>()

        val returnedImageId = docker.build(
                Paths.get(workdir), imageName, ProgressHandler { message ->
            val imageId = message.buildImageId()
            if (imageId != null) {
                imageIdFromMessage.set(imageId)
            }
        })

        logger.info("Docker image created with id {}", returnedImageId)
        return returnedImageId
    }

    /**
     * Delete docker image by its id
     * module: Module with docker id attr
     */
    override fun deleteImage(module: Module) {
        val docker: DockerClient = DefaultDockerClient.fromEnv().build()
        docker.removeImage(module.dockerId)
    }


}