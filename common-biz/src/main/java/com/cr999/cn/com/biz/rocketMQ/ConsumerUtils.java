package com.cr999.cn.com.biz.rocketMQ;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.ArrayList;
import java.util.List;

public class ConsumerUtils {

    static String consumerGroup="please_rename_unique_group_name";
    static String namesrvAddr="192.168.209.128:9876";
    static List<String> dataList=new ArrayList<String>();
    static String topic="TopicTest";
    static String tags="TagA";

    public static void main(String[] args) throws Exception {

        consumer(consumerGroup,namesrvAddr,topic,tags);
        //orderedConsumer(consumerGroup,namesrvAddr,topic,tags);

    }

    /**
     * 顺序消费消息
     * @param consumerGroup
     * @param namesrvAddr
     * @param topic
     * @param tags
     * @throws Exception
     */
    public static void consumer(String consumerGroup, String namesrvAddr, String topic, String tags) throws Exception {
        // 实例化消费者
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer(consumerGroup);
        // 设置NameServer的地址
        consumer.setNamesrvAddr(namesrvAddr);
        //订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe(topic,"*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
               // System.out.printf("%s 接收消息: %s %n %s", Thread.currentThread().getName(), msgs,new String(msgs.get(0).getBody()));
                msgs.forEach(v->{
                    System.out.println(Thread.currentThread().getName()+"接收消息:"+new String(v.getBody()));
                    System.out.println(Thread.currentThread().getName()+"接收消息:"+new String(v.getBody())+"消耗时间："+ (System.currentTimeMillis() - v.getBornTimestamp()));

                });

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者实例
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

    /**
     * 顺序消费消息
     * @param consumerGroup
     * @param namesrvAddr
     * @param topic
     * @param tags
     * @throws Exception
     */
    public static void orderedConsumer(String consumerGroup, String namesrvAddr, String topic, String tags) throws Exception {
        // 实例化消费者
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer(consumerGroup);
        // 设置NameServer的地址
        consumer.setNamesrvAddr(namesrvAddr);
        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe(topic,"*");
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(false);
                msgs.forEach(v->{
                    System.out.println(Thread.currentThread().getName()+"接收消息:"+new String(v.getBody())+"队列："+v.getQueueId());
                });
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        // 启动消费者实例
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

}
