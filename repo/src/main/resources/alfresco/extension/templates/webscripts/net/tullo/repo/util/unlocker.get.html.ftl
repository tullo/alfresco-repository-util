<!DOCTYPE html>
<html><head><title>Node Unlocker</title></head>
<body>
<#if lockedNodes??><h3>Locked nodes</h3>
    <#list lockedNodes as node><a href="./unlocker.html?nodeRef=${node.nodeRef}">unlock</a> ${node.nodeName} ${node.nodeRef}
        <#if node_has_next><br/>
            <#else><br/><a href="./unlocker.html?unlockAll=true">unlock all nodes</a>
        </#if>
    </#list>
    <#elseif unlockCount??><h3>Unlocked nodes: ${unlockCount}</h3>
        <p><a href="./unlocker.html">update</a> (wait a few seconds after an unlock - give SOLR a chance to update the search index)</p>
    <#else><h3>No locked nodes found!</h3>
        <p><a href="./unlocker.html">update</a></p>
</#if>
</body></html>