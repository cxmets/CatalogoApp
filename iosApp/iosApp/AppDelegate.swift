import UIKit
import shared // Importa o módulo compartilhado (o nome pode variar)

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        // Inicializa Koin (se não for chamado dentro de MainViewController)
        // No nosso caso, MainViewController já chama initKoin(), então esta linha pode ser redundante,
        // mas é bom saber onde colocar se a estrutura fosse diferente.
        // KoinIOSKt.doInitKoin() // O nome da função gerada pode variar ligeiramente. Verifique o nome exato.

        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            // Cria o UIViewController a partir da função em MainViewController.kt
            let mainViewController = MainViewControllerKt.MainViewController()

            // Define como o root view controller
            window.rootViewController = mainViewController
            window.makeKeyAndVisible()
        }
        return true
    }
}