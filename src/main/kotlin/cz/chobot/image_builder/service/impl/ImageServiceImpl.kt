package cz.chobot.image_builder.service.impl

import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.bo.ModuleVersion
import cz.chobot.image_builder.bo.User
import cz.chobot.image_builder.enum.ModuleType
import cz.chobot.image_builder.repository.ModuleRepository
import cz.chobot.image_builder.exception.GitException
import cz.chobot.image_builder.service.IDockerService
import cz.chobot.image_builder.service.IGitService
import cz.chobot.image_builder.service.IImageService
import org.eclipse.jgit.api.errors.JGitInternalException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


@Service
class ImageServiceImpl : IImageService {

    private val logger = LoggerFactory.getLogger(ImageServiceImpl::class.java)

    @Autowired
    private lateinit var dockerService: IDockerService

    @Autowired
    private lateinit var gitService: IGitService

    @Autowired
    private lateinit var moduleRepository: ModuleRepository

    @Value("\${image.git.repo.uri}")
    private val baseImageGitRepoUri: String? = null

    @Value("\${working.directory}")
    private val workingDirectory: String? = null

    @Value("\${gitlab.access.token}")
    private val gitlabToken: String? = null

    @Value("\${gitlab.url}")
    private val gitlabUrl: String? = null

    @Value("\${gitlab.push.url}")
    private val gitlabPushUrl: String? = null

    /**
     * Create new image with user code. First clone base repository with controller configuration and
     * after that put user code to directory user_code and create new repository.
     * From this repository is created new docker image and his id is returned.
     * module: module with user code
     * user: user
     * return docker id of new image
     */
    override fun createComposeImage(module: Module, user: User): Optional<String> {
        module.name = module.name.replace("\\s".toRegex(), "")
        module.actualVersion.name = module.actualVersion.name.replace("\\s".toRegex(), "")
        val buildPath = "$workingDirectory/${user.login}/${module.name}/${module.actualVersion.name}"
        val path = Paths.get(buildPath)

        try {
            logger.info("Creating project for user {} module name {} on path {}", user.login, module.name, buildPath)
            if (Files.exists(path)) {
                cleanUserSpace("$workingDirectory}")
            }

            if (module.type == ModuleType.LAMBDA.code) {
                gitService.cloneGitRepo(baseImageGitRepoUri!!, buildPath) // clone base repo == flask endpoint which call user codes
                val repoUrl = gitService.createGitlabProject(user.login, module.name) // create git repo on gitlab
                module.actualVersion.commitId = gitService.createGitRepo("$buildPath/user_code", repoUrl, module) // create git repo from user codes
            } else if (module.type == ModuleType.REPOSITORY.code) {
                gitService.cloneGitRepo(module.repositoryUrl, buildPath) // clone user repository
            }

            module.dockerId = dockerService.buildImage(buildPath, user.login, module) // create docker image from base repo and user codes

            module.actualVersion.module = module
            moduleRepository.save(module)

            return Optional.of(module.dockerId)
        } catch (exc: JGitInternalException) {
            logger.error("Unable to create repository to given path {}", buildPath)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        } catch (exc: GitException) {
            logger.error("Unable to create gitlab project for user {} module {}", user.login, module.name)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        } catch (exc: Exception) {
            logger.error("Unexpected error occurred while creating image for user {} module {}", user.login, module.name)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        }

        return Optional.empty()
    }


