package com.youdao.smartbatis.interceptor;

import com.youdao.smartbatis.CurrentDataSourceHoler;
import com.youdao.smartbatis.DataSource;
import com.youdao.smartbatis.DataSourceType;
import com.youdao.smartbatis.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于注解的读写分离
 *
 * @author liugang
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update",
                args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query",
                args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
})
@Slf4j
public class AnnotationInterceptor implements Interceptor {
    private static final Map<String,DataSourceType> cache = new ConcurrentHashMap<String, DataSourceType>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        String id = mappedStatement.getId();
        DataSourceType curDataSourceType = DataSourceType.WRITE;
        if(cache.containsKey(id))
            curDataSourceType = cache.get(id);
        else {
            Method method = getMappedInterfaceMethod(id);
            if (method != null && method.isAnnotationPresent(DataSource.class)) {
                curDataSourceType = method.getAnnotation(DataSource.class).type();
                log.debug("@@ROUTING_DATASOURCE {}", curDataSourceType);
            }
            cache.put(id,curDataSourceType);
        }
        CurrentDataSourceHoler.setCurrentDataSource(curDataSourceType);
        log.debug("@@CURRENT_DATASOURCE {}", CurrentDataSourceHoler.getCurrentDataSource());
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if(target instanceof Executor)
            return Plugin.wrap(target,this);
        else
            return target;
    }

    @Override
    public void setProperties(Properties properties) {}

    private Method getMappedInterfaceMethod(String id){
        String[] items = id.split("\\.");
        ArrayList<String> nameList = new ArrayList<String>(Arrays.asList(items));
        if (nameList.size() < 2)
            return null;
        String methodName = nameList.get(nameList.size()-1);
        nameList.remove(nameList.size()-1);
        String className = StringUtils.join(nameList,".");
        Method method = ReflectUtil.getMethodByName(ReflectUtil.getClass(className),methodName);
        return method;
    }
}
