import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class SkiersClient {
  final static private int NUM_THREADS = 20;
  static protected CountDownLatch latch = new CountDownLatch(20);
  private static List<List<Long>> times = new ArrayList<>();

  public static void main(String[] args) throws InterruptedException {
    AtomicInteger scount = new AtomicInteger(0);
    AtomicInteger ucount = new AtomicInteger(0);
    long start = System.currentTimeMillis();
    for (int i = 0; i < NUM_THREADS; i++) {
      List<Long> list = new ArrayList<>();
      times.add(list);
      SkiersClientThread skiersClientThread = new SkiersClientThread(scount, ucount, list);
      skiersClientThread.start();
    }
    latch.await(); //wait until the latch count is zero
    long end = System.currentTimeMillis();
    List<Long> allTimes = new ArrayList<>();
    //Calculate performance
    for (List<Long> time : times) {
      System.out.println(time.size());
      for (Long t : time) {
        allTimes.add(t);
      }
    }
    Collections.sort(allTimes);
    System.out.println(allTimes.size());
    double sum = 0;
    for (Long time : allTimes) {
      sum += time;
    }
    long totalTime = end - start;

    int size = allTimes.size();
    System.out.println("The mean is: " + sum / size + " milli seconds");
    System.out.println("The median is: "
            + (allTimes.get(size / 2) + allTimes.get(size / 2 - 1)) / 2 + " milli seconds");
    System.out.println("99th percentile: " + allTimes.get((int)(0.99 * size) - 1) + " milli seconds");
    System.out.println("Throughput: " + 200000 / (totalTime / 1000.0));
    System.out.println("Max response time: " + Collections.max(allTimes));
    System.out.println("Min response time: " + Collections.min(allTimes));
  }
}
