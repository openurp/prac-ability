[#ftl]
[@b.head/]
[@b.toolbar title="能力素质拓展课院系认定管理"/]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="applysearchForm" action="!search" title="ui.searchForm" target="applyList" theme="search"]
          <input type="hidden" name="orderBy" value="apply.updatedAt desc"/>
          [@b.textfield name="apply.std.code" label="学号"/]
          [@b.textfield name="apply.std.name" label="姓名"/]
          [@b.textfield name="apply.std.state.grade" label="年级"/]
          [@b.date label="考试日期" name="apply.acquiredOn"/]
          [@b.select items=statuses label="状态" empty="..." name="apply.status"/]
      [/@]
  </div>
  <div class="search-list">
      [@b.div id="applyList" href="!search" /]
  </div>
</div>

[@b.foot/]
