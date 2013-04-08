var nodes = search.luceneSearch("ASPECT:workingcopy");

for each(var node in nodes)
{
    logger.log("removing aspect 'cm:workingcopy' from" + node.name + ", " + node.nodeRef);
    node.removeAspect("cm:workingcopy");
    node.save();
}
