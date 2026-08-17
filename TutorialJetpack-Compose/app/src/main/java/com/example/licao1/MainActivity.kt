package com.example.licao1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.licao1.licao4.Conversation
import com.example.licao1.licao4.SampleData
import com.example.licao1.ui.theme.Licao1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Licao1Theme {
                Conversation(SampleData.conversationSample)
            }
        }
    }
}

/**
 * Função de saudação original removida para focar no tutorial das lições.
 */