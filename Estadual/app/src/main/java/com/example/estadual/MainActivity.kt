package com.example.estadual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.estadual.ui.theme.EstadualTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EstadualTheme {
                Scaffold(

                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TopAppBar(
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.secondary),
                        title = { Text("Estadual")                        }
                    )


                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, name: String) {
    val name = "Rodrigo"
    Column() {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    TextField(
        value="$name",
        onValueChange = {}
    )
}
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EstadualTheme {
        Greeting()

    }
}