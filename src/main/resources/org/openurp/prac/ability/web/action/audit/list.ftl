[#ftl]
[@b.head/]
[@b.grid items=applies var="apply"]
    [@b.gridbar]
      bar.addItem("单个审核",action.single('auditForm'));
      bar.addItem("批量同意",action.multi('batchAudit',"确认审核通过？","&passed=1"));
      bar.addItem("批量驳回",action.multi('batchAudit',"确认审核不通过，驳回到学生重修修改?","&passed=0"));
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.code" title="学号" width="10%"/]
        [@b.col property="std.name" title="姓名" width="8%"]
           <a href="javascript:void(0)" onclick="singleAudit(this)" title="点击进入审批">${apply.std.name}</a>
        [/@]
        [@b.col property="std.state.grade.code" title="年级" width="6%"/]
        [@b.col property="certificate.name" title="证书" width="22%"/]
        [@b.col property="subjects" title="证书内课程"/]
        [@b.col property="std.state.squad.name" title="班级" width="12%"]
          <div class="text-ellipsis">${(apply.std.state.squad.name)!}</div>
        [/@]
        [@b.col property="acquiredOn" title="获得年月" width="7%"]${(apply.acquiredOn?string("yyyy-MM"))!"--"}[/@]
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
