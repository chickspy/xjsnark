package xjsnark.zerocash;

/*Generated by MPS */

import java.util.Random;
import java.math.BigInteger;

public class TestUtilities {

  /*package*/ static Random rand = new Random(1);

  public static final long[] K_CONST = {1116352408L, 1899447441L, 3049323471L, 3921009573L, 961987163L, 1508970993L, 2453635748L, 2870763221L, 3624381080L, 310598401L, 607225278L, 1426881987L, 1925078388L, 2162078206L, 2614888103L, 3248222580L, 3835390401L, 4022224774L, 264347078L, 604807628L, 770255983L, 1249150122L, 1555081692L, 1996064986L, 2554220882L, 2821834349L, 2952996808L, 3210313671L, 3336571891L, 3584528711L, 113926993L, 338241895L, 666307205L, 773529912L, 1294757372L, 1396182291L, 1695183700L, 1986661051L, 2177026350L, 2456956037L, 2730485921L, 2820302411L, 3259730800L, 3345764771L, 3516065817L, 3600352804L, 4094571909L, 275423344L, 430227734L, 506948616L, 659060556L, 883997877L, 958139571L, 1322822218L, 1537002063L, 1747873779L, 1955562222L, 2024104815L, 2227730452L, 2361852424L, 2428436474L, 2756734187L, 3204031479L, 3329325298L};
  public static final long[] H_CONST = {1779033703L, 3144134277L, 1013904242L, 2773480762L, 1359893119L, 2600822924L, 528734635L, 1541459225L};


  public static BigInteger nextRandomBigInteger(int numBits) {
    BigInteger result = new BigInteger(numBits, rand);
    return result;
  }

  public static int nextRandomInt() {
    return rand.nextInt();
  }

  public static int nextRandomPositiveInt() {
    return rand.nextInt(Integer.MAX_VALUE);
  }


  public static int[] nextRandomIntArray(int n) {
    int[] a = new int[n];
    for (int i = 0; i < n; i++) {
      a[i] = nextRandomInt();
    }
    return a;
  }

  public static int[] zeroArray(int n) {
    int[] a = new int[n];
    return a;
  }


  public static int[] sha2(int[] input) {
    if (input.length != 16) {
      throw new IllegalArgumentException("This method only accepts 16 32-bit words as inputs");
    }

    int[] digest = new int[8];
    int[] words = new int[64];


    for (int i = 0; i < 8; i++) {
      digest[i] = ((int) H_CONST[i]);
    }


    int a = digest[0];
    int b = digest[1];
    int c = digest[2];
    int d = digest[3];
    int e = digest[4];
    int f = digest[5];
    int g = digest[6];
    int h = digest[7];

    for (int j = 0; j < 16; j++) {
      words[j] = input[j];
    }

    for (int j = 16; j < 64; j++) {
      int s0 = rotateRight(words[j - 15], 7) ^ rotateRight(words[j - 15], 18) ^ (words[j - 15] >>> 3);
      int s1 = rotateRight(words[j - 2], 17) ^ rotateRight(words[j - 2], 19) ^ (words[j - 2] >>> 10);
      words[j] = words[j - 16] + s0 + words[j - 7] + s1;
    }

    for (int j = 0; j < 64; j++) {
      int s0 = rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22);
      int maj = (a & b) ^ (a & c) ^ (b & c);
      int t2 = s0 + maj;

      int s1 = rotateRight(e, 6) ^ rotateRight(e, 11) ^ rotateRight(e, 25);
      int ch = e & f ^ ~(e) & g;
      int t1 = h + s1 + ch + ((int) K_CONST[j]) + words[j];
      h = g;
      g = f;
      f = e;
      e = d + t1;
      d = c;
      c = b;
      b = a;
      a = t1 + t2;
    }


    digest[0] = digest[0] + a;
    digest[1] = digest[1] + b;
    digest[2] = digest[2] + c;
    digest[3] = digest[3] + d;
    digest[4] = digest[4] + e;
    digest[5] = digest[5] + f;
    digest[6] = digest[6] + g;
    digest[7] = digest[7] + h;

    return digest;




  }

  public static int rotateRight(int in, int r) {
    return (in >>> r) | (in << (32 - r));
  }

  public static int[] concat(int[] a1, int idx1, int l1, int[] a2, int idx2, int l2) {
    int[] res = new int[l1 + l2];
    for (int i = 0; i < l1; i++) {
      res[i] = a1[i + idx1];
    }
    for (int i = 0; i < l2; i++) {
      res[i + l1] = a2[i + idx2];
    }
    return res;
  }

  public static int[] computeCommitment(PourCircuitSampleIO.SampleCoin coin) {
    return COMM_s(COMM_r(coin.rand, coin.pubKey.a_pk, coin.rho), coin.value);
  }


  public static int[] PRF(String type, int[] x, int[] z) {
    // truncate 2 least significant bits 
    // See page 22 in  http://zerocash-project.org/media/pdf/zerocash-extended-20140518.pdf  
    z = truncate(z, 2);

    int mask = 0;
    if (type.equals("addr")) {
      mask = 0;
    } else if (type.equals("sn")) {
      mask = 0x40000000;
    } else if (type.equals("pk")) {
      mask = 0x80000000;
    }

    int[] input = new int[16];
    for (int i = 0; i < 16; i++) {
      if (i < 8) {
        input[i] = x[i];
      } else if (i == 8) {
        input[i] = z[i - 8] | mask;
      } else {
        input[i] = z[i - 8];
      }
    }
    return TestUtilities.sha2(input);
  }

  private static int[] COMM_r(int[] r, int[] a_pk, int[] rho) {
    int[] input1 = TestUtilities.concat(a_pk, 0, a_pk.length, rho, 0, rho.length);
    int[] out1 = TestUtilities.sha2(input1);
    int[] input2 = TestUtilities.concat(r, 0, r.length, out1, 0, out1.length / 2);
    return TestUtilities.sha2(input2);
  }

  private static int[] COMM_s(int[] k, BigInteger val) {
    int[] paddedVal = new int[]{0, 0, 0, 0, 0, 0, val.shiftRight(32).intValue(), val.intValue()};
    int[] input = TestUtilities.concat(k, 0, k.length, paddedVal, 0, paddedVal.length);
    return TestUtilities.sha2(input);
  }


  // truncates n least signicant bits. n is assumed to be <= 32
  // This is to follow the implementation decision in (page 22): http://zerocash-project.org/media/pdf/zerocash-extended-20140518.pdf

  public static int[] truncate(int[] words, int n) {

    if (n > 32 || n < 0) {
      throw new IllegalArgumentException("Invalid truncation argument");
    }
    int[] t = new int[words.length];
    for (int i = 0; i < words.length; i++) {
      t[i] = words[i];
    }
    t[words.length - 1] = t[words.length - 1] >>> n;
    for (int i = words.length - 2; i >= 0; i--) {
      t[i + 1] = t[i + 1] | (t[i] << (32 - n));
      t[i] = t[i] >>> n;
    }
    return t;
  }


  public static BigInteger intToUnsignedBigInteger(int x) {
    if (x >= 0) {
      return BigInteger.valueOf(x);
    } else {
      return BigInteger.valueOf(x).add(BigInteger.ONE.shiftLeft(32));
    }
  }




}