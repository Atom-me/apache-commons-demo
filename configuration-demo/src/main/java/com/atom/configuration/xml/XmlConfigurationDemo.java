package com.atom.configuration.xml;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

/**
 * @author Atom
 */
public class XmlConfigurationDemo {
    public static void main(String[] args) throws ConfigurationException {
//        getNormalXmlConfig();
//        getComplexXmlConfig();

        getComplexXmlConfigUseXPath();
    }

    private static void getComplexXmlConfigUseXPath() throws ConfigurationException {
        XMLConfiguration config = new XMLConfiguration("const2.xml");
        config.setExpressionEngine(new XPathExpressionEngine());
        System.err.println(config.getString("databases/database[name = 'dev']/url"));
        System.err.println(config.getString("databases/database[name = 'production']/url"));
    }


    private static void getComplexXmlConfig() throws ConfigurationException {
        Configuration config = new XMLConfiguration("const2.xml");
        System.err.println(config.getString("databases.database(0).url"));
        System.err.println(config.getString("databases.database(1).url"));
    }

    private static void getNormalXmlConfig() throws ConfigurationException {
        Configuration conf = new XMLConfiguration("const.xml");
        System.err.println(conf.getString("database.url"));
        System.err.println(conf.getString("database.port"));
    }
}
