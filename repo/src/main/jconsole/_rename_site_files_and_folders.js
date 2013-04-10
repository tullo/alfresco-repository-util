//var sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
var fPathLength, cPathLength, nodeRef = ""; // to actually rename nodes give nodeRef a value other than ""
var folderPathMin = 50,
    folderNameMinLength = 50;
var contentPathMin = 120,
    contentNameMinLength = 110;

var nodes = search.luceneSearch("PATH:\"/app:company_home/st:sites/*//*\"");

function rename(n, isFolder) {
    var temp = String(n.name);
    logger.log("   ..renaming: " + temp);
    var currentTitle = n.properties["cm:title"];
    if (currentTitle)
    {
        // logger.log("....currentTitle: " + currentTitle + " [" + temp + "]");
    }
    else
    {
        var title = "renamed-";
        logger.log("    ..currentTitle: NONE, fname [" + temp + "]");
        if (isFolder)
        {
            title = title + temp;
        } else
        {
            title = temp.substring(0, temp.lastIndexOf("."));
        }
        logger.log("    ..newTitle: " + title);
        n.properties["cm:title"] = title;
        var fname = "renamed" + new java.util.Random().nextLong() + temp.substring(temp.lastIndexOf("."));
        n.name = fname;
        n.save();
        logger.log(".....renamed to: " + fname);
        return;
    }

    // title already set, update description instead
    var currentDesc = n.properties["cm:description"];
    if (currentDesc)
    {
        logger.log("     ..currentDesc: " + currentDesc + " [" + temp + "]");
        logger.log("     ..newDesc: " + currentDesc + ", [" + temp + "]");
        n.properties["cm:description"] = currentDesc + ", " + temp;
    }
    else
    {
        logger.log("    ..currentDesc: NONE [" + temp + "]");
        logger.log("    ..newDesc: " + temp);
        n.properties["cm:description"] = temp;
    }
    var cname = "renamed" + new java.util.Random().nextLong();
    temp = cname + temp.substring(temp.lastIndexOf("."));
    n.name = temp;
    n.save();
    logger.log("  ...renamed to: " + temp);
}

for each(var node in nodes)
{
    if ("cm:folder" === String(node.typeShort))
    {
        fPathLength = node.displayPath.length();
        if (fPathLength > folderPathMin)
        {
            var flength = node.name.length();
            if (flength > folderNameMinLength)
            {
                logger.log("Folder name [" + node.name.length() + "]: " + node.name);
                logger.log("  " + node.typeShort + " : " + node.nodeRef);
                logger.log("  ..displayPath: (" + fPathLength + ") " + node.displayPath);
                if (nodeRef) {
                    rename(node, true);
                }
            }
        }
    }

    if ("cm:content" === String(node.typeShort))
    {
        cPathLength = node.displayPath.length();
        if (cPathLength > contentPathMin)
        {
            var clength = node.name.length();
            if (clength > contentNameMinLength)
            {
                var cname = String(node.name);
                if (cname.length > 130)
                {
                    cname = "_truncated [" + cname.substring(0, 100) + "___" + cname.substring(cname.lastIndexOf("."));
                }
                logger.log("Content name [" + cname.length + "]: " + cname + "]");
                logger.log("  " + node.typeShort + " : " + node.nodeRef);
                logger.log("  ..displayPath: (" + cPathLength + ") " + node.displayPath);
                if (nodeRef) {
                    rename(node, false);
                }
            }
        }
    }
}
