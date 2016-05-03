package cc.nctu1210.view;

/**
 * Created by User on 2016/4/28.
 */
public class ParentCreateChildItem {
    public String id;
    public int spinner_select = 0;

    public ParentCreateChildItem(String id) {
        this.id = id;
    }

    public ParentCreateChildItem(String id, int spinner_select) {
        this.id = id;
        this.spinner_select = spinner_select;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setSpinnerSelect(int select) {
        this.spinner_select = select;
    }

    public String getID() {
        return this.id;
    }
    public int getSpinnerSelect() {
        return this.spinner_select;
    }
}
