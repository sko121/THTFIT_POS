/*
BootReceiver.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.linphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import com.thtfit.pos.R;
import com.thtfit.pos.activity.MainActivity;
import android.util.Log;

import android.os.SystemProperties;
import com.thtfit.SmartTerminal.SysConfiguration;
import android.content.SharedPreferences;


public class BootReceiver extends BroadcastReceiver {
	private static String SYS_CONFIG_LOG_URL ="com.thtfit.pos_boot_preferences";
	private static String BOOT_FLAG ="ISUPGRADEDBOOT"; 
	private SharedPreferences sp_sys_config;
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
	 	{
	 		String strProductSubMachine = SystemProperties.get("ro.product.SubMachineId", "");
			String StringMachineID=SystemProperties.get("ro.product.MachineId", ""); 
			Log.d("BOOT", "strProductSubMachine = " + strProductSubMachine); 
 			Log.d("BOOT", "StringMachineID = " + StringMachineID); 
			if((strProductSubMachine.equals("") || strProductSubMachine == null) && (StringMachineID.equals("A97S"))) 
			{
				 Intent newIntent = new Intent(context, MainActivity.class);
		         newIntent.setAction("android.intent.action.MAIN"); //MyActivity action defined in androidManifest.xml
		         newIntent.addCategory("android.intent.category.LAUNCHER");//MyActivity category defined in AndroidManifest.xml
		         newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //If activity is not launched in Activity environment, this flag is mandatory to set
		         context.startActivity(newIntent);
				 
				 //read xml 
				sp_sys_config = context.getSharedPreferences(SYS_CONFIG_LOG_URL, 0);
				String strBootFlag =  sp_sys_config.getString(BOOT_FLAG,null); 
 				Log.d("BOOT", "strBootFlag:  = " + strBootFlag); 
				boolean  bIsNeedRetryFlag = true; 
				if ( null !=strBootFlag && (!strBootFlag.isEmpty())){ 
					
					SysConfiguration pConfig = new SysConfiguration();
					if(strBootFlag.equals("0")) // 
					{//restore file
						if(pConfig != null)
						{
							//pConfig.backOrRestorConfigFile("", ""); //
							boolean  bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/com.thtfit.pos_preferences.xml"
									,"/data/data/com.thtfit.pos/shared_prefs/com.thtfit.pos_preferences.xml"); //  
							if(!bSuccess)
							{
								bIsNeedRetryFlag = bSuccess;
							}
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/define_alarm_config.xml",
 									"/data/data/com.thtfit.pos/shared_prefs/define_alarm_config.xml"); //
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/Pos.db",
									"/data/data/com.thtfit.pos/databases/Pos.db"); //  
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/Pos.db-shm",
									"/data/data/com.thtfit.pos/databases/Pos.db-shm"); //  
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/Pos.db-wal",
									"/data/data/com.thtfit.pos/databases/Pos.db-wal"); // 
							if(!bSuccess)
							{
								bIsNeedRetryFlag = bSuccess;
							}
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/outside_alarm_config.xml",
									"/data/data/com.thtfit.pos/shared_prefs/outside_alarm_config.xml"); // 
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/inside_alarm_config.xml",
									"/data/data/com.thtfit.pos/shared_prefs/inside_alarm_config.xml"); // 
							if(!bSuccess)
							{
								bIsNeedRetryFlag = bSuccess;
							}
							if(bIsNeedRetryFlag)
							{
								SharedPreferences.Editor editorBootFlag = sp_sys_config.edit();
								editorBootFlag.putString(BOOT_FLAG,"1");
								editorBootFlag.commit(); 
							}
						}
					}
					else if(strBootFlag.equals("1")) // 
					{
						/*backup to /mnt/private/<package_name>*/
						 
						boolean  bSuccess = pConfig.backOrRestorConfigFile("/data/data/com.thtfit.pos/shared_prefs/com.thtfit.pos_preferences.xml",
									"/mnt/private/com.thtfit.pos/com.thtfit.pos_preferences.xml"); //  
						bSuccess = pConfig.backOrRestorConfigFile("/data/data/com.thtfit.pos/shared_prefs/define_alarm_config.xml",
								"/mnt/private/com.thtfit.pos/define_alarm_config.xml"); // 
						bSuccess = pConfig.backOrRestorConfigFile("/data/data/com.thtfit.pos/databases/Pos.db",
								"/mnt/private/com.thtfit.pos/Pos.db"); // 
						bSuccess = pConfig.backOrRestorConfigFile("/data/data/com.thtfit.pos/shared_prefs/inside_alarm_config.xml",
								"/mnt/private/com.thtfit.pos/inside_alarm_config.xml"); // 
						bSuccess = pConfig.backOrRestorConfigFile("/data/data/com.thtfit.pos/shared_prefs/outside_alarm_config.xml",
								"/mnt/private/com.thtfit.pos/outside_alarm_config.xml"); //
						bSuccess = pConfig.backOrRestorConfigFile("/data/data/com.thtfit.pos/databases/Pos.db-wal",
								"/mnt/private/com.thtfit.pos/Pos.db-wal"); // 
						bSuccess = pConfig.backOrRestorConfigFile("/data/data/com.thtfit.pos/databases/Pos.db-shm",
								"/mnt/private/com.thtfit.pos/Pos.db-shm"); // 
					}  
				}else { 
					
					SysConfiguration pConfig = new SysConfiguration();
					if(pConfig != null)
					{
							boolean  bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/com.thtfit.pos_preferences.xml"
									,"/data/data/com.thtfit.pos/shared_prefs/com.thtfit.pos_preferences.xml"); //  
							if(!bSuccess)
							{
								bIsNeedRetryFlag = bSuccess;
							}
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/define_alarm_config.xml",
 									"/data/data/com.thtfit.pos/shared_prefs/define_alarm_config.xml"); //
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/Pos.db",
									"/data/data/com.thtfit.pos/databases/Pos.db"); //  
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/Pos.db-shm",
									"/data/data/com.thtfit.pos/databases/Pos.db-shm"); //  
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/Pos.db-wal",
									"/data/data/com.thtfit.pos/databases/Pos.db-wal"); // 
							if(!bSuccess)
							{
								bIsNeedRetryFlag = bSuccess;
							}
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/outside_alarm_config.xml",
									"/data/data/com.thtfit.pos/shared_prefs/outside_alarm_config.xml"); // 
							bSuccess = pConfig.backOrRestorConfigFile("/mnt/private/com.thtfit.pos/inside_alarm_config.xml",
									"/data/data/com.thtfit.pos/shared_prefs/inside_alarm_config.xml"); // 
							if(!bSuccess)
							{
								bIsNeedRetryFlag = bSuccess;
							}
							//if(bIsNeedRetryFlag)// must call once backup
							{
								SharedPreferences.Editor editorBootFlag = sp_sys_config.edit();
								editorBootFlag.putString(BOOT_FLAG,"1");
								editorBootFlag.commit(); 
							}
					}  
				}
				
			}
	 	}

		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
			context.getString(R.string.pref_autostart_key), false)) {
				Intent lLinphoneServiceIntent = new Intent(Intent.ACTION_MAIN);
				lLinphoneServiceIntent.setClass(context, LinphoneService.class);
				context.startService(lLinphoneServiceIntent);
		}
	}
}

