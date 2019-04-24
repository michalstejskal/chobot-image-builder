package cz.chobot.network_builder

import cz.chobot.image_builder.NetworkBuilderApplication
import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.bo.User
import cz.chobot.image_builder.repository.ModuleRepository
import cz.chobot.image_builder.service.IDockerService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@ActiveProfiles("development")
@SpringBootTest(classes = [NetworkBuilderApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class CreateImageTest {

    @Autowired
    private lateinit var dockerService: IDockerService

    @Autowired
    private lateinit var moduleRepository: ModuleRepository

    @Value("\${working.directory}")
    private val workingDirectory: String? = null


    private lateinit var module: Module
    private lateinit var user: User

    @Before
    fun createModule() {
        val allModules = moduleRepository.findAll()
        if (allModules.isEmpty()) {
            throw IllegalStateException()
        }

        module = allModules[0]
        user = module.network.user
    }

    @Test
    fun createImageTest() {
        val buildPath = "$workingDirectory/${user.login}/${module.name}/${module.actualVersion.name}"
        dockerService.buildImage(buildPath, user.login, module)
    }

    @After
    fun deleteModule(){
        dockerService.deleteImage(module)
    }
}