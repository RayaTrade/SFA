package Model;

public class Customer {
    public String Number;
    public String Name;
    public String Price_list;
    public String DueDateFrom;
    public String DueDateTo;
    public String ScheuleID;
    public String VisitID;

    public String CUSTOMER_CLASS;
    public String CREATION_DATE;
    public String CUSTOMER_CATEGORY;
    public String PRIMARY_SALESREP_NAME;
    public String PAYMENT_TERM;
    public String CITY;
    public String COUNTRY;
    public String CREDIT_LIMIT;
    public String OVER_DRAFT;
    public String REGION;
    public String AREA;

    public Customer(String number, String name, String price_list, String dueDateFrom, String dueDateTo, String scheuleID, String visitID) {
        Number = number;
        Name = name;
        Price_list = price_list;
        DueDateFrom = dueDateFrom;
        DueDateTo = dueDateTo;
        ScheuleID = scheuleID;
        VisitID = visitID;
    }

    public Customer() {
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice_list() {
        return Price_list;
    }

    public void setPrice_list(String price_list) {
        Price_list = price_list;
    }

    public String getDueDateFrom() {
        return DueDateFrom;
    }

    public void setDueDateFrom(String dueDateFrom) {
        DueDateFrom = dueDateFrom;
    }

    public String getScheuleID() {
        return ScheuleID;
    }

    public void setScheuleID(String scheuleID) {
        ScheuleID = scheuleID;
    }

    public String getVisitID() {
        return VisitID;
    }

    public void setVisitID(String visitID) {
        VisitID = visitID;
    }

    public String getDueDateTo() {
        return DueDateTo;
    }

    public void setDueDateTo(String dueDateTo) {
        DueDateTo = dueDateTo;
    }

    public String getCUSTOMER_CLASS() {
        return CUSTOMER_CLASS;
    }

    public void setCUSTOMER_CLASS(String CUSTOMER_CLASS) {
        this.CUSTOMER_CLASS = CUSTOMER_CLASS;
    }

    public String getCREATION_DATE() {
        return CREATION_DATE;
    }

    public void setCREATION_DATE(String CREATION_DATE) {
        this.CREATION_DATE = CREATION_DATE;
    }

    public String getCUSTOMER_CATEGORY() {
        return CUSTOMER_CATEGORY;
    }

    public void setCUSTOMER_CATEGORY(String CUSTOMER_CATEGORY) {
        this.CUSTOMER_CATEGORY = CUSTOMER_CATEGORY;
    }

    public String getPRIMARY_SALESREP_NAME() {
        return PRIMARY_SALESREP_NAME;
    }

    public void setPRIMARY_SALESREP_NAME(String PRIMARY_SALESREP_NAME) {
        this.PRIMARY_SALESREP_NAME = PRIMARY_SALESREP_NAME;
    }

    public String getPAYMENT_TERM() {
        return PAYMENT_TERM;
    }

    public void setPAYMENT_TERM(String PAYMENT_TERM) {
        this.PAYMENT_TERM = PAYMENT_TERM;
    }

    public String getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    public String getCOUNTRY() {
        return COUNTRY;
    }

    public void setCOUNTRY(String COUNTRY) {
        this.COUNTRY = COUNTRY;
    }

    public String getCREDIT_LIMIT() {
        return CREDIT_LIMIT;
    }

    public void setCREDIT_LIMIT(String CREDIT_LIMIT) {
        this.CREDIT_LIMIT = CREDIT_LIMIT;
    }

    public String getOVER_DRAFT() {
        return OVER_DRAFT;
    }

    public void setOVER_DRAFT(String OVER_DRAFT) {
        this.OVER_DRAFT = OVER_DRAFT;
    }

    public String getREGION() {
        return REGION;
    }

    public void setREGION(String REGION) {
        this.REGION = REGION;
    }

    public String getAREA() {
        return AREA;
    }

    public void setAREA(String AREA) {
        this.AREA = AREA;
    }

}
