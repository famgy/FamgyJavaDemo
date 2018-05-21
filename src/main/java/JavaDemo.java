import java.sql.*;
import java.util.Properties;

public class JavaDemo {

    public static void main(String[] args) {
        try {
            /* Class.forName(xxx) loads the jdbc classes and creates a drivermanager class factory */
            Class.forName("org.mariadb.jdbc.Driver");

            /* Properties for user and password */
            Properties p = new Properties();
            p.put("user", "famgy");
            p.put("password", "abcd");

            /* Now try to connect */
            System.out.println("Connecting...");
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/cbg", p);
            System.out.println("Connect ok!");

            /* Query info*/
            System.out.println("Create Statement");
            Statement statement = conn.createStatement();
            System.out.println("statement ok!");

//            System.out.println("Execute Update");
//            statement.executeUpdate("INSERT INTO cbg_equip (equip_name, game_ordersn, server_id) VALUE ('北冥龙军', 'ssppppp', 128)");
//            System.out.println("Update ok");

            System.out.println("Execute Query");
            ResultSet resultSet = statement.executeQuery("SELECT id, equip_name, game_ordersn, server_id FROM cbg_equip");
            System.out.println("Query ok");

            System.out.println("Show Result");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String equip_name = resultSet.getString("equip_name");
                String game_ordersn = resultSet.getString("game_ordersn");
                int server_id = resultSet.getInt("server_id");

                System.out.print("Id : " + id);
                System.out.print(", 装备名称 ：" + equip_name);
                System.out.print(", 商品sn : " + game_ordersn);
                System.out.println(", 服务器id : " + server_id);
            }


            /* Close */
            System.out.println("Close ResultSet");
            resultSet.close();
            System.out.println("Close Statement");
            statement.close();
            System.out.println("Close Connection");
            conn.close();


            CbgEquip cbgEquip = new CbgEquip();
            cbgEquip.requestEquipItem("http://xy2-android2.cbg.163.com/cbg-center/query.py?page=1&orderby=selling_time+DESC&equip_type=103920&platform=android&app_version=2.0.8&need_check_license=1&sdk_version=25&device_name=One+S&app_version_code=2080&os_version=7.1.2&package_name=com.netease.xy2cbg&os_name=ville");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("\n====================");
        System.out.println("Goodbye!");
    }
}
