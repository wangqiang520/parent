package com.cr999.cn.test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class test1 {
    public static void main(String[] args) throws Exception {
        //1.使用反射获取Driver实现类对象
        Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
        //这里将原始的object类型强制转换为Driver类型
        Driver driver = (Driver) clazz.newInstance();

        //2.提供另外三个连接的基本信息
        String url = "jdbc:mysql://127.0.0.1:3306/user-center";
        String user = "root";
        String password = "root";

        //注册驱动
        DriverManager.registerDriver(driver);

        //获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);

    }


    public void testConnection3() throws Exception {
        //1.使用反射获取Driver实现类对象
        Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
        //这里将原始的object类型强制转换为Driver类型
        Driver driver = (Driver) clazz.newInstance();

        //2.提供另外三个连接的基本信息
        String url = "jdbc:mysql://127.0.0.1:3306/user-center";
        String user = "root";
        String password = "root";

        //注册驱动
        DriverManager.registerDriver(driver);

        //获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println(conn);
    }

    public void aa(int a){
        a=2;
    }
    public void aa(StringBuffer sb){
        sb.append("ss");
    }
    public void aa(Test test){
        test.aa="ccccc";
    }
}
