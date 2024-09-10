[#ftl]
[@b.head/]
<div class="container" style="width:95%">
  <nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
      <div class="navbar-header">
        <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>能力素质拓展证书登记</a>
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

    <div style="background-color: #e9ecef;border-radius: .3rem;padding: 2rem 2rem;margin-bottom: 2rem;">
      <h4>能力素质拓展证书登记</h4>
      <div>请选择以下证书，进行登记。</div>
    </div>

  [@b.card class="card-info card-outline"]
     [@b.card_header]<h5>资格证书</h5>

             <div class="input-group input-group-sm">
               <input class="form-control form-control-navbar" type="search" name="q" value="" oninput="return search(this.value);" aria-label="Search" placeholder="输入搜索关键词" autofocus="autofocus">
               <div class="input-group-append">
                 <button class="input-group-text" type="submit">
                   <i class="fas fa-search"></i>
                 </button>
               </div>
             </div>
     [/@]
     <div class="container-fluid" style="display:flex;" id="certificate_list">
        [#list certificates?keys?sort_by('code') as k]
          [#assign v = certificates.get(k)/]
          <div style="width:360px;max-height:500px;overflow:auto;margin-bottom:10px" >
            <table class="table table-hover table-sm">
              <thead>
                <tr><th>${k.name}(${v?size}项)</th></tr>
              </thead>
              <tbody>
              [#list v?sort_by(["name"]) as cert]
              <tr><td>[@b.a class="dropdown-item" title="用"+cert.name+"申请认定"
                          href="!edit?projectId="+project.id+"&certificate.id="+cert.id]${cert.name}[/@]
              </td></tr>
              [/#list]
              </tbody>
            </table>
          </div>
        [/#list]
     </div>
  [/@]
</div> [#--container--]
<script>
   function removeApply(elem){
     if(confirm("确定删除?")){
       return bg.Go(elem,null)
     }else{
       return false;
     }
   }
   function search(q){
    q= q.toUpperCase();
    jQuery("#certificate_list table tbody tr").each(function(i,e){
      var tds = jQuery(e).children("td");
      var matched = (q=="");
      if(!matched){
        for(var idx=0;idx < tds.length;idx++){
          if(q=='' || tds[idx].innerHTML.indexOf(q)>-1){
            matched=true;
            break;
          }
        }
      }
      if(matched){
        jQuery(e).show();
      }else{
        jQuery(e).hide();
      }
    });
    return false;
   }
</script>
[@b.foot/]
