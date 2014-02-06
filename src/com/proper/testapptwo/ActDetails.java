package com.proper.testapptwo;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.core.MediaType;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.proper.testapptwo.R;
import com.proper.testapptwo.data.Product;
import com.proper.testapptwo.data.ScanTest;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ActDetails extends Activity {
	private Bitmap mBitmap = null;
	private long timeElapsed;
	private EditText txtScanBy;
	private ScanTest currentItem;
	private static final int MSG_BCODE_STARTING = 22;
	private static final int MSG_DONE = 11;
	private Handler codeImageHandler = null;
	protected ProgressDialog bcDialog;
	private boolean hasBcRan = false;
	private int bcRunCount = 0;
	//private boolean qryResult = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lyt_details);
		txtScanBy = (EditText) findViewById(R.id.etxtScanBy);
		Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ButtonClick(v);
			}
		});
		
		codeImageHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_BCODE_STARTING:
						bcDialog = new ProgressDialog(ActDetails.this);
			            CharSequence message = "Working hard...contacting webservice...";
			            CharSequence title = "Please Wait";
			            bcDialog.setCancelable(true);
			            bcDialog.setCanceledOnTouchOutside(false);
			            bcDialog.setMessage(message);
			            bcDialog.setTitle(title);
			            bcDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			            bcDialog.show();
						break;
					case MSG_DONE:
						if (bcDialog != null && bcDialog.isShowing() == true) {
							bcDialog.dismiss();
						}
						break;
				}
			}
			
		};
		populateUiControls(savedInstanceState);
	}
	
	private void ButtonClick(View v) {
		switch (v.getId()) {
			case R.id.btnSubmit:
				//prepare some values to be passed to the database
				String inputText = txtScanBy.getText().toString();
				if (inputText != null && !inputText.trim().equalsIgnoreCase("")) {
					//QueryDb(inputText);
					currentItem.setTestDoneBy(inputText);
					wserverPost qryTask = new wserverPost();
					qryTask.execute(currentItem);
				}
				break;
			default:
				 AlertDialog.Builder builder = new AlertDialog.Builder(this);
			        builder.setMessage(R.string.alert_exploded)
			               .setPositiveButton(R.string.but_ok, new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                       // ZO SOMETHING!
			                   }
			               });
			        builder.show();
			               /*.setNegativeButton(R.string.but_cancel, new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                       // User cancelled the dialog
			                   }
			               });
			        Log.d("Unexplicable Error", "For some reason this code has exploded please alert your IT staff");
					Toast.makeText(this, "For some reason this code has exploded please alert your IT staff", Toast.LENGTH_LONG);*/
		}
	}
	
	private void QueryDb() {
		//currentItem.setTestDoneBy(name);
		//String serviceUrl = "http://192.168.10.248:9080/com.lebel.restsample/api/v1/scans/postscan";
		String serviceUrl = "http://192.168.10.14:8080/com.lebel.restsample/api/v1/scans/postscan";
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(serviceUrl);
			ObjectMapper mapper = new ObjectMapper();
			String input = mapper.writeValueAsString(currentItem);
			//ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
			//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader()); //fix
			ClientResponse response = webResource.type(MediaType.APPLICATION_JSON.toString()).post(ClientResponse.class, input);
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}
	 
			//System.out.println("Output from Server .... \n");
			//String output = response.getEntity(String.class);
			//System.out.println(output);
		}
		catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void populateUiControls(Bundle form) {
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			//Yell, Blue murder !
			return;
		}
		Product prod = (Product) extras.getSerializable("PRODUCT_EXTRA");
		String barcode = extras.getString("SCANDATA_EXTRA");
		long startTime = extras.getLong("TIME_EXTRA") != 0 ? extras.getLong("TIME_EXTRA") : 0;
		//Populate views
		TextView txtArtist = (TextView) findViewById(R.id.txtv_Artist);
		TextView txtTitle = (TextView) findViewById(R.id.txtv_Title);
		
		TextView lblShortDesc = (TextView) findViewById(R.id.lblShortDesc);
		TextView lblISBN = (TextView) findViewById(R.id.lblISBN);
		TextView lblFormat = (TextView) findViewById(R.id.lblFormat);
		TextView lblBinNo = (TextView) findViewById(R.id.lblBinNumber);
		TextView lblOutOfStock = (TextView) findViewById(R.id.lblOutOfStock);
		TextView lblOnHand = (TextView) findViewById(R.id.lblOnHand);
		TextView lblPrice = (TextView) findViewById(R.id.lblPrice);
		TextView lblTime = (TextView) findViewById(R.id.lblTime);
		TextView lblScanBy = (TextView) findViewById(R.id.lblScanBy);
		
		TextView txtShortDesc = (TextView) findViewById(R.id.txtvShortDesc);
		TextView txtISBN = (TextView) findViewById(R.id.txtvISBN);
		TextView txtFormat = (TextView) findViewById(R.id.txtvFormat);
		TextView txtBinNo = (TextView) findViewById(R.id.txtvBinNumber);
		TextView txtOutOfStock = (TextView) findViewById(R.id.txtvOutOfStock);
		TextView txtOnHand = (TextView) findViewById(R.id.txtvOnHand);
		TextView txtPrice = (TextView) findViewById(R.id.txtvPrice);
		TextView txtTime = (TextView) findViewById(R.id.txtvTime);
		
		txtArtist.setText(prod.getArtist()) ; txtTitle.setText(prod.getTitle());
		lblShortDesc.setText("Short Description:") ; lblISBN.setText("ISBN:");
		lblFormat.setText("Format:") ; lblBinNo.setText("Bin Number:");
		lblOutOfStock.setText("Out of Stock:") ; lblOnHand.setText("Stock On Hand:");
		lblPrice.setText("Price:") ; lblTime.setText("Time Elapsed:");
		lblScanBy.setText("Test Performed By:");
		txtShortDesc.setText(prod.getShortDescription()) ; txtISBN.setText(prod.getBarcode());
		txtFormat.setText(prod.getFormat()) ; txtBinNo.setText(prod.getBinNo());
		txtOutOfStock.setText(String.format("%s", prod.getOutOfStock())); 
		txtOnHand.setText(String.format("%s", prod.getOnHand()));
		txtPrice.setText(String.format("£    %s", prod.getDealerPrice()));
		txtTime.setText(String.format("%s", timeElapsed));
		
		//if barcode !=null => draw barcode else use the image in resources as default
		if (barcode != null && !barcode.equalsIgnoreCase("")) {
			if (hasBcRan == false && bcRunCount == 0) {
				final String bcode = barcode;
				Runnable bcRunnable = new Runnable() {

					@Override
					public void run() {
						generateBarCode(bcode);
					}
					
				};
				Thread bcThread = new Thread(bcRunnable);
				bcThread.setName("bcThread");
				bcThread.run();
			}
		}
		else {
			ImageView barcodeImage = (ImageView) findViewById(R.id.imgBarcode);
			barcodeImage.setImageResource(R.drawable.barcode_ean13);
		}
		hasBcRan = false;
		bcRunCount = 0;
		
		timeElapsed = System.currentTimeMillis() - startTime;
		
		//Build new ScanTest
		currentItem = new ScanTest();
		currentItem.setProductId(prod.getProductId()); 
		currentItem.setQueryTime(timeElapsed);
	}
	
	public void generateBarCode(String data) {
		if (hasBcRan == false) {
	    	codeImageHandler.obtainMessage(MSG_BCODE_STARTING).sendToTarget();
	    	if (bcRunCount == 0) { bcRunCount ++; }
			com.google.zxing.Writer c9 = new Code128Writer();
		    try {
		        BitMatrix bm = c9.encode(data,BarcodeFormat.CODE_128,380, 168);
		    	//BitMatrix bm = c9.encode(data,BarcodeFormat.EAN_13,380, 168);
		        mBitmap = Bitmap.createBitmap(380, 168, Config.ARGB_8888);

		        for (int i = 0; i < 380; i++) {
		            for (int j = 0; j < 168; j++) {

		                mBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
		            }
		        }
		    } catch (WriterException e) {
		        e.printStackTrace();
		    }
		    if (mBitmap != null) {
		    	ImageView mImageView = (ImageView) findViewById(R.id.imgBarcode);
		    	mImageView.setImageBitmap(mBitmap);
		    }
		    
		    codeImageHandler.obtainMessage(MSG_DONE).sendToTarget();
	    }
	}
	
	private class wserverPost extends AsyncTask<ScanTest, Integer, Boolean> {
		protected ProgressDialog mDialog;
		@Override
		protected Boolean doInBackground(ScanTest... params) {
			QueryDb();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (mDialog != null && mDialog.isShowing() == true) {mDialog.dismiss();}
		}

		@Override
		protected void onPreExecute() {
			mDialog = new ProgressDialog(ActDetails.this);
            CharSequence message = "Working hard...contacting webservice...";
            CharSequence title = "Please Wait";
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage(message);
            mDialog.setTitle(title);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
		}
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.startActivity(new Intent(ActDetails.this,com.android.barcode.ActMain.class));
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mnu_details, menu);
		return true;
	}

}