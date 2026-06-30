import ComposeApp
import OneSignalFramework

final class PushNotificationServiceIos: NSObject, NotificationService, OSNotificationClickListener {

    private let analyticsHelper: AnalyticsHelper
    private let bridge: IosBridge

    init(analyticsHelper: AnalyticsHelper, bridge: IosBridge) {
        self.analyticsHelper = analyticsHelper
        self.bridge = bridge
        super.init()
    }

    func start() {
        OneSignal.Notifications.addClickListener(self)
    }

    func onClick(event: OSNotificationClickEvent) {
        guard let additionalData = event.notification.additionalData else { return }
        let raw = additionalData["station_id"]
        let stationId: Int32?
        if let rawString = raw as? String, let parsed = Int32(rawString) {
            stationId = parsed
        } else if let rawNumber = raw as? NSNumber {
            stationId = rawNumber.int32Value
        } else {
            stationId = nil
        }
        guard let resolvedId = stationId else { return }

        PushAnalyticsExtKt.trackPushNotificationTapped(
            analyticsHelper,
            notificationType: "price_alert"
        )
        bridge.handlePushTap(stationId: resolvedId)
    }
}
