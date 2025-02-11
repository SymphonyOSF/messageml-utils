package org.finos.symphony.messageml.messagemlutils.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Short id generator. Url-friendly. Non-predictable. Cluster-compatible.
 *
 * <p></p>
 * Inspired from <a href="https://github.com/dylang/shortid">shortid</a>
 */
public class ShortID {

  public static final String DEFAULT_ALPHABET =
      "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-";

  private static final long DEFAULT_REDUCE_TIME = 1403265799803L;

  private static final int DEFAULT_VERSION = 6;
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final Random random;

  // Ignore all milliseconds before a certain time to reduce the size of the date entropy without
  // sacrificing uniqueness.
  // This number should be updated every year or so to keep the generated id short.
  // To regenerate `new Date() - 0` and bump the version. Always bump the version!
  private final long reduceTime;

  // don't change unless we change the algos or REDUCE_TIME
  // must be an integer and less than 16
  private final int version;

  // if you are using cluster or multiple servers use this to make each instance
  // has a unique value for worker
  // Note: I don't know if this is automatically set when using third
  // party cluster solutions such as pm2.
  private final int clusterWorkerId;
  private char[] shuffled;
  // Counter is used when shortid is called multiple times in one second.
  private final AtomicInteger counter;
  // Remember the last time shortid was called in case counter is needed.
  private volatile long previousSeconds;

  private ShortID(Random random, long reduceTime, int version,
      int clusterWorkerId) {
    this.random = random;
    this.counter = new AtomicInteger();
    this.reduceTime = reduceTime;
    this.version = version;
    this.clusterWorkerId = clusterWorkerId;
  }

  public ShortID() {
    this(SECURE_RANDOM, DEFAULT_REDUCE_TIME, DEFAULT_VERSION, 0);
  }

  /**
   * Generate unique id and returns it.
   */
  public String generate() {
    // this is costly to initialize so we do it only on first call
    // this is not thread safe but it should not have any impact, we just want random letters
    if (shuffled == null) {
      shuffled = shuffle(DEFAULT_ALPHABET);
    }

    String str = "";

    long seconds = (long) Math.floor((System.currentTimeMillis() - reduceTime) * 0.001);

    int counterValue;
    if (seconds == previousSeconds) {
      counterValue = counter.incrementAndGet();
    } else {
      counter.set(0);
      counterValue = 0;
      previousSeconds = seconds;
    }

    str = str + encode(version);
    str = str + encode(clusterWorkerId);

    if (counterValue > 0) {
      str = str + encode(counterValue);
    }

    str = str + encode((int) seconds);

    return str;
  }

  private String encode(int number) {
    int loopCounter = 0;
    boolean done = false;

    StringBuilder str = new StringBuilder();

    int index;
    while (!done) {
      index = ((number >> (4 * loopCounter)) & 0x0f) | randomByte();
      str.append(shuffled[index]);
      done = number < (Math.pow(16, loopCounter + 1.0));
      loopCounter++;
    }
    return str.toString();
  }

  private int randomByte() {
    byte[] bytes = new byte[1];
    random.nextBytes(bytes);
    return bytes[0] & 0x30;
  }

  private char[] shuffle(String alphabet) {
    StringBuilder source = new StringBuilder(alphabet);
    StringBuilder target = new StringBuilder(source.length());

    double r;
    int charIndex;

    while (source.length() > 0) {
      r = random.nextDouble();
      charIndex = (int) Math.floor(r * source.length());

      target.append(source.charAt(charIndex));
      source.deleteCharAt(charIndex);
    }

    return target.toString().toCharArray();
  }
}
