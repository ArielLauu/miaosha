package com.ariel.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MQConfig {

    public static final String MIAOSHA_QUEUE="miaosha.queue";
    public static final String QUEUE="queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEADERS_QUEUE = "headers.queue";
    public static final String TOPIC_EXCHANGE= "topicExchange";
    public static final String ROUTING_KEY1= "topic.key1";
    public static final String ROUTING_KEY2= "topic.#";
    public static final String FANOUT_EXCHANGE= "fanoutExchange";
    public static final String HEADERS_EXCHANGE= "headersExchange";

    /**
     * Direct模式 交换机Exchange
     * */
    @Bean
    public Queue queue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }
    @Bean
    public Queue directQueue() {
        return new Queue(QUEUE, true);
    }

    /**
     * Topic模式 交换机Exchange
     * */
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }
    /**
     * Fanout模式（广播） 交换机Exchange
     * */
    @Bean
    public FanoutExchange fanoutExchage(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding FanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchage());
    }
    @Bean
    public Binding FanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchage());
    }
    /**
     * Header模式（广播） 交换机Exchange
     * */
    @Bean
    public HeadersExchange headersExchage(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headersQueue() {
        return new Queue(HEADERS_QUEUE, true);
    }
    @Bean
    public Binding HeadersBinding() {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchage()).whereAll(map).match();
    }
}
