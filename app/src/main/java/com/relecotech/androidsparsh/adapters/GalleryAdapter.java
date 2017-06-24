package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.activities.Gallery;
import com.relecotech.androidsparsh.controllers.GalleryListData;

import java.util.List;

/**
 * Created by ajinkya on 10/21/2015.
 */
public class GalleryAdapter extends BaseAdapter {

    protected List<GalleryListData> galleryList;
    Context context;
    String user = Gallery.loggedInUserForGalleryListAdapter;
    LayoutInflater inflater;
    ViewHolder holder = new ViewHolder();

    public GalleryAdapter(Context context, List<GalleryListData> galleryList) {
        this.galleryList = galleryList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return galleryList.size();
    }

    public GalleryListData getItem(int position) {
        return galleryList.get(position);
    }

    public long getItemId(int position) {
        return galleryList.get(position).getNullId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder holder = new ViewHolder();

        if (convertView == null) {

            //holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.gallery_list_item, parent, false);

//            holder.moreIcon = (ImageView) convertView.findViewById(R.id.moreIconId);
            holder.galleryImg = (ImageView) convertView.findViewById(R.id.gallery_ImageView);
            holder.galleryDate = (TextView) convertView.findViewById(R.id.gallery_txt_Date);
            holder.galleryTitle = (TextView) convertView.findViewById(R.id.gallery_txt_Title);
            holder.galleryTag = (TextView) convertView.findViewById(R.id.gallery_tag_field);
            holder.galleryCount = (TextView) convertView.findViewById(R.id.gallery_img_count);
//            holder.galleryDesc = (TextView) convertView.findViewById(R.id.descTv);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GalleryListData galleryListData = galleryList.get(position);
//        holder.galleryImg.setImageResource(galleryListData.getImage());
        holder.galleryDate.setText(galleryListData.getPostDate());
        holder.galleryTitle.setText(galleryListData.getTitle());
        holder.galleryCount.setText(String.valueOf(galleryListData.getImageCount()));
        holder.galleryTag.setText(galleryListData.getTag());
        System.out.println("galleryListData.getImage() " + galleryListData.getUrl());
        Glide.clear(holder.galleryImg);
        Glide.with(context).load(galleryListData.getUrl())
                .thumbnail(0.5f)
                .crossFade()
                .override(100,100)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.galleryImg);


//        holder.moreIcon.setTag(holder);
//        convertView.setTag(holder);
//
//        holder.moreIcon.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                View p = (View) v.getParent();
//                ViewHolder holder1 = (ViewHolder) v.getTag();
//                System.out.println(" INSIDE GET VIEW MORE ICON CLICK");
//                holder.galleryDesc.setText(galleryListData.getImgDesc());
//            }
//        });

        return convertView;
    }

    private class ViewHolder {
        TextView galleryDate;
        TextView galleryTitle;
        TextView galleryTag;
        TextView galleryCount;
        TextView galleryDesc;
        ImageView galleryImg;
        ImageView moreIcon;
    }

}
