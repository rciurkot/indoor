package org.altbeacon.beacon

/**
 * Created by rafalciurkot on 15.12.14.
 */
public class EstimoteBeaconParser : BeaconParser() {
    {
        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
    }
}