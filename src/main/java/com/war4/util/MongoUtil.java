package com.war4.util;

import org.springframework.data.mongodb.core.query.Update;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * mongodb数据库工具类
 * Created by E_Iva on 2018.4.21.0021.
 */
public class MongoUtil {
    private MongoUtil() {
    }

    /**
     * 创建mongodb的update，设置对象中的所有字段
     *
     * @param o       对象
     * @param setNull 是否设置空值
     */
    public static Update createUpdate(Object o, boolean setNull) {
        Update update = new Update();
        try {
            PropertyDescriptor[] pds;
            pds = Introspector.getBeanInfo(o.getClass(), Object.class).getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                Object ret = pd.getReadMethod().invoke(o);
                if (setNull || ret != null) {
                    if (pd.getWriteMethod() != null) {
                        update.set(pd.getName(), ret);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

}
