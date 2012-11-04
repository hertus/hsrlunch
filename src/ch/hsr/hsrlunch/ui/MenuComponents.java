package ch.hsr.hsrlunch.ui;

public class MenuComponents {

    static class Category {
        String mTitle;
        Category(String title) {
            mTitle = title;
        }
    }
    
    class Item {

        String mTitle;
        int mIconRes;

        Item(String title, int iconRes) {
            mTitle = title;
            mIconRes = iconRes;
        }
    }
    
}
