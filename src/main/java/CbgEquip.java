import com.google.gson.Gson;
import gson.GsonEquipDetail;
import gson.GsonEquipItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CbgEquip {
    static Gson gson;

    public CbgEquip() {
        this.gson = new Gson();
    }

    public void requestEquipItem(String urlItem) {
        String jsonString = run(urlItem);
        if (jsonString.isEmpty() == true) {
            System.out.println("Error JsonString is null!");
            return;
        }

        GsonEquipItem gsonEquipItem = gson.fromJson(jsonString, GsonEquipItem.class);

        for (GsonEquipItem.Equip equip : gsonEquipItem.equip_list) {
            requestEquipDetail(equip.game_ordersn, equip.equip_serverid);
        }

        return;
    }

    /* http://xy2-android2.cbg.163.com/cbg-center/query.py?act=get_equip_detail&game_ordersn=112500FGF4&serverid=109 */
    public void requestEquipDetail(String game_ordersn, int equip_serverid) {
        String urlDetailPart1 = "http://xy2-android2.cbg.163.com/cbg-center/query.py?act=get_equip_detail&game_ordersn=";
        String urlDetailPart2 = "&serverid=";
        String urlDetail = urlDetailPart1 + game_ordersn + urlDetailPart2 + equip_serverid;

        String jsonString = run(urlDetail);
        if (jsonString.isEmpty() == true) {
            System.out.println("Error JsonString is null!");
            return;
        }

        GsonEquipDetail gsonEquipDetail = gson.fromJson(jsonString, GsonEquipDetail.class);

        System.out.println("----");
        System.out.println("Equip WebUrl : " + gsonEquipDetail.equip.equip_detail_url);

        parseEquipDetailUrl(gsonEquipDetail.equip.equip_detail_url);
    }

    public void parseEquipDetailUrl(String equip_detail_url) {
        try {
            Document document = Jsoup.connect(equip_detail_url).get();
            System.out.println("Url Title : " + document.title());

            Element elementDiv = document.getElementById("equip_desc_value");
            String var = elementDiv.text();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String run(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
