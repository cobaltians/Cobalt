package fr.cobaltians.cobaltcatalog.fragments;

import fr.cobaltians.cobaltcatalog.R;
import fr.cobaltians.cobalt.fragments.CobaltFragment;

import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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
    protected static String kValue = "value";
    protected static String kIndex = "index";

	private Button btnDoSomeMath, btnTestAuto;

    private ArrayList<Object> mArrayTest;
    private boolean testFailed = false;

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
                mArrayTest = new ArrayList<Object>();
                mArrayTest.add(0, "quotes : it's working \"great\"");
                mArrayTest.add(1, "le + c'est top.");
                mArrayTest.add(2, "url &eactue;é&12;\n3#23:%20'\\u0020hop");
                mArrayTest.add(3, "'{ obj_representation : \"test\"}'");
                mArrayTest.add(4, "emoji \ue415 \\ue415 u{1f604}");
                mArrayTest.add(5, "https://famicitys.s3.amazonaws.com/photos/019/558/630/normal/881558d70ae5b7023209cb609250cb84cabd301b.jpg?AWSAccessKeyId=1RZJ66V99R267YCDQSG2&Expires=1401263985&Signature=xbE%2B49MCgE7/WTKqnvwQ3f4zYmg%3D");

                launchTest(0);
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
                    int index = echo.getInt(kIndex);
                    String stringForTest = echo.getString(kValue);
                    if (stringForTest.equals(mArrayTest.get(index))) {
                        Log.d(TAG, "test OK for the String : "+stringForTest);
                    }
                    else {
                        testFailed = true;
                        Log.e(TAG, "test failed !!!! send is : "+ mArrayTest.get(index));
                        Log.e(TAG, "received : "+stringForTest);
                    }
                    if (index < mArrayTest.size()-1) {
                        launchTest(++index);
                    }
                    else if (index==mArrayTest.size()-1) {
                        if (testFailed) {
                            Toast.makeText(mContext, "Some tests failed ! Check logs.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, "All tests passed ! No errors", Toast.LENGTH_SHORT).show();
                        }
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

    private void launchTest(int index){
        JSONObject data = new JSONObject();

            try {
                JSONObject stringToTest = new JSONObject();
                stringToTest.put(kIndex, index);
                stringToTest.put(kValue, mArrayTest.get(index));
                data.put(kValues, stringToTest);

                sendEvent(JSEcho, data, JSEchoCallback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
}
