import Model.EquipInfo;
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
import java.util.ArrayList;

public class CbgEquip {
    static Gson gson;

    public CbgEquip() {
        this.gson = new Gson();
    }

    public int requestEquipItem(String urlItem, ArrayList<EquipInfo> equipInfoList) {
        String jsonString = run(urlItem);

        if (jsonString.isEmpty() == true) {
            System.out.println("Error JsonString is null!");
            return -1;
        }

        GsonEquipItem gsonEquipItem = gson.fromJson(jsonString, GsonEquipItem.class);
        if (gsonEquipItem == null) {
            System.out.println("Error gsonEquipItem is null!");
            return -1;
        }

        if (gsonEquipItem.equip_list == null) {
            System.out.println("Error gsonEquipItem.equip_list is null!");
            return -1;
        }

        if (gsonEquipItem.equip_list.size() <= 0) {
            System.out.println("equip_list size is 0");
            return -1;
        }

        for (GsonEquipItem.Equip equip : gsonEquipItem.equip_list) {
            EquipInfo equipInfo = new EquipInfo();
            equipInfo.game_ordersn = equip.game_ordersn;
            equipInfo.equip_serverid = equip.equip_serverid;
            requestEquipDetail(equipInfo);

            equipInfoList.add(equipInfo);
        }

        return 0;
    }

    /* http://xy2-android2.cbg.163.com/cbg-center/query.py?act=get_equip_detail&game_ordersn=112500FGF4&serverid=109 */
    public void requestEquipDetail(EquipInfo equipInfo) {
        String urlDetailPart1 = "http://xy2-android2.cbg.163.com/cbg-center/query.py?act=get_equip_detail&game_ordersn=";
        String urlDetailPart2 = "&serverid=";
        String urlDetail = urlDetailPart1 + equipInfo.game_ordersn + urlDetailPart2 + equipInfo.equip_serverid;

        String jsonString = run(urlDetail);
        if (jsonString.isEmpty() == true) {
            System.out.println("Error JsonString is null!");
            return;
        }

        GsonEquipDetail gsonEquipDetail = gson.fromJson(jsonString, GsonEquipDetail.class);
        if (gsonEquipDetail == null) {
            System.out.println("Error gsonEquipDetail is null!");
            return;
        }

        if (gsonEquipDetail.equip == null){
            System.out.println("Error gsonEquipDetail.equip is null!");
            return;
        }

        System.out.println("----");
        System.out.println("Equip WebUrl : " + gsonEquipDetail.equip.equip_detail_url);
        equipInfo.equip_detail_url = gsonEquipDetail.equip.equip_detail_url;

        parseEquipDetailUrl(equipInfo);

        return;
    }

    public EquipInfo parseEquipDetailUrl(EquipInfo equipInfo) {
        String fatherTag = "";

        try {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setCssEnabled(false);

            HtmlPage htmlPage = webClient.getPage(equipInfo.equip_detail_url);
            String html = htmlPage.asXml();
            Document document = Jsoup.parse(html);

            Element elementDiv = document.getElementById("equip_detail_description");
            if (elementDiv == null) {
                System.out.println("Error JsonString is null!");
            }

            Elements elements = elementDiv.getElementsByTag("span");
            equipInfo.equip_name = elements.get(1).text();

            for (Element element : elements) {
                String var = element.text();
                System.out.println(var);

                if (!fatherTag.isEmpty()) {
                    String[] varArrayTmp = var.split(" ");
                    String varTmp = varArrayTmp[1];

                    String[] varArray = varTmp.split("\\)");
                    switch (fatherTag) {
                        case "气血":
                            equipInfo.xueChuZhi = varArray[0];
                            fatherTag = "";
                            break;
                        case "攻击":
                            equipInfo.gongChuZhi = varArray[0];
                            fatherTag = "";
                            break;
                        case "法力":
                            equipInfo.faChuZhi = varArray[0];
                            fatherTag = "";
                            break;
                        case "速度":
                            equipInfo.suChuZhi = varArray[0];
                            fatherTag = "";
                            break;
                        case "禅定":
                            equipInfo.chanChuZhi = varArray[0];
                            fatherTag = "";
                            break;
                        default:
                            break;
                    }

                    continue;
                }

                if (var.startsWith("成长率")) {
                    String[] varArray = var.split(" ");
                    if (varArray.length >= 2) {
                        equipInfo.growthRate = varArray[1];
                    }
                } else if (var.startsWith("觉醒技")) {
                    String[] varArray = var.split("：");
                    if (varArray.length >= 2) {
                        equipInfo.jueXingJi = varArray[1];
                    }
                } else if (var.contains("饰品")) {
                    String[] varArray = var.split("：");
                    if (varArray.length >= 2) {
                        equipInfo.shiPin = varArray[1];
                    }
                } else if (var.startsWith("气血")) {
                    fatherTag = "气血";
                } else if (var.startsWith("攻击")) {
                    fatherTag = "攻击";
                } else if (var.startsWith("法力")) {
                    fatherTag = "法力";
                } else if (var.startsWith("速度")) {
                    fatherTag = "速度";
                } else if (var.startsWith("禅定")) {
                    fatherTag = "禅定";
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return equipInfo;
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
