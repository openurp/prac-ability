[#ftl]
[#assign std= apply.std/]
<table class="infoTable">
    <tr>
      <td class="title">证书名称：</td>
      <td>${apply.certificate.name}</td>
      <td class="title">证书内课程：</td>
      <td>${apply.subjects}</td>
      <td class="title">填写时间：</td>
      <td>${(apply.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">获得年月：</td>
      <td>${apply.acquiredIn}</td>
      <td class="title">证书编号：</td>
      <td>${apply.certificateNo!}</td>
      <td class="title">认定范围：</td>
      <td>${apply.finished?string("按照证书认定","按照课程认定")}</td>
    </tr>
    <tr>
      <td class="title">成绩材料：</td>
      <td>
        [#if attachmentPaths.get(apply)??]
        <a href="${attachmentPaths.get(apply)}" target="_blank"><i class="fa fa-paperclip"></i>下载附件</a>
        [#else]--[/#if]
      </td>
      <td class="title">审核部门：</td>
      <td colspan="3">${(apply.auditDepart.name)!}</td>
    </tr>
    <tr>
      <td class="title">申请说明：</td>
      <td colspan="5">${apply.reasons!}</td>
    </tr>
    <tr>
      <td class="title">审核状态：</td>
      <td colspan="5"><span class="[#if apply.status=="审核通过"]text-success[#else]text-danger[/#if]">${apply.status} ${apply.auditOpinion!}</span></td>
    </tr>
  </table>
