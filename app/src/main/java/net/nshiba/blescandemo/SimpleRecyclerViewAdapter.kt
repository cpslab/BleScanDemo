package net.nshiba.blescandemo

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.nshiba.blescandemo.databinding.ItemSimpleRecyclerBinding

class SimpleRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<SimpleRecyclerViewAdapter.BindingHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    final val VIEWTYPE_DEVICE_LIST = 1

    final val VIEWTYPE_GATT_SERVICE_LIST = 2

    final val VIEWTYPE_GATT_CHARACTERISTIC_LIST = 3

    val deviceList = mutableListOf<BluetoothDevice>()

    val gattServiceList = mutableListOf<BluetoothGattService>()

    val gattCharacteristicList = mutableListOf<BluetoothGattCharacteristic>()

    var listener: ((View, Int) -> Unit)? = null

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        var recyclerView = recyclerView
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val v = inflater.inflate(R.layout.item_simple_recycler, parent, false)
        return BindingHolder(v, listener)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        val binding = holder.binding

        return when(getItemViewType(position)) {
            VIEWTYPE_DEVICE_LIST -> {
                binding.deviceAddress.text = "address: ${deviceList[position].address}"
                binding.deviceName.text = "name: ${deviceList[position].name}"
            }
            VIEWTYPE_GATT_SERVICE_LIST -> {
                binding.deviceAddress.text = "service uuid: ${gattServiceList[position].uuid}"
            }
            VIEWTYPE_GATT_CHARACTERISTIC_LIST -> {
                binding.deviceAddress.text = "characteristic uuid: ${gattCharacteristicList[position].uuid}"
                binding.deviceName.text = "characteristic description: ${gattCharacteristicList[position].descriptors}"
            }
            else -> return
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (deviceList.isNotEmpty()) {
            VIEWTYPE_DEVICE_LIST
        } else if (gattServiceList.isNotEmpty()) {
            VIEWTYPE_GATT_SERVICE_LIST
        } else if (gattCharacteristicList.isNotEmpty()) {
            VIEWTYPE_GATT_CHARACTERISTIC_LIST
        } else {
            throw IllegalStateException("not found data list")
        }
    }

    override fun getItemCount(): Int {
        return if (deviceList.isNotEmpty()) {
            deviceList.size
        } else if (gattServiceList.isNotEmpty()) {
            gattServiceList.size
        } else if (gattCharacteristicList.isNotEmpty()) {
            gattCharacteristicList.size
        } else {
            0
        }
    }

    fun addDevice(data: BluetoothDevice) {
        addDevice(listOf(data))
    }

    fun addDevice(dataList: List<BluetoothDevice>) {
        val beforePos = deviceList.size
        deviceList.addAll(dataList)
        notifyItemRangeInserted(beforePos, dataList.size)
    }

    fun addGattService(data: BluetoothGattService) {
        addGattService(listOf(data))
    }

    fun addGattService(dataList: List<BluetoothGattService>) {
        val beforePos = deviceList.size
        gattServiceList.addAll(dataList)
        notifyItemRangeInserted(beforePos, dataList.size)
    }

    fun addGattCharacteristic(data: BluetoothGattCharacteristic) {
        addGattCharacteristic(listOf(data))
    }

    fun addGattCharacteristic(dataList: List<BluetoothGattCharacteristic>) {
        val beforePos = deviceList.size
        gattCharacteristicList.addAll(dataList)
        notifyItemRangeInserted(beforePos, dataList.size)
    }

    fun clearData() {
        deviceList.clear()
        gattServiceList.clear()
        gattCharacteristicList.clear()
        notifyDataSetChanged()
    }

    class BindingHolder(itemView: View, private val listener: ((View, Int) -> Unit)?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val binding: ItemSimpleRecyclerBinding = DataBindingUtil.bind<ItemSimpleRecyclerBinding>(itemView)

        init {
            binding.itemContainer.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener?.invoke(v, layoutPosition)
        }
    }
}
