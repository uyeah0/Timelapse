package superbrain.rowan.com.questionnaire;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import superbrain.rowan.com.questionnaire.databinding.DialogShowvideoBinding;

public class ShowVideoDialog extends Dialog {

    DialogShowvideoBinding binding;

    private Uri videoUri;

    private Activity activity;
    Context context;

    ShowVideoDialog(Context context, Uri videoUri, Activity activity) {
        super(context);
        this.videoUri = videoUri;
        this.activity = activity;
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_showvideo, null, false);
        setContentView(binding.getRoot());
        String uriStr = this.videoUri.toString();
        uriStr = uriStr.substring(16, uriStr.length() - 12);
        binding.videoView.setVideoURI(this.videoUri);
        binding.videoView.setOnPreparedListener(mp -> binding.videoView.start());
        binding.videoView.setOnCompletionListener(mp -> cancel());
    }
}
