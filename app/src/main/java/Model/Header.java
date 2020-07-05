package Model;

public class Header {
    private int id;
    private String CountryCode;
    private String CustomerNumber;
    private String Submitter;
    private String Long;
    private String Lat;
    private String Comment;
    private String DeliveryMethod;
    private String TransactionType;
    private String Total;
    private String CustomerName;
    private String CreateDate;
    private String PromoID;
    private String PreOrder_Total_History;
    private String PreOrder_Total;

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getPromoID() {
        return PromoID;
    }

    public void setPromoID(String promoID) {
        PromoID = promoID;
    }

    public String getPreOrder_Total_History() {
        return PreOrder_Total_History;
    }

    public void setPreOrder_Total_History(String preOrder_Total_History) {
        PreOrder_Total_History = preOrder_Total_History;
    }

    public String getPreOrder_Total() {
        return PreOrder_Total;
    }

    public void setPreOrder_Total(String preOrder_Total) {
        PreOrder_Total = preOrder_Total;
    }





    public Header() {
    }

    public Header(int id, String countryCode, String customerNumber, String submitter, String aLong, String lat, String comment, String deliveryMethod, String transactionType,String total) {
        this.id = id;
        CountryCode = countryCode;
        CustomerNumber = customerNumber;
        Submitter = submitter;
        Long = aLong;
        Lat = lat;
        Comment = comment;
        DeliveryMethod = deliveryMethod;
        TransactionType = transactionType;
        Total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCustomerNumber() {
        return CustomerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        CustomerNumber = customerNumber;
    }

    public String getSubmitter() {
        return Submitter;
    }

    public void setSubmitter(String submitter) {
        Submitter = submitter;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getDeliveryMethod() {
        return DeliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        DeliveryMethod = deliveryMethod;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }
}
