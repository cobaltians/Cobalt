package fr.haploid.androidnativebridge.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import fr.haploid.androidnativebridge.R;
import fr.haploid.androidnativebridge.fragments.HTMLFragment;
import fr.haploid.androidnativebridge.fragments.HTMLPopUpWebview;

/**
 * {@link Activity} that will contain a {@link HTMLFragment}.
 * 
 * @author Diane
 */
public class HTMLActivity extends FragmentActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutToInflate());

		if (savedInstanceState == null) {

			HTMLFragment myFragment = getFragment();
			
			Bundle b = getIntent().getExtras();
			if(b != null && b.containsKey(HTMLFragment.kExtras))
			{	
				myFragment.setArguments(b.getBundle(HTMLFragment.kExtras));
			}

			if (findViewById(getFragmentContainerId()) != null) {
				android.support.v4.app.FragmentTransaction fTransition;
				fTransition = getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), myFragment);
				fTransition.commit();
			}
			else Log.e(getClass().getSimpleName(), "ERROR : Fragment container not found");
		}
	}

	/**
	 * This method returns a new instance of the fragment that will be added in HTMLActivity.
	 * This method should be overridden in subclasses.
	 * @return a new instance of the fragment that should be added in this {@link HTMLActivity}
	 */
	protected HTMLFragment getFragment()
	{
		return new HTMLFragment();
	}
	
	protected int getLayoutToInflate()
	{
		return R.layout.activity_html;
	}
	
	public int getFragmentContainerId()
	{
		return R.id.fragment_container;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_html, menu);
		return true;
	}
	
	/**
	 * The default method called by Android when the backButton is pressed.
	 * This method should NOT be overridden in subclasses.
	 */
	@Override
	public void onBackPressed() {
		HTMLFragment f = (HTMLFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
		if(f != null)
		{
			f.askWebViewForBackPermission();
		}
	}
	
	/**
	 * This method is called from the corresponding {@link HTMLFragment} when the webview has agreed to enable the backButton.
	 * This method should NOT be overridden in subclasses.
	 */
	public void goBack()
	{
		runOnUiThread(new Runnable() {
								public void run() {
									goBackWithSuper();
								}
							});
	}
	
	/**
	 * This method is called from the corresponding {@link HTMLPopUpWebview} when the popup has been dismissed.
	 * This method may be overridden in subclasses.
	 */
	public void onWebPopupDismiss(String fileName,Object additionalParams)
	{
		HTMLFragment f = (HTMLFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
		if(f != null)
		{
			f.onWebPopupDismiss(fileName, additionalParams);
		}
	}
	
	
	private void goBackWithSuper()
	{
		super.onBackPressed();
	}
}
