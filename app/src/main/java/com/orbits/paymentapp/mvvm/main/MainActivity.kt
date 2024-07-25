package com.orbits.paymentapp.mvvm.main

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orbits.paymentapp.R
import com.orbits.paymentapp.databinding.ActivityMainBinding
import com.orbits.paymentapp.helper.AlertDialogInterface
import com.orbits.paymentapp.helper.AppController
import com.orbits.paymentapp.helper.BaseActivity
import com.orbits.paymentapp.helper.Constants
import com.orbits.paymentapp.helper.Dialogs
import com.orbits.paymentapp.helper.Extensions.asDouble
import com.orbits.paymentapp.helper.Global
import com.orbits.paymentapp.helper.Global.showSnackBar
import com.orbits.paymentapp.helper.PrefUtils.getAppPassword
import com.orbits.paymentapp.helper.PrefUtils.getMasterKey
import com.orbits.paymentapp.helper.PrefUtils.getUserDataResponse
import com.orbits.paymentapp.helper.PrefUtils.isCodeVerified
import com.orbits.paymentapp.helper.PrefUtils.setAppPassword
import com.orbits.paymentapp.helper.PrefUtils.setMasterKey
import com.orbits.paymentapp.helper.PrefUtils.setUserDataResponse
import com.orbits.paymentapp.helper.ServerService
import com.orbits.paymentapp.helper.TCPServer
import com.orbits.paymentapp.helper.WebSocketClient
import com.orbits.paymentapp.helper.helper_model.AppMasterKeyModel
import com.orbits.paymentapp.helper.helper_model.PasswordModel
import com.orbits.paymentapp.helper.helper_model.UserDataModel
import com.orbits.paymentapp.helper.helper_model.UserResponseModel
import com.orbits.paymentapp.interfaces.CommonInterfaceClickEvent
import com.orbits.paymentapp.interfaces.MessageListener
import com.orbits.paymentapp.mvvm.main.adapter.ClientListAdapter
import com.orbits.paymentapp.mvvm.main.model.ClientDataModel
import com.orbits.paymentapp.mvvm.settings.SettingsActivity
import io.nearpay.sdk.Environments
import io.nearpay.sdk.NearPay
import io.nearpay.sdk.utils.PaymentText
import io.nearpay.sdk.utils.enums.AuthenticationData
import io.nearpay.sdk.utils.enums.NetworkConfiguration
import io.nearpay.sdk.utils.enums.PurchaseFailure
import io.nearpay.sdk.utils.enums.TransactionData
import io.nearpay.sdk.utils.enums.UIPosition
import io.nearpay.sdk.utils.listeners.PurchaseListener
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.OutputStream
import java.net.Socket
import java.util.Locale
import java.util.UUID

