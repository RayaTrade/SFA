package Model;

public class Serial {
    public int Id;
    public String HeaderID;
    public String ItemID;

    public String Box_number;
    public String ItemCode;
    public String Model;
    public String SerialNumber;
    public boolean isSelected;

    public Serial(String itemCode, String model, String serialNumber,String Box_number ,boolean isSelected) {
        ItemCode = itemCode;
        Model = model;
        SerialNumber = serialNumber;
        this.Box_number=Box_number;
        this.isSelected = isSelected;
    }


    public String getBox_number() {
        return Box_number;
    }

    public void setBox_number(String box_number) {
        Box_number = box_number;
    }
    public String getHeaderID() {
        return HeaderID;
    }

    public void setHeaderID(String headerID) {
        HeaderID = headerID;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public Serial() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
