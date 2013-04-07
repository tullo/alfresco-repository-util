<#if lockedNodes??>Locked nodes:<br/><#list lockedNodes as node><a href="./unlocker.html?nodeRef=${node.nodeRef}">unlock</a> ${node.nodeName} ${node.nodeRef}
    <#if node_has_next><br/>
        <#else><br/><a href="./unlocker.html?unlockAll=true">unlock all nodes</a>
    </#if></#list>
<#elseif unlockCount??>Unlocked nodes: ${unlockCount}
<#else>No locked nodes found!</#if>