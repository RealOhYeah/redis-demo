package com.heima.test;

import com.heima.jedis.util.ClusterSlotHashUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JedisCluster测试类
 */
public class JedisClusterTest {

    private JedisCluster jedisCluster;

    /**
     * 初始化JedisCluster实例
     */
    @BeforeEach
    void setUp() {
        // 配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig(); // 创建JedisPoolConfig实例
        poolConfig.setMaxTotal(8); // 最大连接数
        poolConfig.setMaxIdle(8); // 最大空闲连接数
        poolConfig.setMinIdle(0); // 最小空闲连接数
        poolConfig.setMaxWaitMillis(1000); // 连接池最大阻塞等待时间（单位：毫秒）
        HashSet<HostAndPort> nodes = new HashSet<>(); // Redis集群节点列表
        nodes.add(new HostAndPort("192.168.150.101", 7001));
        nodes.add(new HostAndPort("192.168.150.101", 7002));
        nodes.add(new HostAndPort("192.168.150.101", 7003));
        nodes.add(new HostAndPort("192.168.150.101", 8001));
        nodes.add(new HostAndPort("192.168.150.101", 8002));
        nodes.add(new HostAndPort("192.168.150.101", 8003));
        jedisCluster = new JedisCluster(nodes, poolConfig); // 创建JedisCluster实例
    }

    /**
     * 使用MSet来批量设置String类型的key-value
     */
    @Test
    void testMSet() {
        jedisCluster.mset("name", "Jack", "age", "21", "sex", "male");

    }

    /**
     * 将一个HashMap中的键值对分组并使用JedisCluster进行批量设置（mset）
     * 创建了一个包含键值对的HashMap，并将其条目按特定规则分组（通过ClusterSlotHashUtil.calculateSlot计算槽位）。
     * (由于切换redis中集群的节点时会有性能损耗，所以先把所有的key存储的插槽计算出来之后，在一组插槽的同时都写入同一个节点可以节省性能)
     * ，将分组后的键值对转换为JedisCluster的mset操作所需的字符串数组格式，并执行mset命令将数据批量写入Redis集群。
     */
    @Test
    void testMSet2() {
        Map<String, String> map = new HashMap<>(3);
        map.put("name", "Jack");
        map.put("age", "21");
        map.put("sex", "Male");

        Map<Integer, List<Map.Entry<String, String>>> result = map.entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        entry -> ClusterSlotHashUtil.calculateSlot(entry.getKey()))
                ); // 按槽位分组
        for (List<Map.Entry<String, String>> list : result.values()) {
            String[] arr = new String[list.size() * 2]; // 假设每个键值对占两个位置
            int j = 0;
            for (int i = 0; i < list.size(); i++) {
                j = i<<2;
                Map.Entry<String, String> e = list.get(0);
                arr[j] = e.getKey();
                arr[j + 1] = e.getValue();
            }
            jedisCluster.mset(arr); // 执行mset操作
        }
    }

    /**
     * 确保关闭JedisCluster实例以释放资源
     */
    @AfterEach
    void tearDown() {
        if (jedisCluster != null) {
            jedisCluster.close();
        }
    }
}