class MainActivity : BaseActivity(), MessageListener {
    private lateinit var tcpServer: TCPServer
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var binding: ActivityMainBinding
    private var outStream: OutputStream? = null
    private lateinit var socket: Socket
    private var adapter = ClientListAdapter()
    private var arrListClients = ArrayList<String>()
    private var arrListKoiskClients = ArrayList<String>()
    private lateinit var nearpay: NearPay
    private var clientModel = ClientDataModel()
    val gson = Gson()
    var amount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)


        println("here is 111 ${getMasterKey()?.masterKey}")
        if (getMasterKey()?.masterKey == null){
            Global.getRandomDeviceId()
            setMasterKey(
                result = AppMasterKeyModel(
                    masterKey = Global.getRandomDeviceId()
                )
            )

        }
        if (getAppPassword()?.appPassword == null){
            setAppPassword(
                result = PasswordModel(
                    appPassword = "1234"
                )
            )
        }


        startServerService()
        initializeToolbar()
        initializeNearPay()
    }

    private fun startServerService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        Intent(applicationContext, ServerService::class.java).also { intent ->
            intent.action = ServerService.Actions.START.toString()
            startService(intent)
            println("here is service started")
            initializeSocket()
        }

    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = "Aflak",
            isBackArrow = false,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    if (type == Constants.TOOLBAR_ICON_ONE) {
                        val appSalt = byteArrayOf(17, 43, 99, 82, 55, 28, 40, 90)
                        val masterPassword =  Global.getMasterKey(this@MainActivity,appSalt)

                        println("here is master password $masterPassword")

                        Dialogs.showPasswordDialog(
                            activity = this@MainActivity,
                            alertDialogInterface = object : AlertDialogInterface {
                                override fun onYesClick() {
                                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                                    startActivity(intent)
                                }

                                override fun onMasterYesClick() {
                                    val code = Global.getRandomDeviceId()
                                    setMasterKey(
                                        result = AppMasterKeyModel(
                                            masterKey = code
                                        )
                                    )
                                    Dialogs.showChangePasswordDialog(
                                        activity = this@MainActivity,
                                        alertDialogInterface = object : AlertDialogInterface {
                                            override fun onSubmitPasswordClick(password: String) {
                                                setAppPassword(
                                                    result = PasswordModel(
                                                        appPassword = password
                                                    )
                                                )
                                                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                                                startActivity(intent)
                                            }

                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        )
    }

    private fun initializeSocket() {
        tcpServer = TCPServer(8085, this)
        Thread {
            tcpServer.start()
        }.start()

        webSocketClient = WebSocketClient(8085)
        webSocketClient.start()

    }

    private fun initializeNearPay() {
        nearpay = NearPay.Builder()
            .context(this)
            .authenticationData(AuthenticationData.Email("development@aflak.com.sa"))
            .environment(Environments.SANDBOX)
            .locale(Locale.getDefault())
            .networkConfiguration(NetworkConfiguration.DEFAULT)
            .uiPosition(UIPosition.CENTER_BOTTOM)
            .paymentText(PaymentText("يرجى تمرير الطاقة", "please tap your card"))
            .loadingUi(true)
            .build()
    }

    private fun updateClientList(clients: List<String>) {
        runOnUiThread {
            adapter.updateClients(clients)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*Intent(applicationContext, ServerService::class.java).also { intent ->
            intent.action = ServerService.Actions.STOP.toString()
            startService(intent)
            println("here is service started")
            tcpServer.stop()
            webSocketClient.stop()
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: String) {

    }

    override fun onMessageJsonReceived(json: JsonObject) {
        runOnUiThread {
            if (!json.isJsonNull) {
                println("Received json in activity: $json")
                clientModel = ClientDataModel(
                    code = json.get("code")?.asString ?: "",
                    amount = json.get("amount")?.asString ?: "",
                    client_id = json.get("client_id")?.asString ?: "",
                    transaction_id = json.get("transaction_id")?.asString ?: "",
                    time = json.get("time")?.asString ?: "",
                    desc = json.get("desc")?.asString ?: "",
                    currency = json.get("currency")?.asString ?: "",
                    transaction_type = json.get("transaction_type")?.asString ?: "",
                )

                val code = clientModel.code
                amount = clientModel.amount ?: ""

                if (isCodeVerified()) {
                    if (code?.isEmpty() == true) {
                        println("here is client list fahad")
                        showSuccessNotification(this@MainActivity,"Payment", "Payment With Near Pay Now")
                        //callPurchase(amount.asDouble())
                    }
                } else {
                    if (code == getUserDataResponse()?.code) {
                        binding.root.showSnackBar("Client Connected")
                        setUserDataResponse(
                            UserResponseModel(
                                code = getUserDataResponse()?.code,
                                data = UserDataModel(
                                    isCodeVerified = true
                                )
                            )
                        )
                    } else {
                        socket.close()
                        binding.root.showSnackBar("Client Disconnected")
                    }
                }
            } else {
                socket.close()
                binding.root.showSnackBar("Client Disconnected")
            }
        }
    }

    override fun onClientDisconnected() {
        setUserDataResponse(
            UserResponseModel(
                code = getUserDataResponse()?.code,
                data = UserDataModel(
                    isCodeVerified = false
                )
            )
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onClientConnected(clientSocket: Socket?) {
        Thread {
            try {
                outStream = clientSocket?.getOutputStream()
                if (clientSocket != null) {
                    socket = clientSocket
                    arrListKoiskClients.clear()
                    arrListKoiskClients.addAll(tcpServer.arrListKoiskClients)
                    runOnUiThread {
                        tcpServer.observeClientList().observe(this) { clients ->
                            binding.root.showSnackBar("Client Connected")
                            arrListClients.addAll(clients)
                        }
                    }
                }
                println("Connected to server")
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }.start()
    }

    private fun callPurchase(amount: Double) {
        val customerReferenceNumber = "9ace70b7-977d-4094-b7f4-4ecb17de6753"
        val enableReceiptUi = true
        val enableReversal = true
        val finishTimeOut: Long = 10
        val requestId = UUID.randomUUID()
        val enableUiDismiss = true
        val nearpayAmount = (amount * 100).toLong()

        nearpay.purchase(
            nearpayAmount,
            customerReferenceNumber,
            enableReceiptUi,
            enableReversal,
            finishTimeOut,
            requestId,
            enableUiDismiss,
            object :
                PurchaseListener {

                override fun onPurchaseApproved(transactionData: TransactionData) {
                    val jsonObject = JsonObject()
                    println("here is transaction data $transactionData")
                    jsonObject.add("transactionData", gson.toJsonTree(transactionData))
                    sendMessageToWebSocketClient(arrListClients.lastOrNull() ?: "", jsonObject)
                    moveTaskToBack(true)
                }


                override fun onPurchaseFailed(purchaseFailure: PurchaseFailure) {
                    when (purchaseFailure) {
                        is PurchaseFailure.PurchaseDeclined -> {
                            println("here is 1111")
                            println("here is ${purchaseFailure.transactionData}")
                            val jsonObject = JsonObject()
                            jsonObject.add(
                                "transactionData",
                                gson.toJsonTree(purchaseFailure.transactionData)
                            )
                            sendMessageToWebSocketClient(arrListClients.lastOrNull() ?: "", jsonObject)
                            moveTaskToBack(true)

                        }

                        is PurchaseFailure.PurchaseRejected -> {

                            println("here is 222")
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("status_message", "failure")
                            jsonObject.addProperty("description", purchaseFailure.message)
                            println("here is purchase rejected")
                            sendMessageToWebSocketClient(arrListClients.lastOrNull() ?: "", jsonObject)
                            moveTaskToBack(true)
                        }

                        is PurchaseFailure.AuthenticationFailed -> {
                            nearpay.updateAuthentication(AuthenticationData.Jwt("JWT HERE"))
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("status_message", "failure")
                            println("here is purchase rejected ${jsonObject}")
                            sendMessageToWebSocketClient(arrListClients.lastOrNull() ?: "", jsonObject)
                            moveTaskToBack(true)
                        }

                        is PurchaseFailure.InvalidStatus -> {
                            println("here is 4444")
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("status_message", "failure")
                            sendMessageToWebSocketClient(arrListClients.lastOrNull() ?: "", jsonObject)
                            moveTaskToBack(true)
                        }

                        is PurchaseFailure.GeneralFailure -> {
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("status_message", "failure")
                            sendMessageToWebSocketClient(arrListClients.lastOrNull() ?: "", jsonObject)
                            moveTaskToBack(true)
                        }

                        is PurchaseFailure.UserCancelled -> {
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("status_message", "failure")
                            sendMessageToWebSocketClient(arrListClients.lastOrNull() ?: "", jsonObject)
                            moveTaskToBack(true)
                        }
                    }
                }
            })

    }


    private fun sendMessageToWebSocketClient(clientId: String, jsonObject: JsonObject) {
        val clientHandler = TCPServer.WebSocketManager.getClientHandler(clientId)
        if (clientHandler != null && clientHandler.isWebSocket) {
            println("here is message sent 111")
            Thread {
                val jsonMessage = gson.toJson(jsonObject)
                println("here is message sent 222")
                clientHandler.sendMessageToClient(clientId, jsonMessage)
            }.start()
            // Optionally handle success or error
        } else {
            // Handle case where clientHandler is not found or not a WebSocket client
        }
    }

    private fun showSuccessNotification(context: Context, title: String, message: String) {
        // Create an explicit intent for the activity you want to open when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val drawable = resources.getDrawable(R.drawable.ic_app_logo, null)
        val bitmap = (drawable as BitmapDrawable).bitmap

        val customNotificationLayout = RemoteViews(context.packageName, R.layout.layout_notification).apply {
            setImageViewResource(R.id.txtTitle, R.drawable.ic_notification_img)
        }

        // Create the notification using NotificationCompat.Builder
        val builder = NotificationCompat.Builder(context, AppController.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_logo)  // Set your notification icon
            .setContent(customNotificationLayout)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent,true)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(/* notificationId */ 1, builder.build())
    }

    fun showFailureNotification(context: Context, title: String, message: String) {
        // Create an explicit intent for the activity you want to open when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create the notification using NotificationCompat.Builder
        val builder = NotificationCompat.Builder(context, AppController.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_logo)  // Set your notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(/* notificationId */ 1, builder.build())
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra("notification")) {
            println("here is notification 111")
            callPurchase(amount.asDouble())
        }
    }

}