package net.nshiba.blescandemo

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.PermissionChecker
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import net.nshiba.blescandemo.databinding.ActivityMainBinding
import android.bluetooth.BluetoothGattService
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        checkBluetoothEnable()
        val bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        bleScanModel = BleScanModel(bluetoothAdapter, this::onScanResult, this::showGatt)
        initLeScan()
        initRecyclerView()
    }

    private fun checkBluetoothEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        }

//        if (!bluetoothAdapter.isEnabled()) {
//            val requireEnableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(requireEnableBluetooth, REQUEST_ENABLE_BT)
//        }
    }

    private fun initLeScan() {
        binding.scanBtn.setOnClickListener({
            if (!bleScanModel.isScanning) {
                recyclerAdapter.clearData()
                binding.header.text = "scan result"

                bleScanModel.scanLe()
            } else {
                Toast.makeText(this, "is scanning ble...\nplease wait...", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initRecyclerView() {
        recyclerAdapter.listener = this::onItemClick
        binding.deviceList.adapter = recyclerAdapter
        binding.deviceList.layoutManager = LinearLayoutManager(this)
    }

    private fun onItemClick(v: View, position: Int) {
        if (bleScanModel.isScanning) {
            Toast.makeText(this, "is scanning ble...\nplease wait...", Toast.LENGTH_SHORT).show()
            return
        }

        when(recyclerAdapter.getItemViewType(position)) {
            recyclerAdapter.VIEWTYPE_DEVICE_LIST -> {
                val device = recyclerAdapter.deviceList[position]
                showDeviceDetail(device)
            }
            recyclerAdapter.VIEWTYPE_GATT_SERVICE_LIST -> {
                val service = recyclerAdapter.gattServiceList[position]
                binding.header.text = service.uuid.toString()

                recyclerAdapter.clearData()
                recyclerAdapter.addGattCharacteristic(service.characteristics)
            }
        }
    }

    private fun onScanResult(callback: Int, result: ScanResult) {
        if (!recyclerAdapter.deviceList.contains(result.device)) {
            recyclerAdapter.addDevice(result.device)
        }
    }

    private fun showDeviceDetail(device: BluetoothDevice) {
        Log.d(TAG, "----- device detail -----")
        Log.d(TAG, "address: ${device.address}")
        Log.d(TAG, "name: ${device.name}")
        bleScanModel.connect(this, device)
    }

    private fun showGatt(gatt: BluetoothGatt, status: Int) {
        gatt.services?.let {
            handler.post {
                recyclerAdapter.clearData()
                recyclerAdapter.addGattService(it)

                binding.header.text = gatt.device.name
                showServices(it)
            }

        }
    }

    private fun showServices(services: List<BluetoothGattService>) {
        for (s in services) {
            // サービス一覧を取得したり探したりする処理
            // あとキャラクタリスティクスを取得したり探したりしてもよい
            Log.d(TAG, "service: ${s.uuid}")
            if (!s.includedServices.isEmpty()) {
                showServices(s.includedServices)
            }
            if (!s.characteristics.isEmpty()) {
                showCharacteristic(s.characteristics)
            }
        }
    }

    private fun showCharacteristic(characteristices: List<BluetoothGattCharacteristic>) {
        for (c in characteristices) {
            // サービス一覧を取得したり探したりする処理
            // あとキャラクタリスティクスを取得したり探したりしてもよい
            Log.d(TAG, "characteristic: ${c.uuid}")
        }
    }

    private final val TAG = MainActivity::class.java.simpleName

    private lateinit var binding: ActivityMainBinding

    private lateinit var bleScanModel: BleScanModel

    private val recyclerAdapter by lazy { SimpleRecyclerViewAdapter(this) }

    private val handler = Handler()

}
