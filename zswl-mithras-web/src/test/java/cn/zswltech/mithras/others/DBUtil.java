package cn.zswltech.mithras.others; /**
 * @author luyi
 */

import cn.hutool.json.JSONUtil;
import org.flowable.common.engine.impl.util.ReflectUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {


    /**
     * 检查是否有不能反序列化的BYTES_
     *
     * @param args
     */
    public static void main(String[] args) {
        //声明Connection对象
        Connection con;//驱动程序名
        String driver = "com.mysql.cj.jdbc.Driver";
        //URL指向要访问的数据库名--sixibiheye,（自行更改成自己的库名）
        String url = "jdbc:mysql://10.158.20.215:3306/mithras_pre?useSSL=false";
        //MySQL配置时的用户名（自行更改成自己的用户名）
        String user = "pre_user";
        //MySQL配置时的密码（自行更改成自己的密码）
        String password = "Zszl@2023De*#";
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                System.out.println("Succeeded connecting to the Database!");
            }
            try {
                //2.创建statement类对象，用来执行SQL语句！！
                Statement statement = con.createStatement();
                //要执行的SQL语句
                String sql = "select * from act_ge_bytearray\n" +
                        "where ID_ IN (select BYTEARRAY_ID_ from  act_ru_variable where PROC_INST_ID_ IN (\n" +
                        "    select ID_ from act_hi_procinst where END_TIME_ is null\n" +
                        "    ))\n" +
                        "\n" +
                        "\n" +
                        ";";
                ResultSet resultSet = statement.executeQuery(sql);
                List<String> idList = new ArrayList<>();
                int count = 0;
                int total = 0;
                while (resultSet.next()) {
                    total++;
                    String name = resultSet.getString("NAME_");
                    String id = resultSet.getString("ID_");
                    if (!name.contains("var")) {
                        continue;
                    }
                    byte[] bytes = resultSet.getBytes("BYTES_");
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    try {
                        ObjectInputStream ois = createObjectInputStream(bais);
                        Object deserializedObject = ois.readObject();

                    } catch (Exception e) {
                        idList.add(id);
                        count++;
                        e.printStackTrace();
                    }

                }
                System.out.println(JSONUtil.toJsonStr(idList));
                System.out.println(count);
            } catch (Exception e) {
                e.printStackTrace();
            }


            con.close();
        } catch (ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch (Exception e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        }// TODO: handle exception
    }

    protected static ObjectInputStream createObjectInputStream(InputStream is) throws IOException {
        return new ObjectInputStream(is) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                return ReflectUtil.loadClass(desc.getName());
            }
        };
    }
}
