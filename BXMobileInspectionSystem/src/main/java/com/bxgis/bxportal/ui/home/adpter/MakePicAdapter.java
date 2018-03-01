package com.bxgis.bxportal.ui.home.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.myInterface.IMakePic;
import com.bxgis.bxportal.myInterface.OnImageUploadListener;
import com.bxgis.bxportal.utils.ImageLoader;
import com.bxgis.bxportal.widget.Constatnt;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiaozhu on 2017/11/20.
 * 选择图片列表器
 */

public class MakePicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext = null;

    private LayoutInflater mInflater = null;

    List<String> mPhotoInfoList = new ArrayList<>();

    private IMakePic mMakePic = null;

    /**
     * 显示还是编辑
     */
    private int type = Constatnt.SHOW;

    /**
     * 图片选取的最大数量，当达到最大数量是，不显示增加按钮
     */
    private int maxCount = Constatnt.MAX_COUNT;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public MakePicAdapter(Context context, List<String> photoInfoList, IMakePic makePic) {
        this(context, photoInfoList, makePic, Constatnt.SHOW);
    }

    public MakePicAdapter(Context context, List<String> photoInfoList, IMakePic makePic, int type) {
        mContext = context;
        mPhotoInfoList = photoInfoList;
        mMakePic = makePic;
        this.type = type;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * 判断是否需要显示一个增加按钮，当达到最大数量是不显示增加按钮
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (getType() == Constatnt.SHOW) {
            return mPhotoInfoList.size();
        } else {
            int num = mPhotoInfoList.size();
            return num == getMaxCount() ? getMaxCount() : ++num;
        }
    }

    /**
     * 判断选择哪种布局，一种是显示布局，一种是增加布局（有删除按钮）
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (getType() == Constatnt.SHOW) {
            return Constatnt.ITEM_CONTENT;
        } else {
            if (position == mPhotoInfoList.size()) {
                return Constatnt.ITEM_ADD;
            }
            return Constatnt.ITEM_CONTENT;
        }
    }

    /**
     * 返回列表数据
     *
     * @return
     */
    public List<String> getDataList() {
        return this.mPhotoInfoList;
    }

    /**
     * 更新数据集
     *
     * @param dataList
     */
    public void setDataList(List<String> dataList) {
        mPhotoInfoList = dataList;
        notifyDataSetChanged();
    }

    /**
     * 增加一个
     *
     * @param
     */
    public void addItem(String imagepath) {
        if (this.mPhotoInfoList.size() < getMaxCount()) {
            this.mPhotoInfoList.add(imagepath);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("返回的tiewType = " + viewType);
        if (getType() == Constatnt.SHOW) {
            return new ContentViewHolder(mInflater.inflate(R.layout.item_adapter_photo_list, parent, false));
        } else {
            if (viewType == Constatnt.ITEM_ADD) {
                return new AddViewHolder(mInflater.inflate(R.layout.item_photo_add, parent, false));
            } else {
                return new ContentViewHolder(mInflater.inflate(R.layout.item_adapter_photo_list, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        //显示图集
        if (getType() == Constatnt.SHOW) {
            //显示图片数据
            if (holder instanceof ContentViewHolder) {
                String path = getDataList().get(position);
                //加载图片
                ImageLoader.showImage(mContext, path, ((ContentViewHolder) holder).mIvPhoto);

                //删除按钮隐藏
                ((ContentViewHolder) holder).mIvDelete.setVisibility(View.GONE);

            }
        } else {
            if (holder instanceof ContentViewHolder) {
                String path = getDataList().get(position);
                //加载图片
                ImageLoader.showImage(mContext, path, ((ContentViewHolder) holder).mIvPhoto);

                //图片右上角显示删除按钮
                ((ContentViewHolder) holder).mIvDelete.setVisibility(View.VISIBLE);
                final String finalPath = path;

                //删除按钮操作
                ((ContentViewHolder) holder).mIvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDataList().remove(holder.getLayoutPosition());
                        notifyDataSetChanged();
                        mMakePic.delPic(finalPath);

                    }
                });

            } else if (holder instanceof AddViewHolder) {
                ((AddViewHolder) holder).mImageAdd.setVisibility(View.VISIBLE);
                //显示添加按钮
                ((AddViewHolder) holder).mImageAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMakePic.setPicUploadListener(new OnImageUploadListener() {
                            @Override
                            public void onImageUpload(String photoInfo) {
                                //成功回调
                                addItem(photoInfo);
                            }

                            @Override
                            public void onImageUploadList(List<String> photoInfos) {

                                //剩余可传图片数
                                int left = getMaxCount() - getDataList().size();

                                //如果可添加的图片数量大于一次性选择的图片数量
                                if (left >= photoInfos.size()) {
                                    left = photoInfos.size();
                                }

                                //依次添加
                                for (int i = 0; i < left; i++) {
                                    addItem(photoInfos.get(i));
                                }
                            }
                        });
                        mMakePic.takePic(position);
                    }

                });
            }

        }
    }

    public static class AddViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageAdd;

        public AddViewHolder(View footView) {
            super(footView);
            mImageAdd = (ImageView) itemView.findViewById(R.id.imageviewadd);
        }
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        ImageView mIvPhoto;

        ImageView mIvDelete;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            mIvDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }
}
