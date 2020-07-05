package Model;

public class Permission {
    private String MenuID;
    private String MenuName;

    public Permission(String menuID, String menuName) {
        MenuID = menuID;
        MenuName = menuName;
    }

    public Permission() {
    }

    public String getMenuID() {
        return MenuID;
    }

    public void setMenuID(String menuID) {
        MenuID = menuID;
    }

    public String getMenuName() {
        return MenuName;
    }

    public void setMenuName(String menuName) {
        MenuName = menuName;
    }
}
