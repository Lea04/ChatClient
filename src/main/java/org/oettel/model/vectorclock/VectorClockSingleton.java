package org.oettel.model.vectorclock;

import org.oettel.configuration.ClientConfigurationSingleton;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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


    public void order() throws IOException {

        if(ClientConfigurationSingleton.getInstance().getHoldbackQueue().size() > 1) {

            while(ClientConfigurationSingleton.getInstance().getHoldbackQueue().size() > 0) {
                List<VectorClockEntry> newList = new ArrayList<>();
                ClientConfigurationSingleton.getInstance().getHoldbackQueue().stream().forEach(holdbackMessage -> {
                    AtomicBoolean istSmallest = new AtomicBoolean(false);
                    ClientConfigurationSingleton.getInstance().getHoldbackQueue().forEach(holdbackMessageCompare ->{
                        for(int i = 0; i < holdbackMessage.getVectorClockEntries().size();i++){
                            if(holdbackMessage.getVectorClockEntries().get(i).getClockCount()== holdbackMessageCompare.getVectorClockEntries().get(i).getClockCount()){
                                istSmallest.set(true);
                            }else if(holdbackMessage.getVectorClockEntries().get(i).getClockCount()< holdbackMessageCompare.getVectorClockEntries().get(i).getClockCount()){
                                istSmallest.set(true);
                            }else if(holdbackMessage.getVectorClockEntries().get(i).getClockCount()> holdbackMessageCompare.getVectorClockEntries().get(i).getClockCount()){
                                istSmallest.set(false);
                            }
                        }
                    });
                    if(istSmallest.get()){
                        ClientConfigurationSingleton.getInstance().getDeliveryQueue().add(holdbackMessage);
                        ClientConfigurationSingleton.getInstance().getHoldbackQueue().remove(holdbackMessage);
                    }
                });

            }
        }else{
            ClientConfigurationSingleton.getInstance().setDeliveryQueue(ClientConfigurationSingleton.getInstance().getHoldbackQueue());
        }
    }
}
