import SwiftUI
import ComposeApp
import BackgroundTasks

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {

    private static let stationSyncId = "com.gasguru.stationsync"

    private var bridge: IosBridge?
    private var pushService: PushNotificationServiceIos?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        initKoin(launchOptions: launchOptions)
        registerBackgroundTasks()
        scheduleStationSync()
        return true
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        scheduleStationSync()
    }

    // MARK: - Koin

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

    // MARK: - Background sync (BGTaskScheduler)

    private func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: Self.stationSyncId,
            using: nil
        ) { [weak self] task in
            self?.handleStationSync(task as! BGAppRefreshTask)
        }
    }

    private func scheduleStationSync() {
        let request = BGAppRefreshTaskRequest(identifier: Self.stationSyncId)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 30 * 60)
        try? BGTaskScheduler.shared.submit(request)
    }

    private func handleStationSync(_ task: BGAppRefreshTask) {
        // Re-schedule the next fetch before doing any work.
        scheduleStationSync()

        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }

        bridge?.refreshStations { success in
            task.setTaskCompleted(success: success)
        }
    }
}
