package pl.rciurkot.indoor.beacon

import org.altbeacon.beacon.BeaconParser

/**
 * Created by rafalciurkot on 15.12.14.
 */

val ESTIMOTE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D"

public class EstimoteBeaconParser : BeaconParser() {
    {
        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
    }
}