package com.cr999.cn.com.biz.zookeeper;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnBean(value = {CuratorConfig.class})
public class ZooKeeperUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperUtils.class);

    //zk连接
    private CuratorFramework client;
    //路径分隔符
    private static final String PATH_SEPARATOR = "/";
    @Resource
    Environment environment;

    public ZooKeeperUtils(CuratorFramework client)
    {
        this.client = client;
    }

    /**
     * 注册服务节点信息
     */
    public void registerService() {
        try {
            StringBuffer pathSb= new StringBuffer();
            pathSb.append("/services/");
            String applicationName= environment.getProperty("spring.application.name");
            if(StringUtils.isBlank(applicationName)){
                String errMsg="应用程序名称为空[spring.application.name}";
                logger.error(errMsg);
                throw new Exception(errMsg);
            }
            pathSb.append(applicationName);
            pathSb.append("-");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String port=environment.getProperty("server.port");
            StringBuffer valueSb=new StringBuffer();
            valueSb.append(hostAddress);
            valueSb.append(":");
            valueSb.append(port);
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(pathSb.toString(),valueSb.toString().getBytes());
            logger.info("zookeeper 服务注册成功");
            logger.info("节点路径："+pathSb.toString());
        } catch (Exception e) {
            logger.error("zookeeper 服务注册失败", e);
        }
    }

    /**
     * 创建空节点，默认持久节点
     *
     * @param path
     *            节点路径
     * @param node
     *            节点名称
     * @return 完整路径
     */
    public String createNode(String path, String node)
    {
        return createNode(path, node, CreateMode.PERSISTENT);
    }

    /**
     * 创建带类型的空节点
     *
     * @param path
     *            节点路径
     * @param node
     *            节点名称
     * @param createMode
     *            类型 CreateMode.PERSISTENT: 创建节点后，不删除就永久存在
     *            CreateMode.PERSISTENT_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
     *            CreateMode.EPHEMERAL：创建后，回话结束节点会自动删除
     *            CreateMode.EPHEMERAL_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
     * @return 路径
     */
    public String createNode(String path, String node, CreateMode createMode)
    {
        path = buildPath(path, node);
        logger.info("create node for path: {} with createMode: {}", path,
                createMode.name());
        try
        {

            client.create().creatingParentsIfNeeded().withMode(createMode)
                    .forPath(path);

            logger.info("create node :{} sucessfully", node);
            return path;
        }
        catch (Exception e)
        {
            logger.error(
                    "create node for path: {} with createMode: {} failed!",
                    path, createMode.name(), e);
            return null;
        }
    }

    /**
     * 创建节点，默认持久节点
     *
     * @param path
     *            节点路径
     * @param node
     *            节点名称
     * @param value
     *            节点值
     * @return 完整路径
     */
    public String createNode(String path, String node, String value)
    {
        return createNode(path, node, value, CreateMode.PERSISTENT);
    }

    /**
     * 创建节点，默认持久节点
     *
     * @param path
     *            节点路径
     * @param node
     *            节点名称
     * @param value
     *            节点值
     * @param createMode
     *            节点类型
     * @return 完整路径
     */
    public String createNode(String path, String node, String value,
            CreateMode createMode)
    {
        if (Objects.isNull(value))
        {
            logger.error("ZooKeeper节点值不能为空!");
        }
        path = buildPath(path, node);
        logger.info("create node for path: {}, value: {}, with createMode: {}",
                path, value, createMode.name());
        try
        {
            client.create().creatingParentsIfNeeded().withMode(createMode)
                    .forPath(path, value.getBytes());
            return path;
        }
        catch (Exception e)
        {
            logger.error(
                    "create node for path: {}, value: {}, with createMode: {} failed!",
                    path, value, createMode.name(), e);
        }
        return null;
    }

    /**
     * 获取节点数据
     *
     * @param path
     *            路径
     * @param node
     *            节点名称
     * @return 完整路径
     */
    public String get(String path, String node)
    {
        path = buildPath(path, node);
        try
        {
            byte[] bytes = client.getData().forPath(path);
            if (bytes.length > 0)
            {
                return new String(bytes);
            }
        }
        catch (Exception e)
        {
            logger.error("get value for path: {}, node: {} failed!", path,
                    node, e);
        }
        return null;
    }

    /**
     * 更新节点数据
     *
     * @param path
     *            节点路径
     * @param node
     *            节点名称
     * @param value
     *            更新值
     * @return 完整路径
     */
    public String update(String path, String node, String value)
    {
        if (Objects.isNull(value))
        {
            logger.error("ZooKeeper节点值不能为空!");
        }
        path = buildPath(path, node);
        logger.info("update path: {} to value: {}", path, value);

        try
        {
            client.setData().forPath(path, value.getBytes());
            return path;
        }
        catch (Exception e)
        {
            logger.error("update path: {} to value: {} failed!", path, value);
        }
        return null;
    }

    /**
     * 删除节点，并且递归删除子节点
     *
     * @param path
     *            路径
     * @param node
     *            节点名称
     * @return 路径
     */
    public boolean delete(String path, String node)
    {
        path = buildPath(path, node);
        logger.info("delete node for path: {}", path);

        try
        {
            client.delete().deletingChildrenIfNeeded().forPath(path);
            return true;
        }
        catch (Exception e)
        {
            logger.error("delete node for path: {} failed!", path);
        }
        return false;
    }

    /**
     * 获取子节点
     * 
     * @param path
     *            节点路径
     * @return
     */
    public List<String> getChildren(String path)
    {
        if (StringUtils.isEmpty(path))
        {
            return null;
        }

        if (!path.startsWith(PATH_SEPARATOR))
        {
            path = PATH_SEPARATOR + path;
        }

        try
        {
            return client.getChildren().forPath(path);
        }
        catch (Exception e)
        {
            logger.error("get children path:{} error", path, e);
        }
        return null;
    }

    /**
     * 判断节点是否存在
     *
     * @param path
     *            路径
     * @param node
     *            节点名称
     * @return 结果
     */
    public boolean exists(String path, String node)
    {
        try
        {
            List<String> list = getChildren(path);
            return !CollectionUtils.isEmpty(list) && list.contains(node);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 申请锁，指定请求等待时间
     *
     * @param path
     *            加锁zk节点
     * @param time
     *            时间
     * @param unit
     *            时间单位
     * @param runnable
     *            执行方法
     */
    public void lock(String path, long time, TimeUnit unit, Runnable runnable)
    {
        try
        {
            InterProcessMutex lock = new InterProcessMutex(client, path);
            if (lock.acquire(time, unit))
            {
                try
                {
                    runnable.run();
                }
                finally
                {
                    lock.release();
                }
            }
            else
            {
                logger.error("获取锁超时：{}!", path);
            }
        }
        catch (Exception e)
        {
            logger.error("获取锁失败: {}!", path);
        }
    }

    /**
     * 申请锁，指定请求等待时间
     *
     * @param path
     *            加锁zk节点
     * @param time
     *            时间
     * @param unit
     *            时间单位
     * @param callable
     *            执行方法
     * @return .
     */
    public <T> T lock(String path, long time, TimeUnit unit,
            Callable<T> callable)
    {
        try
        {
            InterProcessMutex lock = new InterProcessMutex(client, path);
            if (lock.acquire(time, unit))
            {
                try
                {
                    return callable.call();
                }
                finally
                {
                    lock.release();
                }
            }
            else
            {
                logger.error("获取锁超时：{}!", path);
            }
        }
        catch (Exception e)
        {
            logger.error("获取锁失败: {}!", path);
        }
        return null;
    }

    /* *//**//**
     * 对一个节点进行监听，监听事件包括指定的路径节点的增、删、改的操作
     *
     * @param path
     *            节点路径
     * @param listener
     *            回调方法
     * @throws Exception 
     *//*

    public void watchNode(String path,boolean dataIsCompressed,final ZooKeeperCallback zooKeeperCallback)throws Exception
    {
        try
        {
            final NodeCache nodeCache = new NodeCache(client, path,dataIsCompressed);
            nodeCache.getListenable().addListener(new NodeCacheListener()
            {
                public void nodeChanged() throws Exception
                {
                    ChildData childData = nodeCache.getCurrentData();
                    logger.info("ZNode节点状态改变, path={}", childData.getPath());
                    logger.info("ZNode节点状态改变, data={}", childData.getData());
                    logger.info("ZNode节点状态改变, stat={}", childData.getStat());
                    
                    //处理业务逻辑
                    zooKeeperCallback.call();
                }
            });      
            
            nodeCache.start();
        }
        catch (Exception e)
        {
            logger.error("创建NodeCache监听失败, path={}",path);
        }
    }*/

    /**
     * 对指定的路径节点的一级子目录进行监听，不对该节点的操作进行监听，对其子目录的节点进行增、删、改的操作监听
     *
     * @param path
     *            节点路径
     * @param listener
     *            回调方法
     */
    public void watchChildren(String path, PathChildrenCacheListener listener)
    {
        try
        {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(client,
                    path, true);
            pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
            pathChildrenCache.getListenable().addListener(listener);
        }
        catch (Exception e)
        {
            logger.error("watch children failed for path: {}", path, e);
        }
    }

    /**
     * 将指定的路径节点作为根节点（祖先节点），对其所有的子节点操作进行监听，呈现树形目录的监听，可以设置监听深度，最大监听深度为2147483647（
     * int类型的最大值）
     *
     * @param path
     *            节点路径
     * @param maxDepth
     *            回调方法
     * @param listener
     *            监听
     */
    public void watchTree(String path, int maxDepth, TreeCacheListener listener)
    {
        try
        {
            TreeCache treeCache = TreeCache.newBuilder(client, path)
                    .setMaxDepth(maxDepth).build();
            treeCache.start();
            treeCache.getListenable().addListener(listener);
        }
        catch (Exception e)
        {
            logger.error("watch tree failed for path: {}", path, e);
        }
    }

    public String buildPath(String path, String node)
    {
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(node))
        {
            logger.error("ZooKeeper路径或者节点名称不能为空！");
        }

        if (!path.startsWith(PATH_SEPARATOR))
        {
            path = PATH_SEPARATOR + path;
        }

        if (PATH_SEPARATOR.equals(path))
        {
            return path + node;
        }
        else
        {
            return path + PATH_SEPARATOR + node;
        }
    }
}