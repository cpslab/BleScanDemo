package net.nshiba.blescandemo

import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import java.util.*
import kotlin.concurrent.schedule

class BleScanModel(val bluetoothAdapter: BluetoothAdapter,
                   val showScanResult: (callbackType: Int, result: ScanResult) -> Unit,
                   val showGatt: (gatt: BluetoothGatt, status: Int) -> Unit) {

    fun scanLe() {
        Timer().schedule(SCAN_PERIOD) {
            bluetoothLeScanner.stopScan(scanCallback)
            isScanning = false
        }

        bluetoothLeScanner.startScan(scanCallback)
        isScanning = true

//            val scanFilter = ScanFilter.Builder()
//                    .setDeviceName("ESP_GATTS_DEMO")
//                    .build()
//            val scanSettings = ScanSettings.Builder()
//                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
//                    .build()
//            bluetoothLeScanner.startScan(
//                    mutableListOf(scanFilter).toMutableList(), scanSettings, scanCallback)
    }

    fun connect(context: Context, device: BluetoothDevice) {
        val bluetoothGatt = device.connectGatt(context, false, gattCallback);
        bluetoothGatt?.connect();
    }

    fun discoverService(bluetoothGatt: BluetoothGatt?) {
        bluetoothGatt?.discoverServices();
    }

//    --- variables ---

    private final val TAG = BleScanModel::class.java.simpleName

    private val bluetoothLeScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

    private final val REQUEST_ENABLE_BT = 1

    private final val SCAN_PERIOD: Long = 10000

    var isScanning = false

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            showScanResult(callbackType, result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.let { showGatt(gatt, status) }
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                discoverService(gatt);
            }
        }
    }
}