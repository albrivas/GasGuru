import ComposeApp
import Mixpanel

final class MixpanelAnalyticsHelperIos: NSObject, AnalyticsHelper {

    override init() {
        let token = AnalyticsModuleIosKt.getMixpanelToken()
        Mixpanel.initialize(options: MixpanelOptions(token: token, trackAutomaticEvents: false))
    }

    func logEvent(event: AnalyticsEvent) {
        var properties: [String: MixpanelType] = ["category": event.category]
        for param in event.extras {
            properties[param.key] = param.value
        }
        Mixpanel.mainInstance().track(event: event.type, properties: properties)
    }

    func updateSuperProperties(properties: [String: Any]) {
        let mixpanelProps = properties.reduce(into: [String: MixpanelType]()) { result, pair in
            if let value = pair.value as? MixpanelType {
                result[pair.key] = value
            }
        }
        Mixpanel.mainInstance().registerSuperProperties(mixpanelProps)
    }
}
