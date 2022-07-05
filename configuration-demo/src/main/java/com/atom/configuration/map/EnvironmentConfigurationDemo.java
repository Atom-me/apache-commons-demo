package com.atom.configuration.map;

import org.apache.commons.configuration.EnvironmentConfiguration;

/**
 * @author Atom
 */
public class EnvironmentConfigurationDemo {
    public static void main(String[] args) {
        EnvironmentConfiguration config = new EnvironmentConfiguration();
        System.err.println(config.getString("USER"));
        System.err.println(config.getString("SHELL"));
    }
}
