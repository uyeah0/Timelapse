package superbrain.rowan.com.questionnaire;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import superbrain.rowan.com.questionnaire.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int CALL_VIDEO_CAM = 0;

    private static final int PERMISSIONS = 100;

    private static final String[] GROUPS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
        //mBinding.callCameraBtn.setOnClickListener(view -> callVideoCam());
    }

    private void callVideoCam() {
        Intent callVideoCamIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        try {
            PackageManager packageManager = getPackageManager();

            final ResolveInfo resolveInfo = packageManager.resolveActivity(callVideoCamIntent, CALL_VIDEO_CAM);

            Intent coreIntent = new Intent();
            String packageName = resolveInfo.activityInfo.packageName;
            String name = resolveInfo.activityInfo.name;
            coreIntent.setComponent(new ComponentName(packageName, name));
            coreIntent.setAction(Intent.ACTION_MAIN);
            coreIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(coreIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
