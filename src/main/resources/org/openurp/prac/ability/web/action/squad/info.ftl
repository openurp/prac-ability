<style>
  .info-panel-header{
    border-bottom: 1px solid #048BB3;
    color:#048BB3;
    margin-bottom:0px;
    margin-top:10px;
    padding: 0px 0px 1px 0px;
  }
  .info-panel-title{
    font-size: 0.875rem;
  }
  table.info-table{
    table-layout:fixed;
  }
  table.info-table td.title {
    padding: 0.2rem 0rem;
    text-align:right;
    color: #6c757d !important;
  }
</style>
  <div class="card card-primary card-outline">
    <div class="card-header">
      <h4 class="card-title">${squad.name}</h4>
      <div class="card-tools">
        [@b.a href="!download?squad.id="+squad.id target="_blank"]<i class="fa-solid fa-file-excel"></i>下载数据[/@]
      </div>
    </div>
    <div class="card-body" style="padding-top: 0px;">
      <h6 class="info-panel-header"><span class="info-panel-title">基本信息</span></h6>
      <table class="table table-sm info-table">
        <colgroup>
          <col width="7%"/>
          <col width="18%"/>
          <col width="7%"/>
          <col width="18%"/>
          <col width="7%"/>
          <col width="18%"/>
          <col width="7%"/>
          <col width="18%"/>
        </colgroup>
        <tr>
          <td class="title">年级:</td>
          <td>${(squad.grade)!}</td>
          <td class="title">名称:</td>
          <td>${(squad.name)!}</td>
          <td class="title">层次:</td>
          <td>${(squad.level.name)!}</td>
          <td class="title">学生类别:</td>
          <td>${(squad.stdType.name)!}</td>
        </tr>
        <tr>
          <td class="title">院系:</td>
          <td>${(squad.department.name)!}</td>
          <td class="title">专业:</td>
          <td>${(squad.major.name)!}</td>
          <td class="title">方向:</td>
          <td>${(squad.direction.name)!}</td>
          <td class="title">校区:</td>
          <td>${(squad.campus.name)!}</td>
        </tr>
      </table>
      <h6 class="info-panel-header"><span class="info-panel-title">认定证书和学分统计</span></h6>
      [@b.tabs]
        [@b.tab label="认定学分统计"]
          [#assign completeCount=0/]
          [#list credits as c]
            [#if c.credits > 0.99]
              [#assign completeCount=completeCount+1/]
            [/#if]
          [/#list]
          [#if credits?size>0]
          <div class="progress">
            <div class="progress-bar bg-success" role="progressbar" aria-valuenow="${completeCount/credits?size*100}"
            aria-valuemin="0" aria-valuemax="100" style="width:${completeCount/credits?size*100}%">
              ${(completeCount/credits?size)?string.percent}
            </div>
          </div>
          [/#if]
          [@b.grid items=credits var="credit" theme="mini"]
            [@b.row]
              [@b.col title="序号" width="5%"]${credit_index+1}[/@]
              [@b.col property="std.code" title="学号" width="12%"/]
              [@b.col property="std.name" title="姓名" width="10%"]
                ${credit.std.name}
              [/@]
              [@b.col title="性别" property="std.gender.name" width="12%"/]
              [@b.col title="专业和方向" property="std.state.major.name"]
                ${credit.std.state.major.name} ${(credit.std.state.direction.name)!}
              [/@]
              [@b.col title="学分" property="credits" width="6%"]
                [#if credit.credits<1]
                  <span style="color:red">${credit.credits}</span>
                [#else]
                  ${credit.credits}
                [/#if]
              [/@]
              [@b.col title="学籍状态" property="std.state.status.name" width="10%"/]
            [/@]
          [/@]
        [/@]

        [@b.tab label="申请认定证书"]
          [@b.grid items=applies var="apply" theme="mini"]
            [@b.row]
              [@b.col property="std.code" title="学号" width="10%"/]
              [@b.col property="std.name" title="姓名" width="8%"/]
              [@b.col property="certificate.name" title="证书" width="30%"/]
              [@b.col property="subjects" title="证书内课程"/]
              [@b.col property="acquiredIn" title="获得年月" width="6%"]${(apply.acquiredIn?string("yyyy-MM"))!"--"}[/@]
              [@b.col title="学分" property="credits" sortable="false" width="4%"/]
              [@b.col property="status" title="状态" width="13%"]${apply.status}[/@]
              [@b.col property="updatedAt" title="提交时间" width="13%"]${apply.updatedAt?string("MM-dd HH:mm")}[/@]
            [/@]
          [/@]
        [/@]

      [/@]
    </div>
  </div>
