package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ViewDeviceActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var mqttService: MQTTService
    private var apiController = APIController(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_device)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { onBackPressed() }
        findViewById<ImageButton>(R.id.power_button).setOnClickListener { changeDeviceStatus()}
        findViewById<ImageButton>(R.id.power_icon_button).setOnClickListener { changeDeviceStatus()}
        findViewById<ImageButton>(R.id.setting_button).setOnClickListener{ showDeviceSettingPopup() }

        val device = intent.getSerializableExtra("device") as Device
        mqttService = MainActivity.mqttService
        findViewById<TextView>(R.id.device_room).text = device.room
        findViewById<TextView>(R.id.device_name).text = device.name
        findViewById<TextView>(R.id.device_status).text = if (device.status) "ON" else "OFF"
        findViewById<ImageView>(R.id.device_image).setImageDrawable(Utility(this).getDeviceImage(device))
    }

    private fun changeDeviceStatus(){
        val device = intent.getSerializableExtra("device") as Device
        device.status = !device.status
        val statusStr: String = if (device.status) "ON" else "OFF"
        findViewById<TextView>(R.id.device_status).text = statusStr
//        mqttService.sendDataMQTT(makeMessage(device, (if (device.status) 1 else 0).toString()).toString())

        apiController.jsonObjectGET("/"){ res ->
            println(res.toString())
        }

        apiController.jsonObjectPOST("/turn-device", makeMessage(device,
            (if (device.status) 1 else 0).toString())){ res ->
            println(res.toString())
        }

    }

    private fun makeMessage(device: Device, data: String): JSONObject{
        val message = JSONObject()
        message.put("id", "11")
        message.put("name", "RELAY")
        message.put("data", data)
        message.put("unit", "")
        return message
    }


    private fun showDeviceSettingPopup(){
        val button = findViewById<ImageButton>(R.id.setting_button)
        val popup = PopupMenu(this, button)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.setting_devide_popup_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.option_set_power_schedule -> {
                val i = Intent(this, SetTurnOnOffTimeActivity::class.java)
                startActivity(i)
//                Toast.makeText(this, ,"Set power selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.option_set_auto_mode -> {
                val i = Intent(this, SetAutoModeActivity::class.java)
                startActivity(i)
                true
            }
            else -> false
        }
    }

}