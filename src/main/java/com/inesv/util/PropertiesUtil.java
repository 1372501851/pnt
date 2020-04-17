package com.inesv.util;


import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static Properties prop = null;


    public static String getPropertiesValue(String propertiesName, String propKey) {
        prop = new Properties();
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesName);//获取路径并转换成流
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        String propValue = prop.getProperty(propKey);
        return propValue;
        
    }
}
