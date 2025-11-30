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
import org.beangle.doc.transfer.exporter.ExcelWriter
import org.beangle.web.servlet.util.RequestUtils
import org.beangle.webmvc.annotation.{mapping, param}
import org.beangle.webmvc.view.View
import org.openurp.base.hr.model.Mentor
import org.openurp.base.model.Project
import org.openurp.base.std.model.Squad
import org.openurp.prac.ability.model.{AbilityCredit, AbilityCreditApply}
import org.openurp.starter.web.support.MentorSupport

import java.util

/** 查看班级的学分认定明细
 */
class SquadAction extends MentorSupport {

  protected override def projectIndex(mentor: Mentor)(using project: Project): View = {
    val q = OqlBuilder.from(classOf[Squad], "s")
    q.where("s.mentor=:staff or s.master=:staff", mentor.staff)
    q.orderBy("s.code desc")
    val squads = entityDao.search(q)
    put("squads", squads)
    forward()
  }

  @mapping(value = "{id}")
  def info(@param("id") id: String): View = {
    val squad = entityDao.get(classOf[Squad], id.toLong)
    val credits = entityDao.findBy(classOf[AbilityCredit], "std.state.squad", squad).sortBy(_.std.code)
    val applies = entityDao.findBy(classOf[AbilityCreditApply], "std.state.squad", squad).sortBy(_.updatedAt).reverse
    put("credits", credits)
    put("applies", applies)
    put("squad", squad)
    forward()
  }

  /** 下载班级认定明细
   *
   * @return
   */
  def download(): View = {
    val squad = entityDao.get(classOf[Squad], getLongId("squad"))
    val credits = entityDao.findBy(classOf[AbilityCredit], "std.state.squad", squad)
    val applies = entityDao.findBy(classOf[AbilityCreditApply], "std.state.squad", squad)

    response.setContentType("application/vnd.ms-excel;charset=GBK")
    RequestUtils.setContentDisposition(response, squad.name + " 能力素质拓展证书和学分.xlsx")

    val writer = new ExcelWriter(response.getOutputStream)
    writer.createScheet("证书明细")
    val titles = List("学号", "姓名", "性别", "证书", "证书内课程", "获得年月", "学分", "状态").toBuffer
    writer.writeHeader(Some(squad.name + "能力素质拓展课 证书申请明细"), titles.toArray)
    applies foreach { apply =>
      val data = new util.ArrayList[Any]
      data.add(apply.std.code)
      data.add(apply.std.name)
      data.add(apply.std.gender.name)
      data.add(apply.certificate.name)
      data.add(apply.subjects)
      data.add(apply.acquiredIn.toString)
      data.add(apply.credits.getOrElse("").toString)
      data.add(apply.status.name)
      writer.write(data.toArray)
    }
    writer.createScheet("认定学分明细")
    val titles2 = List("学号", "姓名", "性别", "认定学分").toBuffer
    writer.writeHeader(Some(squad.name + "能力素质拓展课 认定学分明细"), titles2.toArray)
    credits foreach { credit =>
      val data = new util.ArrayList[Any]
      data.add(credit.std.code)
      data.add(credit.std.name)
      data.add(credit.std.gender.name)
      data.add(credit.credits)
      writer.write(data.toArray)
    }
    writer.close()

    null
  }

}
