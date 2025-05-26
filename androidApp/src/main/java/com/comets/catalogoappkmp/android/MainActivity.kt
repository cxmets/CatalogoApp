package com.comets.catalogoappkmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.comets.catalogo.AppNavigation // Será o nosso ponto de entrada da UI no shared module
import com.comets.catalogo.CatalogoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa a Factory que agora está no módulo :shared (acessível ao androidApp)
        CatalogoViewModelFactory.initialize(application)

        setContent {
            // AppNavigation será o Composable raiz da UI definido em shared/commonMain
            // Ele usará CatalogoAppTheme internamente.
            AppNavigation()
        }
    }
}