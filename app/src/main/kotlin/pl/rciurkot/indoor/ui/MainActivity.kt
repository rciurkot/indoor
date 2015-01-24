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
import org.altbeacon.beacon.BeaconManager
import pl.rciurkot.indoor.IndoorApp
import org.altbeacon.beacon.BeaconConsumer
import android.content.ServiceConnection
import android.content.Intent
import pl.rciurkot.indoor.beacon.EstimoteBeaconParser
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Region
import android.widget.TextView
import java.util.HashMap
import pl.rciurkot.indoor.location.Space
import pl.rciurkot.indoor.location.Coord
import pl.rciurkot.indoor.beacon.ESTIMOTE_UUID
import pl.rciurkot.indoor.positioning.HeremanTrilateration
import pl.rciurkot.kotlin.util.notempty
import pl.rciurkot.indoor.mapping.FloorPlan
import pl.rciurkot.indoor.mapping.QuadTreeFloorPlan
import pl.rciurkot.indoor.location.coord
import pl.rciurkot.indoor.mapping.ClassRoom

public class MainActivity : ActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            //            getSupportFragmentManager().beginTransaction().add(R.id.container, PlaceholderFragment()).commit()
            getSupportFragmentManager().beginTransaction().add(R.id.container, AltBeaconFragment()).commit()
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

    public class AltBeaconFragment : Fragment(), BeaconConsumer {
        private val beaconManager = BeaconManager.getInstanceForApplication(IndoorApp.self);
        private var textView: TextView? = null
        private val beaconsMap = HashMap<Int, Beacon>()
        private val space = buildSpace()
        private val floorPlan = buildFloorPlan()

        override fun onCreate(savedInstanceState: Bundle?) {
            super<Fragment>.onCreate(savedInstanceState)
            beaconManager.getBeaconParsers().add(EstimoteBeaconParser())
            BeaconManager.setDebug(false)
            beaconManager bind this

            val coord = coord(1.1, 0.8)
            val nn = floorPlan.neighbours(coord, 0.1)
            val min = nn.minBy { floorPlan.dist(coord, it) }

            Timber.e("nn $nn\nmin $min")

        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)

            textView = rootView.findViewById(R.id.textView) as TextView?
            return rootView
        }

        override fun onDestroy() {
            super<Fragment>.onDestroy()
            beaconManager unbind this
        }

        override fun getApplicationContext(): Context? = getActivity().getApplicationContext()

        override fun unbindService(connection: ServiceConnection?) = getActivity().unbindService(connection)

        override fun bindService(intent: Intent?, connection: ServiceConnection?, mode: Int): Boolean = getActivity().bindService(intent, connection, mode)

        override fun onBeaconServiceConnect() {
            //            beaconManager.setRangeNotifier(object : RangeNotifier {
            //                override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
            //                    if (beacons?.notEmpty ?: false) {
            //                        Timber.d("region $region")
            //                        beacons!! forEach {
            //                            Timber.d("beacon $it dist ${it.getDistance()}")
            //                            beaconsMap += it.id to it
            //                        }
            //
            //                        val sb = StringBuilder()
            //                        //                        val sorted = beacons.sortBy { it.id }
            //
            //                        beaconsMap.values() forEach { sb appendln "${it.getId2()}\u0009${it.getId3()}\u0009${it.getRssi()}\n${it.getDistance().format(2)}\n" }
            //                        getActivity() runOnUiThread { textView!! setText sb }
            //                    }
            //                }
            //            })
            beaconManager.setRangeNotifier(object : RangeNotifier {
                val positionResolver = HeremanTrilateration()
                override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
                    if (beacons.notempty) {
                        beacons!! forEach {
                            space.updateDist(it.uuid, it.getDistance())
                        }
                        val pos = positionResolver calculatePositionIn space
                        val room = floorPlan roomAt pos
                        Timber.e("!!!!!!! ${pos.x} ${pos.y} $room")
                        getActivity() runOnUiThread { textView!! setText "${pos.x}\n${pos.y}\n${space.toString()}\n${room.buildingComponent.name}" }
                    }
                }

            })


            beaconManager startRangingBeaconsInRegion Region(ESTIMOTE_UUID, null, null, null)
        }

        val Beacon.id: Int
            get() = getId2().toInt() * 10000 + getId3().toInt()

        val Beacon.uuid: String
            get() = "${getId1()}_${getId2()}_${getId3()}".toLowerCase()

        fun buildSpace(): Space {
            val space = Space()
            //            space registerCoord Coords("${ESTIMOTE_UUID}_59035_20098".toLowerCase(), 1.0, 0.0)
            //            space registerCoord Coords("${ESTIMOTE_UUID}_34703_1746".toLowerCase(), 0.0, 1.0)
            //            space registerCoord Coords("${ESTIMOTE_UUID}_38742_36688".toLowerCase(), 1.0, 2.0)
            space registerCoord Coord("${ESTIMOTE_UUID}_55296_8624".toLowerCase(), 0.0, 1.25) //GRANATOWY
            space registerCoord Coord("${ESTIMOTE_UUID}_59035_20098".toLowerCase(), 2.0, 0.0) //OLIWKOWY
            space registerCoord Coord("${ESTIMOTE_UUID}_38742_36688".toLowerCase(), 2.0, 2.25) //BŁĘKITNY

            return space
        }

        fun buildFloorPlan() = QuadTreeFloorPlan.Builder()
                .add(coord(0, 0), coord(1, 1), ClassRoom("1"))
                .add(coord(1, 1), coord(2, 2), ClassRoom("2"))
                .build()
    }
}
