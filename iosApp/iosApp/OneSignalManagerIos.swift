import ComposeApp
import OneSignalFramework
import UIKit

final class OneSignalManagerIos: NSObject, OneSignalManager {

    init(launchOptions: [UIApplication.LaunchOptionsKey: Any]?) {
        super.init()
        let appId = NotificationModuleKt.getOneSignalAppId()
        #if DEBUG
        OneSignal.Debug.setLogLevel(.LL_VERBOSE)
        #else
        OneSignal.Debug.setLogLevel(.LL_NONE)
        #endif
        OneSignal.initialize(appId, withLaunchOptions: launchOptions)
    }

    func enablePriceNotificationAlert(enable: Bool) async throws {
        if enable {
            OneSignal.Notifications.requestPermission({ _ in }, fallbackToSettings: false)
        }
        OneSignal.User.addTag(key: "enable_stations_alerts", value: String(enable))
    }

    func getPlayerId() async throws -> String? {
        return OneSignal.User.pushSubscription.id
    }
}
