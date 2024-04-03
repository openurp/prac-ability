[#ftl]
[@b.head/]
[@b.toolbar title="能力素质学分认定时间"]
[/@]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="configIdxForm" action="!search" title="ui.searchForm" target="configList" theme="search"]

    [/@]
  </div>
  <div class="search-list">
    [@b.div id="configList"/]
  </div>
</div>
<script>
    jQuery(function(){
        bg.form.submit(document.configIdxForm);
    });
</script>
[@b.foot/]
