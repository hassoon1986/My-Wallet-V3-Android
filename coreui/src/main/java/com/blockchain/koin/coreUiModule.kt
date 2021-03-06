@file:Suppress("USELESS_CAST")

package com.blockchain.koin

import com.blockchain.CrashLoggerImpl
import com.blockchain.SwapDiagnosticsImpl
import com.blockchain.logging.CrashLogger
import com.blockchain.logging.EventLogger
import com.blockchain.logging.SwapDiagnostics
import com.blockchain.remoteconfig.ABTestExperiment
import com.blockchain.remoteconfig.RemoteConfig
import com.blockchain.remoteconfig.RemoteConfiguration
import com.blockchain.ui.chooser.AccountChooserPresenter
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.dsl.bind
import org.koin.dsl.module
import piuk.blockchain.androidcoreui.BuildConfig
import piuk.blockchain.androidcoreui.utils.OverlayDetection
import piuk.blockchain.androidcoreui.utils.logging.InjectableLogging

val coreUiModule = module {

    scope(payloadScopeQualifier) {

        factory {
            AccountChooserPresenter(get(), get())
        }
    }

    single {
        val config = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettings(config)
        }
    }

    factory { RemoteConfiguration(get()) }
        .bind(RemoteConfig::class)
        .bind(ABTestExperiment::class)

    factory { InjectableLogging(get()) as EventLogger }

    single {
        OverlayDetection(get())
    }

    single {
        CrashLoggerImpl(BuildConfig.DEBUG)
    }.bind(CrashLogger::class)

    single {
        SwapDiagnosticsImpl(crashLogger = get())
    }.bind(SwapDiagnostics::class)
}
