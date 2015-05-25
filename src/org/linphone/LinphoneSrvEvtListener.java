package org.linphone;

import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;

public interface LinphoneSrvEvtListener
{
	public void onRegistrationStateChanged(final RegistrationState RegState, final String Msg);
	public void onCallStateChanged(final LinphoneCall TheLinphoneCall, final State CallState, final String StateDesc);
	public void onConnectivityChanged(final NetworkInfo networkInfo, final ConnectivityManager connMgr);
	public String getToneUri(final LinphoneCall TheLinphoneCall, final State CallState, final String StateDesc);
}

