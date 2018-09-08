package cz.chobot.image_builder.service

import cz.chobot.image_builder.bo.Module
import cz.chobot.image_builder.bo.User


interface IGitService {
    fun createGitlabProject(username: String, projectName: String): String
    fun cloneGitRepo(repoUrl: String, workdir: String)
    fun createGitRepo(workdir: String, gitlabUri: String, module: Module): String
    fun checkoutVersion(workdir: String, gitlabTag: String, module: Module)
    fun updateGitRepo(workdir: String, module: Module, user: User): String
}