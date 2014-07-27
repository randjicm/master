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
    private final Producer producer;
    private final Consumer consumer;

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

    public void startThreading() {

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

    }

}
