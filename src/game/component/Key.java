package game.component;

public class Key {
    private boolean key_right;
    private boolean key_left;
    private boolean key_space;
    private boolean key_right_click;
    private boolean key_left_click;

    public boolean isKey_right() {
        return key_right;
    }

    public void setKey_right(boolean key_right) {
        this.key_right = key_right;
    }

    public boolean isKey_left() {
        return key_left;
    }

    public void setKey_left(boolean key_left) {
        this.key_left = key_left;
    }

    public boolean isKey_space() {
        return key_space;
    }

    public void setKey_space(boolean key_space) {
        this.key_space = key_space;
    }

    public boolean isKey_right_click() {
        return key_right_click;
    }

    public void setKey_right_click(boolean key_right_click) {
        this.key_right_click = key_right_click;
    }

    public boolean isKey_left_click() {
        return key_left_click;
    }

    public void setKey_left_click(boolean key_left_click) {
        this.key_left_click = key_left_click;
    }
        
}
