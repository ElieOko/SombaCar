package t3digitalgroup.vehnixauto.server.route.tracking

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object TrackingScope {
    const val PROTECTED = "${GlobalRoute.PROTECT}/tracking"
    const val PUBLIC = "${GlobalRoute.PUBLIC}/tracking"
}

object TrackingTopics {
    const val VEHICLE = "/topic/tracking/vehicle"
    const val VEHICLE_USER = "/queue/tracking"
    const val KAFKA_VEHICLE_TRACKING = "vehnix.tracking.vehicle"
}
