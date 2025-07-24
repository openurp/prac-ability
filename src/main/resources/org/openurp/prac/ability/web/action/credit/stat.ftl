[@b.head/]
[@b.toolbar title="能力素质拓展课学分认定统计"]
  bar.addBack();
[/@]
  [#function format_percent(n)]
    [#return (n*100*1.0)?string+"%"/]
  [/#function]
  [#macro displayCounter counter,params]
    [#if counter?? && counter?size>0]
      [#if params?size >0 && counter?first < 20]
        [#assign paramStr][#list params as k,v]${k}=${v}[#sep]&[/#list][/#assign]
        <a href="${b.url('!stdList?orderBy=credit.std.code&'+paramStr)}" data-toggle="modal" data-target="#stdList" title="学生信息 ${counter?first}人">${counter?first}</a>
      [#else]
        ${counter?first}
      [/#if]
    [/#if]
  [/#macro]

  [#macro displayMatrix grade dy dxs dyLabel dxLabels,paramNames]
    <h2 id="grade_${grade.id}" style="text-align: center;caption-side: top;padding-bottom: 0.25rem;font-size: 0.9rem;margin: 0px;" class="text-muted" >${grade.name}级学生认定学分分布</h2>
    <table class="table table-bordered table-striped table-sm" style="text-align: center;">
      <thead>
        <tr>
          <th width="5%" rowspan="2" style="vertical-align: middle;">序号</th>
          <th width="15%" rowspan="2" style="vertical-align: middle;">${dyLabel}</th>
          [#list dxLabels as dxLabel]
          [#assign dx = dxs[dxLabel_index]/]
          <th colspan="${matrix.getColumn(dx).values?size}" style="vertical-align: middle;">${dxLabel}</th>
          [/#list]
          <th width="5%">合计</th>
        </tr>
        <tr>
          [#list dxs as dx]
            [#assign d = matrix.getColumn(dx)/]
            [#list d.keys?sort as dk]
            <th>${d.get(dk)!}</th>
            [/#list]
          [/#list]
        </tr>
      </thead>
      <tbody>
      [#assign lmatrix = matrix.groupBy(dy)/]
      [#assign rows=0/]
      [#assign dyValues = lmatrix.getColumn(dy).values/]
      [#list dyValues?values?sort_by("code") as v]
      <tr>
        <td>${v_index+1}</td>
        <td>${v.name}</td>
        [#list dxs as dx]
          [#assign d = matrix.getColumn(dx)/]
          [#assign lgmatrix = matrix.groupBy(dy+","+dx)/]
          [#list d.keys?sort as dk]
          [#assign params={"credit.std.state.grade.id":grade.id?string,"credit.std.state.department.id":v.id?string,paramNames[dx_index]:(dk!'null')?string} /]
          <td>
            [@displayCounter lgmatrix.getCounter(v.id,dk)!,params/]
            [#if dk_index<2]
              [#assign vv = (lgmatrix.getCounter(v.id,dk)?first)!0 /]
              [#if vv >0]
              <span class="text-muted">${format_percent( vv *1.0 / lmatrix.getCounter(v.id)?first)}</span>
              [/#if]
            [/#if]
          </td>
          [/#list]
        [/#list]
        <td>[@displayCounter lmatrix.getCounter(v.id)!,{} /] </td>
      </tr>
      [/#list]

      <tr>
        <td colspan="2">合计</td>
        [#list dxs as dx]
          [#assign gmatrix = matrix.groupBy(dx)/]
          [#assign dvalues = matrix.getColumn(dx).values/]
          [#list dvalues?keys?sort as g]
          <td>
            [@displayCounter gmatrix.getCounter(g)!,{} /]
            [#if g_index <2]
              [#assign vv = (gmatrix.getCounter(g)?first)!0/]
              [#if vv >0]
              <span class="text-muted">${format_percent( vv *1.0 / gmatrix.sum?first)}</span>
              [/#if]
            [/#if]
          </td>
          [/#list]
        [/#list]
        <td>[@displayCounter gmatrix.sum ,{}/]</td>
      </tr>
      </tbody>
    </table>
  [/#macro]

<div class="container">
  [#list grades as grade]
    [#if matrixes.get(grade.id)??]
    [#assign matrix = matrixes.get(grade.id)/]
    [@displayMatrix grade, "depart",["credit"],"院系",["学分"],["credit.credits"]  /]
    [/#if]
  [/#list]
</div>
  [@b.dialog id="stdList" title="学生信息" /]
[@b.foot/]
