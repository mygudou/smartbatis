package com.youdao.smartbatis;

/**
 * @author liugang
 */
public class CurrentDataSourceHoler {
    private static final ThreadLocal<DataSourceType> currentDataSource = new ThreadLocal<DataSourceType>();

    static {
        setCurrentDataSource(DataSourceType.WRITE);
    }
    public static void setCurrentDataSource(DataSourceType dataSourceType){
        currentDataSource.set(dataSourceType);
    }

    public static DataSourceType getCurrentDataSource(){
        return currentDataSource.get();
    }
    
    public static void clearDataSource() {
        currentDataSource.remove();
    }
}
