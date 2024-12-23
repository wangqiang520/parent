package com.cr999.cn.test;

import java.sql.*;

public class JdbcConnect {
    public static void main(String[] args) throws Exception {
        String jdbcName="oracle.jdbc.OracleDriver";//定义驱动程序名为jdbcName内容为com.mysql.jdbc.Driver
        String dbUserName="scott";//定义用户名，写你想要连接到的用户。
        String dbPassword="scott";//用户密码。
        //定义url；jdbc是协议；mysql是子协议：表示数据库系统管理名称；localhost：3306是你数据库来源的地址和目标端口；test是我本人建的表位置所在处，你以你的为标准。
        //防止乱码；useUnicode=true表示使用Unicode字符集；characterEncoding=UTF8表示使用UTF-8来编辑的。
        String dbUrl="jdbc:mysql://localhost:3306/book?"+"useUnicode=true&characterEncoding=UTF8";

        String sql="select * from student";//定义查询语句
        Class.forName(jdbcName); //注册驱动程序，用java.lang包下面的class类里面的Class.froName();方法 此处的driver就是1里面定义的driver，也可以  Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection(dbUrl,dbUserName,dbPassword);//获取数据库连接,使用java.sql里面的DriverManager的getConnectin(String url , String username ,String password )来完成

        //构造一个statement对象来执行sql语句：主要有Statement，PreparedStatement，CallableStatement三种实例来实现
        //三种实现方法分别为：Statement stmt = con.createStatement() ;
        // PreparedStatement pstmt = conn.prepareStatement(sql) ;
        //CallableStatement cstmt =  conn.prepareCall("{CALL demoSp(? , ?)}") ;
        Statement stmt=con.createStatement();

        ResultSet rs=stmt.executeQuery(sql);//执行sql并返还结束 ；ResultSet executeQuery(String sqlString)：用于返还一个结果集（ResultSet）对象。

        //遍历结果集
        while(rs.next()){
            System.out.println("学生编号："+rs.getInt("stuId")+"，学生姓名："+rs.getString("stuName")+"，学生性别："+rs.getString("stuSex")+"，学生年龄："+rs.getInt("stuAge"));//使用getString()方法获取你表里的资料名
        }

        if(rs!=null){//关闭记录集
            try{
                rs.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(stmt!=null){//关闭说明对象
            try{
                stmt.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }

        if(con!=null){//关闭连接，就像关门一样，先关里面的，最后关最外面的
            try{
                con.close();
            }catch(SQLException e){
                e.printStackTrace();
            }

        }
    }
}
