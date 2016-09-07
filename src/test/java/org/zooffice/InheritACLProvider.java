package org.zooffice;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

public class InheritACLProvider implements ACLProvider {

	private CuratorFramework client;

	public void setClient(CuratorFramework client) {
		this.client = client;
	}

	public List<ACL> getDefaultAcl() {
		return ZooDefs.Ids.OPEN_ACL_UNSAFE;
	}

	public List<ACL> getAclForPath(String path) {
		if (this.client == null) {
			return null;
		} else {
			try {
				String unfixedPath = unfixForNamespace(path, client.getNamespace());
		        int i = unfixedPath.lastIndexOf('/');
		        String parentPath = (i > 0) ? unfixedPath.substring(0, i) : "";
				return client.getACL().forPath(parentPath);
			} catch (Exception e) {
				return null;
			}
		}
	}

	String unfixForNamespace(String path, String namespace) {
		if ((namespace != null) && (path != null)) {
			String namespacePath = ZKPaths.makePath(namespace, null);
			if (path.startsWith(namespacePath)) {
				path = (path.length() > namespacePath.length()) ? path.substring(namespacePath.length()) : "/";
			}
		}
		return path;
	}

}
