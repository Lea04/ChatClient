package org.oettel.model.vectorclock;

import org.oettel.configuration.ClientConfigurationSingleton;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class VectorClockSingleton {
    public static VectorClockSingleton instance;
    private List<VectorClockEntry> vectorClockEntryList;

    private VectorClockSingleton() {
        vectorClockEntryList = new ArrayList<>();
    }

    public static VectorClockSingleton getInstance() {
        if (VectorClockSingleton.instance == null) {
            VectorClockSingleton.instance = new VectorClockSingleton();
        }
        return VectorClockSingleton.instance;
    }

    public List<VectorClockEntry> getVectorClockEntryList() {
        return vectorClockEntryList;
    }

    public void setVectorClockEntryList(List<VectorClockEntry> vectorClockEntryList) {
        this.vectorClockEntryList = vectorClockEntryList;
    }

    public void addVectorClockEntryToList(VectorClockEntry vectorClockEntry) {
        this.vectorClockEntryList.add(vectorClockEntry);
    }

    public void updateVectorclock() {
        InetAddress localAddress = ClientConfigurationSingleton.getInstance().getServerAddress();
        vectorClockEntryList.forEach(vectorClockEntry -> {
            if(vectorClockEntry.getIpAdress().toString().contains(localAddress.toString())){
                vectorClockEntry.addOneToCount();
            }
        });
    }

    public void updateExternalVectorclockEntries(VectorClockEntry externalVectorClock) {
        String internalAddress = ClientConfigurationSingleton.getInstance().getServerAddress().toString();
        vectorClockEntryList.forEach(internalVectorClock -> {
            if (!externalVectorClock.getIpAdress().toString().contains(internalAddress)) {
                int newValue = compareClockCounts(internalVectorClock.getClockCount(), externalVectorClock.getClockCount());
                internalVectorClock.setClockCount(newValue);
            }
        });

    }

    private int compareClockCounts(int internalClockCount, int externalClockCount) {
        if (internalClockCount < externalClockCount) {
            return externalClockCount;
        } else if (internalClockCount > externalClockCount) {
            return internalClockCount;
        } else {
            return internalClockCount;
        }
    }
}
