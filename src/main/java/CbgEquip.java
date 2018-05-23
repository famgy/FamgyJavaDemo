import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            HtmlPage htmlPage = webClient.getPage(equip_detail_url);
            String html = htmlPage.asXml();
            Document document = Jsoup.parse(html);

            Element elementDiv = document.getElementById("equip_detail_description");
            Elements elements = elementDiv.getElementsByTag("span");
            for (Element element : elements) {
                String var = element.text();
                System.out.println(var);
            }

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
