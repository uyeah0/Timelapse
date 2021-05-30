package superbrain.rowan.com.questionnaire;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import superbrain.rowan.com.questionnaire.databinding.ActivityRecordingBinding;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RecordingActivity";
    ActivityRecordingBinding mBinding;
    private static final int RECORDING = R.layout.activity_recording;


    private static final int PERMISSIONS = 100;
    GSVAdapter mAdapter;
    boolean mIsCheck;
    Snackbar mSnackbar;
    int mLength;

    private static final String[] GROUPS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, RECORDING);

    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        mBinding.menuBar.setTitle("응원 영상");
        mBinding.recordingImgView.setOnClickListener(this);
        mBinding.uploadImgView.setOnClickListener(this);
        mBinding.deleteImgView.setOnClickListener(this);
        mBinding.videoView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new GSVAdapter(this);
        mBinding.videoView.setAdapter(mAdapter);
        mBinding.videoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mLength = mBinding.videoView.getWidth() / 3 - 24;
                getVideo(mLength);
                mBinding.videoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mSnackbar = null;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.uploadImgView:
                break;
            case R.id.recordingImgView:
                startActivity(new Intent(this, VideoActivity.class));
                break;
            case R.id.deleteImgView:
                toggleChkBox();
                setCustomSnackBar();
                break;
            case R.id.actionTxtView:
                Vector<Uri> fileNames = mAdapter.getFIleNames();
                if (fileNames.size() != 0) {
                    for (int i = 0; i < fileNames.size(); i++) {
                        File file = new File(fileNames.get(i).getPath());
                        if (file.exists()) {
                            boolean delete = file.delete();
                            if (delete) {
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                            mAdapter.clear();
                                            getVideo(mLength);
                                            Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        , 500);
                            } else {
                                Toast.makeText(this, "삭제 not 굳", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                toggleChkBox();
                mSnackbar.dismiss();
                break;
            case R.id.cancelTxtView:
                toggleChkBox();
                mSnackbar.dismiss();
                break;
        }
    }

    private void toggleChkBox() {
        mIsCheck = !mIsCheck;
        mAdapter.setCheckBoxVisibility(mIsCheck);
        mAdapter.notifyDataSetChanged();
    }

    private void setCustomSnackBar() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        } else {
            mSnackbar = Snackbar.make(mBinding.getRoot(), "응원 영상 삭제", Snackbar.LENGTH_INDEFINITE);

            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) mSnackbar.getView();
            View snackBarView = getLayoutInflater().inflate(R.layout.snackbar_delete_video, null);
            TextView actionTxtView = snackBarView.findViewById(R.id.actionTxtView);
            TextView cancelTxtView = snackBarView.findViewById(R.id.cancelTxtView);
            actionTxtView.setOnClickListener(this);
            cancelTxtView.setOnClickListener(this);

            layout.addView(snackBarView, 0);
            mSnackbar.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "granted");
                } else {
                    finish();
                }
        }
    }


    private void checkPermissions() {
        int result;
        ArrayList<String> permissionNeeded = new ArrayList<>();
        for (String p : GROUPS) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(p);
            }
        }
        if (!permissionNeeded.isEmpty()) {
            String[] ps = new String[permissionNeeded.size()];
            for (int i = 0; i < ps.length; i++) {
                ps[i] = permissionNeeded.get(i);
            }
            ActivityCompat.requestPermissions(this, ps, PERMISSIONS);

        }
    }


    private void getVideo(int length) {
        String[] proj = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA
        };
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        Cursor cursor = getContentResolver().query(uri, proj, null, null, orderBy + " DESC");

        //Vector<Menu> menus = new Vector<>();
        assert cursor != null;
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);

            // 썸네일 크기 변경할 때.
            //Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, height);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, length, length);
            String data = cursor.getString(2);
            Log.e("data", "path = " + data);
            //menus.add(new Menu(title, bitmap, Uri.parse(data)));
            mAdapter.setUp(new Menu(title, thumbnail, Uri.parse(data)));
        }

        cursor.close();
        //return menus;
    }

}
