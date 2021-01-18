package com.example.myapplication

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.WorkManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

lateinit var networks: ConfiguredNetworksHandler
lateinit var workManager : WorkManager

class MainActivity : AppCompatActivity() {

    private val shell = ShellFish()
    private lateinit var work: WorkRepository

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        binding.fab.setOnClickListener { view ->
            shell.exists()
            val isConnected = networks.getActiveNetworkSsid()?.contains("the landing strip") == true
            Snackbar.make(view, "Is connected to the landing strip? $isConnected", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            with(shell) {
                if (isConnected) {
                    turnOffWifi()
                    Snackbar.make(view, "Wifi should be off now", Snackbar.LENGTH_LONG).setAction(" Action ", null).show()
                }
            }
        }
        networks = ConfiguredNetworksHandler(
            application,
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        )
        work = WorkRepository(networks)
        workManager = WorkManager.getInstance(applicationContext)
        startWork()
    }

    private fun startWork() {
        work.watchWifi(context = this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp()
            || super.onSupportNavigateUp()
    }
}

fun d(message: String) {
    return Timber.d(message)
}
