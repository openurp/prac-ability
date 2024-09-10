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
import org.beangle.ems.app.EmsApp
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.security.Securities
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.hr.model.Staff
import org.openurp.base.model.{AuditStatus, Project}
import org.openurp.code.edu.model.Certificate
import org.openurp.prac.ability.model.AbilityCreditApply
import org.openurp.starter.web.support.ProjectSupport

/** 辅导员（学院初审）查看和审核申请
 */
class MentorAction extends RestfulAction[AbilityCreditApply] with ProjectSupport {

  protected override def simpleEntityName: String = "apply"

  var businessLogger: WebBusinessLogger = _

  private val statuses = List(AuditStatus.Submited, AuditStatus.PassedByDepartTrial, AuditStatus.RejectedByDepartTrial)

  override protected def indexSetting(): Unit = {
    put("statuses", statuses)

    given project: Project = getProject

    put("certificates", codeService.get(classOf[Certificate]))
    put("levels", project.levels)
    super.indexSetting()
  }

  override protected def getQueryBuilder: OqlBuilder[AbilityCreditApply] = {
    given project: Project = getProject

    val query = super.getQueryBuilder
    query.where("apply.std.project=:project", project)
    query.where("apply.status in :statusList", statuses)
    val departs = getDeparts
    if(departs.isEmpty){
      val staffs = entityDao.findBy(classOf[Staff], "code" -> Securities.user, "school" -> project.school)
      if staffs.isEmpty then query.where("apply.id <0")
      else query.where("apply.std.state.squad.mentor=:mentor", staffs.head)
    }else{
      query.where("apply.std.state.department in(:departs)", departs)
    }
     query
  }

  def auditForm(): View = {
    val apply = getEntity(classOf[AbilityCreditApply], "apply")
    put("apply", apply)
    put("editables", auditableStatuses)
    val repo = EmsApp.getBlobRepository(true)
    put("attachmentPath", repo.url(apply.attachmentPath))
    forward()
  }

  private def auditableStatuses: Set[AuditStatus] = {
    Set(AuditStatus.Submited, AuditStatus.PassedByDepartTrial, AuditStatus.RejectedByDepartTrial)
  }

  def audit(): View = {
    val apply = getEntity(classOf[AbilityCreditApply], "apply")
    val passed = getBoolean("passed", false)
    var msg: String = null
    if (passed) {
      apply.status = AuditStatus.PassedByDepartTrial
      msg = s"${Securities.user}审批通过了${apply.std.code}的认定申请"
    } else {
      apply.status = AuditStatus.RejectedByDepartTrial
      msg = s"${Securities.user}驳回了${apply.std.code}的认定申请"
    }

    apply.auditOpinion = get("auditOpinion")
    entityDao.saveOrUpdate(apply)
    businessLogger.info(msg, apply.id, Map("auditOpinion" -> apply.auditOpinion))
    redirect("search", "审批完成")
  }

  /** 批量审核
   *
   * @return
   */
  def batchAudit(): View = {
    val applies = entityDao.find(classOf[AbilityCreditApply], getLongIds("apply"))
    val passed = getBoolean("passed", false)
    applies foreach { apply =>
      if (auditableStatuses.contains(apply.status)) {
        var msg: String = null
        if (passed) {
          apply.status = AuditStatus.PassedByDepartTrial
          msg = s"${Securities.user}审批通过了${apply.std.code}的认定申请"
        } else {
          apply.status = AuditStatus.RejectedByDepartTrial
          msg = s"${Securities.user}驳回了${apply.std.code}的认定申请"
        }
        businessLogger.info(msg, apply.id, Map.empty)
        entityDao.saveOrUpdate(apply)
      }
    }
    redirect("search", "审批完成")
  }
}
