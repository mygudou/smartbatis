/**
 * @(#)DynamicDataSourceTransactionManager.java, 2017年12月8日. 
 * 
 * Copyright 2017 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.youdao.smartbatis;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    /**
     * 只读事务到读库，读写事务到写库
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {

        //设置数据源
        boolean readOnly = definition.isReadOnly();
        if(readOnly) {
            CurrentDataSourceHoler.setCurrentDataSource(DataSourceType.READ);
        } else {
            CurrentDataSourceHoler.setCurrentDataSource(DataSourceType.WRITE);
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        CurrentDataSourceHoler.clearDataSource();
    }
}
