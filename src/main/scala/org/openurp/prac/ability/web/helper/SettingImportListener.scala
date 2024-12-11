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

package org.openurp.prac.ability.web.helper

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.EntityDao
import org.beangle.doc.transfer.importer.{ImportListener, ImportResult}
import org.openurp.base.edu.model.Major
import org.openurp.base.model.Department
import org.openurp.prac.ability.config.{AbilityCreditConfig, AbilityCreditSetting}

class SettingImportListener(config: AbilityCreditConfig, entityDao: EntityDao) extends ImportListener {

  val school = config.project.school

  override def onItemStart(tr: ImportResult): Unit = {
    entityDao.refresh(config)
    config.settings.find(_.certificate.name == transfer.curData.getOrElse("setting.certificate.code", "")) foreach { s =>
      transfer.current = s
    }
  }

  override def onItemFinish(tr: ImportResult): Unit = {
    val setting = tr.transfer.current.asInstanceOf[AbilityCreditSetting]
    transfer.curData.get("departNames") foreach { departNames =>
      val names = Strings.split(departNames.toString)
      val departs = entityDao.findBy(classOf[Department], "name" -> names, "school" -> school)
      setting.departs.clear()
      setting.departs ++= departs
      setting.special = departs.nonEmpty
      if (names.length != departs.size) {
        tr.addFailure("部门名称错误", departNames)
      }
    }
    transfer.curData.get("majorNames") foreach { majorNames =>
      val names = Strings.split(majorNames.toString)
      val majors = entityDao.findBy(classOf[Major], "name" -> names, "project" -> config.project)
      setting.majors.clear()
      setting.majors ++= majors
      if (names.length != majors.size) {
        tr.addFailure("专业名称错误", majorNames)
      }
    }
    setting.config = config
    config.settings += setting
    setting.collegeReviewRequired = true
    entityDao.saveOrUpdate(setting, config)
  }
}
