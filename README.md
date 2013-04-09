Alfresco Repository Util
=================================

Adds functionality that allows Alfresco admins to cleanup locked nodes caused by left-overs of the "edit-online" action.   

mvn clean package

Copy the resulting jar file (target/alfresco-repository-util-repo-x.x.x.jar) to the exploded alfresco.war (alfresco/WEB-INF/lib/)

Restart the alfresco server

Unlock nodes http://localhost:8080/alfresco/s/net/tullo/repo/util/unlocker.html (WRITE_LOCK/READ_ONLY_LOCK)

Clean up the remaining mess by adding the the jconsole scripts to 'Data Dictionary > Scripts

e.g. run _remove_aspect_checkedOut.js to clean up after a user that forgot to release the lock (by close, check-in)
on a node after online-edit by an MS Office app
