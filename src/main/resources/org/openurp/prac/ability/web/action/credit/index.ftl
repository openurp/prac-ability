[#ftl]
[@b.head/]
[@b.toolbar title="能力素质拓展课学分认定结果"]
  bar.addItem("学分统计","stat()");
  function stat(){
    bg.form.submit(document.abilityCreditsearchForm,"${b.url('!stat')}","main");
  }
[/@]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="abilityCreditsearchForm" action="!search" title="ui.searchForm" target="abilityCreditList" theme="search"]
          <input type="hidden" name="orderBy" value="abilityCredit.updatedAt desc"/>
          [@b.textfield name="abilityCredit.std.code" label="学号"/]
          [@b.textfield name="abilityCredit.std.name" label="姓名"/]
          [@b.textfield name="abilityCredit.std.state.grade.code" label="年级"/]
          [@b.select name="abilityCredit.std.state.department.id" label="院系" items=departs empty="..."/]
          [@b.textfield name="abilityCredit.std.state.squad.name" label="班级"/]
          [@b.textfield name="abilityCredit.credits" label="学分"/]
      [/@]
  </div>
  <div class="search-list">
      [@b.div id="abilityCreditList" href="!search" /]
  </div>
</div>

[@b.foot/]
