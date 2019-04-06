package xjsnark.zerocash;

/*Generated by MPS */

import java.math.BigInteger;

public class PourCircuitSampleIO {

  private static int thresholdHeight = 10;

  private int[][] authPath1;
  private int index1;
  private int[][] authPath2;
  private int index2;
  private PourCircuitSampleIO.SampleCoinCommitment oldCoinComm1;
  private PourCircuitSampleIO.SampleCoinCommitment oldCoinComm2;
  private PourCircuitSampleIO.SampleCoinCommitment newCoinComm1;
  private PourCircuitSampleIO.SampleCoinCommitment newCoinComm2;
  private PourCircuitSampleIO.SampleMerkleTree merkleTree;
  private BigInteger pubVal;
  private int[] h_sig;

  private int[] h1;
  private int[] h2;

  private int[] sn1;
  private int[] sn2;



  public PourCircuitSampleIO(int height) {

    merkleTree = new PourCircuitSampleIO.SampleMerkleTree(height);
    int numExistingCoins = 1 << Math.min(height, thresholdHeight);
    index1 = TestUtilities.nextRandomPositiveInt() % numExistingCoins;
    oldCoinComm1 = merkleTree.leaves[index1];
    authPath1 = merkleTree.getAuthPath(index1);
    index2 = TestUtilities.nextRandomPositiveInt() % numExistingCoins;
    oldCoinComm2 = merkleTree.leaves[index2];
    authPath2 = merkleTree.getAuthPath(index2);

    BigInteger totalInValue = oldCoinComm1.coin.value.add(oldCoinComm2.coin.value);
    BigInteger newV1 = totalInValue.divide(BigInteger.valueOf(2));
    BigInteger newV2 = totalInValue.divide(BigInteger.valueOf(3));
    pubVal = totalInValue.subtract(newV1.add(newV2));

    newCoinComm1 = new PourCircuitSampleIO.SampleCoinCommitment(newV1);
    newCoinComm2 = new PourCircuitSampleIO.SampleCoinCommitment(newV2);

    h_sig = TestUtilities.nextRandomIntArray(8);

    sn1 = TestUtilities.PRF("sn", oldCoinComm1.coin.pubKey.sk.a_sk, oldCoinComm1.coin.rho);
    sn2 = TestUtilities.PRF("sn", oldCoinComm2.coin.pubKey.sk.a_sk, oldCoinComm2.coin.rho);

    int[] h_SigTruncated = TestUtilities.truncate(h_sig, 1);
    h1 = TestUtilities.PRF("pk", oldCoinComm1.coin.pubKey.sk.a_sk, h_SigTruncated);
    h_SigTruncated[0] = h_SigTruncated[0] | (1 << 31);
    h2 = TestUtilities.PRF("pk", oldCoinComm2.coin.pubKey.sk.a_sk, h_SigTruncated);
  }



  public class SampleMerkleTree {

    /*package*/ int[][] hashTreeOfExistingCoins;
    /*package*/ int height;

    // every level but the root will have a dummy node, computed based on dummy nodes in lower levels

    /*package*/ int[][] dummyNodes;
    /*package*/ int[] root;
    /*package*/ PourCircuitSampleIO.SampleCoinCommitment[] leaves;

    public SampleMerkleTree(int height) {

      this.height = height;

      // for simplicity of Merkle Tree construction, we generate coins for 2^h leaves, if height is small 
      // if height > threshold will generate the same dummy coin for the 2^h - 2^threshold 

      int numLeavesNonDummy = 1 << Math.min(height, thresholdHeight);
      leaves = new PourCircuitSampleIO.SampleCoinCommitment[numLeavesNonDummy];
      hashTreeOfExistingCoins = new int[numLeavesNonDummy * 2 - 1][];
      for (int i = 0; i < numLeavesNonDummy; i++) {
        leaves[i] = new PourCircuitSampleIO.SampleCoinCommitment(null);
        hashTreeOfExistingCoins[i + numLeavesNonDummy - 1] = leaves[i].digest;
      }
      for (int i = numLeavesNonDummy - 2; i >= 0; i--) {
        hashTreeOfExistingCoins[i] = TestUtilities.sha2(TestUtilities.concat(hashTreeOfExistingCoins[i * 2 + 1], 0, 8, hashTreeOfExistingCoins[i * 2 + 2], 0, 8));
      }

      root = hashTreeOfExistingCoins[0];
      if (height > thresholdHeight) {
        dummyNodes = new int[height][];
        dummyNodes[height - 1] = new PourCircuitSampleIO.SampleCoinCommitment(BigInteger.valueOf(-1)).digest;
        for (int i = height - 2; i >= 0; i--) {
          dummyNodes[i] = TestUtilities.sha2(TestUtilities.concat(dummyNodes[i + 1], 0, 8, dummyNodes[i + 1], 0, 8));
        }
        for (int i = height - thresholdHeight - 1; i >= 0; i--) {
          root = TestUtilities.sha2(TestUtilities.concat(root, 0, 8, dummyNodes[i], 0, 8));
        }
      }
    }


