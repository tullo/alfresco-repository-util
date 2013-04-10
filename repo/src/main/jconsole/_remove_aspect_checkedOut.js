var nodes = search.luceneSearch("ASPECT:checkedOut");

var nodeRef = "node_ref to cancel checkout on";

for each(var node in nodes)
{

    logger.log("checkedOut: " + node.name + " (" + node.typeShort + ") " + node.nodeRef);

    if (nodeRef === String(node.nodeRef) || nodeRef === "all")
    {
        if (node.hasAspect("cm:checkedOut"))
        {
            logger.log("removing aspect 'cm:checkedOut' from " + node.name + ", " + node.nodeRef);
            if (node.assocs["cm:workingcopylink"])
            {
                var workingcopy = node.assocs["cm:workingcopylink"][0];
                logger.log("..workingcopy: " + workingcopy.name);
                logger.log("..removing association: 'cm:workingcopylink' to " + workingcopy.nodeRef);
                node.removeAssociation(workingcopy, "cm:workingcopylink");
                node.removeAspect("cm:checkedOut");
                node.save();
                logger.log("..removing workingcopy: " + workingcopy.name);
                workingcopy.remove();
                logger.log("==================================================================");
                logger.log("wait a few seconds - give SOLR a chance to update the search index");
                logger.log("==================================================================");
            }
        }
    }
}