    /**
     * Create new image with user code. First clone base repository with controller configuration and
     * after that clone user repository and put user code to directory and commit to repository.
     * From this repository is created new docker image and his id is returned.
     * module: module with user code
     * user: user
     * return docker id of new image
     */
    override fun updateComposeImage(module: Module, user: User): Optional<String> {
        val ver2 = ModuleVersion(1,"verze2", "", module)
        module.versions.add(ver2)
        module.code = "ZGVmIGhhbmRsZShkYXRhLCBjb250ZXh0KToKICAgIHByaW50KCJwcmlqYWwgVkVSWkUgMiBQSUNPanNlbSBkYXRhIikK"
        module.actualVersion = ver2


        module.actualVersion.name = module.actualVersion.name.replace("\\s".toRegex(), "")
        val buildPath = "$workingDirectory/${user.login}/${module.name}/${module.actualVersion.name}"
        val path = Paths.get(buildPath)


        try {
            logger.info("Updating project on path {}", buildPath)

            if (Files.exists(path)) {
                cleanUserSpace(buildPath)
            }

            if (module.type == ModuleType.LAMBDA.code) {
                gitService.cloneGitRepo(baseImageGitRepoUri!!, buildPath) // clone base repo == flask endpoint which call user codes
                module.actualVersion.commitId = gitService.updateGitRepo("$buildPath", module, user) // create git repo from user codes
            } else if (module.type == ModuleType.REPOSITORY.code) {
                gitService.cloneGitRepo(module.repositoryUrl, buildPath) // clone user repository
            }

            module.dockerId = dockerService.buildImage(buildPath, user.login, module) // create docker image from base repo and user codes

            module.actualVersion.module = module
            moduleRepository.save(module)

            logger.info("Project on path {} updated", buildPath)
            return Optional.of(module.dockerId)
        } catch (exc: JGitInternalException) {
            logger.error("Unable to create repository to given path {}", buildPath)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        } catch (exc: GitException) {
            logger.error("Unable to create gitlab project for user {} module {}", user.login, module.name)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        } catch (exc: Exception) {
            logger.error("Unexpected error occurred while creating image for user {} module {}", user.login, module.name)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        }

        return Optional.empty()
    }


    override fun deleteComposeImage(module: Module, user: User): Boolean {
        dockerService.deleteImage(module)
        return true
    }

    /**
     * Create new docker image from user code. Clone base repo, after that clone user repo and create new docker image/
     * module: module with user code
     * user: user
     * return docker id of new image
     */
    override fun restoreComposeImage(module: Module, user: User): Optional<String> {
        module.name = module.name.replace("\\s".toRegex(), "")
        module.actualVersion.name = module.actualVersion.name.replace("\\s".toRegex(), "")
        val buildPath = "$workingDirectory/${user.login}/${module.name}/${module.actualVersion.name}"
        val userRepoUrl = "https://oauth2:$gitlabToken@$gitlabPushUrl/${user.login}-${module.name}.git"
        val path = Paths.get("$workingDirectory/${user.login}/${module.name}")

        logger.info("Restoring project on path {}", buildPath)
        try {
            if (Files.exists(path)) {
                cleanUserSpace("$workingDirectory/${user.login}")
            }

            if (module.type == ModuleType.LAMBDA.code) {
                gitService.cloneGitRepo(baseImageGitRepoUri!!, buildPath) // clone base repo == flask endpoint which call user codes
                gitService.cloneGitRepo(userRepoUrl, "$buildPath/user_code") // clone base repo == flask endpoint which call user codes
                gitService.checkoutVersion("$buildPath/user_code", userRepoUrl, module)
            } else if (module.type == ModuleType.REPOSITORY.code) {
                gitService.cloneGitRepo(module.repositoryUrl, buildPath) // clone user repository
            }

            module.dockerId = dockerService.buildImage(buildPath, user.login, module) // create docker image from base repo and user codes

            moduleRepository.save(module)
            logger.info("Project on path {} restored", buildPath)
            return Optional.of(module.dockerId)
        } catch (exc: JGitInternalException) {
            logger.error("Unable to create repository to given path {}", buildPath)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        } catch (exc: GitException) {
            logger.error("Unable to create gitlab project for user {} module {}", user.login, module.name)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        } catch (exc: Exception) {
            logger.error("Unexpected error occurred while creating image for user {} module {}", user.login, module.name)
            logger.error("{}", exc.stackTrace)
            exc.printStackTrace()
        }

        return Optional.empty()
    }


    // clean directory on specified path
    private fun cleanUserSpace(workDir: String) {
        val dir = File(workDir)
        dir.deleteRecursively()
    }


}