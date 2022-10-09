import java.util.Random;

public class LiftRideEvent {
  private final int skierID;
  private final int resortID;
  private final int liftID;
  private final int time;

  public LiftRideEvent() {
    Random random = new Random();
    this.skierID = random.nextInt(100000) + 1;
    this.resortID = random.nextInt(10) + 1;
    this.liftID = random.nextInt(40) + 1;
    this.time = random.nextInt(360) + 1;
  }

  public int getSkierID() {
    return skierID;
  }

  public int getResortID() {
    return resortID;
  }

  public int getLiftID() {
    return liftID;
  }

  public String getSeasonID() {
    return "2022";
  }

  public String getDayID() {
    return "1";
  }

  public int getTime() {
    return time;
  }
}
