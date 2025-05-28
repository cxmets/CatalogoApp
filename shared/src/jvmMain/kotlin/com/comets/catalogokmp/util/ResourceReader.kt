package com.comets.catalogokmp.util

internal actual fun readProductsJson(): String {
    // Implementação para JVM: Ler do classpath
    // Isso assume que 'products.json' está nos resources do jvmMain
    // ou que o build system copia do commonMain/resources para o local adequado do jvm.
    // Se for executar na JVM, a configuração de resources no build.gradle.kts do shared module
    // para o sourceSet jvmMain precisaria garantir que products.json esteja acessível.
    // Por agora, uma implementação que pode falhar se não configurado é aceitável,
    // pois não planejamos rodar a UI principal na JVM.
    return try {
        ClassLoader.getSystemResourceAsStream("products.json")?.bufferedReader().use { it!!.readText() }
            ?: throw IllegalStateException("products.json não encontrado no classpath da JVM")
    } catch (e: Exception) {
        System.err.println("Erro ao ler products.json na JVM: ${e.message}")
        throw e // Ou retorne uma string JSON vazia/de erro: "[]"
    }
}