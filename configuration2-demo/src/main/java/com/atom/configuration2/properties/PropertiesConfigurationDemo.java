package com.atom.configuration2.properties;


import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.Iterator;

/**
 * @author Atom
 */
public class PropertiesConfigurationDemo {

    public static void main(String[] args) throws ConfigurationException {
        Configurations configurations = new Configurations();

        // 设置编码，此处的实际是个PropertiesConfiguration，它默认采用的是`ISO-8859-1`所以中文乱码~
        // 注意：这个前提是你的properties文件是utf-8编码的~~~
        FileBasedConfigurationBuilder.setDefaultEncoding(PropertiesConfiguration.class, "UTF-8");
        // 每个Configuration代表这一个配置文件~（依赖beanutils这个jar）
        Configuration configuration = configurations.properties("my.properties");


        // 采用Builder模式处理更为复杂的一些场景   比如把逗号分隔的字符串解析到数组、解析到list、前后拼接字符串等等操作
        // 其实你直接configs.properties(...)它的内部原理也是builder模式~
        //FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder("my.properties");
        //builder.addEventListener();
        //builder.getConfiguration();
        //builder.getFileHandler();


        Iterator<String> keys = configuration.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = configuration.getString(key);
            System.err.println(key + " = " + value);
        }
    }
}
