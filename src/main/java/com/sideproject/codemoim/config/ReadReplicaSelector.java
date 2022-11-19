package com.sideproject.codemoim.config;

import java.util.List;

public class ReadReplicaSelector {
    private List<String> readReplicaLookupKeys;
    private int index = 0;

    public void setReadReplicaLookupKeys(List<String> readReplicaLookupKeys) {
        this.readReplicaLookupKeys = readReplicaLookupKeys;
    }

    public String getReadReplicaLookupKey() {
        if (index >= readReplicaLookupKeys.size()) {
            index = 0;
        }

        return readReplicaLookupKeys.get(index++);
    }
}