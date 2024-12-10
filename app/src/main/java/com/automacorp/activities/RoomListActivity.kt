package com.automacorp.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.automacorp.AutomacorpTopAppBar
import com.automacorp.services.ApiServices
import com.automacorp.ui.theme.AutomacorpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RoomListActivity : ComponentActivity() {
    private val navigateBack: () -> Unit = { finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetch rooms from the API
        lifecycleScope.launch(context = Dispatchers.IO) {
            runCatching {
                ApiServices.roomsApiService.findAll().execute() // Make sure the API call is correct
            }
                .onSuccess {
                    val rooms = it.body() ?: emptyList() // Use empty list if the response is null
                    Log.d("RoomListActivity", "Rooms fetched: $rooms") // Log the fetched rooms
                    setContent {
                        RoomList(rooms, navigateBack, ::openRoom) // Use class-level functions
                    }
                }
                .onFailure {
                    it.printStackTrace()
                    // Show a Toast if there's an error fetching data
                    Toast.makeText(this@RoomListActivity, "Error on rooms loading: ${it.message}", Toast.LENGTH_LONG).show()
                    setContent {
                        RoomList(emptyList(), navigateBack, ::openRoom) // Show empty list if there's an error
                    }
            }    }
        }

    // Open the room detail when a room is clicked
    fun openRoom(roomId: Long) {
        val intent = Intent(this, RoomActivity::class.java).apply {
            putExtra("ROOM_ID", roomId)
        }
        startActivity(intent)
    }
}

@Composable
fun RoomList(
    rooms: List<com.automacorp.services.RoomDto>,
    navigateBack: () -> Unit,
    openRoom: (id: Long) -> Unit
) {
    AutomacorpTheme {
        Scaffold(
            topBar = { AutomacorpTopAppBar("Rooms", navigateBack) }
        ) { innerPadding ->
            if (rooms.isEmpty()) {
                Text(
                    text = "No room found",
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(innerPadding),
                ) {
                    items(rooms, key = { it.id }) { room ->
                        RoomItem(
                            room = room,
                            modifier = Modifier.clickable { openRoom(room.id) },
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun RoomItem(room: com.automacorp.services.RoomDto, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            modifier = modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Target temperature: " + (room.targetTemperature?.toString() ?: "?") + "°",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = (room.currentTemperature?.toString() ?: "?") + "°",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}



