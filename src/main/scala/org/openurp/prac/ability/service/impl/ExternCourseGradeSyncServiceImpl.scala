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

import org.beangle.commons.bean.Initializing
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.beangle.ems.app.datasource.AppDataSourceFactory
import org.openurp.base.edu.model.Course
import org.openurp.base.model.Semester
import org.openurp.base.std.model.Student
import org.openurp.code.edu.model.CourseTakeType
import org.openurp.prac.ability.service.ExternCourseGradeSyncService

import java.time.Instant

class ExternCourseGradeSyncServiceImpl extends ExternCourseGradeSyncService, Initializing, Logging {

  var jdbcExecutor: JdbcExecutor = _

  override def init(): Unit = {
    try {
      val ds = new AppDataSourceFactory()
      ds.name = "oracle"
      ds.init()
      jdbcExecutor = new JdbcExecutor(ds.result)
    } catch
      case e: Exception =>
        e.printStackTrace()
        logger.error("Cannot find oracle datasource")
  }

  override def remove(courseGradeId: Long): Unit = {
    if (null != jdbcExecutor) {
      jdbcExecutor.update(s"delete from edu_grade.course_grades where id=${courseGradeId}")
    }
  }

  override def add(std: Student, semester: Semester, course: Course): Long = {
    if (null != jdbcExecutor) {
      val rs = jdbcExecutor.query("select urp.next_id('edu_grade.course_grades') from dual")
      val id = rs.head(0).asInstanceOf[Number].longValue()
      jdbcExecutor.update("insert into edu_grade.course_grades(id,passed,status,updated_at,mark_style_id,project_id," +
        "semester_id,std_id,course_id,course_take_type_id,course_type_id,exam_mode_id,free_listening,score_text)" +
        " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
        id, 1, 2, Instant.now, 1, std.project.id,
        semester.id, std.id, course.id, 1, course.courseType.id, 1, 1, "合格")
      id
    } else {
      0
    }
  }
}
