package space.amboss

import org.bitcoins.core.currency.Satoshis
import org.bitcoins.core.protocol.ln.node.NodeId
import org.bitcoins.testkit.util.BitcoinSAsyncTest

import scala.util.Properties

class AmbossClientTest extends BitcoinSAsyncTest {

  val apiKeyOpt: Option[String] = Properties.envOrNone("AMBOSS_API_KEY")

  val client = new AmbossClient(apiKeyOpt)

  it must "get tickers" in {
    client.getTickers().map { tickers =>
      tickers.find(_.currency == "USD") match {
        case Some(usd) =>
          assert(usd.price > 10_000)
        case None => fail("usd ticket not found")
      }
    }
  }

  it must "get sign info" in {
    client.getSignInfo().map { info =>
      assert(info.identifier.nonEmpty)
      assert(info.message.nonEmpty)
    }
  }

  // todo move to docs
// only works on main net, needs lnd
//  it must "login" ignore { lnd =>
//    for {
//      info <- client.getSignInfo()
//      req = SignMessageRequest(ByteString.copyFromUtf8(info.message))
//      sig <- lnd.lnd.signMessage(req)
//      res <- client.login(60, info.identifier, sig.signature)
//    } yield assert(res.apiKeyOpt.nonEmpty)
//  }

  it must "get a user" in {
    // skip if no api key is set
    if (apiKeyOpt.isEmpty) {
      succeed
    } else {
      client.getUser().map { user =>
        assert(user.nodeId == NodeId(
          "02f7467f4de732f3b3cffc8d5e007aecdf6e58878edb6e46a8e80164421c1b90aa"))
      }
    }
  }

  it must "get magma stats" in {
    client.getMagmaStats().map { stats =>
      assert(stats.channelStats.completed_size > Satoshis.zero)
      assert(stats.channelStats.completed_orders > 0)

      assert(stats.swapsStats.completed_size > Satoshis.zero)
      assert(stats.swapsStats.completed_orders > 0)
    }
  }
}
