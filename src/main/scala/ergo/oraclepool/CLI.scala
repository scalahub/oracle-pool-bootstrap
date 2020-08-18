package ergo.oraclepool

import ergo.api.ErgoAPI
import org.bouncycastle.crypto.digests.Blake2bDigest

object GeneratePoolToken {
  def main(args: Array[String]): Unit = {
    val address = args(0)
    ergo.api.ErgoAPI.issueAsset(address, 1, "POOL", "oracle pool token", 0)
  }
}

object GenerateOracleTokens {
  def main(args: Array[String]): Unit = {
    val address = args(0)
    val count = args(1).toInt
    ergo.api.ErgoAPI.issueAsset(
      address,
      count,
      "ORACLE",
      "oracle pool datapoint token",
      0
    )
  }
}

/*

Usage:
java -cp <jarFile> ergo.oraclepool.GetAddresses <oracleTokenId> <poolTokenId>

Example:
java -cp target/scala-2.12/OracleBootstrap-assembly-0.1.jar ergo.oraclepool.GetAddresses 12caaacb51c89646fac9a3786eb98d0113bd57d68223ccc11754a4f67281daed b662db51cf2dc39f110a021c2a31c74f0a1a18ffffbf73e8a051a7b8c0f09ebc

LiveEpoch: USNVj4rN2DctyA1X4Wc7k1DpgW8MgSmLbXxeDgL3CvheUhjTRZPB4Pp6tqG8yZnLQkJVEvgHeWQjMwbKcYdKkGwKYN7coUCPXDwVjsniPEu7wuyzVsWrDH4q8CRatjfiVE3U8growjbfNUq6xcg8AQdShGAhduZpYeUULp7bgTHwQe7c1oWaFLKszSaZwKExY8TtrvJJacK4mj5bMFAzYGwrTNvMenpYqaiUfPd5e5i2vx3dT23RXYpJs6GZ4Mgyr2yYo98MKWUhxnfBK4UBSm1MLwH3p3oFii3L2KkUrZpBtP5tckGHVi98Hwew2cMhWNugyVRv328MSXTm8USZx1DpAvRxDd4JgCjnxzfWyFUa1qvWXKDcpig9Q9WMucXn8USd1vjT5n5V4h5kBxqsNFZzRmbTGA7KUmgnTu7kFE5PjkfJZSPNZJNmazG6UmjBZhs6DXnaHTnccFtjC11eWYsMm1pU4d4Y4fsJpd6z
EpochPrep: Gxd4hMRT5aE7dxAoMpoGtj74HorsenAQqMhxvzpT7GQEbeUTrx3ZjYHotQn54Ye1ddrLLmLsya6ryMvJYPkcdkCvyv6CMBshCDpEebRdfKGxKaNZ3QEYPomT1eqX594zZJhXddD9eGSXndCzSWLVkcxpFx3ubCWo7zCox1hZqWMbSUGmXLJPWRLpGb8DTBTzxEfqSugnJEUwgg7a2hyh61wthK4FCM7y3zk4vNYYrdGWwKAW2Dz5VPoHaMh3zbRtQbwpdfYBdSbbBHn4MexaXe9SHNU9aP5mSzb8cnYGgE22kLgtYeBq3BPmqeJp3usRz3QYuCE8Z727n6fFGHzJw5drVWetG24eqYkzVoN7mF6DtRxsjrXnQF3u3ofzgzKPgR7Hi6Me8Puz2s
DataPoint: jL2aaqw6XU61SZznxeykLpREPzSmZv8bwbjEsJD6DMfXQLgBc12wMmPpVD81JnLuZUjHRKPysKxKQhcBaqDs7ZAtYwRuYmQojzKK9bHXDUY8N4BiJx8AUG8VEaggD4ztWSeQHrW7EbFxpXgaMKuzuN1Gq4zoYDArstgcrHKwg2uCeGeXiydQXRWEyE8e6noAP13nUBSmNNNVqkM9JGUVAJYo4GGdVFg8FRtFWcNdtbxCKfw4JGVhakCGj4qvd
Deposit: zLSQDVBaDkFiQhpgVYqu9saX3ppCMzmma1qdryGH1x1GTkAjU9vVodDsYrk3H5UvqDmdxJLoDADg69KXyL9gVGW2NER7GxMotdh46Bzr9P9tJwPdgvNhSdoXYrLTemKadCU46aGy81YneoKB7xjz3a1v4Aar3n71XysQ6HwdKcJt8WFKqbZmRx4JnJTtBUtsdD184oU623BXA93cGrG1fFuFzSALqGztnS9Ai4JP6NcM8LE2wU
 */

