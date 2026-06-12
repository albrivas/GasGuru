import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {

    private var bridge: IosBridge?
    private var pushService: PushNotificationServiceIos?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        initKoin(launchOptions: launchOptions)
        return true
    }

    private func initKoin(launchOptions: [UIApplication.LaunchOptionsKey: Any]?) {
        let analyticsHelper: AnalyticsHelper

        #if DEBUG
        analyticsHelper = LogAnalyticsHelperIos()
        #else
        analyticsHelper = MixpanelAnalyticsHelperIos()
        #endif

        let oneSignalManager = OneSignalManagerIos(launchOptions: launchOptions)

        let analyticsModule = AnalyticsModuleIosKt.provideAnalyticsModuleIos(analyticsHelper: analyticsHelper)
        let notificationModule = NotificationModuleKt.provideNotificationModuleIos(oneSignalManager: oneSignalManager)

        bridge = KoinInitKt.doInitKoin(platformModules: [analyticsModule, notificationModule])

        let service = PushNotificationServiceIos(analyticsHelper: analyticsHelper, bridge: bridge!)
        service.start()
        pushService = service

        // Test push notification tap handling by simulating a tap after a delay. Remove this in production.
        // #if DEBUG
        // DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
        //     self.bridge?.handlePushTap(stationId: 1234)
        // }
        // #endif
    }
}
