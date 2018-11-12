package com.war4.repository.impl;

import com.war4.util.BitAndFunction;
import org.hibernate.dialect.MySQLDialect;

/**
 * 自定义方言
 * Created by E_Iva on 2018.3.28.0028.
 */
public class CustomSQLDialect extends MySQLDialect{
    public CustomSQLDialect() {
        super();
        this.registerFunction("bitand", new BitAndFunction());
    }
}
