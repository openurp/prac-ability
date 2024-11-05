[#ftl]
[@b.toolbar title="${apply.std.name}的认定申请"]
  bar.addBack();
[/@]
[#assign std= apply.std/]
[@b.card class="card-info card-outline"]
  [@b.card_header]
    <i class="fas fa-school"></i>&nbsp;${apply.certificate.name}<span style="font-size:0.8em">(${apply.acquiredOn?string("yyyy-MM")})</span>
  [/@]

  <table class="infoTable">
    <tr>
      <td class="title">学生：</td>
      <td>${std.code} ${std.name}</td>
      <td class="title">年级：</td>
      <td>${std.state.grade.name}</td>
      <td class="title">学院：</td>
      <td>${std.department.name}</td>
    </tr>
    <tr>
      <td class="title">培养层次：</td>
      <td>${std.level.name}</td>
      <td class="title">专业/方向：</td>
      <td>${(std.state.major.name)!} ${(std.state.direction.name)!}</td>
      <td class="title">班级：</td>
      <td>${(std.state.squad.name)!}</td>
    </tr>
    <tr>
      <td class="title">证书名称：</td>
      <td>${apply.certificate.name}</td>
      <td class="title">证书内课程：</td>
      <td>${apply.subjects!}</td>
      <td class="title">填写时间：</td>
      <td>${(std.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">获得年月：</td>
      <td>${apply.acquiredOn?string("yyyy-MM")}</td>
      <td class="title">证书编号：</td>
      <td>${apply.certificateNo!}</td>
      <td class="title">认定范围：</td>
      <td>${apply.finished?string("按照证书认定","按照课程认定")}</td>
    </tr>
    <tr>
      <td class="title">成绩材料：</td>
      <td>
        <a href="${attachmentPath}" target="_blank"><i class="fa fa-paperclip"></i>下载附件</a>
      </td>
      <td class="title">审核部门：</td>
      <td>${apply.auditDepart.name}</td>
      <td class="title">审核状态：</td>
      <td><span class="[#if apply.status=="通过"]text-success[#else]text-danger[/#if]">${apply.status} ${apply.auditOpinion!}</span></td>
    </tr>
    <tr>
      <td class="title">申请理由：</td>
      <td colspan="5">${apply.reasons!}</td>
    </tr>
    [#if attachmentPath?contains(".jpg")||attachmentPath?contains(".png")||attachmentPath?contains(".jpeg")]
    <tr>
      <td class="title">材料图片：</td>
      <td colspan="5"><image src="${attachmentPath}" style="width:400px"/></td>
    </tr>
    [/#if]
  </table>
  [#if logHref??]
  <table id="logs" class="grid-table">
    <thead class="grid-head">
      <tr>
        <th style="width: 15%;">操作人</th>
        <th>操作内容</th>
        <th style="width: 18%;">操作时间</th>
        <th style="width: 10%;">IP</th>
        <th style="width: 20%;">浏览器</th>
      </tr>
    </thead>
    <tbody class="grid-body">
    </tbody>
  </table>
  <script>
  jQuery.ajax({
    url: "${logHref}",
    headers:{"Accept":"application/json"},
    [#noparse]
    success:function (datas){
       var tbody = jQuery("#logs tbody");
       for(var i in datas){
         var data = datas[i];
         tbody.append(`<tr><td>${data.operator}</td><td>${data.summary}</td><td>${data.operateAt}</td><td>${data.ip}</td><td>${data.agent}</td></tr>`);
       }
    }
    [/#noparse]
  });
  </script>
  [/#if]
  [#if editable]
  [@b.form name="applyForm" action="!audit" theme="list"]
    [@b.field label="认定学分"]${apply.credits!}分[/@]
    [@b.radios name="passed" value="1" label="是否同意" required="true" onclick="resetOpinion(this)"/]
    [@b.textarea name="auditOpinion" id="auditOpinion" required="true" rows="4" style="width:80%" label="审核意见" placeholder="请填写意见" value="同意认定"/]
    [@b.formfoot]
    <input name="id" value="${apply.id}" type="hidden"/>
    [@b.submit value="提交"/]
    [/@]
  [/@]
  <script>
    function resetOpinion(ele){
      var reject=jQuery(ele).val()=='0';
      if(reject) {
        jQuery("#auditOpinion").val('');
      }else{
        jQuery("#auditOpinion").val('同意认定');
      }
    }
  </script>
  [/#if]
[/@]
