package com.youdao.smartbatis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liugang
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Getter @Setter
    private Object writeDataSource;
    @Getter@Setter
    private List<Object> readDataSourceList;
    private int readDataSourceSize;

    private AtomicInteger counter = new AtomicInteger(0);


    @Override
    public void afterPropertiesSet(){
        if (writeDataSource == null){
            throw new IllegalArgumentException("Property 'writeDataSource' is required");
        }
        setDefaultTargetDataSource(writeDataSource);
        Map<Object,Object> dataSourceMap = new HashMap<Object,Object>();
        dataSourceMap.put(DataSourceType.WRITE.name(),writeDataSource);
        if (readDataSourceList == null){
            readDataSourceSize = 0;
        }else{
            for(int i = 0;i < readDataSourceList.size();i++){
                dataSourceMap.put(DataSourceType.READ.name()+i,readDataSourceList.get(i));
            }
            readDataSourceSize = readDataSourceList.size();
        }
        setTargetDataSources(dataSourceMap);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {

        DataSourceType dataSourceType = CurrentDataSourceHoler.getCurrentDataSource();
        if(dataSourceType == DataSourceType.READ && readDataSourceSize > 0){
            int curentValue = counter.incrementAndGet();
            if(curentValue >= Integer.MAX_VALUE)
                counter.set(0);
            int index = curentValue % readDataSourceSize;
            return DataSourceType.READ.name()+index;
        }
        return DataSourceType.WRITE.name();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }
}
