package Model;

public class SyncBack {
    private int id;
    private String Type;
    private String Id_Offline;
    private String Id_Online;
    private String ItemSaved;
    private String CustNumber;

    public SyncBack() {
    }

    public SyncBack(int id, String type, String id_Offline, String id_Online, String itemSaved, String custNumber) {
        this.id = id;
        Type = type;
        Id_Offline = id_Offline;
        Id_Online = id_Online;
        ItemSaved = itemSaved;
        CustNumber = custNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getId_Offline() {
        return Id_Offline;
    }

    public void setId_Offline(String id_Offline) {
        Id_Offline = id_Offline;
    }

    public String getId_Online() {
        return Id_Online;
    }

    public void setId_Online(String id_Online) {
        Id_Online = id_Online;
    }

    public String getItemSaved() {
        return ItemSaved;
    }

    public void setItemSaved(String itemSaved) {
        ItemSaved = itemSaved;
    }

    public String getCustNumber() {
        return CustNumber;
    }

    public void setCustNumber(String custNumber) {
        CustNumber = custNumber;
    }
}
