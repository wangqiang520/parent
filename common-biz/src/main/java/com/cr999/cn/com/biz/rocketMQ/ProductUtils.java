package com.cr999.cn.com.biz.rocketMQ;

import com.cr999.cn.com.biz.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProductUtils {
    static String producerGroup="please_rename_unique_group_name";
    static String namesrvAddr="192.168.209.128:9876";
    static List<String> dataList=new ArrayList<String>();
    static String topic="TopicTest";
    static String tags="TagA";
    static int delayTimeLevel=4;

    public static void main(String[] args) throws Exception {
        /*dataList.add("王顺兰");
        dataList.add("上官林");
        dataList.add("wangqiang");
        dataList.add("wangjincheng*8");*/
        //CountDownLatch countDownLatch=new CountDownLatch(10);
        for(int i=0;i<50;i++){
            //dataList.add("上官林:"+i);
            dataList.add(i+"");
        }
        //syncProducer(producerGroup,namesrvAddr,dataList,topic,tags);
        //asyncProducer(producerGroup,namesrvAddr,dataList,topic,tags,"2");
        //onewayProducer(producerGroup,namesrvAddr,dataList,topic,tags);
        //orderedProducer(producerGroup,namesrvAddr,dataList,topic,tags);
        scheduledMessageProducer(producerGroup,namesrvAddr,dataList,topic,tags,delayTimeLevel);

    }

    /**
     * 发送同步消息(这种可靠性同步地发送方式使用的比较广泛，比如：重要的消息通知，短信通知。)
     * @param producerGroup
     * @param namesrvAddr
     * @param dataList
     * @param topic
     * @param tags
     * @throws Exception
     */
    public static void syncProducer(String producerGroup, String namesrvAddr, List<String> dataList, String topic, String tags) throws Exception {
        if(dataList.isEmpty()){
            throw new CustomException("消息不能为空");
        }
        //实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        //设置NameServer的地址
        producer.setNamesrvAddr(namesrvAddr);
        //启动producer实例
        producer.start();

        for(String str:dataList){
            Message msg=new Message(topic,tags,str.getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送消息到一个Broker
            SendResult sendResult = producer.send(msg);
            // 通过sendResult返回消息是否成功送达
            System.out.printf("发送消息：%s%n", sendResult);
        }
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }

    /**
     * 发送异步消息(异步消息通常用在对响应时间敏感的业务场景，即发送端不能容忍长时间地等待Broker的响应。)
     * @param producerGroup
     * @param namesrvAddr
     * @param dataList
     * @param topic
     * @param tags
     * @throws Exception
     */
    public static void asyncProducer(String producerGroup, String namesrvAddr, List<String> dataList, String topic, String tags,String keys) throws Exception {
        if(dataList.isEmpty()){
            throw new CustomException("消息不能为空");
        }
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        // 设置NameServer的地址
        producer.setNamesrvAddr(namesrvAddr);
        // 启动Producer实例
        producer.start();
        //设置异步发送失败重试次数，默认为 2
        producer.setRetryTimesWhenSendAsyncFailed(0);
        //消息没有发送成功，是否发送到另外一个Broker中
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);

        int messageCount = dataList.size();
        //初始化messageCount个子线程，直到getCount=0，就会执行主线程，（也就是说要等子线程程全部执行完，主线程才能启动）
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for(String str:dataList){
            Message msg=new Message(topic,tags,keys,str.getBytes(RemotingHelper.DEFAULT_CHARSET));
            //SendCallback接收异步返回结果的回调
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    // 通过sendResult返回消息是否成功送达
                    System.out.printf("发送消息：%s%n",sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    countDownLatch.countDown();
                    System.out.printf("%-10d Exception %n", e);
                    e.printStackTrace();
                }
            });
        }
        // 等待5s（使当前线程在锁存器倒计数至零之前一直等待，除非线程被中断或超出了指定的等待时间。）
        countDownLatch.await(5, TimeUnit.SECONDS);
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }

    /**
     * 单向发送消息(这种方式主要用在不特别关心发送结果的场景，例如日志发送。)
     * @param producerGroup
     * @param namesrvAddr
     * @param dataList
     * @param topic
     * @param tags
     * @throws Exception
     */
    public static void onewayProducer (String producerGroup, String namesrvAddr, List<String> dataList, String topic, String tags) throws Exception {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        // 设置NameServer的地址
        producer.setNamesrvAddr(namesrvAddr);
        // 启动Producer实例
        producer.start();

        for(String str:dataList){
            Message msg=new Message(topic,tags,str.getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送单向消息，没有任何返回结果
            producer.sendOneway(msg);
        }
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }

    /**
     *  顺序消息生产
     * @param producerGroup
     * @param namesrvAddr
     * @param dataList
     * @param topic
     * @param tags
     * @throws Exception
     */
    public static void orderedProducer (String producerGroup, String namesrvAddr, List<String> dataList, String topic, String tags) throws Exception {
        if(dataList.isEmpty()){
            throw new CustomException("消息不能为空");
        }
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        // 设置NameServer的地址
        producer.setNamesrvAddr(namesrvAddr);
        // 启动Producer实例
        producer.start();

        for(String str:dataList){
            Message msg=new Message(topic,tags,str.getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 发送单向消息，没有任何返回结果
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {

                    return mqs.get((int)arg);
                }
            }, Integer.parseInt(str)%2==0?0:1);
            // 通过sendResult返回消息是否成功送达
            System.out.printf("发送消息：%s%n", sendResult);

        }
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }

    /**
     * 发送延时消息(比如电商里，提交了一个订单就可以发送一个延时消息，1h后去检查这个订单的状态，如果还是未付款就取消订单释放库存。)
     * @param producerGroup
     * @param namesrvAddr
     * @param dataList
     * @param topic
     * @param tags
     * @param delayTimeLevel
     * @throws Exception
     */
    public static void scheduledMessageProducer (String producerGroup, String namesrvAddr, List<String> dataList, String topic, String tags,int delayTimeLevel) throws Exception {
        if(dataList.isEmpty()){
            throw new CustomException("消息不能为空");
        }
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        // 设置NameServer的地址
        producer.setNamesrvAddr(namesrvAddr);
        // 启动Producer实例
        producer.start();

        for(String str:dataList){
            Message msg=new Message(topic,tags,str.getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 设置延时等级3,这个消息将在10s之后发送(现在只支持固定的几个时间,详看delayTimeLevel)(1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h)
            msg.setDelayTimeLevel(delayTimeLevel);
            SendResult sendResult = producer.send(msg);
            // 通过sendResult返回消息是否成功送达
            System.out.printf("发送消息：%s%n", sendResult);
        }
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();

    }




}
