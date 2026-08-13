package com.sarmayeyar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sarmayeyar.app.ui.AnalysisScreen
import com.sarmayeyar.app.ui.AssetsScreen
import com.sarmayeyar.app.ui.DashboardScreen
import com.sarmayeyar.app.ui.ProfitLossScreen
import com.sarmayeyar.app.ui.SarmayeYarTheme
import com.sarmayeyar.app.ui.SettingsScreen
import com.sarmayeyar.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(vm)
        }
    }
}

@Composable
private fun App(vm: MainViewModel) {

    var darkMode by remember {
        mutableStateOf(false)
    }

    SarmayeYarTheme(
        darkMode = darkMode,
        onDarkModeChange = {
            darkMode = it
        }
    ) {

        val assets by vm.assets.collectAsState()
        val history by vm.history.collectAsState()
        val prices by vm.prices.collectAsState()
        val busy by vm.busy.collectAsState()

        var tab by remember {
            mutableIntStateOf(0)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),

            bottomBar = {
                NavigationBar {

                    listOf(
                        "داشبورد",
                        "دارایی‌ها",
                        "تحلیل",
                        "سود/زیان",
                        "تنظیمات"
                    ).forEachIndexed { i, label ->

                        NavigationBarItem(
                            selected = tab == i,

                            onClick = {
                                tab = i
                            },

                            icon = {},

                            label = {
                                Text(label)
                            }
                        )
                    }
                }
            }

        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                when (tab) {

                    0 -> DashboardScreen(
                        assets = assets,
                        prices = prices,
                        onRefresh = vm::refreshPrices,
                        busy = busy
                    )

                    1 -> AssetsScreen(
                        assets = assets,
                        onAdd = vm::addAsset,
                        onDelete = vm::deleteAsset,
                        onUpdate = vm::updateAsset
                    )

                    2 -> AnalysisScreen(
                        assets = assets
                    )

                    3 -> ProfitLossScreen(
                        assets = assets,
                        history = history,
                        onRecord = vm::recordProfitLossSnapshot
                    )

                    4 -> SettingsScreen(
                        darkMode = darkMode,
                        onDarkModeChange = {
                            darkMode = it
                        },
                        onBackup = vm::backup
                    )
                }
            }
        }
    }
}
