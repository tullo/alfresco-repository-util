var nodes = search.luceneSearch("ASPECT:lockable");

for each(var node in nodes) {
    logger.log("removing aspect 'cm:lockable' from " + node.name + ", " + node.nodeRef);
    node.removeAspect("cm:lockable");
    node.save();
}