    public int[] getRoot() {
      return root;
    }

    public int[][] getAuthPath(int leafIdx) {

      int[][] authPath = new int[height][];

      // lower auth path nodes will appear first (different order from hashTree) 
      int treeIdx = leafIdx + leaves.length - 1;
      int i = 0;
      while (i < Math.min(thresholdHeight, height)) {
        if (treeIdx % 2 == 1) {
          authPath[i] = hashTreeOfExistingCoins[treeIdx + 1];
        } else {
          authPath[i] = hashTreeOfExistingCoins[treeIdx - 1];
        }
        treeIdx = (treeIdx - 1) / 2;
        i++;
      }

      if (height > thresholdHeight) {
        while (i < height) {
          authPath[i] = dummyNodes[height - i - 1];
          i++;
        }

      }

      return authPath;
    }
    public int[][] getHashTree() {
      return hashTreeOfExistingCoins;
    }
    public int getHeight() {
      return height;
    }
    public PourCircuitSampleIO.SampleCoinCommitment[] getLeaves() {
      return leaves;
    }
  }

  public class SampleCoinCommitment {
    /*package*/ PourCircuitSampleIO.SampleCoin coin;
    /*package*/ int[] digest;

    public SampleCoinCommitment(BigInteger value) {
      //  if value is null, a random value will be generated for the coin 
      // if value is negative, will assume it's for the dummy coins 
      if (value == null || value.signum() >= 0) {
        coin = new PourCircuitSampleIO.SampleCoin(value);
        digest = TestUtilities.computeCommitment(coin);
      } else {
        // a zero array --  we use this branch for dummy coins 
        digest = new int[8];
      }
    }


    public PourCircuitSampleIO.SampleCoin getCoin() {
      return coin;
    }
    public int[] getDigest() {
      return digest;
    }
  }

  public class SampleCoin {
    /*package*/ BigInteger value = TestUtilities.nextRandomBigInteger(63);
    /*package*/ int[] rho = TestUtilities.nextRandomIntArray(8);
    /*package*/ int[] rand = TestUtilities.nextRandomIntArray(12);
    /*package*/ PourCircuitSampleIO.SamplePublicKey pubKey = new PourCircuitSampleIO.SamplePublicKey();

    public SampleCoin(BigInteger value) {
      if (value == null) {
        this.value = TestUtilities.nextRandomBigInteger(63);
      } else {
        this.value = value;
      }
    }

    public PourCircuitSampleIO.SamplePublicKey getPubKey() {
      return pubKey;
    }
    public int[] getRand() {
      return rand;
    }
    public int[] getRho() {
      return rho;
    }
    public BigInteger getValue() {
      return value;
    }
  }

  public class SamplePublicKey {
    /*package*/ PourCircuitSampleIO.SamplePrivateKey sk = new PourCircuitSampleIO.SamplePrivateKey();
    /*package*/ int[] a_pk = TestUtilities.sha2(TestUtilities.concat(sk.a_sk, 0, 8, new int[8], 0, 8));

    public int[] getA_pk() {
      return a_pk;
    }
    public PourCircuitSampleIO.SamplePrivateKey getSk() {
      return sk;
    }
  }

  public class SamplePrivateKey {
    /*package*/ int[] a_sk = TestUtilities.nextRandomIntArray(8);

    public int[] getA_sk() {
      return a_sk;
    }
  }
  public int[][] getAuthPath1() {
    return authPath1;
  }
  public int[][] getAuthPath2() {
    return authPath2;
  }
  public int[] getH_sig() {
    return h_sig;
  }
  public int getIndex1() {
    return index1;
  }
  public int getIndex2() {
    return index2;
  }
  public PourCircuitSampleIO.SampleMerkleTree getMerkleTree() {
    return merkleTree;
  }
  public PourCircuitSampleIO.SampleCoinCommitment getNewCoinComm1() {
    return newCoinComm1;
  }
  public PourCircuitSampleIO.SampleCoinCommitment getNewCoinComm2() {
    return newCoinComm2;
  }
  public PourCircuitSampleIO.SampleCoinCommitment getOldCoinComm1() {
    return oldCoinComm1;
  }
  public PourCircuitSampleIO.SampleCoinCommitment getOldCoinComm2() {
    return oldCoinComm2;
  }
  public BigInteger getPubVal() {
    return pubVal;
  }
  public int[] getH1() {
    return h1;
  }
  public int[] getH2() {
    return h2;
  }
  public int[] getSn1() {
    return sn1;
  }
  public int[] getSn2() {
    return sn2;
  }
}