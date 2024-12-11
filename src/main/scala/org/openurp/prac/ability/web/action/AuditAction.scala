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
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.ems.app.{Ems, EmsApp}
import org.beangle.security.Securities
import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.{AuditStatus, Project, User}
import org.openurp.code.edu.model.Certificate
import org.openurp.prac.ability.model.AbilityCreditApply
import org.openurp.starter.web.support.ProjectSupport

/** 学院审核申请
 */
class AuditAction extends RestfulAction[AbilityCreditApply], ProjectSupport, ExportSupport[AbilityCreditApply] {

  protected override def simpleEntityName: String = "apply"

  var businessLogger: WebBusinessLogger = _

  private val statuses = List(AuditStatus.PassedByDepartTrial, AuditStatus.PassedByDepart, AuditStatus.RejectedByDepart, AuditStatus.Rejected, AuditStatus.Passed)

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
    query.where("apply.auditDepart in(:departs)", getDeparts)
    query
  }

  def auditForm(): View = {
    val apply = getEntity(classOf[AbilityCreditApply], "apply")
    put("apply", apply)
    val statuses = auditableStatuses
    put("editable", statuses.contains(apply.status))
    val repo = EmsApp.getBlobRepository(true)
    put("attachmentPath", repo.url(apply.attachmentPath))
    if (apply.persisted) {
      put("logHref", Ems.api + s"/platform/log/list/${EmsApp.name}/${apply.id}.json")
    }
    forward()
  }

  private def auditableStatuses: Set[AuditStatus] = {
    Set(AuditStatus.Submited, AuditStatus.PassedByDepartTrial, AuditStatus.RejectedByDepartTrial, AuditStatus.PassedByDepart, AuditStatus.RejectedByDepart)
  }

  def audit(): View = {
    val apply = getEntity(classOf[AbilityCreditApply], "apply")
    val auditor = entityDao.findBy(classOf[User], "code", Securities.user).head
    val passed = getBoolean("passed", false)
    var msg: String = null
    if (passed) {
      apply.status = AuditStatus.PassedByDepart
      msg = s"${auditor.name}审批通过了${apply.std.code}的认定申请"
    } else {
      apply.status = AuditStatus.RejectedByDepart
      msg = s"${auditor.name}驳回了${apply.std.code}的认定申请"
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
          apply.status = AuditStatus.PassedByDepart
          msg = s"${Securities.user}审批通过了${apply.std.code}的认定申请"
        } else {
          apply.status = AuditStatus.RejectedByDepart
          msg = s"${Securities.user}驳回了${apply.std.code}的认定申请"
        }
        //businessLogger.info(msg, apply.id, Map.empty)
        entityDao.saveOrUpdate(apply)
      }
    }
    redirect("search", "审批完成")
  }
}
