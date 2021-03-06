package com.github.lppedd.cc.api

import com.github.lppedd.cc.DEFAULT_VCS_PROVIDER_ID
import com.github.lppedd.cc.ICON_DEFAULT_PRESENTATION
import com.github.lppedd.cc.parser.CCParser
import com.github.lppedd.cc.parser.CommitTokens
import com.github.lppedd.cc.parser.ValidToken
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsConfiguration
import org.jetbrains.annotations.ApiStatus

/**
 * @author Edoardo Luppi
 */
@ApiStatus.Internal
private class DefaultVcsCommitSubjectProvider(private val project: Project) : CommitSubjectProvider {
  override fun getId(): String =
    DEFAULT_VCS_PROVIDER_ID

  override fun getPresentation(): ProviderPresentation =
    ProviderPresentation("Default - VCS", ICON_DEFAULT_PRESENTATION)

  override fun getCommitSubjects(commitType: String?, commitScope: String?): Collection<CommitSubject> =
    VcsConfiguration.getInstance(project)
      .recentMessages
      .asReversed()
      .asSequence()
      .take(30)
      .map(CCParser::parseHeader)
      .map(CommitTokens::subject)
      .filterIsInstance<ValidToken>()
      .map(ValidToken::value)
      .map(String::trim)
      .filter(String::isNotEmpty)
      .map(::CommitSubject)
      .toList()
}
