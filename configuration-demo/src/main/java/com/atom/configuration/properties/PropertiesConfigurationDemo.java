package com.atom.configuration.properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Atom
 */
public class PropertiesConfigurationDemo {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigurationDemo.class);
    private static Configuration conf;
    private static final String FIRST_PROPERTIES = "first.properties";


    static {
        try {
            conf = new PropertiesConfiguration(FIRST_PROPERTIES);
        } catch (Exception e) {
            logger.error("load configuration failed : ", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {

        System.out.println(conf.getString("common.name"));
        System.out.println(conf.getString("common.fullname"));
        System.out.println(conf.getInt("common.age"));
        System.out.println(conf.getString("common.addr"));
        System.out.println(conf.getLong("common.count"));

        // 打印include的内容
        System.out.println(conf.getString("java.version"));

        System.out.println();
        System.out.println("=====使用subset方法得到一个子配置类=====");
        Configuration subConfig = conf.subset("common");
        subConfig.getKeys().forEachRemaining((k) -> {
            System.err.println(k + "-->" + subConfig.getString(k));
        });
    }

}
