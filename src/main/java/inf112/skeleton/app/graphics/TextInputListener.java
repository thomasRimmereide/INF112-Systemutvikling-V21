package inf112.skeleton.app.graphics;

import com.badlogic.gdx.Input;

public class TextInputListener implements Input.TextInputListener {

    String text;

    @Override
    public void input(String s) {
        this.text = text;
    }

    @Override
    public void canceled() {

    }
}