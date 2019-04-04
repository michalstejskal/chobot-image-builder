package cz.chobot.network_builder

import cz.chobot.image_builder.NetworkBuilderApplication
import cz.chobot.image_builder.service.impl.GitServiceImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

@ActiveProfiles("test")
@SpringBootTest(classes = [NetworkBuilderApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class GitConnectionTest {
    @Mock
    private val restTemplate: RestTemplate? = null

    @InjectMocks
    private lateinit var gitService: GitServiceImpl

    @Value("\${gitlab.url}")
    private val gitServerUrl: String? = null


    private lateinit var entity: HttpEntity<String>

    @Before
    fun createHeadersEntity() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("PRIVATE-TOKEN", null)
        entity = HttpEntity<String>(headers)
    }

    @Test
    fun testUserModuleTemplateCheckout() {
        val username = "some_username"
        val projectName = "some_projectname"


        Mockito.`when`(restTemplate?.postForEntity("$gitServerUrl/projects?name=$username-$projectName", entity, String::class.java))
                .thenReturn(ResponseEntity("", HttpStatus.CREATED))

        org.springframework.test.util.ReflectionTestUtils.setField(gitService, "gitlabUrl", gitServerUrl)
        val newRepoUrl = gitService.createGitlabProject(username, projectName)
        assert(newRepoUrl != null)
        assert(newRepoUrl.contains(username))
        assert(newRepoUrl.contains(username))
    }
}