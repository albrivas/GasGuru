import ComposeApp
import Mixpanel

final class MixpanelAnalyticsHelperIos: NSObject, AnalyticsHelper {

    override init() {
        let token = AnalyticsSecrets.shared.MIXPANEL_TOKEN
        Mixpanel.initialize(options: MixpanelOptions(token: token, trackAutomaticEvents: false))
    }

    func logEvent(event: AnalyticsEvent) {
        var properties: [String: MixpanelType] = ["category": event.category]

        let extrasSize = Int(event.extras.size)
        for i in 0..<extrasSize {
            if let param = event.extras.get(index: Int32(i)) as? AnalyticsEventParam {
                properties[param.key] = param.value
            }
        }

        Mixpanel.mainInstance().track(event: event.type, properties: properties)
    }

    func updateSuperProperties(properties: [AnyHashable: Any]) {
        let mixpanelProps = properties.reduce(into: [String: MixpanelType]()) { result, pair in
            if let key = pair.key as? String, let value = pair.value as? MixpanelType {
                result[key] = value
            }
        }
        Mixpanel.mainInstance().registerSuperProperties(mixpanelProps)
    }
}
