package com.sideproject.codemoim.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CustomRoutingDataSource extends AbstractRoutingDataSource {

    private static final String MASTER = "master";
    private static final String REPLICA = "replica";
    private final ReadReplicaSelector readReplicaSelector = new ReadReplicaSelector();

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        List<String> readOnlyDataSourceLookupKeys = targetDataSources.keySet()
                .stream()
                .map(obj -> String.valueOf(obj))
                .filter(lookupKey -> lookupKey.contains(REPLICA)).collect(Collectors.toList());

        readReplicaSelector.setReadReplicaLookupKeys(readOnlyDataSourceLookupKeys);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? readReplicaSelector.getReadReplicaLookupKey()
                : MASTER;
    }

}
