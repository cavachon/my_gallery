package com.android.flickr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class flickrActivity extends Activity {
	
	//private static final String FLICKRAPIKEY = "http://www.flickr.com/services/api/keys/399310dfc5ddf828d9d2e41c31edc88a";
	private static final String FLICKRAPIKEY = "399310dfc5ddf828d9d2e41c31edc88a";
	private static final String BASE_API_URL = "http://api.flickr.com/services/rest/?method=flickr.photos.search&text=";
	private static final String LOG_TAG = "flickr";
	
	protected Bitmap bm; 
	
	enum size{
		_s,_t,_z,_m
	};
	
	Drawable image;
	
    /** Called when the activity is first created. */
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		((Gallery) findViewById(R.id.gallery)).setAdapter(new ImageAdapter(this));
		//test comment
    }
    
    public void getMoreFlickrHandler(View v) {
    	((Gallery) findViewById(R.id.gallery)).setAdapter(new ImageAdapter(this));
    }
         
    public void setBackgroundHandler(View v) {
    	
    	final WallpaperManager wp = WallpaperManager.getInstance(this);
    	try {
    		wp.setBitmap(bm);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public class ImageAdapter extends BaseAdapter {
		/** The parent context */
		private Context myContext;
		
		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c) { 
			this.myContext = c; 
		};
		
		public int getCount() {
			return 1;
		}
		
		/** Returns a new ImageView to 
		* be displayed, depending on 
		* the position passed. 
		*/
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(this.myContext);
		
			try {
				bm = flickrApi(randomizer(3) + " clouds", 1);
				//bis.close();
				//is.close();
				/* Apply the Bitmap to the ImageView that will be returned. */
				i.setImageBitmap(bm);
			} catch (IOException e) {				
				e.printStackTrace();
				Log.e("LOG_TAG", e.getMessage());
			} catch (JSONException je) {
				je.printStackTrace();
			}
				
			/* Image should be scaled as width/height are set. */
			i.setScaleType(ImageView.ScaleType.FIT_XY);
		
			/* Set the Width/Height of the ImageView. */
			i.setLayoutParams(new Gallery.LayoutParams(250, 250));
			return i;
		}
		
		/** Returns the size (0.0f to 1.0f) of the views
		* depending on the 'offset' to the center. 
		*/
		public float getScale(boolean focused, int offset) {
			/* Formula: 1 / (2 ^ offset) */
		return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));		
		}

	    private Bitmap flickrApi(String searchPattern, int limit) throws IOException, JSONException {
	    	URL url = new URL(BASE_API_URL + searchPattern + "&api_key=" + FLICKRAPIKEY + "&per_page=" + limit + "&format=json");
	    	URL imageUrl;
	    	URLConnection connection = url.openConnection();
	    	
	    	connection.connect();
	    	    	
	    	String line;
	    	StringBuilder builder = new StringBuilder();
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	
	    	
	    	while((line=reader.readLine()) != null) {
	    		builder.append(line);
	    	}
	    	Log.v(LOG_TAG, "Line 86");
	    	
	    	Log.d("not good", builder.toString());
	    	
	    	int start = builder.toString().indexOf("(") + 1;
	    	int end = builder.toString().length() -1;
	    	
	    	String jSONString = builder.toString().substring(start, end);
	    	
	    	JSONObject jSONObject = new JSONObject(jSONString);
	    	JSONObject jSONObjectInner = jSONObject.getJSONObject("photos");
	    	JSONArray photoArray = jSONObjectInner.getJSONArray("photo");
	    	JSONObject photo = photoArray.getJSONObject((int) (limit*Math.random()));
	    	
	    	imageUrl = new URL(constructFlickrImgUrl(photo, size._m));
	    	
	    	Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openStream());
	    	return bitmap;
	    }
	    
	    private String constructFlickrImgUrl(JSONObject input, Enum size) throws JSONException {
	    	String FARMID = input.getString("farm");
	    	String SERVERID = input.getString("server");
	    	String SECRET = input.getString("secret");
	    	String ID = input.getString("id");
	    	
	    	StringBuilder sb = new StringBuilder();
	    	
	    	sb.append("http://farm");
	    	sb.append(FARMID);
	    	sb.append(".static.flickr.com/");
	    	sb.append(SERVERID);
	    	sb.append("/");
	    	sb.append(ID);
	    	sb.append("_");
	    	sb.append(SECRET);
	    	sb.append(size.toString());
	    	sb.append(".jpg");
	    	
	    	return sb.toString();
	    }
	    
	    private String randomizer(int length) {
	    	char i[] = new char[length];
	    	for (int j=0; j<length; j++) {
	    		i[j] = (char) ((int)5*Math.random()+(int)'a');
	    	}
	    	
	    	return new String(i);
	    }

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
    } 
    
    
}