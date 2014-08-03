/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Milos Randjic
 */
public class ProducerConsumer {

    private final BlockingQueue sharedQueue;
    private  Producer producer;
    private  Consumer consumer;

    public ProducerConsumer(Producer producer, Consumer consumer) {
        sharedQueue = new LinkedBlockingQueue();
        this.producer = producer;
        this.consumer = consumer;
        this.producer.setSharedQueue(sharedQueue);
        this.consumer.setSharedQueue(sharedQueue);
    }

    public ProducerConsumer(int queueCapacity, Producer producer, Consumer consumer) {
        sharedQueue = new LinkedBlockingQueue(queueCapacity);
        this.producer = producer;
        this.consumer = consumer;
        this.producer.setSharedQueue(sharedQueue);
        this.consumer.setSharedQueue(sharedQueue);
    }

    public ProducerConsumer() {
        sharedQueue = new LinkedBlockingQueue();
        
    }
    
     public ProducerConsumer(int queueCapacity) {
        sharedQueue = new LinkedBlockingQueue(queueCapacity);        
    }
    
   
    public void startProducing(){
        new Thread(producer).start();
    }
    
    public void startConsuming(){
        new Thread(consumer).start();
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
        this.producer.setSharedQueue(sharedQueue);
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
        this.consumer.setSharedQueue(sharedQueue);
    }

}
