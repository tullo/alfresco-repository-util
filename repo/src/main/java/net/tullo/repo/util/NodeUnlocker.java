package net.tullo.repo.util;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.*;

public class NodeUnlocker extends DeclarativeWebScript {

    private static final int UNLOCK_COUNT_ZERO = 0;
    private static final int UNLOCK_COUNT_ONE = 1;

    private final Logger logger = LoggerFactory.getLogger(NodeUnlocker.class);

    private SearchService searchService;
    private LockService lockService;
    private NodeService nodeService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache) {

        NodeRef node = null;
        String nodeRef = request.getParameter("nodeRef");
        String unlockAll = request.getParameter("unlockAll");
        Map<String, Object> model = new HashMap<String, Object>();

        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder(" ===== ");
            if (nodeRef != null) {
                sb.append("nodeRef [" + nodeRef + "]");
            } else if (unlockAll != null) {
                sb.append("unlockAll [" + unlockAll + "]");
            }
            logger.debug("executing request: " + request.getServiceMatch().getPath() + sb.toString());
        }

        if (nodeRef != null) {
            node = doNodeLookup(nodeRef);
            if (node != null) {
                model.put("unlockCount", unlockNode(node));
            }
        } else if (Boolean.TRUE.toString().equalsIgnoreCase(unlockAll)) {
            model.put("unlockCount", unlockLockedNodes());
        } else {
            List<Map<String, Object>> lockedNodes = new LinkedList<Map<String, Object>>();
            model.put("lockedNodes", lockedNodes);
            lookupLockedNodes(lockedNodes);

            if (lockedNodes.isEmpty()) {
                model.remove("lockedNodes");
            }
        }

        return model;
    }

    private String queryString(String key, String value) {
        return new StringBuilder(key).append(":\"").append(value).append("\"").toString();
    }

    private void lookupLockedNodes(List<Map<String, Object>> lockedNodes) {

        ResultSet resultSet = null;
        final String lockOwnerQuery = queryString("ISNOTNULL", "cm:lockOwner");

        try {
            logger.debug("looking up locked nodes by query: {}", lockOwnerQuery);

            resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_FTS_ALFRESCO, lockOwnerQuery);

            logger.debug("found [{}] locked nodes", resultSet.length());

            if (resultSet.length() > 0) {
                ResultSetRow node = null;
                Map<String, Object> nodeProps = null;
                Iterator<ResultSetRow> iterator = resultSet.iterator();
                while (iterator.hasNext()) {
                    node = iterator.next();
                    nodeProps = new HashMap<String, Object>(2);
                    nodeProps.put("nodeRef", node.getNodeRef().toString());
                    nodeProps.put("nodeName", nodeService.getProperty(node.getNodeRef(), ContentModel.PROP_NAME));
                    lockedNodes.add(nodeProps);
                }
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    protected NodeRef doNodeLookup(String nodeRef) {

        NodeRef node = null;
        ResultSet resultSet = null;

        try {
            final String idQuery = queryString("ID", nodeRef);

            logger.debug("looking up node by query: {}", idQuery);

            resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_FTS_ALFRESCO, idQuery);

            logger.debug("got [{}] search hits", resultSet.length());

            if (resultSet.length() == 1) {
                node = resultSet.iterator().next().getNodeRef();
            } else {
                logger.debug("found unexpected number [{}] of search hits", resultSet.length());
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return node;
    }

    private int unlockLockedNodes() {

        ResultSet resultSet = null;
        final String lockOwnerQuery = queryString("ISNOTNULL", "cm:lockOwner");

        try {
            logger.debug("locating locked nodes by query: {}", lockOwnerQuery);

            resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_FTS_ALFRESCO, lockOwnerQuery);

            int unlockCount = resultSet.length();
            logger.debug("unlocking [{}] nodes..", unlockCount);

            if (unlockCount > 0) {
                Iterator<ResultSetRow> iterator = resultSet.iterator();
                while (iterator.hasNext()) {
                    unlockNode(iterator.next().getNodeRef());
                }
            }

            return unlockCount;

        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    protected int unlockNode(NodeRef nodeRef) {
        LockStatus lockStatus = lockService.getLockStatus(nodeRef);
        logger.debug("lock status [{}] for {}", lockStatus, nodeRef);

        if (LockStatus.LOCKED.equals(lockStatus) || LockStatus.LOCK_OWNER.equals(lockStatus)) {
            logger.debug("unlocking {}", nodeRef);
            lockService.unlock(nodeRef);
            nodeService.removeAspect(nodeRef, ContentModel.ASPECT_LOCKABLE);
            return UNLOCK_COUNT_ONE;
        }
        return UNLOCK_COUNT_ZERO;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
