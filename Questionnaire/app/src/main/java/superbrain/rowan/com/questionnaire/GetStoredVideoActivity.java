package superbrain.rowan.com.questionnaire;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import java.util.Vector;

import superbrain.rowan.com.questionnaire.databinding.ActivityGetstoredvideoBinding;

public class GetStoredVideoActivity extends AppCompatActivity {

    ActivityGetstoredvideoBinding binding;
    private static final int GSV = R.layout.activity_getstoredvideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, GSV);

        binding.menuListView.setLayoutManager(new GridLayoutManager(this, 2));
        GSVAdapter adapter = new GSVAdapter(this);
        binding.menuListView.setAdapter(adapter);
        getVideo();
        adapter.setUp(getVideo());
        binding.recordingFab.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }


    private Vector<Menu> getVideo() {
        String[] proj = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA
        };
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        Cursor cursor = getContentResolver().query(uri, proj, null, null, orderBy + " DESC");

        Vector<Menu> menus = new Vector<>();
        assert cursor != null;
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);

            // 썸네일 크기 변경할 때.
            //Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, height);
            String data = cursor.getString(2);
            menus.add(new Menu(title, bitmap, Uri.parse(data)));
        }

        cursor.close();
        return menus;
    }

}
