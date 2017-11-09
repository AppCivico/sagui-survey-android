package com.eokoe.sagui.di

import android.app.Application
import com.eokoe.sagui.di.modules.AppModule
import org.koin.android.ext.android.startAndroidContext

/**
 * @author Pedro Silva
 * @since 09/11/17
 */
object AppInjector {
    @JvmStatic
    fun init(app: Application) {
        app.startAndroidContext(app, listOf(AppModule()))
    }
}