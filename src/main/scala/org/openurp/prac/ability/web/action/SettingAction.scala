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

import org.beangle.commons.activation.MediaTypes
import org.beangle.doc.excel.schema.ExcelSchema
import org.beangle.doc.transfer.importer.ImportSetting
import org.beangle.doc.transfer.importer.listener.ForeignerListener
import org.beangle.ems.app.Ems
import org.beangle.webmvc.annotation.{ignore, response}
import org.beangle.webmvc.support.action.{ImportSupport, RestfulAction}
import org.beangle.webmvc.view.{Stream, View}
import org.openurp.base.edu.model.Major
import org.openurp.base.model.Department
import org.openurp.code.edu.model.Certificate
import org.openurp.code.service.CodeService
import org.openurp.prac.ability.config.{AbilityCreditConfig, AbilityCreditSetting}
import org.openurp.prac.ability.web.helper.{CertificateImportListener, SettingImportListener}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class SettingAction extends RestfulAction[AbilityCreditSetting], ImportSupport[AbilityCreditSetting] {
  var codeService: CodeService = _

  @ignore
  protected override def simpleEntityName: String = "setting"

  protected override def editSetting(setting: AbilityCreditSetting): Unit = {
    val configs = entityDao.getAll(classOf[AbilityCreditConfig])
    //refresh setting config
    val config = configs.head
    setting.config = config

    if (!setting.persisted) {
      setting.credits = 1.0f
      setting.collegeReviewRequired = true
    }
    put("project", config.project)
    val certificates = codeService.get(classOf[Certificate]).toBuffer
    certificates --= config.settings.map(_.certificate)
    put("certificates", certificates)
    put("majors", entityDao.findBy(classOf[Major], "project", config.project))
    put("urp", Ems)

    super.editSetting(setting)
  }

  override protected def saveAndRedirect(entity: AbilityCreditSetting): View = {
    val majorIds = getLongIds("major")
    entity.majors.clear();
    if (majorIds.nonEmpty) {
      entity.majors ++= entityDao.find(classOf[Major], majorIds)
    }

    val departIds = getIntIds("depart")
    entity.departs.clear();
    if (departIds.nonEmpty) {
      entity.departs ++= entityDao.find(classOf[Department], departIds)
    }
    entity.special = entity.departs.nonEmpty
    super.saveAndRedirect(entity)
  }

  @response
  def downloadTemplate(): Any = {
    val certificates = codeService.get(classOf[Certificate]).map(x => x.code + " " + x.name)

    val schema = new ExcelSchema()
    val sheet = schema.createScheet("数据模板")
    sheet.title("证书信息模板")
    sheet.remark("特别说明：\n1、不可改变本表格的行列结构以及批注，否则将会导入失败！\n2、必须按照规格说明的格式填写。\n3、可以多次导入，重复的信息会被新数据更新覆盖。\n4、保存的excel文件名称可以自定。")
    sheet.add("证书名称", "setting.certificate.code").ref(certificates).required()
    sheet.add("学院名称", "departNames")
    sheet.add("专业名称", "majorNames")
    sheet.add("备注", "setting.remark").length(100)
    val os = new ByteArrayOutputStream()
    schema.generate(os)
    Stream(new ByteArrayInputStream(os.toByteArray), MediaTypes.ApplicationXlsx, "认定设置.xlsx")
  }

  protected override def configImport(setting: ImportSetting): Unit = {
    val fl = new ForeignerListener(entityDao)
    fl.addForeigerKey("name")
    val config = entityDao.getAll(classOf[AbilityCreditConfig]).head
    setting.listeners = List(fl, new SettingImportListener(config, entityDao))
  }
}
