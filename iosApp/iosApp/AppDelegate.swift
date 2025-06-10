import UIKit
import shared // Importe seu módulo compartilhado

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
	
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        // Inicia o Koin
        KoinIOSKt.doInitKoin()

        window = UIWindow(frame: UIScreen.main.bounds)
        
        // Cria e define o ViewController principal
        let mainViewController = MainViewControllerKt.MainViewController()
        window?.rootViewController = mainViewController
        window?.makeKeyAndVisible()
        
        return true
    }
}
