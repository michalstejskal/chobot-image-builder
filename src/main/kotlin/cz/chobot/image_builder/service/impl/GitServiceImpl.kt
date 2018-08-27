package cz.chobot.image_builder.service.impl

import cz.chobot.image_builder.service.IGitService
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File

@Service
class GitServiceImpl : IGitService {

    @Value("\${gitlab.access.token}")
    private val gitlabToken: String? = null

    @Value("\${gitlab.url}")
    private val gitlabUrl: String? = null

    @Value("\${gitlab.push.url}")
    private val gitlabPushUrl: String? = null

    override fun commitUserFiler(repo: Git, userProjectPath: String, tag: String) {
        val add = repo.add()
        add.addFilepattern(".").call()


        val commit = repo.commit()
        commit.setMessage("init").call();
        repo.tag().setName(tag).call()

        val credentialsProvider = UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", gitlabToken)
        repo.push().setCredentialsProvider(credentialsProvider).call()
        repo.push().setCredentialsProvider(credentialsProvider).setPushTags().call()


    }

    override fun createGitlabProject(username: String, projectName: String): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("PRIVATE-TOKEN", gitlabToken)
        val entity = HttpEntity<String>(headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity("$gitlabUrl/projects?name=$username-$projectName", entity, String::class.java)
        if (!response.statusCode.equals(HttpStatus.CREATED)) {
            // todo chyba
        }
        //ziskam url repozitare z headers
        //val result = ObjectMapper().readValue(response.body, HashMap::class.java)
        //return result["ssh_url_to_repo"].toString()

        return "https://oauth2:$gitlabToken@$gitlabPushUrl/$username-$projectName.git"
    }

    override fun cloneRepo(repoUrl: String, workdir: String) {
        val credentialsProvider = UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", gitlabToken)
        val repo = Git.cloneRepository().setCredentialsProvider(credentialsProvider).setURI(repoUrl).setDirectory(File(workdir)).call()

    }


    override fun createUserRepo(userProjectPath: String, gitlabUri: String) {
        val repo = Git.init().setDirectory(File(userProjectPath)).call()
        val remoteAddCommand = repo.remoteAdd()
        remoteAddCommand.setName("origin")
        remoteAddCommand.setUri(URIish(gitlabUri))
        remoteAddCommand.call()

        commitUserFiler(repo, userProjectPath, "nejakejtag")
    }
}