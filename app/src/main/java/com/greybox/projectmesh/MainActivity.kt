package com.greybox.projectmesh

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.greybox.projectmesh.ui.theme.ProjectMeshTheme
import com.ustadmobile.meshrabiya.ext.bssidDataStore
import com.ustadmobile.meshrabiya.vnet.AndroidVirtualNode
import com.ustadmobile.meshrabiya.vnet.wifi.ConnectBand
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load layout
        //setContentView(R.layout.activity_main)

        // Initialise Meshrabiya
        //initMesh();
        thisNode = AndroidVirtualNode(
            appContext = applicationContext,
            dataStore = applicationContext.dataStore
        )

        // Load content
        setContent {
            PrototypePage()
        }
    }


    @Composable
    private fun PrototypePage()
    {
        Column {
            Text(text = "Project Mesh", fontSize = TextUnit(48f, TextUnitType.Sp))
            Text(text = "This device IP: ${thisNode.address}")
            Text(text = "Connection status: ${thisNode.state}")
            Button(content = {Text("Scan QR code")}, onClick = fun() {  })
            Button(content = {Text("Start hotspot")}, onClick = fun() { initMesh() })
            Text(text = "Other nodes\nblah\bblah\nblah")
            var chatLog by remember { mutableStateOf("") }

            Row {
                var chatMessage by remember { mutableStateOf("") }


                TextField(
                    value = chatMessage,
                    onValueChange = { chatMessage = it},
                    label = { Text("Message") }
                )
                Button(content = {Text("Send")}, onClick = fun() {

                    chatLog += "You: $chatMessage\n"
                    // SEND TO NETWORK HERE
                    chatMessage = ""
                })
            }

            Text(text = chatLog)
        }

    }
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "meshr_settings")
    lateinit var thisNode: AndroidVirtualNode
    private fun initMesh()
    {
        // Create DataStore for network
        //val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "meshr_settings")

        // Enable hotspot
        runBlocking {
            thisNode.setWifiHotspotEnabled(enabled=true, preferredBand = ConnectBand.BAND_5GHZ)
            // Report connect link
            val connectLink = thisNode.state.filter { it.connectUri != null }.firstOrNull()

            if (connectLink == null)
            {
                Log.d("Network","failed to make hotspot")
            }
            else
            {
                Log.d("Network", "connect link: $connectLink")
            }
        }



    }
}