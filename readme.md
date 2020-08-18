# Bootstrap CLI
This is a CLI to bootstrap the oracle pool. The CLI has 5 modules (5 main classes) for each of the following tasks:
1. `ergo.oraclepool.GeneratePoolToken` to generate a singleton pool token
2. `ergo.oraclepool.GenerateOracleTokens` to generate multiple oracle tokens
3. `ergo.oraclepool.GetAddresses` Getting the addresses using the tokens generated
4. `ergo.oraclepool.BootstrapPool` Bootstrapping the epoch preparation box using the address and tokens
5. `ergo.oraclepool.BootstrapOracle` Bootstrapping the oracle boxes using the address and tokens

## Step 1: Configure options

Edit the file [ergp/oraclepool/package.scala](src/main/scala/ergo/oraclepool/package.scala) and update following parameters as desired:

    val oracleReward = 2500000L // NanoErgs
    val minBoxValue = 1500000L // NanoErgs
    val errorMargin = 50 // percent
    val numOracles = 14
    val epochPeriod = 30 // blocks
    val livePeriod = 20 // blocks
    val buffer = 5 // blocks

Edit the file [ergo/api/ErgoAPI.scala](src/main/scala/ergo/api/ErgoAPI.scala) and update following parameters as desired:

    private var apiKey = "hello"
    var baseUrl = "http://192.168.0.200:9053/"
    private val defaultFee = 2000000
    
## Step 2: Compile the Jar

Use the command:
 
    sbt assembly
    
The jar will be stored as `target/scala-2.12/oracle-pool-bootstrap.jar`. 
Copy the jar to some desired location. 
 
## Step 3: Issue tokens

Usage:

    java -cp oracle-pool-bootstrap.jar ergo.oraclepool.GeneratePoolToken <address_to_store_tokens_in>
    java -cp oracle-pool-bootstrap.jar ergo.oraclepool.GenerateOracleTokens <address_to_store_tokens_in> <num_tokens>

Example:

    java -cp oracle-pool-bootstrap.jar ergo.oraclepool.GeneratePoolToken 9fcrXXaJgrGKC8iu98Y2spstDDxNccXSR9QjbfTvtuv7vJ3NQLk
    java -cp oracle-pool-bootstrap.jar ergo.oraclepool.GenerateOracleTokens 9fcrXXaJgrGKC8iu98Y2spstDDxNccXSR9QjbfTvtuv7vJ3NQLk 20

## Step 4: Get addresses

Usage:

    java -cp oracle-pool-bootstrap.jar ergo.oraclepool.GetAddresses <oracleTokenId> <poolTokenId>

Example:

    java -cp oracle-pool-bootstrap.jar  ergo.oraclepool.GetAddresses 12caaacb51c89646fac9a3786eb98d0113bd57d68223ccc11754a4f67281daed b662db51cf2dc39f110a021c2a31c74f0a1a18ffffbf73e8a051a7b8c0f09ebc

Output:

    LiveEpoch: USNVj4rN2DctyA1X4Wc7k1DpgW8MgSmLbXxeDgL3CvheUhjTRZPB4Pp6tqG8yZnLQkJVEvgHeWQjMwbKcYdKkGwKYN7coUCPXDwVjsniPEu7wuyzVsWrDH4q8CRatjfiVE3U8growjbfNUq6xcg8AQdShGAhduZpYeUULp7bgTHwQe7c1oWaFLKszSaZwKExY8TtrvJJacK4mj5bMFAzYGwrTNvMenpYqaiUfPd5e5i2vx3dT23RXYpJs6GZ4Mgyr2yYo98MKWUhxnfBK4UBSm1MLwH3p3oFii3L2KkUrZpBtP5tckGHVi98Hwew2cMhWNugyVRv328MSXTm8USZx1DpAvRxDd4JgCjnxzfWyFUa1qvWXKDcpig9Q9WMucXn8USd1vjT5n5V4h5kBxqsNFZzRmbTGA7KUmgnTu7kFE5PjkfJZSPNZJNmazG6UmjBZhs6DXnaHTnccFtjC11eWYsMm1pU4d4Y4fsJpd6z
    EpochPrep: Gxd4hMRT5aE7dxAoMpoGtj74HorsenAQqMhxvzpT7GQEbeUTrx3ZjYHotQn54Ye1ddrLLmLsya6ryMvJYPkcdkCvyv6CMBshCDpEebRdfKGxKaNZ3QEYPomT1eqX594zZJhXddD9eGSXndCzSWLVkcxpFx3ubCWo7zCox1hZqWMbSUGmXLJPWRLpGb8DTBTzxEfqSugnJEUwgg7a2hyh61wthK4FCM7y3zk4vNYYrdGWwKAW2Dz5VPoHaMh3zbRtQbwpdfYBdSbbBHn4MexaXe9SHNU9aP5mSzb8cnYGgE22kLgtYeBq3BPmqeJp3usRz3QYuCE8Z727n6fFGHzJw5drVWetG24eqYkzVoN7mF6DtRxsjrXnQF3u3ofzgzKPgR7Hi6Me8Puz2s
    DataPoint: jL2aaqw6XU61SZznxeykLpREPzSmZv8bwbjEsJD6DMfXQLgBc12wMmPpVD81JnLuZUjHRKPysKxKQhcBaqDs7ZAtYwRuYmQojzKK9bHXDUY8N4BiJx8AUG8VEaggD4ztWSeQHrW7EbFxpXgaMKuzuN1Gq4zoYDArstgcrHKwg2uCeGeXiydQXRWEyE8e6noAP13nUBSmNNNVqkM9JGUVAJYo4GGdVFg8FRtFWcNdtbxCKfw4JGVhakCGj4qvd
    Deposit: zLSQDVBaDkFiQhpgVYqu9saX3ppCMzmma1qdryGH1x1GTkAjU9vVodDsYrk3H5UvqDmdxJLoDADg69KXyL9gVGW2NER7GxMotdh46Bzr9P9tJwPdgvNhSdoXYrLTemKadCU46aGy81YneoKB7xjz3a1v4Aar3n71XysQ6HwdKcJt8WFKqbZmRx4JnJTtBUtsdD184oU623BXA93cGrG1fFuFzSALqGztnS9Ai4JP6NcM8LE2wU

## Step 5: Bootstrap Pool

TBD

## Step 6: Bootstrap Oracles

TBD
