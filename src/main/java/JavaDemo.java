import Model.EquipInfo;

import java.sql.*;
import java.util.ArrayList;
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

            System.out.println("Create Statement");
            Statement statement = conn.createStatement();
            System.out.println("statement ok!");


            /* Update database */
            CbgEquip cbgEquip = new CbgEquip();

            ArrayList<EquipInfo> equipInfoList = new ArrayList<>();

            for (int page = 1; page < 1000; page++) {
                String urlString = "http://xy2-android2.cbg.163.com/cbg-center/query.py?page=" +
                        page +
                        "&orderby=selling_time+DESC&equip_type=103920&platform=android&app_version=2.0.8&need_check_license=1&sdk_version=25&device_name=One+S&app_version_code=2080&os_version=7.1.2&package_name=com.netease.xy2cbg&os_name=ville";

                if (cbgEquip.requestEquipItem(urlString, equipInfoList) < 0) {
                    break;
                }
            }

            for (EquipInfo equipInfo : equipInfoList) {
                System.out.println("\n===Execute Update===");
                System.out.println(equipInfo.equip_name);
                System.out.println(equipInfo.growthRate);
                System.out.println(equipInfo.xueChuZhi);
                System.out.println(equipInfo.gongChuZhi);
                System.out.println(equipInfo.faChuZhi);
                System.out.println(equipInfo.suChuZhi);
                System.out.println(equipInfo.chanChuZhi);
                System.out.println(equipInfo.equip_detail_url);

                String valueString = "('" + equipInfo.game_ordersn + "','"
                        + equipInfo.equip_name + "',"
                        + equipInfo.equip_serverid + ",'"
                        + equipInfo.growthRate + "','"
                        + equipInfo.xueChuZhi + "','"
                        + equipInfo.gongChuZhi + "','"
                        + equipInfo.faChuZhi + "','"
                        + equipInfo.suChuZhi + "','"
                        + equipInfo.chanChuZhi + "','"
                        + equipInfo.equip_detail_url +"')";

                String exeString = "INSERT INTO cbg_equip (" +
                        "game_ordersn, " +
                        "equip_name, " +
                        "server_id, " +
                        "grouth_rate, " +
                        "born_xue, " +
                        "born_gong, " +
                        "born_fa, " +
                        "born_su, " +
                        "born_chan, " +
                        "detail_url) VALUE " + valueString +
                        " ON DUPLICATE KEY UPDATE " +
                        "game_ordersn=" + "'" + equipInfo.game_ordersn + "'," +
                        "equip_name=" + "'" + equipInfo.equip_name + "'," +
                        "server_id=" + equipInfo.equip_serverid + "," +
                        "grouth_rate=" + "'" + equipInfo.growthRate + "'," +
                        "born_xue=" + "'" + equipInfo.xueChuZhi + "'," +
                        "born_gong=" + "'" + equipInfo.gongChuZhi + "'," +

                        "born_fa=" + "'" + equipInfo.faChuZhi + "'," +
                        "born_su=" + "'" + equipInfo.suChuZhi + "'," +
                        "born_chan=" + "'" + equipInfo.chanChuZhi + "'," +
                        "detail_url=" + "'" + equipInfo.equip_detail_url +"'"
                        ;

                System.out.println("===exe sql=== : " + exeString);
                statement.executeUpdate(exeString);
            }

            /* Query info*/
            System.out.println("\nExecute Query");
            ResultSet resultSet = statement.executeQuery("SELECT game_ordersn, equip_name, server_id FROM cbg_equip");
            System.out.println("Query ok\n");

            System.out.println("\n\nShow Result");
            while (resultSet.next()) {
                String game_ordersn = resultSet.getString("game_ordersn");
                String equip_name = resultSet.getString("equip_name");
                int server_id = resultSet.getInt("server_id");

                System.out.print(", 商品sn : " + game_ordersn);
                System.out.print(", 装备名称 ：" + equip_name);
                System.out.println(", 服务器id : " + server_id);
            }

            /* Close */
            System.out.println("Close ResultSet");
            resultSet.close();
            System.out.println("Close Statement");
            statement.close();
            System.out.println("Close Connection");
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("\n====================");
        System.out.println("Goodbye!");
    }
}
