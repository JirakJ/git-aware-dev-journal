package com.jakubjirak.devjournal.git

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager

/**
 * Provides current git context (branch name, HEAD commit) using Git4Idea API.
 * Registered as a project service only when Git4Idea is available.
 */
@Service(Service.Level.PROJECT)
class GitContextProvider(private val project: Project) {

    fun getCurrentBranch(): String? {
        return try {
            val repositoryManager = GitRepositoryManager.getInstance(project)
            val repositories = repositoryManager.repositories
            if (repositories.isNotEmpty()) {
                repositories.first().currentBranchName
            } else {
                null
            }
        } catch (e: Exception) {
            LOG.warn("Failed to get current branch", e)
            null
        }
    }

    fun getHeadCommitHash(): String? {
        return try {
            val repositoryManager = GitRepositoryManager.getInstance(project)
            val repositories = repositoryManager.repositories
            if (repositories.isNotEmpty()) {
                repositories.first().currentRevision
            } else {
                null
            }
        } catch (e: Exception) {
            LOG.warn("Failed to get HEAD commit", e)
            null
        }
    }

    companion object {
        private val LOG = Logger.getInstance(GitContextProvider::class.java)

        fun getInstance(project: Project): GitContextProvider =
            project.getService(GitContextProvider::class.java)
    }
}
