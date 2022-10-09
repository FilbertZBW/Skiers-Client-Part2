import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.opencsv.CSVWriter;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

public class SkiersClientThread extends Thread {

  protected AtomicInteger successfulCount;
  protected AtomicInteger unsuccessfulCount;
  protected List<Long> times;

  public SkiersClientThread() {

  }

  public SkiersClientThread(AtomicInteger successfulCount, AtomicInteger unsuccessfulCount,
                            List<Long> times) {
    this.successfulCount = successfulCount;
    this.unsuccessfulCount = unsuccessfulCount;
    this.times = times;
  }

  @Override
  public void run() {
    int NUM_REQUESTS = 10000;
    File file = new File("./record.csv");
    FileWriter outputfile = null;
    try {
      outputfile = new FileWriter(file, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    CSVWriter writer = new CSVWriter(outputfile);
    List<String[]> data = new ArrayList<>();
    for (int i = 0; i < NUM_REQUESTS; i++) {
      SkiersApi skiersApi = new SkiersApi();
      skiersApi.getApiClient().setBasePath("http://18.237.10.252:8080/lab2_war/");
      LiftRide liftRideBody = new LiftRide();
      LiftRideEvent liftRideEvent = new LiftRideEvent();
      liftRideBody.setTime(liftRideEvent.getTime());
      liftRideBody.setLiftID(liftRideEvent.getLiftID());

      try {
        long start = System.currentTimeMillis();
        ApiResponse<Void> apiResponse = skiersApi.writeNewLiftRideWithHttpInfo(liftRideBody,
                liftRideEvent.getResortID(), liftRideEvent.getSeasonID(), liftRideEvent.getDayID(),
                liftRideEvent.getSkierID());
        int statusCode = apiResponse.getStatusCode();
        int cnt = 0;
        while ((statusCode / 100 == 4 || statusCode / 100 == 5) && cnt < 4) {
          ApiResponse<Void> resp = skiersApi.writeNewLiftRideWithHttpInfo(liftRideBody,
                  liftRideEvent.getResortID(), liftRideEvent.getSeasonID(), liftRideEvent.getDayID(),
                  liftRideEvent.getSkierID());
          statusCode = resp.getStatusCode();
          cnt++;
        }
        if (statusCode == 200) {
          successfulCount.incrementAndGet();
        } else {
          unsuccessfulCount.incrementAndGet();
        }
        long end = System.currentTimeMillis();
        long latency = end - start;
        times.add(latency);
        data.add(new String[]{"" + start, "POST", "" + latency / 1000.0, "" + statusCode});
      } catch (ApiException e) {
        System.err.println("Exception when calling SkiersApi#writeNewLiftRideWithHttpInfo");
        e.printStackTrace();
      }
    }
    writer.writeAll(data);
    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    SkiersClient.latch.countDown();
  }
}
