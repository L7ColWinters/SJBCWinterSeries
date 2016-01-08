package sjbc.l7colwinters.sjbcwinterseries.eventresultsgathering;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import sjbc.l7colwinters.sjbcwinterseries.R;
import sjbc.l7colwinters.sjbcwinterseries.pref.Pref;

public class HomeFragment extends Fragment implements View.OnClickListener , GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "HomeFragment";
    private static final int RC_SIGN_IN = 42;
    private GoogleApiClient mGoogleApiClient = null;
    private static final String FAQ_GOOGLE_ACCOUNT = "https://support.google.com/accounts/answer/1728595?hl=en";

    private View mSignInButton = null;
    private View mSignOutButton = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //also sync with google calendar of main winter series events
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mSignInButton = view.findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        mSignOutButton = view.findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);

        if(Pref.getString(Pref.GOOGLE_ACCOUNT_ID,null) != null){
            mSignInButton.setVisibility(View.GONE);
            mSignOutButton.setVisibility(View.VISIBLE);
        }

        TextView resultsTextView = ((TextView) view.findViewById(R.id.view_results_message));
        resultsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        resultsTextView.setText(Html.fromHtml(getResources().getString(R.string.home_fragment_welcome_message_results)));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                if(signInIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    Log.e(TAG,"google play services not found");//TBD: should this be a toast or a snackbar?
                }
                break;
            case R.id.sign_out_button:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Pref.putString(Pref.GOOGLE_ACCOUNT_ID,null);
                        mSignOutButton.setVisibility(View.GONE);
                        mSignInButton.setVisibility(View.VISIBLE);
                    }
                });
                break;
            default:
                Log.d(TAG, "onClick - unhandled, id=" + v.getId());
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: errCode = " + connectionResult.getErrorCode());
        switch(connectionResult.getErrorCode()){
            case ConnectionResult.INVALID_ACCOUNT:
            case ConnectionResult.SIGN_IN_FAILED:
            case ConnectionResult.SIGN_IN_REQUIRED:
                Pref.putString(Pref.GOOGLE_ACCOUNT_ID,null);
                if(mSignOutButton.getVisibility() == View.VISIBLE){
                    mSignOutButton.setVisibility(View.GONE);
                    mSignInButton.setVisibility(View.VISIBLE);
                }
                break;
            default:
                Toast.makeText(getContext(),R.string.on_google_sign_in_failure,Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            Log.d(TAG, "Unknown request code returned in fragment: " + requestCode);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(getContext(),getContext().getString(R.string.success_sign_in), Toast.LENGTH_LONG).show();
            //remove sign in button
            mSignInButton.setVisibility(View.GONE);
            //show sign out button
            mSignOutButton.setVisibility(View.VISIBLE);
            Pref.putString(Pref.GOOGLE_ACCOUNT_ID,acct.getId());
        } else {
            // Signed out, show unauthenticated UI.
            Snackbar.make(getView(), getContext().getString(R.string.failed_to_sign_in), Snackbar.LENGTH_LONG)
                .setAction(R.string.help,new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent faqBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FAQ_GOOGLE_ACCOUNT));
                        if(faqBrowserIntent.resolveActivity(getContext().getPackageManager()) != null){
                            startActivity(faqBrowserIntent);
                        }
                    }
                })
                .setActionTextColor(Color.BLUE)
                .show();
        }
    }
}
