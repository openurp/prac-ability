[#ftl]
[@b.head/]
  [@b.toolbar title="能力素质拓展课证书新增/修改"]
    bar.addBack();
  [/@]
<div class="container" style="width:95%">
[#include "step.ftl"/]
[@displayStep ['填写登记信息','学院初审','${apply.std.department.name}审核','教务处备案'] 0/]
  [@b.card]
    [@b.card_header]<h5> 能力素质拓展课证书登记 (${apply.certificate.name})</h5>[/@]
    [@b.card_body class="pt-0"]
      [@b.form name="exemptionForm" action="!save" theme="list"  onsubmit="checkAttachment" enctype="multipart/form-data"]
        [@b.field label="学生"]${(apply.std.code)!} ${(apply.std.name)!}[/@]
        [@b.field label="年级"]${apply.std.grade.code}[/@]
        [@b.field label="学院、专业"]${(apply.std.department.name)!} ${(apply.std.major.name)!}[/@]
        [@b.field label="证书名称"]${apply.certificate.name}[/@]
        [@b.field label="审核部门"]${apply.std.state.department.name}[/@]
        [#if certificate.subjects??][#assign scope="0:按课程认定,1:按照整个证书认定"][#else][#assign scope="1:按照整个证书认定"][/#if]
        [@b.textfield name="apply.subjects" maxlength="100" label="通过课程" required="true" value=apply.subjects!
           style="width:200px" comment=certificate.subjects placeholder="全部完成可以填写全部"/]
        [@b.date label="获得年月" name="apply.acquiredOn" value=apply.acquiredOn! required="true" format="yyyy-MM"
               placeholder="证书课程获得年月/证书获得年月" comment="证书课程获得年月/证书获得年月" /]
        [#if certificate.subjects??]
          [@b.textfield name="apply.certificateNo" label="证书编号" value=apply.certificateNo! maxlength="100" required="false" comment="按照课程认定时选填"/]
        [#else]
          [@b.textfield name="apply.certificateNo" label="证书编号" value=apply.certificateNo! maxlength="100" required="true" /]
        [/#if]

        [@b.field label="证明材料" required="true"]
         <input type="file" name="attachment">
         [#if apply.attachmentPath??]已上传[/#if]
         <span style="color:blue">为方便审核，请上传图片类型的证明材料</span>
        [/@]
        [@b.textarea name="apply.reasons" label="其他说明" value=apply.reasons maxlength="500" rows="5" style="width:70%" required="false"/]
        [@b.formfoot]
          <input type="hidden" name="apply.id" value="${apply.id!}"/>
          <input type="hidden" name="certificate.id" value="${certificate.id}"/>
          <input type="hidden" name="projectId" value="${(apply.std.project.id)!}"/>
          [@b.submit value="提交申请" /]
        [/@]
        <script>
          function checkAttachment(form){
            [#if !apply.attachmentPath??]
            if("" == form['attachment'].value){
              alert("缺少成绩证明材料");
              return false;
            }
            [/#if]
            return true;
          }
        </script>
      [/@]
    [/@]
  [/@]
</div>
[@b.foot/]
