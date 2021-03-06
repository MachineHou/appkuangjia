package com.example.slbappcomm.uploadimg2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slbappcomm.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shining on 2017/11/8.
 */

public class UploadPicActivity extends AppCompatActivity {

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private final int maxImgCount = 9;               //允许选择图片最大数

    protected ArrayList<ImageItem> mImageItems;
    private int mCurrentPosition;
    private ArrayList<? extends ImageItem> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadimg2);

//        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
//        ImageLoader.getInstance().init(config);     //UniversalImageLoader初始化
        //最好放到 Application oncreate执行
        initImagePicker();
        initWidget();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());    //设置图片加载器
        imagePicker.setShowCamera(true);                       //显示拍照按钮
        imagePicker.setMultiMode(true);                       //是否多选
        imagePicker.setCrop(true);                             //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                    //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(1000);                        //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(1000);                       //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                          //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                          //保存文件的高度。单位像素
    }

    private void initWidget() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        onclick();
    }

    private void onclick() {
        adapter.setOnItemClickListener(new ImagePickerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == IMAGE_ITEM_ADD) {
                    List<String> names = new ArrayList<>();
                    names.add("拍照");
                    names.add("相册");
                    showDialog(new SelectDialog.SelectDialogListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0: // 直接调起相机
                                    //打开选择,本次允许选择的数量
//                                        ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                    Intent intent = new Intent(UploadPicActivity.this, ImageGridActivity.class);
                                    intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                    startActivityForResult(intent, REQUEST_CODE_SELECT);
                                    break;
                                case 1:
                                    //打开选择,本次允许选择的数量
//                                        ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                    Intent intent1 = new Intent(UploadPicActivity.this, ImageGridActivity.class);
                                    /* 如果需要进入选择的时候显示已经选中的图片，
                                     * 详情请查看ImagePickerActivity
                                     * */
                                    intent1.putParcelableArrayListExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                                    startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                    break;
                                default:
                                    break;
                            }

                        }
                    }, names);
                } else {//打开预览
                    Intent intentPreview = new Intent(UploadPicActivity.this, ImagePreviewDelActivity.class);
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                }
            }
        });
        adapter.setOnItemClickListenerX(new ImagePickerAdapter.OnRecyclerViewItemClickListenerX() {
            @Override
            public void onItemClickX(View view, int position) {
                //删除操作
                mCurrentPosition = position;
//                ToastUtil.showToastCenter("delete!");
                showDeleteDialog();
            }
        });
    }

    private void showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 是否删除此张图片
     */
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("要删除这张照片吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //移除当前图片刷新界面
                mImageItems = adapter.getImages();
                mImageItems.remove(mCurrentPosition);
                selImageList.remove(mCurrentPosition);
                mImageItems.size();
                adapter.setImages(mImageItems);
                //
                images = adapter.getImages();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = data.getParcelableArrayListExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = data.getParcelableArrayListExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }
    }

}
