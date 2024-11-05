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
import org.beangle.ems.app.{Ems, EmsApi, EmsApp}
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.security.Securities
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.AuditStatus
import org.openurp.prac.ability.model.AbilityCreditApply
import org.openurp.prac.ability.service.AbilityCreditApplyService
import org.openurp.starter.web.support.ProjectSupport

/** 教务处备案审核
 */
class AdminAction extends RestfulAction[AbilityCreditApply], ProjectSupport, ExportSupport[AbilityCreditApply] {

  protected override def simpleEntityName: String = "apply"

  var businessLogger: WebBusinessLogger = _
  var abilityCreditApplyService: AbilityCreditApplyService = _

  private val statuses = List(AuditStatus.Submited, AuditStatus.PassedByDepart, AuditStatus.RejectedByDepart,
    AuditStatus.PassedByDepartTrial, AuditStatus.RejectedByDepartTrial,
    AuditStatus.Passed, AuditStatus.Rejected)

  override protected def indexSetting(): Unit = {
    put("statuses", statuses)
    super.indexSetting()
  }

  override protected def getQueryBuilder: OqlBuilder[AbilityCreditApply] = {
    val query = super.getQueryBuilder
    query.where("apply.std.project=:project", getProject)
    query.where("apply.status in(:statusList)", statuses)
    query
  }

  def auditForm(): View = {
    val apply = getEntity(classOf[AbilityCreditApply], "apply")
    put("apply", apply)
    put("editables", statuses)
    val repo = EmsApp.getBlobRepository(true)
    put("attachmentPath", repo.url(apply.attachmentPath))
    if(apply.persisted){
      put("logHref",Ems.api+s"/platform/log/list/${EmsApp.name}/${apply.id}.json")
    }
    forward()
  }

  def audit(): View = {
    val applies = entityDao.find(classOf[AbilityCreditApply], getLongIds("apply"))
    val passed = getBoolean("passed", false)
    applies foreach { apply =>
      var msg: String = null
      if (passed) {
        abilityCreditApplyService.accept(apply)
        msg = s"${Securities.user}同意通过了${apply.std.code}的认定申请"
      } else {
        abilityCreditApplyService.reject(apply)
        msg = s"${Securities.user}驳回了${apply.std.code}的认定申请"
      }
      businessLogger.info(msg, apply.id, Map.empty)
    }
    redirect("search", "审批完成")
  }

}
