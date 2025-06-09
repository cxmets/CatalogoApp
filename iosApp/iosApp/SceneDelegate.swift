import UIKit
import SwiftUI
import shared

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = (scene as? UIWindowScene) else { return }

        let window = UIWindow(windowScene: windowScene)
        
        // Cria o UIViewController a partir da sua função Kotlin.
        let mainViewController = MainViewControllerKt.mainViewController()
        
        // Define o controller como a tela principal da janela.
        window.rootViewController = mainViewController
        
        self.window = window
        window.makeKeyAndVisible()
    }
}
