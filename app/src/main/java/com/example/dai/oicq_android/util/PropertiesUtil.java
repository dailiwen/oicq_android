package com.example.dai.oicq_android.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件(properties)中的内容
 * @author dailiwen
 * @date 2018/04/20
 */
public class PropertiesUtil {
    private static Properties urlProps;
    public static Properties getProperties(){
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context获取setting.properties的FileInputStream
            //InputStream in = c.getAssets().open("appConfig.properties");
            //方法二：通过class获取setting.properties的FileInputStream
            InputStream in = PropertiesUtil.class.getResourceAsStream("/assets/appConfig.properties");
            props.load(in);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        urlProps = props;
        return urlProps;
    }
}
