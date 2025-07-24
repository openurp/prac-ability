[#ftl]
[@b.head/]
[@b.grid items=abilityCredits var="abilityCredit"]
    [@b.gridbar]
      bar.addItem("${b.text("action.export")}",action.exportData("std.code:学号,std.name:姓名,std.state.grade.name:年级,"+
                "std.level.name:培养层次,std.state.department.name:院系,std.state.major.name:专业,std.state.squad.name:班级,"+
                "std.state.squad.mentor.name:辅导员,credits:学分",null,'fileName=认定学分结果'));
    [/@]
    [@b.row]
        [@b.col property="std.code" title="学号" width="12%"/]
        [@b.col property="std.name" title="姓名" width="10%"]
          [#if abilityCredit.credits>0]
          <a href="${b.url('!details?std.id='+abilityCredit.std.id)}" data-toggle="modal" data-target="#applyList">
           ${abilityCredit.std.name}
          </a>
          [#else]
          ${abilityCredit.std.name}
          [/#if]
        [/@]
        [@b.col title="年级" property="std.state.grade.name" width="6%"/]
        [@b.col title="培养层次" property="std.level.name" width="7%"/]
        [@b.col property="std.state.department.name" title="院系" width="6%"]
          ${abilityCredit.std.state.department.shortName!abilityCredit.std.state.department.name}
        [/@]
        [@b.col title="专业" property="std.state.major.name" width="14%"/]
        [@b.col title="班级" property="std.state.squad.name"/]
        [@b.col title="学分" property="credits" width="6%"/]
        [@b.col property="updatedAt" title="更新时间" width="7%"]${abilityCredit.updatedAt?string("MM-dd HH:mm")}[/@]
    [/@]
[/@]
  [@b.dialog id="applyList" title="学生申请认定明细" /]
[@b.foot/]
