[#ftl]
[@b.head/]
[@b.grid items=applies var="apply"]
    [@b.gridbar]
      bar.addItem("单个审核",action.single('auditForm'));
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.code" title="学号" width="10%"/]
        [@b.col property="std.name" title="姓名" width="8%"]
           <a href="javascript:void(0)" onclick="singleAudit(this)" title="点击进入审批">${apply.std.name}</a>
        [/@]
        [@b.col property="std.state.grade.code" title="年级" width="6%"/]
        [@b.col property="certificate.name" title="证书" width="18%"/]
        [@b.col property="subjects" title="证书内课程"/]
        [@b.col property="std.state.department.name" title="院系" width="8%"]
          ${apply.std.state.department.shortName!apply.std.state.department.name}
        [/@]
        [@b.col property="acquiredOn" title="获得年月" width="8%"]${(apply.acquiredOn?string("yyyy-MM"))!"--"}[/@]
        [@b.col title="学分" property="credits" sortable="false" width="6%"/]
        [@b.col property="status" title="状态" width="9%"]${apply.status}[/@]
        [@b.col property="updatedAt" title="提交时间" width="9%"]${apply.updatedAt?string("yy-MM-dd HH:mm")}[/@]
    [/@]
[/@]
<script>
   function singleAudit(elem){
      jQuery(elem).parents("tr").children("td:first").children("input").prop("checked","checked")
      action.single("auditForm").func();
   }
</script>
[@b.foot/]
