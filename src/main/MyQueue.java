package main;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 线程安全队列
 * @author zyw
 *
 * @param <T>
 */
public class MyQueue<T> {
	 private LinkedList<T> userUrlQueue=new LinkedList<T>();
	 private Object lock=new Object();
	 
	 /**
	  * 获取队列是否为空
	  * @return
	  */
	 public boolean isEmpty() {
		 boolean empty=true;
		 //synchronized (lock) {
			 empty=userUrlQueue.isEmpty();
		//}

		return empty;
	}
	 
	 /**
	  * 将一个元素插入队列尾
	  * @param t
	  */
	 public void put(T t) {
		 synchronized (lock) {
			userUrlQueue.addLast(t);
		}
	}
	 
	 /**
	  * 队列头取出一个元素
	  * @return
	  */
	 public T  poll() {
		 T t=null;
		 synchronized (lock) {
			 t=(T) userUrlQueue.removeFirst();
		 }
		return t;
	}
}
