package org.oettel.model.vectorclock;

import java.net.InetAddress;

public class VectorClockEntry {
    InetAddress ipAdress;
    int clockCount;

    public VectorClockEntry() {
    }

    public void setClockCount(int clockCount) {
        this.clockCount = clockCount;
    }

    public VectorClockEntry(InetAddress ipAdress, int clockCount) {
        this.ipAdress = ipAdress;
        this.clockCount = clockCount;
    }

    public InetAddress getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(InetAddress ipAdress) {
        this.ipAdress = ipAdress;
    }

    public int getClockCount() {
        return clockCount;
    }

    public void addCount() {
        this.clockCount = this.clockCount + 1;
    }

    public void addOneToCount() {
        this.clockCount = clockCount + 1;
    }
}
