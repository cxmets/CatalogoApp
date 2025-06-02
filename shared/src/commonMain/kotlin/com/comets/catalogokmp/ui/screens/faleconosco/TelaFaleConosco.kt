package com.comets.catalogokmp.ui.screens.faleconosco

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.desc_imagem_texto_nexpart_contato
import catalogokmp.shared.generated.resources.desc_logo_nexpart_topbar
import catalogokmp.shared.generated.resources.fale_conosco_assunto_email_padrao
import catalogokmp.shared.generated.resources.fale_conosco_desc_acao_email
import catalogokmp.shared.generated.resources.fale_conosco_desc_acao_instagram_oficial
import catalogokmp.shared.generated.resources.fale_conosco_desc_acao_site_nexpart
import catalogokmp.shared.generated.resources.fale_conosco_desc_acao_telefone
import catalogokmp.shared.generated.resources.fale_conosco_desc_acao_whatsapp
import catalogokmp.shared.generated.resources.fale_conosco_label_email
import catalogokmp.shared.generated.resources.fale_conosco_label_instagram
import catalogokmp.shared.generated.resources.fale_conosco_label_site
import catalogokmp.shared.generated.resources.fale_conosco_label_telefone
import catalogokmp.shared.generated.resources.fale_conosco_label_whatsapp
import catalogokmp.shared.generated.resources.fale_conosco_mensagem_final
import catalogokmp.shared.generated.resources.fale_conosco_mensagem_padrao_whatsapp
import catalogokmp.shared.generated.resources.i_instagram
import catalogokmp.shared.generated.resources.i_mail
import catalogokmp.shared.generated.resources.i_phone
import catalogokmp.shared.generated.resources.i_whatsapp
import catalogokmp.shared.generated.resources.i_web
import catalogokmp.shared.generated.resources.logo
import catalogokmp.shared.generated.resources.tela_fale_conosco_titulo_topbar
import catalogokmp.shared.generated.resources.text_dark
import catalogokmp.shared.generated.resources.text_light
import catalogokmp.shared.generated.resources.voltar
import com.comets.catalogokmp.model.UserThemePreference
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.util.AppConstants
import com.comets.catalogokmp.util.IntentHandler
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFaleConoscoScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val intentHandler: IntentHandler = koinInject()
    val appViewModel: AppViewModel = koinInject()
    val userThemePreference by appViewModel.userThemePreference.collectAsState()
    val systemIsDark = isSystemInDarkTheme()

    var isProcessingPopBack by remember { mutableStateOf(false) }

    val mensagemPadraoWhatsAppResolved = stringResource(Res.string.fale_conosco_mensagem_padrao_whatsapp)
    val assuntoEmailPadraoResolved = stringResource(Res.string.fale_conosco_assunto_email_padrao)

    val currentEffectiveDarkTheme = when (userThemePreference) {
        UserThemePreference.LIGHT -> false
        UserThemePreference.DARK -> true
        UserThemePreference.SYSTEM -> systemIsDark
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(Res.string.tela_fale_conosco_titulo_topbar)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isProcessingPopBack) { return@IconButton }
                        isProcessingPopBack = true
                        navigator.pop()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.voltar))
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = stringResource(Res.string.desc_logo_nexpart_topbar),
                        modifier = Modifier.padding(end = 16.dp).size(36.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
            )
        },
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val textImageResourceId = if (currentEffectiveDarkTheme) {
                Res.drawable.text_dark
            } else {
                Res.drawable.text_light
            }

            // Adiciona um Spacer aqui para o espaçamento superior
            Spacer(modifier = Modifier.height(52.dp)) // Você pode ajustar este valor conforme necessário

            Image(
                painter = painterResource(textImageResourceId),
                contentDescription = stringResource(Res.string.desc_imagem_texto_nexpart_contato),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1200f / 379f)
                    .padding(bottom = 28.dp), // Removido o top padding daqui, agora controlado pelo Spacer acima
                contentScale = ContentScale.Fit
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ContactItem(
                    iconPainter = painterResource(Res.drawable.i_mail),
                    text = stringResource(Res.string.fale_conosco_label_email, AppConstants.EMAIL_EMPRESA_VAL),
                    contentDescription = stringResource(Res.string.fale_conosco_desc_acao_email, AppConstants.EMAIL_EMPRESA_VAL),
                    enabled = !isProcessingPopBack,
                    onClick = { intentHandler.abrirEmail(AppConstants.EMAIL_EMPRESA_VAL, assuntoEmailPadraoResolved) }
                )

                ContactItem(
                    iconPainter = painterResource(Res.drawable.i_phone),
                    text = stringResource(Res.string.fale_conosco_label_telefone, AppConstants.NUMERO_TELEFONE_DISCADOR_VAL),
                    contentDescription = stringResource(Res.string.fale_conosco_desc_acao_telefone, AppConstants.NUMERO_TELEFONE_DISCADOR_VAL),
                    enabled = !isProcessingPopBack,
                    onClick = { intentHandler.abrirDiscador(AppConstants.NUMERO_TELEFONE_DISCADOR_VAL) }
                )

                ContactItem(
                    iconPainter = painterResource(Res.drawable.i_whatsapp),
                    text = stringResource(Res.string.fale_conosco_label_whatsapp, AppConstants.TELEFONE_EMPRESA_VAL),
                    contentDescription = stringResource(Res.string.fale_conosco_desc_acao_whatsapp, AppConstants.TELEFONE_EMPRESA_VAL),
                    enabled = !isProcessingPopBack,
                    onClick = { intentHandler.abrirWhatsApp(AppConstants.TELEFONE_EMPRESA_VAL, mensagemPadraoWhatsAppResolved) }
                )

                ContactItem(
                    iconPainter = painterResource(Res.drawable.i_instagram),
                    text = stringResource(Res.string.fale_conosco_label_instagram, AppConstants.INSTAGRAM_PROFILE_NAME),
                    contentDescription = stringResource(Res.string.fale_conosco_desc_acao_instagram_oficial),
                    enabled = !isProcessingPopBack,
                    onClick = { intentHandler.abrirInstagram(AppConstants.INSTAGRAM_URL_VAL) }
                )

                ContactItem(
                    iconPainter = painterResource(Res.drawable.i_web),
                    text = stringResource(Res.string.fale_conosco_label_site, AppConstants.WEBSITE_NAME),
                    contentDescription = stringResource(Res.string.fale_conosco_desc_acao_site_nexpart),
                    enabled = !isProcessingPopBack,
                    onClick = { intentHandler.abrirUrlGenerica(AppConstants.WEBSITE_URL_VAL) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(Res.string.fale_conosco_mensagem_final),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
fun ContactItem(
    iconPainter: Painter,
    text: String,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = contentDescription,
                modifier = Modifier.size(60.dp),
                tint = Color.Unspecified
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}