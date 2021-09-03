package com.daqsoft.module_workbench.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daqsoft.module_workbench.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    public static final String TAG = "PictureSelector";
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 9;

    private boolean addVisible = true;

    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public GridImageAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mIvDel;
        ImageView mIvVideo;

        public ViewHolder(View view) {
            super(view);
            mImg = view.findViewById(R.id.fiv);
            mIvDel = view.findViewById(R.id.iv_del);
            mIvVideo = view.findViewById(R.id.video);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            if (addVisible){
                return list.size() + 1;
            }else {
                return list.size();
            }
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.gv_filter_image,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private boolean isShowAddItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            if(addVisible) {
                viewHolder.mImg.setImageResource(R.mipmap.add_tzgg_add);
                viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnAddPicClickListener.onAddPicClick();
                    }
                });
                viewHolder.mIvDel.setVisibility(View.GONE);
            }else {
                viewHolder.mIvDel.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.mIvDel.setVisibility(View.VISIBLE);
            viewHolder.mIvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = viewHolder.getBindingAdapterPosition();
                    if (index != RecyclerView.NO_POSITION && list.size() > index) {
                        list.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, list.size());
                    }
                }
            });
            LocalMedia media = list.get(position);

            if (media.getMimeType().equals(PictureMimeType.MIME_TYPE_VIDEO)){
                viewHolder.mIvVideo.setVisibility(View.VISIBLE);
            }

            if (media == null || TextUtils.isEmpty(media.getPath())) {
                return;
            }
            int chooseModel = media.getChooseModel();
            String path;
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图
                path = media.getPath();
            }

            Log.i(TAG, "原图地址::" + media.getPath());

            if (media.isCut()) {
                Log.i(TAG, "裁剪地址::" + media.getCutPath());
            }
            if (media.isCompressed()) {
                Log.i(TAG, "压缩地址::" + media.getCompressPath());
                Log.i(TAG, "压缩后文件大小::" + new File(media.getCompressPath()).length() / 1024 + "k");
            }
            if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
                Log.i(TAG, "Android Q特有地址::" + media.getAndroidQToPath());
            }
            if (media.isOriginal()) {
                Log.i(TAG, "是否开启原图功能::" + true);
                Log.i(TAG, "开启原图功能后地址::" + media.getOriginalPath());
            }

//            long duration = media.getDuration();
//            viewHolder.tvDuration.setVisibility(PictureMimeType.eqVideo(media.getMimeType())
//                    ? View.VISIBLE : View.GONE);
//            if (chooseModel == PictureMimeType.ofAudio()) {
//                viewHolder.tvDuration.setVisibility(View.VISIBLE);
//                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
//                        (R.drawable.picture_icon_audio, 0, 0, 0);
//
//            } else {
//                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
//                        (R.drawable.picture_icon_video, 0, 0, 0);
//            }
//            viewHolder.tvDuration.setText(DateUtils.formatDurationTime(duration));
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(R.drawable.picture_audio_placeholder);
            } else {
                Glide.with(viewHolder.itemView.getContext())
                        .load(path.startsWith("content://") && !media.isCut() && !media.isCompressed() ? Uri.parse(path) : path)
                        .apply(new RequestOptions()
                                .placeholder(R.color.white_ffffff)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop())
                        .into(viewHolder.mImg);
            }
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = viewHolder.getBindingAdapterPosition();
                        mItemClickListener.onItemClick(adapterPosition, v);
                    }
                });
            }
        }

    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    public void setAddVisible(boolean visible){
        this.addVisible = visible;
    }
}
