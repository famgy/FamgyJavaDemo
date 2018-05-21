package gson;

import java.util.List;

public class GsonEquipItem {
    public int status;
    public String order_field;
    public int num_per_page;
    public List<Equip> equip_list;

    public static class Equip {
        public String game_ordersn;
        public int equip_serverid;
    }
}
