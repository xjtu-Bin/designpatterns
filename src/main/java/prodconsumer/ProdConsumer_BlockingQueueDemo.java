package prodconsumer;

import java.lang.reflect.Array;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MyResource {

    private volatile boolean FLAG = true;//默认开启，进行生产+消费

    private AtomicInteger atomicInteger = new AtomicInteger();

    ArrayBlockingQueue<String> blockingDeque = null;

    public MyResource(ArrayBlockingQueue<String> blockingDeque) {
        this.blockingDeque = blockingDeque;
        System.out.println(blockingDeque.getClass().getName());
    }

    public void myprod() throws InterruptedException {
        String data = null;
        boolean offer;
        while (FLAG) {
            data = atomicInteger.incrementAndGet()+"";
            offer = blockingDeque.offer(data,2L, TimeUnit.SECONDS);
            if (offer) {
                System.out.println(Thread.currentThread().getName()+"\t 插入队列" + data + "成功");
            } else {
                System.out.println(Thread.currentThread().getName()+"\t 插入队列" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName()+"\t 生产结束");
    }

    public void mycons() throws InterruptedException {
        String data = null;
        while (FLAG) {
            data = blockingDeque.poll(2L,TimeUnit.SECONDS);
            if(data == null || data.equalsIgnoreCase("")){
                FLAG = false;
                System.out.println(Thread.currentThread().getName() + "\t 超过2s没有获取到，退出");
                return;
            }
            System.out.println(Thread.currentThread().getName()+"\t 消费队列" + data +"成功");
        }
    }

    public void stop(){
        this.FLAG = false;
    }
}
public class ProdConsumer_BlockingQueueDemo {

    public static void main(String[] args) {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<String>(10));

        new Thread( () -> {
            System.out.println(Thread.currentThread().getName() + "\t 生产线程启动");
            try {
                myResource.myprod();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"prod").start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t消费线程启动");
            try {
                myResource.mycons();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"cons").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myResource.stop();
    }

}
