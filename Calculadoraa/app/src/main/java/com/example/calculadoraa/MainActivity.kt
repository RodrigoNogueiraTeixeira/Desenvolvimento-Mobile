package com.example.calculadoraa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF9F0A),
            background = Color.Black,
            surface = Color(0xFF1C1C1E),
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

@Composable
fun CalculatorScreen() {
    var displayValue by remember { mutableStateOf("0") }
    var secondaryDisplay by remember { mutableStateOf("") }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<String?>(null) }
    var shouldResetDisplay by remember { mutableStateOf(false) }

    fun formatNumber(num: Double): String {
        return if (num == num.toLong().toDouble()) num.toLong().toString() else num.toString()
    }

    fun onNumberClick(number: String) {
        if (displayValue == "0" || shouldResetDisplay) {
            displayValue = number
            shouldResetDisplay = false
        } else {
            displayValue += number
        }
    }

    fun onOperatorClick(op: String) {
        val current = displayValue.toDoubleOrNull() ?: 0.0
        operand1 = current
        operator = op
        secondaryDisplay = "${formatNumber(current)} $op"
        shouldResetDisplay = true
    }

    fun onEqualClick() {
        val op1 = operand1
        val op = operator
        val op2 = displayValue.toDoubleOrNull()

        if (op1 != null && op != null && op2 != null) {
            val result = when (op) {
                "+" -> op1 + op2
                "-" -> op1 - op2
                "×" -> op1 * op2
                "÷" -> if (op2 != 0.0) op1 / op2 else Double.NaN
                else -> 0.0
            }
            secondaryDisplay = "${formatNumber(op1)} $op ${formatNumber(op2)} ="
            displayValue = if (result.isNaN()) "Erro" else formatNumber(result)
            operand1 = null
            operator = null
            shouldResetDisplay = true
        }
    }

    fun onClearClick() {
        displayValue = "0"
        secondaryDisplay = ""
        operand1 = null
        operator = null
        shouldResetDisplay = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Microphone Icon
        Box(
            modifier = Modifier
                .size(60.dp, 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voz",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Displays
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = secondaryDisplay,
                color = Color.Gray,
                fontSize = 24.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = displayValue,
                color = Color.White,
                fontSize = 80.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 20.dp),
                maxLines = 1
            )
        }

        // Keyboard
        val buttons = listOf(
            listOf("C" to Color(0xFF424242), "+/-" to Color(0xFF424242), "%" to Color(0xFF424242), "÷" to Color(0xFFFF9F0A)),
            listOf("7" to Color(0xFF333333), "8" to Color(0xFF333333), "9" to Color(0xFF333333), "×" to Color(0xFFFF9F0A)),
            listOf("4" to Color(0xFF333333), "5" to Color(0xFF333333), "6" to Color(0xFF333333), "-" to Color(0xFFFF9F0A)),
            listOf("1" to Color(0xFF333333), "2" to Color(0xFF333333), "3" to Color(0xFF333333), "+" to Color(0xFFFF9F0A)),
            listOf("0" to Color(0xFF333333), "." to Color(0xFF333333), "=" to Color(0xFFFF9F0A))
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                row.forEach { (label, color) ->
                    val weight = if (label == "0") 2.15f else 1f
                    CalculatorButton(
                        text = label,
                        backgroundColor = color,
                        modifier = Modifier
                            .weight(weight)
                            .height(80.dp)
                    ) {
                        when {
                            label == "C" -> onClearClick()
                            label == "=" -> onEqualClick()
                            label in listOf("+", "-", "×", "÷") -> onOperatorClick(label)
                            label.all { it.isDigit() || it == '.' } -> onNumberClick(label)
                            label == "+/-" -> {
                                displayValue = if (displayValue.startsWith("-")) displayValue.drop(1) else "-$displayValue"
                            }
                            label == "%" -> {
                                val current = displayValue.toDoubleOrNull() ?: 0.0
                                displayValue = formatNumber(current / 100)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(40.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            // .redGlowEffect(radius = 12.dp)
            .clip(shape)
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorTheme {
        CalculatorScreen()
    }
}

fun Modifier.redGlowEffect(radius: Dp): Modifier = this.drawBehind {
    val paint = Paint().asFrameworkPaint()
    val shadowColor = Color.Red.copy(alpha = 0.4f).toArgb()
    
    paint.setShadowLayer(
        radius.toPx(),
        0f, 0f,
        shadowColor
    )
    
    drawIntoCanvas { canvas ->
        val frameworkCanvas = canvas.nativeCanvas
        val rect = android.graphics.RectF(0f, 0f, size.width, size.height)
        val cornerRadius = 40.dp.toPx()
        frameworkCanvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }
}
