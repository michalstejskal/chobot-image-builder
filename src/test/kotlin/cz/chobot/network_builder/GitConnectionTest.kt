package cz.chobot.network_builder

import com.spotify.docker.client.messages.Network
import cz.chobot.image_builder.service.impl.GitServiceImpl
import org.eclipse.jgit.api.Git
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import java.io.File
import org.eclipse.jgit.lib.Repository
import org.junit.Before
import org.springframework.http.*

@SpringBootTest
@RunWith(MockitoJUnitRunner::class)
@TestPropertySource(properties = arrayOf(
        "image.git.repo.uri=https://gitlab.fit.cvut.cz/stejsmi5/chobot_handler_base_image.git",
        "gitlab.url=https://gitlab.fit.cvut.cz/api/v4/"))
class GitConnectionTest {
    @Mock
    private val restTemplate: RestTemplate? = null

    @InjectMocks
    private val gitService = GitServiceImpl()

    @Value("\${gitlab.url}")
    private val gitServerUrl: String? = null


    private lateinit var entity: HttpEntity<String>

    @Before
    fun createHeadersEntity() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("PRIVATE-TOKEN", "some-token")
        entity = HttpEntity<String>(headers)
    }

    @Test
    fun testUserModuleTemplateCheckout() {
        val username = "some_username"
        val projectName = "some_projectname"

        Mockito.`when`(restTemplate?.postForEntity("$gitServerUrl/projects?name=$username-$projectName", entity, String::class.java))
                .thenReturn(ResponseEntity("", HttpStatus.CREATED))


        val newRepoUrl = gitService.createGitlabProject(username, projectName)
        assert(newRepoUrl != null)
        assert(newRepoUrl.contains(username))
        assert(newRepoUrl.contains(username))
    }
}