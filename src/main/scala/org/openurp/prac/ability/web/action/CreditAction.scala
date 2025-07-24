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

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.reflect.BeanInfos
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.stat.{Columns, Matrix}
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.beangle.webmvc.view.View
import org.openurp.base.model.{Department, Project}
import org.openurp.base.std.model.Grade
import org.openurp.prac.ability.model.{AbilityCredit, AbilityCreditApply}
import org.openurp.prac.ability.service.AbilityCreditApplyService
import org.openurp.starter.web.support.ProjectSupport

import java.time.LocalDate

class CreditAction extends RestfulAction[AbilityCredit], ProjectSupport, ExportSupport[AbilityCredit] {

  var abilityCreditApplyService: AbilityCreditApplyService = _

  override def indexSetting(): Unit = {
    super.indexSetting()

    given project: Project = getProject

    put("departs", getDeparts)
  }

  override def getQueryBuilder: OqlBuilder[AbilityCredit] = {
    val query = super.getQueryBuilder
    query.where("abilityCredit.std.project=:project", getProject)
    query
  }

  def details(): View = {
    val stdId = getLongId("std")
    val applies = entityDao.findBy(classOf[AbilityCreditApply], "std.id", stdId)
    put("applies", applies)
    forward()
  }

  /** 在籍的有申请信息的年级
   *
   * @param project
   * @return
   */
  private def getGrades(project: Project): Seq[Grade] = {
    val q = OqlBuilder.from[Grade](classOf[AbilityCreditApply].getName, "y")
    q.where("y.std.project=:project", project)
    q.where(":today between y.std.beginOn and y.std.endOn", LocalDate.now)
    q.select("distinct y.std.state.grade")
    entityDao.search(q).sortBy(_.code)
  }

  /** 认定学分统计
   *
   * @return
   */
  def stat(): View = {
    val project = getProject
    val grades = getGrades(project)

    //增补缺失的数据
    grades foreach { grade =>
      abilityCreditApplyService.init(project, grade)
    }

    val q2 = OqlBuilder.from[Array[Any]](classOf[AbilityCredit].getName, "credit")
    q2.where("credit.std.registed=true")
    q2.where("credit.std.project=:project", project)
    q2.where(":today between credit.std.beginOn and credit.std.endOn", LocalDate.now)
    q2.groupBy("credit.std.state.grade.id,credit.std.state.department.id,credit.credits")
    q2.select("credit.std.state.grade.id,std.state.department.id,credit.credits,count(*)")
    val datas = Collections.newBuffer[Matrix.Row]

    entityDao.search(q2) foreach { d =>
      val gradeId = d(0).asInstanceOf[Long] //年级
      val departId = d(1).asInstanceOf[Int] //部门ID
      val credits = d(2).asInstanceOf[Number].floatValue() //学分
      val count = d(3).asInstanceOf[Number].intValue() //人数
      val data = Matrix.Row(Seq(gradeId, departId, credits), Array(count))
      datas.addOne(data)
    }

    val columns = Columns(entityDao)
    columns.add("grade", "年级", datas, classOf[Grade])
    columns.add("depart", "院系", datas, classOf[Department])
    columns.add("credit", "学分", datas)
    val matrix = new Matrix(columns.build(), datas)
    put("matrixes", matrix.split("grade"))
    put("grades", grades)
    if (!BeanInfos.cached(classOf[Matrix])) {
      BeanInfos.of(classOf[Matrix])
    }
    forward()
  }

  def stdList(): View = {
    val q = OqlBuilder.from(classOf[AbilityCredit], "credit")
    populateConditions(q)
    q.limit(1, 100)
    val credits = entityDao.search(q)
    put("credits", credits)
    forward()
  }
}
