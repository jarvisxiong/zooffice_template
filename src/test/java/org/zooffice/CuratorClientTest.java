package org.zooffice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException.NoAuthException;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CuratorClientTest {

	private CuratorFramework client;

	@Before
	public void initClient() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		
		InheritACLProvider aclProvider = new InheritACLProvider();
		client = CuratorFrameworkFactory.builder()
				// .authorization("ip", "10.34.64.36".getBytes()) //doesn't work
				.namespace("service1")
				.connectString("10.34.64.36:2181")
				.retryPolicy(retryPolicy)
				.aclProvider(aclProvider)
				.build();
		aclProvider.setClient(client);
		// CuratorFramework client = CuratorFrameworkFactory.newClient("10.34.64.36:2181", retryPolicy);
		client.start();
	}

	@After
	public void closeClient() {
		client.close();
	}

	/**
	 * node app1 can be accessed from 10.34.64.36
	 * 
	 * @param args
	 * @throws Exception
	 */
	@Test
	public void testApp1() throws Exception {
		String app1Path = "app1";
		if (client.checkExists().forPath(app1Path) != null) {
			deletePath(app1Path);
		}

		Id id36 = new Id("ip", "10.34.64.36");
		ArrayList<ACL> acl36 = new ArrayList<ACL>(Collections.singletonList(new ACL(Perms.ALL, id36)));
		String subPath = "/app1/subapp1NoACL";
		
		/**
		client.create().withACL(acl36).forPath(app1Path, "app1's service1.app1-from-client".getBytes());
		//create sub without ACL.
		//ERROR: can not create, has no auth on this node, so can not create sub-node.
		client.create().forPath(subPath);
		*/
		//create node and sub-node at first, and then create set ACL on node.
		client.create().forPath(app1Path, "app1's service1.app1".getBytes());
		client.create().forPath(subPath, "app1's sub".getBytes());
		client.setACL().withACL(acl36).forPath(app1Path);
	

		List<String> childPathList = client.getChildren().forPath("");
		System.out.println("list of root: /");
		for (String path : childPathList) {
			System.out.println(path);
		}

		List<ACL> app1List = client.getACL().forPath(app1Path);
		System.out.println("ACL list of: app1");
		for (int i = 0; i < app1List.size(); i++) {
			System.out.println(app1List.get(i));
		}
		System.out.println("app1's sub data:" + new String(client.getData().forPath(subPath)));
		try {
			System.out.println("app1 data:" + new String(client.getData().forPath(app1Path)));
			Assert.assertTrue("my IP has no access on app1 node", false);
		} catch (NoAuthException e) {
			Assert.assertTrue("my IP has no access on app1 node", true);
		}
	}

	/**
	 * node app2 can be accessed from 10.34.63.28
	 * 
	 * @throws Exception
	 */
	@Test
	public void testApp2() throws Exception {

		String app2Path = "app2";
		if (client.checkExists().forPath(app2Path) != null) {
			deletePath(app2Path);
		}
		Id myIpId = new Id("ip", "10.34.63.28");
		ArrayList<ACL> myIpACL = new ArrayList<ACL>(Collections.singletonList(new ACL(Perms.ALL, myIpId)));
		client.create().withACL(myIpACL).forPath(app2Path, "app2's myData2-from-client".getBytes());
		
		//create sub without ACL.
		String subPath = "/app2/subapp2NoACL";
		client.create().forPath(subPath);
		
		List<String> childPathList = client.getChildren().forPath("");
		System.out.println("list of root: /");
		for (String path : childPathList) {
			System.out.println(path);
		}

		List<ACL> app2List = client.getACL().forPath(app2Path);
		System.out.println("ACL list of: app2");
		for (int i = 0; i < app2List.size(); i++) {
			System.out.println(app2List.get(i));
		}
		System.out.println("app2 data:" + new String(client.getData().forPath(app2Path)));
		System.out.println("app2sub data:" + new String(client.getData().forPath(subPath)));

	}

	/**
	 * 28 has no access right on App1, but for the sub node "app1/subapp1NoACL", there is no ACL set,
	 * then 28 can access/modify the data of "app1/subByApp2" 
	 * @throws Exception
	 */
	@Test
	public void deleteSubNodeWithoutAuth() throws Exception {
		String app1SubPath = "/app1/subapp1NoACL";
		String app1SubPathNew = app1SubPath + "/newNodeByOther";
		if (client.checkExists().forPath(app1SubPath) != null) {
			//can not delete, sub-path "subapp1NoACL" belongs to "app1", and 28 has no right.
			//client.delete().forPath(app1SubPath);
			
			//can create sub-node under "subapp1NoACL"
			client.create().forPath(app1SubPathNew, "app1.subByApp2's data".getBytes());
			
			//can get data from "subapp1NoACL"
			System.out.println(app1SubPath + " 's data:" + new String(client.getData().forPath(app1SubPath)));
			System.out.println(app1SubPathNew + " 's data:" + new String(client.getData().forPath(app1SubPathNew)));
			client.delete().forPath(app1SubPathNew);
		}
	}
	
	private void deletePath(String path) throws Exception {
		List<String> subDirList = client.getChildren().forPath(path);
		for (String subDir : subDirList) {
			String subPath = path + "/" + subDir;
			deletePath(subPath);
		}
		client.delete().forPath(path);
		System.out.println("Path " + path + " is deleted.");
	}
}
