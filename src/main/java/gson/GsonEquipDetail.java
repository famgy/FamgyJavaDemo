package gson;

import java.util.List;

public class GsonEquipDetail {
    public int equipid;
    public String price_desc;
    public Equip equip;

    public class Equip {
        public String equip_detail_url;
    }
}