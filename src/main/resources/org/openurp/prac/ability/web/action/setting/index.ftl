[#ftl]
[@b.head/]
[@b.toolbar title="能力素质学分认定设置"]
[/@]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="configIdxForm" action="!search" title="ui.searchForm" target="configList" theme="search"]
      [@b.textfield label="证书名称" name="setting.certificate.name"/]
      [@b.textfield label="适用专业" name="majorName"/]
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
