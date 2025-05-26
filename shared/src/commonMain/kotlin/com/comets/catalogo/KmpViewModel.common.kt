package com.comets.catalogo

import androidx.compose.runtime.Composable
import com.rickclephas.kmp.observableviewmodel.ViewModel // A classe base do KMP ViewModel

@Composable
expect inline fun <reified T : ViewModel> getKmpViewModel(key: String? = null): T