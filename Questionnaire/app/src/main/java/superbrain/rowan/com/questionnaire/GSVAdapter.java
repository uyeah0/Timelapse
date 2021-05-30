package superbrain.rowan.com.questionnaire;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.Vector;

import superbrain.rowan.com.questionnaire.databinding.ItemMenuBinding;
// GSV = Get Stored Video
public class GSVAdapter extends RecyclerView.Adapter<GSVAdapter.GSVHolder> {

    private Vector<Menu> menus = new Vector<>();

    Context context;
    Activity activity;

    private boolean checkBoxVisible;



    public GSVAdapter(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @NonNull
    @Override
    public GSVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ItemMenuBinding binding = ItemMenuBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new GSVHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GSVHolder gsvHolder, int position) {
        ItemMenuBinding binding = gsvHolder.binding;
        Menu menu  = menus.get(position);
        String title = menu.getTitle();
        Bitmap img = menu.getImg();
        Uri uri = menu.getUri();
        binding.menuTitleImgView.setImageBitmap(img);
        if(checkBoxVisible) {
            binding.checkBox.setVisibility(View.VISIBLE);
            binding.menuTitleImgView.setOnClickListener(view -> {
                boolean isChecked = binding.checkBox.isChecked();
                if (isChecked) {
                    menu.setDelete(false);
                    binding.checkBox.setChecked(false);
                } else {
                    menu.setDelete(true);
                    binding.checkBox.setChecked(true);
                }
                Log.e("position = " + position, "isChecked = " + menu.isDelete());
                Log.e("get Items = ",String.valueOf(menus.get(position).isDelete()));
            });

        } else {
            binding.checkBox.setVisibility(View.GONE);
            binding.checkBox.setOnCheckedChangeListener(null);
            binding.menuTitleImgView.setOnClickListener(view -> {
                ShowVideoDialog dialog = new ShowVideoDialog(activity, uri, activity);
                dialog.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    void clear() {
        this.menus = null;
        this.menus = new Vector<>();
        notifyDataSetChanged();
    }

    public void setUp(Vector<Menu> menus) {
        this.menus = menus;
        notifyDataSetChanged();
    }

    public void setUp(Menu menu) {
        this.menus.add(menu);
        notifyDataSetChanged();
    }

    Vector<Uri> getFIleNames() {
        Vector<Uri> files = new Vector<>();
        for (int i = 0; i < menus.size(); i++) {
            Menu menu = menus.get(i);
            if(menu.isDelete()) files.add(menu.getUri());
        }
        return files;
    }

    void setCheckBoxVisibility(boolean visibility) {
        this.checkBoxVisible = visibility;
        Log.e("checkBoxVisible", String.valueOf(checkBoxVisible));
    }

    class GSVHolder extends RecyclerView.ViewHolder {
        ItemMenuBinding binding;

        GSVHolder(ItemMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}

