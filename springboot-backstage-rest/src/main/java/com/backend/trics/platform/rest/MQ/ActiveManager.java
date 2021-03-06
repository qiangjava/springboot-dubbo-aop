package com.backend.trics.platform.rest.MQ;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * Created by liyongqiang on 2018/12/25 下午7:44
 */
@Component
public class ActiveManager {
    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;
    @Value("${activemq.trade1}")
    private String queueName;
    @Value("${activemq.trade2}")
    private String queueName1;
    /**
     * @param data
     * @desc 即时发送
     */
    public void send(String data) {
        Destination destination = new ActiveMQQueue(queueName1);
        this.jmsMessagingTemplate.convertAndSend(destination, data);
    }

    public void delaySend(String text, Long time) {
        //获取连接工厂
        ConnectionFactory connectionFactory = this.jmsMessagingTemplate.getConnectionFactory();
        try {
            //获取连接
            Connection connection = connectionFactory.createConnection();
            connection.start();
            //获取session
            Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 创建一个消息队列
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage message = session.createTextMessage(text);
            //设置延迟时间
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
            //发送
            producer.send(message);
            session.commit();
            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }
}

