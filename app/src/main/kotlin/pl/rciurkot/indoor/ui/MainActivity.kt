package pl.rciurkot.indoor.ui

import android.support.v7.app.ActionBarActivity
import android.support.v7.app.ActionBar
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.os.Build

import pl.rciurkot.indoor.R
import android.bluetooth.BluetoothAdapter
import android.os.Handler
import kotlin.properties.Delegates
import android.content.Context
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothDevice
import timber.log.Timber
import java.util.Arrays

public class MainActivity : ActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, PlaceholderFragment()).commit()
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment : Fragment() {
        private val mBluetoothAdapter: BluetoothAdapter by Delegates.lazy {
            val bluetoothManager = getActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.getAdapter();
        }
        private var mScanning: Boolean = false
        private val mHandler: Handler by Delegates.lazy { Handler() };

        // Stops scanning after 10 seconds.
        private val SCAN_PERIOD = 10000L


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            mHandler.hashCode() //initializes
            mBluetoothAdapter.hashCode() //initializes
            return rootView
        }

        override fun onResume() {
            super<Fragment>.onResume()
            scanLeDevice(true)
        }

        private val mLeScanCallback = object : BluetoothAdapter.LeScanCallback {
            override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
                Timber.d("device $device rssi $rssi scanRecoder ${Arrays.toString(scanRecord)}")
            }
        }

        private fun scanLeDevice(enable: Boolean) {
            Timber.d("scanLeDevice $enable")
            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(object : Runnable {
                    override fun run() {
                        mScanning = false
                        mBluetoothAdapter.stopLeScan(mLeScanCallback)
                    }
                }, SCAN_PERIOD)

                mScanning = true
                mBluetoothAdapter.startLeScan(mLeScanCallback)
            } else {
                mScanning = false
                mBluetoothAdapter.stopLeScan(mLeScanCallback)
            }
        }
    }
}
