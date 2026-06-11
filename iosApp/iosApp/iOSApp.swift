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
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        initKoin()
        return true
    }

    private func initKoin() {
        let analyticsHelper: AnalyticsHelper

        #if DEBUG
        analyticsHelper = LogAnalyticsHelperIos()
        #else
        analyticsHelper = MixpanelAnalyticsHelperIos()
        #endif

        let analyticsModule = AnalyticsModuleIosKt.provideAnalyticsModuleIos(analyticsHelper: analyticsHelper)

        KoinInitKt.doInitKoin(platformModules: [analyticsModule])
    }
}