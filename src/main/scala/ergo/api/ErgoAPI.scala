package ergo.api

import ergo.api.Curl._
import org.json.JSONObject

object ErgoAPI {
  private var apiKey = "hello"
  var baseUrl = "http://192.168.0.200:9053/"
  private val defaultFee = 2000000

  private def authHeader: Array[(String, String)] =
    Array(
      ("accept", "application/json"),
      ("api_key", apiKey),
      ("Content-Type", "application/json")
    )

  private val noAuthHeader: Array[(String, String)] = Array(("accept", "application/json"))

  private def query(
      endPoint: String,
      isAuth: Boolean = false,
      reqType: ReqType = Get,
      params: Seq[(String, String)] = Nil,
      data: Option[String] = None
  ) =
    curl(
      baseUrl + endPoint,
      if (isAuth) authHeader else noAuthHeader,
      reqType,
      params
    )(data)

  def getP2SAddress(script: String): String =
    new JSONObject(query("script/p2sAddress", isAuth = false, PostJsonRaw, Nil, Some(s"""{"source":"$script"}"""))).get("address").toString

  def getErgoTree(address: String): String = new JSONObject(query(s"script/addressToTree/$address")).get("tree").toString

  def getEccPoint(address: String): String = new JSONObject(query(s"utils/addressToRaw/$address")).get("raw").toString

  def issueAsset(address: String, amount: Long, name: String, description: String, decimals: Int): String = {
    val jsonRaw =
      s"""
         |{
         |  "requests": [
         |    {
         |      "address": "$address",
         |      "amount": $amount,
         |      "name": "$name",
         |      "description": "$description",
         |      "decimals": $decimals
         |    }
         |  ],
         |  "fee": $defaultFee,
         |  "inputsRaw": [],
         |  "dataInputsRaw": []
         |}
         |""".stripMargin
    query("wallet/transaction/send", isAuth = true, PostJsonRaw, Nil, Some(jsonRaw))
  }

  def send(address: String, amount: Long, tokenId: String, tokenAmount: Long, registers: Seq[String]): String = {
    val registerString: String = Seq("R4", "R5", "R6") zip registers map {
      case (r, v) => s""""$r":"$v""""
    } mkString ","

    val jsonRaw =
      s"""
         |{
         |  "requests": [
         |    {
         |      "address": "$address",
         |      "value": $amount,
         |      "assets": [
         |         {
         |           "tokenId": "$tokenId",
         |           "amount": $tokenAmount
         |         }
         |      ], 
         |      "registers": {
         |         $registerString
         |      }
         |    }
         |  ],
         |  "fee": $defaultFee
         |}
         |""".stripMargin

    println("send request")
    println(jsonRaw)
    query("wallet/transaction/send", isAuth = true, PostJsonRaw, Nil, Some(jsonRaw))
  }

}
