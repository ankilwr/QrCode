package com.shihang.zxing.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.shihang.zxing.R;
import java.util.Hashtable;


/**
 * @author J!nl!n
 * @date 2015年1月14日 下午2:21:11
 * @type MainActivity
 * @todo ZXing生成和扫描二维码
 */
public class MainActivity extends Activity implements OnClickListener {
	private EditText et;
	private ImageView iv;
	private Button btn1, btn2, btn3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	/**
	 *
	 */
	private void initView() {
		et = (EditText) findViewById(R.id.et);
		btn1 = (Button) findViewById(R.id.btn1);
		btn1.setOnClickListener(this);
		btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(this);
		btn3 = (Button) findViewById(R.id.btn3);
		btn3.setOnClickListener(this);
		iv = (ImageView) findViewById(R.id.iv);
	}

	/**
	 * 用字符串生成二维码
	 * @param str
	 * @author J!nl!n
	 * @return
	 * @throws WriterException
	 */
	public Bitmap create2DCode(String str) throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 480, 480, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
			case R.id.btn1:
				String content = et.getText().toString().trim();
				try {
					if (!TextUtils.isEmpty(content)) {
						Bitmap bitmap = create2DCode(content);
						iv.setImageBitmap(bitmap);
					} else {
						Toast.makeText(MainActivity.this, "请输入要生成的字符串", Toast.LENGTH_SHORT).show();
					}
				} catch (WriterException e) {
					e.printStackTrace();
				}
				break;
			case R.id.btn2:
				String content2 = et.getText().toString().trim();
				try {
					if (!TextUtils.isEmpty(content2)) {
						Bitmap bitmap = creatBarcode(this, content2,720, 300, true);
						iv.setImageBitmap(bitmap);
					} else {
						Toast.makeText(MainActivity.this, "请输入要生成的字符串", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.btn3:
				startActivity(new Intent(MainActivity.this, CaptureActivity.class));
				break;
			default:
				break;
		}
	}


	/**
	 * 生成条形码
	 * @param context
	 * @param contents  需要生成的内容
	 * @param desiredWidth 生成条形码的宽带
	 * @param desiredHeight 生成条形码的高度
	 * @param displayCode 是否在条形码下方显示内容
	 * @return
	 */
	public Bitmap creatBarcode(Context context, String contents, int desiredWidth, int desiredHeight, boolean displayCode) {
		Bitmap ruseltBitmap = null;
		//图片两端所保留的空白的宽度
		int marginW = 20;
		//条形码的编码类型
		BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;
		if (displayCode) {
			Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
			Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth + 2 * marginW, desiredHeight, context);
			ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0, desiredHeight));
		} else {
			ruseltBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
		}
		return ruseltBitmap;
	}

	/**
	 * 生成条形码的Bitmap
	 * @param contents 需要生成的内容
	 * @param format 编码格式
	 * @param desiredWidth
	 * @param desiredHeight
	 * @throws WriterException
	 */
	protected Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) {
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;

		BitMatrix result = null;
		try {
			result = new MultiFormatWriter().encode(contents, format, desiredWidth, desiredHeight, null);
		} catch (WriterException e) {
			e.printStackTrace();
		}

		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// All are 0, or black, by default
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/** 生成条形码编码的Bitmap */
	protected Bitmap creatCodeBitmap(String contents, int width, int height, Context context) {
		TextView tv = new TextView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(layoutParams);
		tv.setText(contents);
		tv.setHeight(height);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setWidth(width);
		tv.setDrawingCacheEnabled(true);
		tv.setTextColor(Color.BLACK);
		tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
		tv.buildDrawingCache();
		Bitmap bitmapCode = tv.getDrawingCache();
		return bitmapCode;
	}

	/**
	 * 将两个Bitmap合并成一个(条形码和编码)
	 * @param first
	 * @param second
	 * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
	 * @return
	 */
	protected Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
		if (first == null || second == null || fromPoint == null) {
			return null;
		}
		int marginW = 20;
		Bitmap newBitmap = Bitmap.createBitmap(
				first.getWidth() + second.getWidth() + marginW,
				first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, marginW, 0, null);
		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newBitmap;
	}
}
