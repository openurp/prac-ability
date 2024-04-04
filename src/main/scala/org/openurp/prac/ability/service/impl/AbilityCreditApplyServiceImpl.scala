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

package org.openurp.prac.ability.service.impl

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.base.edu.model.Course
import org.openurp.base.model.AuditStatus.{Passed, Rejected}
import org.openurp.base.std.model.Student
import org.openurp.prac.ability.model.{AbilityCredit, AbilityCreditApply, AbilityCreditConfig}
import org.openurp.prac.ability.service.{AbilityCreditApplyService, ExternCourseGradeSyncService}

import java.time.Instant

class AbilityCreditApplyServiceImpl extends AbilityCreditApplyService {

  var entityDao: EntityDao = _

  var externCourseGradeSyncService: Option[ExternCourseGradeSyncService] = None

  override def accept(apply: AbilityCreditApply): Unit = {
    apply.status = Passed
    entityDao.saveOrUpdate(apply)
    val std = apply.std
    val credit = autoStatCredit(std)
    val requiredCredits = entityDao.getAll(classOf[AbilityCreditConfig]).head.credits
    if (credit.credits.toInt >= requiredCredits && credit.courseGradeId.isEmpty) {
      externCourseGradeSyncService.foreach { s =>
        val courseGradeId = s.add(std, apply.semester, getCourse(std, requiredCredits))
        if (courseGradeId > 0) {
          credit.courseGradeId = Some(courseGradeId)
          entityDao.saveOrUpdate(credit)
        }
      }
    }
  }

  private def getCourse(std: Student, requiredCredits: Int): Course = {
    val q = OqlBuilder.from(classOf[Course], "c")
    q.where("c.project=:project", std.project)
    q.where("c.department=:depart", std.department)
    q.where("c.name=:name", "能力素质拓展课")
    q.where("c.defaultCredits=:credits", requiredCredits)
    entityDao.search(q).head
  }

  override def reject(apply: AbilityCreditApply): Unit = {
    apply.status = Rejected
    entityDao.saveOrUpdate(apply)
    val requiredCredits = entityDao.getAll(classOf[AbilityCreditConfig]).head.credits
    val credit = autoStatCredit(apply.std)
    if (credit.credits.toInt < requiredCredits && credit.courseGradeId.nonEmpty) {
      val existCourseGradeId = credit.courseGradeId.get
      credit.courseGradeId = None
      entityDao.saveOrUpdate(credit)
      externCourseGradeSyncService foreach { s =>
        s.remove(existCourseGradeId)
      }
    }
  }

  private def autoStatCredit(std: Student): AbilityCredit = {
    val applies = entityDao.findBy(classOf[AbilityCreditApply], "std" -> std, "status" -> Passed)
    val credits = applies.map(_.credits).sum
    val abilityCredit = entityDao.findBy(classOf[AbilityCredit], "std", std).headOption.getOrElse(new AbilityCredit(std))
    abilityCredit.credits = credits
    abilityCredit.updatedAt = Instant.now
    entityDao.saveOrUpdate(abilityCredit)
    abilityCredit
  }

}
