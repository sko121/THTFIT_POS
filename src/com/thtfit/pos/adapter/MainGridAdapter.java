package com.thtfit.pos.adapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thtfit.pos.R;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.util.widget.FooterView;

/**
 * 适配器类ProductAdapter
 * 
 */
public class MainGridAdapter extends BaseAdapter {

	private List<Product> products = new ArrayList<Product>();
	private Context context;

	private boolean footerViewEnable = false;
	private OnClickListener listener;
	private FooterView footerView;
	
	private PosApplication application;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
	

	public MainGridAdapter(List<Product> pro_list, Context context) {
		super();
		if (pro_list != null) {
			this.products = pro_list;
		}
		this.context = context;
		application = (PosApplication) context.getApplicationContext();
//		notifyDataSetChanged();
		
	}

	public boolean isFooterViewEnable() {
		return footerViewEnable;
	}

	public void setFootreViewEnable(boolean enable) {
		footerViewEnable = enable;
	}

	public void setOnFooterViewClickListener(OnClickListener l) {
		listener = l;
	}

	@SuppressWarnings("unused")
	private int getDisplayWidth(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int screenWidth = displaymetrics.widthPixels;
		int screenHeight = displaymetrics.heightPixels;
		return screenWidth;
	}

	public FooterView getFooterView() {
		return footerView;
	}

	public void setFooterViewStatus(int status) {
		if (footerView != null) {
			footerView.setStatus(status);
		}
	}

	@Override
	public int getCount() {
		if (null != products) {
			return products.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if (footerViewEnable && position == products.size() - 1) {
			if (footerView == null) {
				footerView = new FooterView(parent.getContext());

				GridView.LayoutParams pl = new GridView.LayoutParams(
						getDisplayWidth((Activity) context),
						LayoutParams.WRAP_CONTENT);
				footerView.setLayoutParams(pl);
				footerView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (listener != null) {
							listener.onClick(v);
						}
					}
				});
			}
			setFooterViewStatus(FooterView.MORE);
			return footerView;
		}

		ViewHolder viewHolder;
		if (convertView == null
				|| (convertView != null && convertView == footerView)) {
			convertView = inflater.inflate(R.layout.griditeminfo, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.grid_itemName);
			viewHolder.price = (TextView) convertView
					.findViewById(R.id.grid_itemPrice);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.grid_itemImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText((CharSequence) products.get(position)
				.getName());
		viewHolder.price.setText((CharSequence) products.get(position)
						.getPrice());

	    DisplayImageOptions options;  
	    options = new DisplayImageOptions.Builder()  
	    .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
	    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中  
	    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
	    //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
	    //设置图片加入缓存前，对bitmap进行设置  
	    //.preProcessor(BitmapProcessor preProcessor)  
	    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
	    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
	    .build();//构建完成  
		
	    
	    if(products.get(position).getImagePath().contains("content:")){
	    	imageLoader.displayImage(products.get(position).getImagePath(), viewHolder.image, options);
	    }else{
	    	imageLoader.displayImage("http://"+products.get(position).getImagePath(), viewHolder.image, options);
	    }
		
//		ImageLoader.getInstance().displayImage("http://192.168.130.85:8080/SmartPos/images/products/21.jpg", viewHolder.image);
		
		return convertView;
	}

	
	public Bitmap imageProcessing(Uri myUri) {

		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, myUri, proj, null,
				null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		String img_path = cursor.getString(column_index);

		File file = new File(img_path);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		int be = options.outHeight / 200;
		if (be <= 0) {
			be = 10;
		}
		options.inSampleSize = be;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
				options);
		cursor.close();
		return bitmap;

	}

	public static Bitmap readBitmapAutoSize(String filePath, int outWidth,
			int outHeight) {
		// outWidth和outHeight是目标图片的最大宽度和高度，用作限制
				FileInputStream fs = null;
				BufferedInputStream bs = null;
				try {
					fs = new FileInputStream(filePath);
					bs = new BufferedInputStream(fs);
					BitmapFactory.Options options = setBitmapOption(filePath,
							outWidth, outHeight);
					Bitmap bitmap = BitmapFactory.decodeStream(bs, null,
							options);
					return bitmap;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						bs.close();
						fs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		return null;
	}

	private static BitmapFactory.Options setBitmapOption(String file,
			int width, int height) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		// 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
		// BitmapFactory.decodeFile(file, opt);

		int outWidth = opt.outWidth; // 获得图片的实际高和宽
		int outHeight = opt.outHeight;
		opt.inDither = false;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		// 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
		opt.inSampleSize = 4;
		// 计算缩放比
		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
			int sampleSize = (outWidth / width + outHeight / height) / 2;
			opt.inSampleSize = sampleSize;
		}
		opt.inJustDecodeBounds = false;
		return opt;
	}

	public String uriToPath(Uri myUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, myUri, proj, null,
				null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		String img_path = cursor.getString(column_index);
		cursor.close();
		return img_path;
	}

	// Bitmap转换成Drawable
	public Drawable bitmapToDrawable(Bitmap bitmap) {
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				context.getResources(), bitmap);
		Drawable drawable = (Drawable) bitmapDrawable;
		return drawable;
	}

	public class ViewHolder {
		public TextView title;
		public TextView price;
		public ImageView image;
	}

}
