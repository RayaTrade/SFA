package Model;

public class TransactionTender {
    private int id;
    private String HeaderId;
    private String TenderType;
    private String Submitter;
    private String Total;

    public TransactionTender(int id, String headerId, String tenderType, String submitter, String total) {
        this.id = id;
        HeaderId = headerId;
        TenderType = tenderType;
        Submitter = submitter;
        Total = total;
    }

    public TransactionTender() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
    }

    public String getTenderType() {
        return TenderType;
    }

    public void setTenderType(String tenderType) {
        TenderType = tenderType;
    }

    public String getSubmitter() {
        return Submitter;
    }

    public void setSubmitter(String submitter) {
        Submitter = submitter;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }
}
