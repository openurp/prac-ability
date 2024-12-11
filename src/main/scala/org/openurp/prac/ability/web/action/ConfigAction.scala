/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.prac.ability.web.action

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.annotation.ignore
import org.beangle.webmvc.support.action.RestfulAction
import org.beangle.webmvc.view.View
import org.openurp.code.edu.model.EducationLevel
import org.openurp.prac.ability.config.AbilityCreditConfig
import org.openurp.starter.web.support.ProjectSupport

class ConfigAction extends RestfulAction[AbilityCreditConfig] with ProjectSupport {

  @ignore
  protected override def simpleEntityName: String = {
    "config"
  }

  protected override def indexSetting(): Unit = {
    put("project", getProject)
    super.indexSetting()
  }

  override protected def getQueryBuilder: OqlBuilder[AbilityCreditConfig] = {
    val query = super.getQueryBuilder
    query.where("config.project=:project", getProject)
    query
  }

  override protected def saveAndRedirect(entity: AbilityCreditConfig): View = {
    entity.project = getProject
    entity.levels.clear()
    entity.levels.addAll(entityDao.find(classOf[EducationLevel], getIntIds("level")))
    super.saveAndRedirect(entity)
  }

  protected override def editSetting(config: AbilityCreditConfig): Unit = {
    put("project", getProject)
  }
}
