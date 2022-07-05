package com.atom.configuration.map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Atom
 */
public class MapConfigurationDemo {

    public static void main(String[] args) {
        Map<String, Object> source = new HashMap<>();
        source.put("common.name", "YourBatman");
        source.put("common.age", 18);

        Configuration configuration = new MapConfiguration(source);
        System.out.println(configuration.getString("common.name"));
        System.out.println(configuration.getInt("common.age"));
    }
}
