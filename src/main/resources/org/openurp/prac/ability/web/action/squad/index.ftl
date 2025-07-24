[#ftl]
[@b.head/]
[@b.toolbar title="素质能力拓展课班级查询"/]
<div class="container-fluid">
  <div class="row">
    <div class="col-2" id="accordion">
       [#if squads?size>0]
       [#assign firstSquad = squads?first/]
       <div class="card card-primary card-outline">
         <div class="card-header" id="stat_header_1">
           <h5 class="mb-0">
              <button class="btn btn-link" data-toggle="collapse" data-target="#stat_body_1" aria-expanded="true" aria-controls="stat_body_1" style="padding: 0;">
                我的班级
              </button>
           </h5>
         </div>
         <div id="stat_body_1" class="collapse show" aria-labelledby="stat_header_1" data-parent="#accordion">
           <div class="card-body" style="padding-top: 0px;">
             <table class="table table-hover table-sm" style="font-size:0.9em;">
               <tbody>
               [#list squads as squad]
                  [@displaySquad squad/]
                [/#list]
               </tbody>
             </table>
           </div>
         </div>
       </div>
       [/#if]
     </div><!--end col-2-->

     [#if firstSquad??]
     [@b.div class="col-10" id="course_list" href="!info?id="+firstSquad.id/]
     [#else]
     <div>你还没有代班级</div>
     [/#if]
  </div><!--end row-->
</div><!--end container-->

[#macro displaySquad squad]
<tr>
 <td>
   [@b.a href="!info?id="+squad.id target="course_list"]<span>${squad.name}</span>[/@]
 </td>
</tr>
[/#macro]
[@b.foot/]
