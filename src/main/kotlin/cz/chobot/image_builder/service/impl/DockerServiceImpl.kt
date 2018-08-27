package cz.chobot.image_builder.service.impl

import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.ProgressHandler
import cz.chobot.image_builder.service.IDockerService
import org.springframework.stereotype.Service
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference

@Service
class DockerServiceImpl : IDockerService {

    override fun buildImage(workdir: String, username: String, applicationName: String, tag: String): String {
        val docker: DockerClient = DefaultDockerClient.fromEnv().build()
        val imageIdFromMessage = AtomicReference<String>()

        val returnedImageId = docker.build(
                Paths.get(workdir), "$username-$applicationName-$tag", ProgressHandler { message ->
            val imageId = message.buildImageId()
            if (imageId != null) {
                imageIdFromMessage.set(imageId)
            }
        })

        return returnedImageId
    }

}