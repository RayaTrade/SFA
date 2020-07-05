package Model;

public class PreOrderHistory {
    public String ID;
    public String CustomerName;
    public String CustomerNumber;
    public String Status;
    public String Total;

    public PreOrderHistory(String ID, String customerName, String customerNumber, String status, String total) {
        this.ID = ID;
        CustomerName = customerName;
        CustomerNumber = customerNumber;
        Status = status;
        Total = total;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerNumber() {
        return CustomerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        CustomerNumber = customerNumber;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }
}
