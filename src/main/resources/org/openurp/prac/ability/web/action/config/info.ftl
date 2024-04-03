[#ftl/]
[@b.head/]
[@b.toolbar title="开关详细信息"]
    bar.addItem("${b.text("action.edit")}","bg.Go('config!edit.action','configList');");
    bar.addBack("${b.text("action.back")}");
[/@]
[#assign labInfo]${b.text("ui.building.info")}[/#assign]
<table class="infoTable" width="100%" >
     <tr>
          <td class="title">开关代码</td>
        <td class="content">${(config.code)!}</td>
          <td class="title">开关名称</td>
          <td class="content">${(config.name)!}</td>
    </tr>
     <tr>
          <td class="title">开始日期</td>
          <td class="content">${(config.beginAt?string("yyyy-MM-dd HH:mm"))?if_exists}</td>
          <td class="title">结束日期</td>
          <td class="content">${(config.endAt?string("yyyy-MM-dd HH:mm"))?if_exists}</td>
     </tr>
     <tr>
          <td class="title" width="15%">备注</td>
          <td class="content" colspan="3" width="85%">${(config.remark?html)!}</td>
     </tr>
</table>
[@b.foot/]
