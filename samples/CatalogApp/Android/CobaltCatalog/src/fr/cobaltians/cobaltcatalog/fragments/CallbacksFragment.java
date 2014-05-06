package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobaltcatalog.R;

import fr.cobaltians.cobalt.fragments.CobaltFragment;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CallbacksFragment extends CobaltFragment {

	protected static String JSAddValues = "addValues";
    protected static String JSValuesCallback = "valuesCallback";
    protected static String JSEcho = "echo";
    protected static String JSEchoCallback = "echoCallback";
    protected static String kResult = "result";
    protected static String kValues = "values";

	private Button btnDoSomeMath, btnTestAuto;

    ArrayList<Object> arrayTest ;

	@Override
	protected int getLayoutToInflate() {
		return R.layout.callbacks_fragment;
	}

	@Override
	protected void setUpViews(View rootView) {
		super.setUpViews(rootView);
        btnDoSomeMath = (Button) rootView.findViewById(R.id.btnDoSomeMaths);
        btnTestAuto = (Button) rootView.findViewById(R.id.btnTestAuto);
		}

	@Override
	protected void setUpListeners() {
        btnDoSomeMath.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				JSONObject data = new JSONObject();
				try {
					ArrayList<Integer> values = new ArrayList<Integer>();
					values.add(1);
					values.add(3);
					data.put(kValues, new JSONArray(values));
					sendEvent(JSAddValues, data, JSValuesCallback);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

        btnTestAuto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                launchTest();
            }
        });
	}

	// unhandled JS messages
	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
	@Override
	protected boolean onUnhandledEvent(String name, JSONObject data, String callback) {
        if (name.equals(JSAddValues)) {
            try {
                JSONArray values = data.getJSONArray(kValues);
                int val1 = values.getInt(0);
                int val2 = values.getInt(1);
                JSONObject result = new JSONObject();
                result.put(kResult, val1+val2);
                sendCallback(callback, result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (name.equals(JSEcho)) {
            sendCallback(callback, data);
            return true;
        }
		return false;
	}
	@Override
	protected boolean onUnhandledCallback(String name, JSONObject data) {
        if (name.equals(JSValuesCallback)) {
            int result = data.optInt(kResult, 0);
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("result is : "+ result);
            AlertDialog mAlert = alert.create();
            mAlert.setCanceledOnTouchOutside(true);
            mAlert.show();
            return true;
        }
        if (name.equals(JSEchoCallback)) {
            JSONObject echo = data.optJSONObject(kValues);
            if (echo != null) {
                try {
                    int index = echo.getInt("index");
                    String test = echo.getString("value");
                    if (test.equals(arrayTest.get(index))) {
                        Log.d(TAG, "test OK for the String : "+test);
                    }
                    else {
                        Log.d(TAG, "test failed !!!! send is : "+arrayTest.get(index));
                        Log.d(TAG, "received : "+test);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return true;
        }

		return false;
	}

	@Override
	protected void onPullToRefreshRefreshed() {
	}

	@Override
	protected void onInfiniteScrollRefreshed() {		
	}

    private void launchTest(){
        JSONObject data = new JSONObject();
        arrayTest = new ArrayList<Object>();
        arrayTest.add(0, "quotes : it's working \"great\"");
        arrayTest.add(1, "url &eactue;é&12;\n3#23:%20'\\u0020hop");
        arrayTest.add(2, "'{ obj_representation : \"test\"}'");
        arrayTest.add(3, "emoji \ue415 \\ue415 u{1f604}");
        arrayTest.add(4, "https://famicitys.s3.amazonaws.com/photos/019/558/630/normal/881558d70ae5b7023209cb609250cb84cabd301b.jpg?AWSAccessKeyId=1RZJ66V99R267YCDQSG2&Expires=1401263985&Signature=xbE%2B49MCgE7/WTKqnvwQ3f4zYmg%3D");

        for (int i = 0; i < arrayTest.size(); i++) {
            try {
                JSONObject test = new JSONObject();
                test.put("index", i);
                test.put("value", arrayTest.get(i));
                data.put(kValues, test);

                sendEvent(JSEcho, data, JSEchoCallback);
                Log.d(TAG, "send test to web with this object : "+test.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
