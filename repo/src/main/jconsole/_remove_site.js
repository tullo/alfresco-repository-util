var nodes = search.luceneSearch("TYPE:\"{http://www.alfresco.org/model/site/1.0}site\"");

var nodeRef = "site node_ref of site to delete";

for each(var node in nodes)
{
    logger.log("site: " + node.name + " (" + node.typeShort + ") " + node.nodeRef);

    if (nodeRef === String(node.nodeRef))
    {
        logger.log("Deleting site: " + node.name + ", " + node.nodeRef);
        var children = node.children
        if (children)
        {
            for each(var child in children)
            {
                logger.log("..deleting : " + child.name);
                node.removeNode(child);
            }
        }
        if (node.hasAspect("sys:undeletable"))
        {
            logger.log("..removing aspect 'sys:undeletable' from " + node.name);
            node.removeAspect("sys:undeletable");
            node.save();
        }
        node.remove();
        logger.log("..site deleted");
    }
}
