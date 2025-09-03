import SwiftUI

@main
struct iOSApp: App {
    init() {
        KoinKt.initKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}