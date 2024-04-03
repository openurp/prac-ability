[#ftl]
[@b.head/]
<div class="container" style="width:95%">
  <nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
      <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>能力素质拓展学分认定申请</a>
      </div>
    </div>
  </nav>
  [@b.messages slash="4"/]

[#if applies?size>0]
  [#list applies as apply]
  [#assign settingId=0/]
  [#if config??]
    [#list config.settings as setting]
      [#if setting.certificate=apply.certificate]
        [#assign settingId=setting.id/]
        [#break/]
      [/#if]
    [/#list]
  [/#if]
  [#assign title]
     <i class="fas fa-school"></i> &nbsp;${apply.certificate.name}<span style="font-size:0.8em">(${apply.acquiredOn?string("yyyy-MM")})</span>
     [#if editables?seq_contains(apply.status)]
       [#if settingId>0][@b.a href="!edit?apply.id="+apply.id+"&settingId="+settingId class="btn btn-sm btn-info"]<i class="far fa-edit"></i>修改[/@][/#if]
       [@b.a href="!remove?apply.id="+apply.id+"&projectId="+student.project.id onclick="return removeApply(this);" class="btn btn-sm btn-warning"]<i class="fas fa-times"></i>删除申请[/@]
     [#else]${apply.status}
     [/#if]
  [/#assign]
  [@b.card class="card-info card-outline"]
     [@b.card_header]
      ${title}
     [/@]
     [#include "applyInfo.ftl"/]
  [/@]
  [/#list]
[/#if]

  [#if !config??]
    <div style="background-color: #e9ecef;border-radius: .3rem;padding: 2rem 2rem;margin-bottom: 2rem;">
      <h4>能力素质拓展学分认定</h4>
      <pre style="border-bottom: 1px solid rgba(0,0,0,.125);white-space: pre-wrap;color:red">申请业务还未开放</pre>
    </div>
  [/#if]
  [#if config??]
    <div style="background-color: #e9ecef;border-radius: .3rem;padding: 2rem 2rem;margin-bottom: 2rem;">
      <h4>能力素质拓展学分认定</h4>
      <pre style="border-bottom: 1px solid rgba(0,0,0,.125);white-space: pre-wrap;">${config.notice!}</pre>
      <div>请选择以下证书，进行认定。</div>
    </div>
  [/#if]

  [#if specials?size>0]
  [@b.card class="card-info card-outline"]
     [@b.card_header]<h5>学科专业资格证书</h5>[/@]
     <div  class="container-fluid" style="display:flex;">
        [#list specials?keys as k]
          [#assign v = specials.get(k)/]
          <div style="width:360px;max-height:500px;overflow:auto;margin-bottom:10px" >
            <table class="table table-hover table-sm">
              <thead>
                <tr><th>${k.name}(${v?size}项)</th></tr>
              </thead>
              <tbody>
              [#list v?sort_by(["certificate","name"]) as setting]
              <tr><td>[@b.a class="dropdown-item" title="用"+setting.certificate.name+"申请认定"
                          href="!edit?projectId="+project.id+"&settingId="+setting.id]${setting.certificate.name}[/@]
              </td></tr>
              [/#list]
              </tbody>
            </table>
          </div>
        [/#list]
     </div>
  [/@]
  [/#if]

  [#if commons?size>0]
  [@b.card class="card-info card-outline"]
     [@b.card_header]<h5>全校通用职业资格证书</h5>[/@]
     <div  class="container-fluid" style="display:flex;">
        [#list commons?keys as k]
          [#assign v = commons.get(k)/]
          <div style="width:360px;max-height:500px;overflow:auto;margin-bottom:10px" >
            <table class="table table-hover table-sm">
              <thead>
                <tr><th>${k.name}(${v?size}项)</th></tr>
              </thead>
              <tbody>
              [#list v?sort_by(["certificate","name"]) as setting]
              <tr><td>[@b.a class="dropdown-item" title="用"+setting.certificate.name+"申请认定"
                          href="!edit?projectId="+project.id+"&settingId="+setting.id]${setting.certificate.name}[/@]
              </td></tr>
              [/#list]
              </tbody>
            </table>
          </div>
        [/#list]
     </div>
  [/@]
  [/#if]

</div> [#--container--]
<script>
   function removeApply(elem){
       if(confirm("确定删除?")){
         return bg.Go(elem,null)
       }else{
         return false;
       }
   }
</script>
[@b.foot/]
