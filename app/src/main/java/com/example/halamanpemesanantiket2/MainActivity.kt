package com.example.halamanpemesanantiket2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicketScreen()
                }
            }
        }
    }
}

// 1. Parent Composable (State Holder / Stateful)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen() {
    // State yang dikelola oleh Parent
    var customerName by rememberSaveable { mutableStateOf("") }
    var ticketCount by rememberSaveable { mutableStateOf(1) }
    val ticketPrice = 50000 // Harga tiket per lembar (Rp 50.000)

    // State status dan proses
    var statusMessage by rememberSaveable { mutableStateOf("Silakan pesan tiket") }
    var isProcessing by remember { mutableStateOf(false) }
    var orderTrigger by remember { mutableStateOf(0) }

    // LaunchedEffect untuk menangani side-effect proses pemesanan
    LaunchedEffect(orderTrigger) {
        if (orderTrigger > 0) {
            if (customerName.isBlank()) {
                statusMessage = "Nama Masih Kosong"
            } else {
                isProcessing = true
                statusMessage = "Memproses pesanan..."

                // Menunggu proses selama 5 detik
                delay(5000)

                statusMessage = "Tiket telah dipesan"
                isProcessing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pemesanan Tiket", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E56C8))
            )
        }
    ) { innerPadding ->
        // Mengalirkan state ke bawah dan menerima event ke atas (State Hoisting)
        TicketFormContent(
            modifier = Modifier.padding(innerPadding),
            customerName = customerName,
            onNameChange = { customerName = it },
            ticketCount = ticketCount,
            onIncrement = { ticketCount++ },
            onDecrement = { if (ticketCount > 1) ticketCount-- },
            ticketPrice = ticketPrice,
            totalPrice = ticketCount * ticketPrice,
            isProcessing = isProcessing,
            statusMessage = statusMessage,
            onOrderClick = { orderTrigger++ }
        )
    }
}

// 2. Child Composable (Stateless & Reusable)
@Composable
fun TicketFormContent(
    modifier: Modifier = Modifier,
    customerName: String,
    onNameChange: (String) -> Unit,
    ticketCount: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    ticketPrice: Int,
    totalPrice: Int,
    isProcessing: Boolean,
    statusMessage: String,
    onOrderClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Input Nama
        Text("Nama", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = customerName,
            onValueChange = onNameChange,
            placeholder = { Text("Masukkan nama Anda") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isProcessing
        )

        Spacer(Modifier.height(18.dp))

        // Kontrol Jumlah Tiket
        Text("Jumlah Tiket", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onDecrement,
                enabled = !isProcessing && ticketCount > 1,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2EAF8)),
                modifier = Modifier.weight(1f)
            ) {
                Text("-", color = Color(0xFF1E56C8), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "$ticketCount",
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Button(
                onClick = onIncrement,
                enabled = !isProcessing,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2EAF8)),
                modifier = Modifier.weight(1f)
            ) {
                Text("+", color = Color(0xFF1E56C8), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Ringkasan Harga
        Text(
            text = "Harga per Tiket: Rp $ticketPrice | Total: Rp $totalPrice",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(20.dp))

        // Tombol Pesan
        Button(
            onClick = onOrderClick,
            enabled = !isProcessing,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isProcessing) Color(0xFFA0B4DE) else Color(0xFF1E56C8)
            )
        ) {
            Text("Pesan Tiket", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(24.dp))

        // Kotak Status UI
        val (bgColor, textColor) = when (statusMessage) {
            "Nama Masih Kosong" -> Pair(Color(0xFFFFEAEA), Color(0xFFD32F2F))
            "Tiket telah dipesan" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
            "Memproses pesanan..." -> Pair(Color(0xFFEBF2FD), Color(0xFF1E56C8))
            else -> Pair(Color(0xFFF5F5F5), Color.DarkGray)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = textColor
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Text(
                    text = "Status: $statusMessage",
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}