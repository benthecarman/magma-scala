package space.amboss

import akka.actor.ActorSystem
import org.bitcoins.core.currency.Satoshis
import org.bitcoins.core.protocol.ln.node.NodeId
import space.amboss.Amboss._
import sttp.capabilities.akka.AkkaStreams
import sttp.client3.akkahttp.AkkaHttpBackend
import sttp.client3.{SttpBackend, UriContext}

import scala.concurrent.Future

class AmbossClient(private[amboss] val apiKeyOpt: Option[String])(implicit
    system: ActorSystem) {
  import system.dispatcher

  final private val uri = uri"https://api.amboss.space/graphql"

  implicit private val backend: SttpBackend[Future, AkkaStreams] =
    AkkaHttpBackend.usingActorSystem(system)

  def getTickers(): Future[Vector[TickerView]] = {
    val query =
      Query.getTickers {
        (Ticker.currency ~ Ticker.price.map(BigDecimal(_))).mapN(TickerView)
      }

    query.toRequest(uri).send(backend).map(_.body).map {
      case Left(err)    => throw new RuntimeException(err.toString)
      case Right(value) => value.toVector
    }
  }

  def getSignInfo(): Future[LoginSignInfo] = {
    val query = Query.getSignInfo {
      (SignInfo.expiry ~ SignInfo.identifier ~ SignInfo.message)
        .mapN(LoginSignInfo)
    }

    query.toRequest(uri).send(backend).map(_.body).map {
      case Left(err)    => throw new RuntimeException(err.toString)
      case Right(value) => value
    }
  }

  def login(
      seconds: Long,
      identifier: String,
      signature: String): Future[AmbossClient] = {
    val query = Mutation.login(details = Some("From magma-scala"),
                               identifier = identifier,
                               seconds = Some(seconds.toDouble),
                               signature = signature,
                               token = Some(true))

    query.toRequest(uri).send(backend).map(_.body).map {
      case Left(err)    => throw new RuntimeException(err.toString)
      case Right(value) => new AmbossClient(Some(value))
    }
  }

  def getUser(): Future[AmbossUser] = {
    require(apiKeyOpt.isDefined, "No API key provided")

    val query = Query.getUser {
      val marketQuery =
        (UserMarketInfo.enabled ~ UserMarketInfo.has_active_offers ~
          UserMarketInfo.pending_buyer_orders ~ UserMarketInfo.pending_seller_orders)
          .mapN(AmbossUserMarketInfo)

      (UserInfo.pubkey.map(NodeId(_)) ~ UserInfo.market(marketQuery))
        .mapN(AmbossUser)
    }

    val request =
      query.toRequest(uri).header("Authorization", "Bearer " + apiKeyOpt.get)

    request.send(backend).map(_.body).map {
      case Left(err) => throw new RuntimeException(err.toString)
      case Right(value) =>
        value.getOrElse(throw new RuntimeException("No user found"))
    }
  }

  // ---- Magma API ----

  private def ambossStringToSatoshis(string: String): Satoshis = {
    Satoshis(string.toDouble.toLong)
  }

  def getMagmaStats(): Future[MagmaStats] = {
    val channelsQ =
      (MagmaStatsInfo.average_apr.map(
        _.toDouble) ~ MagmaStatsInfo.completed_fees.map(
        ambossStringToSatoshis) ~
        MagmaStatsInfo.completed_orders.map(
          _.toInt) ~ MagmaStatsInfo.completed_size.map(
          ambossStringToSatoshis) ~ MagmaStatsInfo.latest_apr.map(_.toDouble))
        .mapN(MagmaChannelsStats)

    val swapsQ =
      (SwapsStatsInfo.completed_fees.map(
        ambossStringToSatoshis) ~ SwapsStatsInfo.completed_orders.map(
        _.toInt) ~ SwapsStatsInfo.completed_size.map(ambossStringToSatoshis))
        .mapN(MagmaSwapsStats)

    val query = Query.getMagmaInfo {
      (MagmaInfo.stats(channelsQ) ~ MagmaInfo.swap_stats(swapsQ))
        .mapN(MagmaStats)
    }

    query.toRequest(uri).send(backend).map(_.body).map {
      case Left(err)    => throw new RuntimeException(err.toString)
      case Right(value) => value
    }
  }
}
