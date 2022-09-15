package space.amboss

import org.bitcoins.core.currency.Satoshis
import org.bitcoins.core.protocol.ln.node.NodeId

abstract class AmbossModel

case class TickerView(currency: String, price: BigDecimal) extends AmbossModel

case class LoginSignInfo(expiry: String, identifier: String, message: String)
    extends AmbossModel

case class AmbossUserMarketInfo(
    enabled: Boolean,
    has_active_offers: Boolean,
    pending_buyer_orders: Double,
    pending_seller_orders: Double
) extends AmbossModel

case class AmbossUser(nodeId: NodeId, market: AmbossUserMarketInfo)
    extends AmbossModel

case class MagmaChannelsStats(
    average_apr: Double,
    completed_fees: Satoshis,
    completed_orders: Int,
    completed_size: Satoshis,
    latest_apr: Double
) extends AmbossModel

case class MagmaSwapsStats(
    completed_fees: Satoshis,
    completed_orders: Int,
    completed_size: Satoshis
) extends AmbossModel

case class MagmaStats(
    channelStats: MagmaChannelsStats,
    swapsStats: MagmaSwapsStats
) extends AmbossModel
