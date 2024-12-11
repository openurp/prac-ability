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

import jakarta.servlet.http.Part
import org.beangle.commons.lang.Strings
import org.beangle.ems.app.EmsApp
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.model.{AuditStatus, Project}
import org.openurp.base.std.model.Student
import org.openurp.code.edu.model.Certificate
import org.openurp.prac.ability.model.AbilityCreditApply
import org.openurp.starter.web.support.StudentSupport

import java.time.Instant

/** 学生能力素质拓展证书上报
 */
class StdCertAction extends StudentSupport with EntityAction[AbilityCreditApply] {

  var businessLogger: WebBusinessLogger = _

  protected override def projectIndex(student: Student): View = {
    val applies = entityDao.findBy(classOf[AbilityCreditApply], "std", student)
    put("applies", applies)
    val repo = EmsApp.getBlobRepository(true)
    val paths = applies.map(x => (x, repo.url(x.attachmentPath)))
    put("attachmentPaths", paths.toMap)
    put("editables", Set(AuditStatus.Draft, AuditStatus.Submited, AuditStatus.Rejected, AuditStatus.RejectedByDepart))

    given project: Project = getProject

    val certificates = getCodes(classOf[Certificate])
    put("certificates", certificates.groupBy(_.category))
    forward()
  }

  def edit(): View = {
    val certificate = entityDao.get(classOf[Certificate], getIntId("certificate"))
    val apply =
      getLong("apply.id") match {
        case None =>
          val ap = new AbilityCreditApply
          ap.std = getStudent
          ap
        case Some(id) => entityDao.get(classOf[AbilityCreditApply], id)
      }

    given project: Project = apply.std.project

    val std = apply.std
    apply.certificate = certificate
    if (!apply.persisted && certificate.subjects.isEmpty) {
      apply.finished = true
      apply.subjects = "全部"
    }
    put("certificate", certificate)
    put("apply", apply)
    forward()
  }

  def remove(): View = {
    val std = getStudent
    val apply = entityDao.get(classOf[AbilityCreditApply], getLongId("apply"))
    if (apply.std == std) {
      if (apply.status == AuditStatus.Passed || apply.status == AuditStatus.PassedByDepart) {
        redirect("index", "已经审核通过，暂时不能删除")
      } else {
        val repo = EmsApp.getBlobRepository(true)
        if (Strings.isNotEmpty(apply.attachmentPath)) {
          repo.remove(apply.attachmentPath)
        }
        entityDao.remove(apply)
        businessLogger.info(s"${std.code} ${std.name}删除了${apply.certificate}认定申请", apply.id, Map.empty)
        redirect("index", "删除成功")
      }
    } else {
      redirect("index", "删除成功")
    }
  }

  def save(): View = {
    val std = getStudent
    val apply = populateEntity(classOf[AbilityCreditApply], "apply")
    val certificate = entityDao.get(classOf[Certificate], getIntId("certificate"))
    if (apply.status == AuditStatus.Passed || apply.status == AuditStatus.PassedByDepart) {
      redirect("index", "已经审核通过，暂时不能修改")
    } else {
      apply.std = std
      apply.updatedAt = Instant.now
      apply.certificate = certificate
      apply.semester = getSemester
      apply.auditDepart = std.department
      apply.auditOpinion = None
      apply.credits = None

      val parts = getAll("attachment", classOf[Part])
      if (parts.nonEmpty && null != parts.head && parts.head.getSize > 0) {
        val repo = EmsApp.getBlobRepository(true)
        if (Strings.isNotEmpty(apply.attachmentPath)) {
          repo.remove(apply.attachmentPath)
        }
        val part = parts.head
        val meta = repo.upload("/ability/certificate", part.getInputStream,
          std.code + "_" + part.getSubmittedFileName, std.code + " " + std.name);
        apply.attachmentPath = meta.filePath
      }

      if (Strings.isNotBlank(apply.attachmentPath)) {
        apply.status = AuditStatus.Submited
        entityDao.saveOrUpdate(apply)
        val details = Map("subjects" -> apply.subjects, "certificateNo" -> apply.certificateNo.getOrElse("--"))
        businessLogger.info(s"${std.code} ${std.name}提交了${certificate.name}证书登记申请", apply.id, details)
        redirect("index", s"&projectId=${std.project.id}", "提交成功")
      } else {
        redirect("edit", s"&certificate.id=${certificate.id}&projectId=${std.project.id}&apply.id=${apply.id}", "缺少附件")
      }
    }
  }
}