object GetAddresses {
  def main(args: Array[String]): Unit = {
    val oracleTokenIdBase58 = args(0).decodeHex.encodeBase58
    val poolTokenIdBase58 = args(1).decodeHex.encodeBase58
    val minPoolBoxValue = minBoxValue + (numOracles + 1) * oracleReward
    val liveEpochScript =
      s"""{ val oldDatapoint = SELF.R4[Long].get;
         |  val delta = oldDatapoint / 100 * $errorMargin;
         |  val minDatapoint = oldDatapoint - delta;
         |  val maxDatapoint = oldDatapoint + delta;
         |
         |  val oracleBoxes = CONTEXT.dataInputs.filter{(b:Box) =>
         |    b.R5[Coll[Byte]].get == SELF.id &&
         |    b.tokens(0)._1 == fromBase58(\\"$oracleTokenIdBase58\\") &&
         |    b.R6[Long].get >= minDatapoint &&
         |    b.R6[Long].get <= maxDatapoint
         |  };
         |
         |  val pubKey = oracleBoxes.map{(b:Box) => proveDlog(b.R4[GroupElement].get)}(0);
         |
         |  val sum = oracleBoxes.fold(0L, { (t:Long, b: Box) => t + b.R6[Long].get });
         |
         |  val average = sum / oracleBoxes.size;
         |
         |  val oracleRewardOutputs = oracleBoxes.fold((1, true), { (t:(Int, Boolean), b:Box) =>
         |    (t._1 + 1, t._2 &&
         |               OUTPUTS(t._1).propositionBytes == proveDlog(b.R4[GroupElement].get).propBytes &&
         |               OUTPUTS(t._1).value >= $oracleReward)
         |  });
         |
         |  val epochPrepScriptHash = SELF.R6[Coll[Byte]].get;
         |
         |  sigmaProp(
         |    blake2b256(OUTPUTS(0).propositionBytes) == epochPrepScriptHash &&
         |    oracleBoxes.size > 0 &&
         |    OUTPUTS(0).tokens == SELF.tokens &&
         |    OUTPUTS(0).R4[Long].get == average &&
         |    OUTPUTS(0).R5[Int].get == SELF.R5[Int].get + $epochPeriod &&
         |    OUTPUTS(0).value >= SELF.value - (oracleBoxes.size + 1) * $oracleReward &&
         |    oracleRewardOutputs._2
         |  ) && pubKey
         |}
         |""".stripMargin.lines.mkString

    val liveEpochAddress = ErgoAPI.getP2SAddress(liveEpochScript)
    println
    println(s"LiveEpoch: $liveEpochAddress")

    val liveEpochErgoTree: Array[Byte] = ErgoAPI.getErgoTree(liveEpochAddress).decodeHex
    val digest = new Blake2bDigest(256)
    val liveEpochScriptHash = new Array[Byte](32)
    digest.update(liveEpochErgoTree, 0, liveEpochErgoTree.length)
    digest.doFinal(liveEpochScriptHash, 0)
    val liveEpochScriptHashBase58 = liveEpochScriptHash.encodeBase58

    val epochPrepScript =
      s"""
         |{ val canStartEpoch = HEIGHT > SELF.R5[Int].get - $livePeriod;
         |  val epochNotOver = HEIGHT < SELF.R5[Int].get;
         |  val epochOver = HEIGHT >= SELF.R5[Int].get;
         |  val enoughFunds = SELF.value >= $minPoolBoxValue;
         |
         |  val maxNewEpochHeight = HEIGHT + $epochPeriod + $buffer;
         |  val minNewEpochHeight = HEIGHT + $epochPeriod;
         |
         |  if (OUTPUTS(0).R6[Coll[Byte]].isDefined) {
         |    val isliveEpochOutput = OUTPUTS(0).R6[Coll[Byte]].get == blake2b256(SELF.propositionBytes) &&
         |                            blake2b256(OUTPUTS(0).propositionBytes) == fromBase58(\\"$liveEpochScriptHashBase58\\");
         |    sigmaProp(
         |      epochNotOver && canStartEpoch && enoughFunds &&
         |      OUTPUTS(0).R4[Long].get == SELF.R4[Long].get &&
         |      OUTPUTS(0).R5[Int].get == SELF.R5[Int].get &&
         |      OUTPUTS(0).tokens == SELF.tokens &&
         |      OUTPUTS(0).value >= SELF.value &&
         |      isliveEpochOutput
         |    ) || sigmaProp(
         |      epochOver &&
         |      enoughFunds &&
         |      OUTPUTS(0).R4[Long].get == SELF.R4[Long].get &&
         |      OUTPUTS(0).R5[Int].get >= minNewEpochHeight &&
         |      OUTPUTS(0).R5[Int].get <= maxNewEpochHeight &&
         |      OUTPUTS(0).tokens == SELF.tokens &&
         |      OUTPUTS(0).value >= SELF.value &&
         |      isliveEpochOutput
         |    )
         |  } else {
         |    sigmaProp(
         |      OUTPUTS(0).R4[Long].get == SELF.R4[Long].get &&
         |      OUTPUTS(0).R5[Int].get == SELF.R5[Int].get &&
         |      OUTPUTS(0).propositionBytes == SELF.propositionBytes &&
         |      OUTPUTS(0).tokens == SELF.tokens &&
         |      OUTPUTS(0).value > SELF.value
         |    )
         |  }
         |}
         |""".stripMargin.lines.mkString

    val epochPrepAddress = ErgoAPI.getP2SAddress(epochPrepScript)
    println
    println(s"EpochPrep: $epochPrepAddress")

    val dataPointScript =
      s"""
         |{ val pubKey = SELF.R4[GroupElement].get;
         |  val liveEpochBox = CONTEXT.dataInputs(0);
         |  val validLiveEpochBox = liveEpochBox.tokens(0)._1 == fromBase58(\\"$poolTokenIdBase58\\") &&
         |                          blake2b256(liveEpochBox.propositionBytes) == fromBase58(\\"$liveEpochScriptHashBase58\\");
         |  sigmaProp(
         |    OUTPUTS(0).R4[GroupElement].get == pubKey &&
         |    OUTPUTS(0).R5[Coll[Byte]].get == liveEpochBox.id &&
         |    OUTPUTS(0).R6[Long].get > 0 &&
         |    OUTPUTS(0).propositionBytes == SELF.propositionBytes &&
         |    OUTPUTS(0).tokens == SELF.tokens &&
         |    validLiveEpochBox
         |  ) && proveDlog(pubKey)
         |}
         |""".stripMargin.lines.mkString

    val dataPointAddress = ErgoAPI.getP2SAddress(dataPointScript)
    println
    println(s"DataPoint: $dataPointAddress")

    val epochPrepErgoTree: Array[Byte] = ErgoAPI.getErgoTree(epochPrepAddress).decodeHex
    val epochPrepScriptHash = new Array[Byte](32)
    digest.update(epochPrepErgoTree, 0, epochPrepErgoTree.length)
    digest.doFinal(epochPrepScriptHash, 0)
    val epochPrepScriptHashBase58 = epochPrepScriptHash.encodeBase58

    val depositScript =
      s"""
         |{
         |  val allFundingBoxes = INPUTS.filter{(b:Box) =>
         |    b.propositionBytes == SELF.propositionBytes
         |  };
         |  val totalFunds = allFundingBoxes.fold(0L, { (t:Long, b: Box) => t + b.value });
         |  sigmaProp(
         |    blake2b256(INPUTS(0).propositionBytes) == fromBase58(\\"$epochPrepScriptHashBase58\\") &&
         |    OUTPUTS(0).propositionBytes == INPUTS(0).propositionBytes &&
         |    OUTPUTS(0).value >= INPUTS(0).value + totalFunds &&
         |    OUTPUTS(0).tokens(0)._1 == fromBase58(\\"$poolTokenIdBase58\\")
         |  )
         |}
         |""".stripMargin.lines.mkString

    val depositAddress = ErgoAPI.getP2SAddress(depositScript)
    println
    println(s"Deposit: $depositAddress")
  }
}

object BootStrapPool {
  def main(args: Array[String]): Unit = {
    val epochPrepAddress = args(0)
    val poolTokenId = args(1)
    val initialDataPoint = args(2).toLong
    val r4: String = "0502" // construct using initialDataPoint
    val r5: String = "0480b518"
    val sendRes = ergo.api.ErgoAPI.send(epochPrepAddress, minBoxValue, poolTokenId, 1, Seq(r4, r5))
    println(sendRes)
  }
}

object BootStrapOracle {
  def main(args: Array[String]): Unit = {
    val proveDlogAddress = args(0)
    val oracleTokenId = args(1)
    val eccPoint = ergo.api.ErgoAPI.getEccPoint(proveDlogAddress)
    val r4: String = "07" + eccPoint
    val r5: String = "0e0101"
    val r6: String = "0504"
    val sendRes = ergo.api.ErgoAPI.send(proveDlogAddress, minBoxValue, oracleTokenId, 1, Seq(r4, r5, r6))
    println(sendRes)
  }
}
