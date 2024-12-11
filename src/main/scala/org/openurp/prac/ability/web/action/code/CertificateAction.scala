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

package org.openurp.prac.ability.web.action.code

import org.beangle.commons.activation.MediaTypes
import org.beangle.data.dao.OqlBuilder
import org.beangle.doc.excel.schema.ExcelSchema
import org.beangle.doc.transfer.importer.ImportSetting
import org.beangle.doc.transfer.importer.listener.ForeignerListener
import org.beangle.webmvc.annotation.response
import org.beangle.webmvc.support.action.{ImportSupport, RestfulAction}
import org.beangle.webmvc.support.helper.QueryHelper
import org.beangle.webmvc.view.Stream
import org.openurp.code.edu.model.{Certificate, CertificateCategory}
import org.openurp.code.service.CodeService
import org.openurp.prac.ability.web.helper.CertificateImportListener

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class CertificateAction extends RestfulAction[Certificate], ImportSupport[Certificate] {
  var codeService: CodeService = _

  override protected def editSetting(cert: Certificate): Unit = {
    put("categories", entityDao.getAll(classOf[CertificateCategory]))
  }

  override protected def getQueryBuilder: OqlBuilder[Certificate] = {
    val query = super.getQueryBuilder
    QueryHelper.addActive(query, getBoolean("active"))
    query
  }

  @response
  def downloadTemplate(): Any = {
    val categories = codeService.get(classOf[CertificateCategory]).map(x => x.code + " " + x.name)

    val schema = new ExcelSchema()
    val sheet = schema.createScheet("数据模板")
    sheet.title("证书信息模板")
    sheet.remark("特别说明：\n1、不可改变本表格的行列结构以及批注，否则将会导入失败！\n2、必须按照规格说明的格式填写。\n3、可以多次导入，重复的信息会被新数据更新覆盖。\n4、保存的excel文件名称可以自定。")
    sheet.add("证书代码", "certificate.code").length(10).required().remark("≤10位")
    sheet.add("证书名称", "certificate.name").length(100).required()
    sheet.add("证书类别", "certificate.category.code").ref(categories).required()
    sheet.add("发证机构代码", "certificate.institutionCode").length(30)
    sheet.add("发证机构名称", "certificate.institutionName").length(100)
    sheet.add("证书内课程", "certificate.subjects").length(500)
    val os = new ByteArrayOutputStream()
    schema.generate(os)
    Stream(new ByteArrayInputStream(os.toByteArray), MediaTypes.ApplicationXlsx, "证书模板.xlsx")
  }

  protected override def configImport(setting: ImportSetting): Unit = {
    val fl = new ForeignerListener(entityDao)
    fl.addForeigerKey("name")
    setting.listeners = List(fl, new CertificateImportListener(entityDao))
  }
}
