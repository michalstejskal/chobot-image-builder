package cz.chobot.image_builder.service.impl

import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.bo.UserCode
import cz.chobot.image_builder.service.IDockerService
import cz.chobot.image_builder.service.IGitService
import cz.chobot.image_builder.service.IImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.*


@Service
class ImageServiceImpl : IImageService {

    @Autowired
    private lateinit var dockerService: IDockerService


    @Autowired
    private lateinit var gitService: IGitService

    @Value("\${image.git.repo.uri}")
    private val imageGitRepoUri: String? = null

    @Value("\${working.directory}")
    private val workingDirectory: String? = null


    override fun createHandlerImage(module: Module): Module {
        val userProjectPath = "$workingDirectory/${module.username}/${module.name}"

        // clone base repo == flask endpoint which call user codes
        gitService.cloneRepo(imageGitRepoUri!!, userProjectPath)

        // create git repo on gitlab
        val repoUrl = gitService.createGitlabProject(module.username, module.name)

        // create files from user codes in defined path
        createUserCode(module.userCodes, "$userProjectPath/user_code")

        // create git repo from user codes
        gitService.createUserRepo("$userProjectPath/user_code", repoUrl)

        //create docker image from base repo and user codes
        dockerService.buildImage(userProjectPath, module.username, module.name, module.tag)



        return module
    }

    override fun updateHandlerImage(module: Module): Module {

    }

    private fun createUserCode(userCodes: List<UserCode>, userProjectPath: String) {
        val userCodeDir = File(userProjectPath)
        userCodeDir.mkdir()
        userCodes.map { userCode ->
            val decoded = Base64.getDecoder().decode(userCode.encodedCode)
            createFile(decoded, userCode.responseClass, userProjectPath)
        }
    }

    private fun createFile(userCode: ByteArray, fileName: String, repoPath: String) {
        val file = File("$repoPath/$fileName.py")
        file.printWriter().use { out -> out.println(String(userCode)) }
    }


//    private fun cleanUserSpace(workdir: String){
//        val dir = File(workdir)
//        dir.deleteRecursively()
//    }


}