package com.example.dukaansetu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.dukaansetu.ai.AIManager
import com.example.dukaansetu.ai.BillOCRManager
import com.example.dukaansetu.ai.BillParser
import com.example.dukaansetu.ai.DukaanDatabase
import com.example.dukaansetu.ai.IntentResult
import com.example.dukaansetu.ai.InventoryInsightsManager
import com.example.dukaansetu.ai.ProductEntity
import com.example.dukaansetu.ai.UdhaarEntity
import com.example.dukaansetu.ai.StockUpdateManager
import com.example.dukaansetu.ai.UdhaarManager
import com.example.dukaansetu.ai.VoiceInputManager
import com.example.dukaansetu.ui.theme.DukaanSetuTheme
import kotlinx.coroutines.launch
import com.example.dukaansetu.ai.SaleManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DukaanSetuTheme {
                DukaanSetuScreen()
            }
        }
    }
}

@Composable
fun DukaanSetuScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = remember {
        DukaanDatabase.getDatabase(context)
    }

    val voiceInputManager = remember {
        VoiceInputManager(context)
    }

    val ocrManager = remember {
        BillOCRManager()
    }

    val aiManager = remember {
        AIManager()
    }

    val billParser = remember {
        BillParser()
    }

    val stockManager = remember {
        StockUpdateManager(database.productDao())
    }

    val udhaarManager = remember {
        UdhaarManager(database.udhaarDao())
    }

    val inventoryInsightsManager = remember {
        InventoryInsightsManager()
    }

    var recognizedText by remember {
        mutableStateOf("Tap the button and speak")
    }

    var result by remember {
        mutableStateOf<IntentResult?>(null)
    }

    var ocrText by remember {
        mutableStateOf("No bill scanned yet")
    }

    var billItems by remember {
        mutableStateOf(
            emptyList<com.example.dukaansetu.ai.BillItem>()
        )
    }

    var stockMessage by remember {
        mutableStateOf("")
    }

    var udhaarMessage by remember {
        mutableStateOf("")
    }

    var smartInsight by remember {
        mutableStateOf("")
    }

    var stockItems by remember {
        mutableStateOf(emptyList<ProductEntity>())
    }

    var udhaarItems by remember {
        mutableStateOf(emptyList<UdhaarEntity>())
    }

    val salesHistory = remember {
        mutableStateMapOf<String, MutableList<Double>>()
    }

    fun refreshDatabaseData() {

        scope.launch {
            stockItems = stockManager.getAllStock()
            udhaarItems = udhaarManager.getAllBalances()
        }
    }

    fun updateSmartInsight(product: String) {

        scope.launch {

            val currentStock =
                stockManager.getStock(product)

            val history =
                salesHistory[product] ?: emptyList()

            val insight =
                inventoryInsightsManager.generateInsight(
                    product = product,
                    currentStock = currentStock,
                    salesHistory = history
                )

            smartInsight = buildString {
                append("🤖 Smart Insight\n")
                append("${insight.message}\n")
                append(
                    "Predicted Demand: " +
                            "${insight.predictedDemand}"
                )
            }
        }
    }

    fun processVoiceCommand(text: String) {

        recognizedText = text

        val parsedResult =
            aiManager.processInput(text)

        result = parsedResult

        scope.launch {

            when (parsedResult.intent) {

                "SALE" -> {

                    if (
                        parsedResult.product != null &&
                        parsedResult.quantity != null
                    ) {

                        val product =
                            parsedResult.product

                        val quantity =
                            parsedResult.quantity

                        val removed =
                            stockManager.removeStock(
                                product,
                                quantity
                            )

                        if (removed) {

                            stockMessage =
                                "$product stock reduced by $quantity"

                            val history =
                                salesHistory.getOrPut(
                                    product
                                ) {
                                    mutableListOf()
                                }

                            history.add(quantity)

                            updateSmartInsight(product)

                        } else {

                            stockMessage =
                                "Not enough $product stock"

                            updateSmartInsight(product)
                        }
                    }
                }

                "ADD_CREDIT" -> {

                    if (
                        parsedResult.customer != null &&
                        parsedResult.amount != null
                    ) {

                        val customer =
                            parsedResult.customer

                        val amount =
                            parsedResult.amount

                        udhaarManager.addCredit(
                            customer,
                            amount
                        )

                        udhaarMessage =
                            "$customer udhaar added: ₹$amount"
                    }
                }

                "PAYMENT" -> {

                    if (
                        parsedResult.customer != null &&
                        parsedResult.amount != null
                    ) {

                        val customer =
                            parsedResult.customer

                        val amount =
                            parsedResult.amount

                        val paid =
                            udhaarManager.recordPayment(
                                customer,
                                amount
                            )

                        udhaarMessage =
                            if (paid) {
                                "$customer payment received: ₹$amount"
                            } else {
                                "Payment is greater than current balance"
                            }
                    }
                }
            }

            refreshDatabaseData()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                voiceInputManager.startListening(
                    onResult = { text ->
                        processVoiceCommand(text)
                    },
                    onError = { error ->
                        recognizedText = error
                    }
                )

            } else {

                recognizedText =
                    "Microphone permission denied"
            }
        }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { imageUri ->

            if (imageUri != null) {

                ocrManager.recognizeText(
                    context = context,
                    imageUri = imageUri,

                    onResult = { text ->

                        ocrText = text

                        billItems =
                            billParser.parse(text)

                        scope.launch {

                            billItems.forEach { item ->

                                item.quantity?.let { quantity ->

                                    stockManager.addStock(
                                        item.product,
                                        quantity
                                    )
                                }
                            }

                            refreshDatabaseData()

                            billItems.forEach { item ->
                                updateSmartInsight(
                                    item.product
                                )
                            }
                        }
                    },

                    onError = { error ->

                        ocrText =
                            "OCR Error: ${error.message}"

                        billItems =
                            emptyList()
                    }
                )
            }
        }

    LaunchedEffect(Unit) {
        refreshDatabaseData()
    }

    DisposableEffect(Unit) {

        onDispose {

            voiceInputManager.destroy()
            ocrManager.close()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "DukaanSetu"
            )

            Text(
                text = recognizedText,
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {

                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {

                        voiceInputManager.startListening(
                            onResult = { text ->
                                processVoiceCommand(text)
                            },
                            onError = { error ->
                                recognizedText = error
                            }
                        )

                    } else {

                        permissionLauncher.launch(
                            Manifest.permission.RECORD_AUDIO
                        )
                    }
                }
            ) {
                Text("🎤 Speak")
            }

            result?.let { intentResult ->

                Text(
                    text =
                        "Intent: ${intentResult.intent}",
                    modifier =
                        Modifier.padding(top = 20.dp)
                )

                intentResult.product?.let {
                    Text("Product: $it")
                }

                intentResult.quantity?.let {
                    Text("Quantity: $it")
                }

                intentResult.customer?.let {
                    Text("Customer: $it")
                }

                intentResult.amount?.let {
                    Text("Amount: ₹$it")
                }
            }

            if (stockMessage.isNotEmpty()) {

                Text(
                    text = stockMessage,
                    modifier =
                        Modifier.padding(top = 12.dp)
                )
            }

            if (udhaarMessage.isNotEmpty()) {

                Text(
                    text = udhaarMessage,
                    modifier =
                        Modifier.padding(top = 12.dp)
                )
            }

            if (smartInsight.isNotEmpty()) {

                Text(
                    text = smartInsight,
                    modifier =
                        Modifier.padding(top = 20.dp)
                )
            }

            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier =
                    Modifier.padding(top = 24.dp)
            ) {
                Text("📷 Scan Bill")
            }

            Text(
                text = "OCR Result:",
                modifier =
                    Modifier.padding(top = 20.dp)
            )

            Text(
                text = ocrText,
                modifier =
                    Modifier.padding(top = 8.dp)
            )

            if (billItems.isNotEmpty()) {

                Text(
                    text = "Detected Items:",
                    modifier =
                        Modifier.padding(top = 20.dp)
                )

                billItems.forEach { item ->

                    Text(
                        text = buildString {

                            append(item.product)

                            item.quantity?.let {
                                append(" | Qty: $it")
                            }

                            item.price?.let {
                                append(" | Price: ₹$it")
                            }
                        },

                        modifier =
                            Modifier.padding(top = 4.dp)
                    )
                }
            }

            Text(
                text = "Current Stock:",
                modifier =
                    Modifier.padding(top = 24.dp)
            )

            stockItems.forEach { item ->

                Text(
                    text =
                        "${item.product}: ${item.quantity}",
                    modifier =
                        Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "Udhaar Balances:",
                modifier =
                    Modifier.padding(top = 20.dp)
            )

            udhaarItems.forEach { item ->

                Text(
                    text =
                        "${item.customer}: ₹${item.balance}",
                    modifier =
                        Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}