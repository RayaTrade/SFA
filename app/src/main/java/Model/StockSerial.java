package Model;

public class StockSerial {
    private int Id;
    private String ItemCode;
    private String Model;
    private String SerialNumber;

    public StockSerial(int id, String itemCode, String model, String serialNumber) {
        Id = id;
        ItemCode = itemCode;
        Model = model;
        SerialNumber = serialNumber;
    }

    public StockSerial() {
    }

    public int getId() {
        return Id;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }
}
