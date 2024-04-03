[#ftl]
[@b.head/]
[@b.grid items=certificates var="certificate"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
    bar.addItem("导入",action.method('importForm'));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="5%" property="code" title="代码"]${certificate.code}[/@]
    [@b.col property="name" title="名称"/]
    [@b.col width="30%" property="institutionName" title="发证机构"/]
    [@b.col width="20%" property="category.name" title="证书类型"/]
    [@b.col width="20%" property="subjects" title="证书内课程"/]
  [/@]
[/@]
[@b.foot/]
