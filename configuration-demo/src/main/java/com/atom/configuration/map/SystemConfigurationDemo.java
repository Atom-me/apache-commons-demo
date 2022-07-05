package com.atom.configuration.map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.SystemConfiguration;

/**
 * @author Atom
 */
public class SystemConfigurationDemo {
    public static void main(String[] args) {
        Configuration conf = new SystemConfiguration();
        System.err.println(conf.getString("user.home"));
        System.err.println(conf.getString("user.name"));
    }
}
