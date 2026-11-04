package com.example.wecareto_project

import android.app.Application
import com.example.wecareto_project.di.AppContainer

class WeCareApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
