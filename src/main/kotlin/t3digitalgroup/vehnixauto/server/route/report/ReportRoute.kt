package t3digitalgroup.vehnixauto.server.route.report

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object ListingReportScope {
    const val PROTECTED = "${GlobalRoute.PROTECT}/${ReportFeatures.LISTING_REPORT_PATH}"
}

object ReportFeatures {
    const val LISTING_REPORT_PATH = "listing-reports"
}
