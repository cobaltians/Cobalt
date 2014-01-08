package fr.cobaltians.cobaltcatalog.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import fr.cobaltians.cobalt.fragments.HTMLFragment;

import android.os.AsyncTask;
import android.util.Log;

class GoogleAsyncTask extends AsyncTask<String, String, String>{

	private HTMLFragment f;
	private String callBackID;
	private String value;
	
	public GoogleAsyncTask(HTMLFragment f,String callbackId,String value)
	{
		super();
		this.f = f;
		this.callBackID = callbackId;
		this.value = value;
	}
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(this.callBackID != null && this.value != null && result != null)
        {
        	f.sendCallbackResponse(this.callBackID, this.value+" "+result);
        }
        else Log.e(getClass().getName(), "ERROR IN ON POST EXECUTE");
    }
}