package single;

/**
 * @author LiuBin
 * 第一步定义一个私有的类对象为null
 * 第二步单例模式构造方法肯定是私有的。
 * 第三步定义public的get对象方法。
 * 但是以上只在单机情况下试用，如果高并发，可以在get方法上加synchronized,但是把整个方法都锁了，效率不高。
 * 所以有了DCL（双端检锁模式）加锁前后的都要判断
 * DCL<双端检锁） 机制不一定线程安全，原因是有指令重排序的存在，加入volatile可以禁止指令重排
 * 原因在于某一个线程执行到第一次检测，读取到的instance不为null时， instance的引用对象可能没有完成初始化。
 * instance =new SingletonDemo¢;可以分为以下3步完成(伪代码）
 * 1.memory = allocate();1/1.分配对象内存空间
 * 2.instance(memory); /12.初始化对象
 * 3.instance 二memory;113.设置instance指向刚分配的内存地址，此时instance ！=null
 * 步骤2和步骤3不存在数据依赖关系，而且无论重排前还是重排后程序的执行结果在单线程中并没有改变，因此这种重排优化是允许的。
 * memory = allocate(); //1.分配对象内存空间
 * instance = memory; //3. 设置instance指向刚分配的内存地址，此时instance! =null, 但是对象还没有初始化完成 instance(memory); //2. 初始化对象
 * 但是指令重排只会保证串行语义的执行的一- 致性(单线程)，但并不会关心多线程间的语义-致性。
 * 所以当一条线程访问instance不为nul时，由于instance实例未必已初始化完成，也就造成了线程安全问题。
 */

public class SingletonDemo {
    private static volatile SingletonDemo instance = null;

    private SingletonDemo(){
        System.out.println(Thread.currentThread().getName()+"\t 我是构造方法SingletonDemo。");
    }

    public static  SingletonDemo getInstance() {
        /*//单机下单线程没问题
        if (instance == null) {
            instance = new SingletonDemo();
        }
        return instance;*/

        if (instance == null) {
            synchronized (SingletonDemo.class) {
                if (instance == null) {
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }
    public static void main(String[] args){
        /*
        //单机模式下测试
        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());*/

        for (int i = 1; i <=10; i++){
            new Thread(()-> {
                SingletonDemo.getInstance();
            },String.valueOf(i)).start();
        }
    }
}
