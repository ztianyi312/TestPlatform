package com.tyzhou.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
		client.start();
		try {
			//
			setDataAsync(client, "/zk_test/path", "modify".getBytes());
			client.create().inBackground().forPath("/zk_test/path", "data".getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CountDownLatch cd = new CountDownLatch(1);
		try {
			cd.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CloseableUtils.closeQuietly(client);
	}

	public static void      setDataAsync(CuratorFramework client, String path, byte[] payload) throws Exception
   {
       // this is one method of getting event/async notifications
       CuratorListener listener = new CuratorListener()
        {

		public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
			System.out.println(event.getType());
			
		}
      };
       client.getCuratorListenable().addListener(listener);

       // set data for the given node asynchronously. The completion notification
        // is done via the CuratorListener.
        client.setData().inBackground().forPath(path, payload);
    }
}
