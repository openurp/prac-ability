[#ftl]
[@b.head/]
[@b.grid items=settings var="setting" ]
    [@b.gridbar]
        bar.addItem("${b.text("action.add")}",action.add());
        bar.addItem("${b.text("action.edit")}",action.edit());
        bar.addItem("${b.text("action.delete")}",action.remove());
        bar.addItem("导入",action.method('importForm'));
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="certificate.name" title="证书名称" width="20%"/]
        [@b.col property="credits" title="学分" width="5%"/]
        [@b.col title="适用学院" width="10%"]
          [#list setting.departs as d]${d.shortName!d.name}[#sep]&nbsp;[/#list]
        [/@]
        [@b.col property="certificate.majors" title="适用专业" width="20%"]
          [#list setting.majors as major]${major.name}[#sep]&nbsp;[/#list]
        [/@]
        [@b.col property="certificate.subjects" title="证书内课程" width="20%"/]
        [@b.col property="remark" title="备注"]
          <span>${setting.remark!}<span>
        [/@]
    [/@]
[/@]

[@b.foot/]
