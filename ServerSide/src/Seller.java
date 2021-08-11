public class Seller {

    private int sellerID;
    private String sellerName;

    public Seller(int sellerID, String sellerName) {
        this.sellerID = sellerID;
        this.sellerName = sellerName;
    }

    public int getSellerID() {
        return sellerID;
    }

    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

}
