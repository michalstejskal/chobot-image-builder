package cz.chobot.image_builder.service.impl

import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.bo.User
import cz.chobot.image_builder.enum.ModuleType
import cz.chobot.image_builder.exception.GitException
import cz.chobot.image_builder.service.IGitService
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File
import java.util.*

//todo add support clone from other places than gitlab
@Service
class GitServiceImpl : IGitService {

    private val logger = LoggerFactory.getLogger(GitServiceImpl::class.java)

    @Value("\${gitlab.access.token}")
    private val gitlabToken: String? = null

    @Value("\${gitlab.url}")
    private val gitlabUrl: String? = null

    @Value("\${gitlab.push.url}")
    private val gitlabPushUrl: String? = null

    override fun createGitlabProject(username: String, projectName: String): String {
        logger.info("Creating new gitlab project for user {}", username)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("PRIVATE-TOKEN", gitlabToken)
        val entity = HttpEntity<String>(headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity("$gitlabUrl/projects?name=$username-$projectName", entity, String::class.java)
        if (response.statusCode != HttpStatus.CREATED) {
            logger.error("Unable to create gitlab project, response from gitlab: {}", response.body)
            throw GitException("Unable to create gitlab project")
        }
        //ziskam url repozitare z response body
        return "https://oauth2:$gitlabToken@$gitlabPushUrl/$username-$projectName.git"
    }

    override fun cloneGitRepo(repoUrl: String, workdir: String) {
        logger.info("Cloning repo {} to path {}", repoUrl, workdir)
        val credentialsProvider = UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", gitlabToken)
        Git.cloneRepository().setCredentialsProvider(credentialsProvider).setURI(repoUrl).setDirectory(File(workdir)).call()
        logger.info("Repo cloned")
    }


    /**
     * Create new gitlab repository with user code inside and commit it to gitlab.
     * workdir: path to user code, there will be new repo created
     * gitlabUri: uri to gitlab, where new repo will be created
     * module: module with user code
     * return commit SHA-1 hash which is save to module version
     */
    override fun createGitRepo(workdir: String, gitlabUri: String, module: Module): String {
        logger.info("Creating git repository from user module on path {}", workdir)

        createUserCode(module, workdir) // create files from user codes in defined path
        val repo = Git.init().setDirectory(File(workdir)).call()
        val remoteAddCommand = repo.remoteAdd()
        remoteAddCommand.setName("origin")
        remoteAddCommand.setUri(URIish(gitlabUri))
        remoteAddCommand.call()

        val commitId = commitUserFiles(repo, module)
        logger.info("Git repository created on path {}", workdir)
        return commitId

    }

    /**
     * Update repository on given path with new user code on gitlab. Commit new code
     * workdir: path to user code, there will be new repo created
     * gitlabUri: uri to gitlab, where new repo will be created
     * module: module with user code
     * return commit SHA-1 hash which is save to module version
     */
    override fun updateGitRepo(workdir: String, module: Module, user: User): String {
        val userRepoUrl = "https://oauth2:$gitlabToken@$gitlabPushUrl/${user.login}-${module.name}.git"

        logger.info("Updating git repository from user module on path {}", workdir)
        cloneGitRepo(userRepoUrl, "$workdir/user_code") // clone base repo == flask endpoint which call user codes
        createUserCode(module, "$workdir/user_code") // create files from user codes in defined path
        val repo = Git.init().setDirectory(File("$workdir/user_code")).call()

        val commitId = commitUserFiles(repo, module)
        logger.info("Git repository created on path {}", workdir)
        return commitId
    }

    /**
     * Checkout code from specific commit in module actual version attr.
     * workdir: path to user code, there will be new repo created
     * gitlabUri: uri to gitlab, where new repo will be created
     * module: module with user code
     */
    override fun checkoutVersion(workdir: String, gitlabTag: String, module: Module) {
        logger.info("Checking out gitlab version")
        val git = Git.open(File(workdir))
        git.checkout()
                .setCreateBranch(true)
                .setName(module.actualVersion.name)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint(module.actualVersion.commitId)
                .call()
        logger.info("Git version checked out")
    }


    /**
     * Create new commit and make push to repository
     * repo: git repository with changed code which should be commited
     * module: user module
     * return commit SHA-1 hash
     */
    private fun commitUserFiles(repo: Git, module: Module): String {
        val credentialsProvider = UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", gitlabToken)

        val add = repo.add()
        add.addFilepattern(".").call()

        val commit = repo.commit()
        val commitCall = commit.setMessage(module.actualVersion.name).call()
        repo.push().setCredentialsProvider(credentialsProvider).call()
        return commitCall.name
    }

    // Take user code encode it from base64 and put it to file on specified path (call fce createFile)
    private fun createUserCode(module: Module, userProjectPath: String) {
        logger.info("Creating user module from user code")
        val userCodeDir = File(userProjectPath)
        userCodeDir.mkdir()
        val decoded = Base64.getDecoder().decode(module.code)
        createFile(decoded, "handler", userProjectPath)
        logger.info("User module created")
    }

    private fun createFile(userCode: ByteArray, fileName: String, repoPath: String) {
        logger.info("Creating file for user code")
        val file = File("$repoPath/$fileName.py")
        file.printWriter().use { out -> out.println(String(userCode)) }
        logger.info("File with user code created")
    }
}