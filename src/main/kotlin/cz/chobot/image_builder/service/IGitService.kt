package cz.chobot.image_builder.service

import org.eclipse.jgit.api.Git

interface IGitService {
    fun commitUserFiler(repo: Git, userProjectPath: String, tag: String)
    fun createGitlabProject(username: String, projectName: String): String
    fun cloneRepo(repoUrl: String, workdir: String)
    fun createUserRepo(userProjectPath: String, gitlabUri: String)
}