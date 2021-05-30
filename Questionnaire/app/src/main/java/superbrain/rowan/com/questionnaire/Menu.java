package superbrain.rowan.com.questionnaire;

import android.graphics.Bitmap;
import android.net.Uri;

public class Menu {

    private String title;
    private Bitmap img;
    private Uri uri;
    private boolean delete;

    public Menu(String title, Bitmap img, Uri uri) {
        this.title = title;
        this.img = img;
        this.uri = uri;
        this.delete = false;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isDelete() {
        return delete;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImg() {
        return img;
    }

    public Uri getUri() {
        return uri;
    }
}
