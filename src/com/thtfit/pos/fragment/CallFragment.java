package com.thtfit.pos.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.thtfit.pos.R;

/*
public class CallFragment extends Fragment implements OnClickListener{

    private ImageButton btnNum0 = null;
	private ImageButton btnNum1 = null;
	private ImageButton btnNum2 = null;
	private ImageButton btnNum3 = null;
	private ImageButton btnNum4 = null;
	private ImageButton btnNum5 = null;
	private ImageButton btnNum6 = null;
	private ImageButton btnNum7 = null;
	private ImageButton btnNum8 = null;
	private ImageButton btnNum9 = null;
	private ImageButton btnNum10 = null;
	private ImageButton btnNum11 = null;
    private ImageButton btnCallOn = null;
    private ImageButton btnCallOff = null;
	private ImageButton imageVolume;
	private View mView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_call, container,
				false);
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();
		super.onActivityCreated(savedInstanceState);
	}
	
	public void initView(){
        btnNum0 = (ImageButton) mView.findViewById(R.id.image_num_0);
        btnNum1 = (ImageButton) mView.findViewById(R.id.image_num_1);
		btnNum2 = (ImageButton) mView.findViewById(R.id.image_num_2);
		btnNum3 = (ImageButton) mView.findViewById(R.id.image_num_3);
		btnNum4 = (ImageButton) mView.findViewById(R.id.image_num_4);
		btnNum5 = (ImageButton) mView.findViewById(R.id.image_num_5);
		btnNum6 = (ImageButton) mView.findViewById(R.id.image_num_6);
		btnNum7 = (ImageButton) mView.findViewById(R.id.image_num_7);
		btnNum8 = (ImageButton) mView.findViewById(R.id.image_num_8);
        btnNum9 = (ImageButton) mView.findViewById(R.id.image_num_9);
        btnNum10 = (ImageButton) mView.findViewById(R.id.image_num_asterisk);
        btnNum11 = (ImageButton) mView.findViewById(R.id.image_num_octothorpe);
        btnCallOn = (ImageButton) mView.findViewById(R.id.image_num_13);
		btnCallOff = (ImageButton) mView.findViewById(R.id.image_num_14);
		imageVolume = (ImageButton) mView.findViewById(R.id.image_num_volume);
		
		imageVolume.setOnClickListener(this);

		btnNum0.setOnClickListener(this);
		btnNum1.setOnClickListener(this);
		btnNum2.setOnClickListener(this);
		btnNum3.setOnClickListener(this);
		btnNum4.setOnClickListener(this);
		btnNum5.setOnClickListener(this);
		btnNum6.setOnClickListener(this);
		btnNum7.setOnClickListener(this);
		btnNum8.setOnClickListener(this);
		btnNum9.setOnClickListener(this);
		btnNum10.setOnClickListener(this);
		btnNum11.setOnClickListener(this);
		btnCallOn.setOnClickListener(this);
		btnCallOff.setOnClickListener(this);
	}
    

    public void onTranFragment(){
   
    }
	@Override
	public void onClick(View v) {
      String TopStackEntryName = FragmentMgrHelper.getTopStackEntryName(getFragmentManager());
		switch (v.getId()) {
		case R.id.image_num_volume:
			AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_SAME,
	                AudioManager.FX_FOCUS_NAVIGATION_UP);
			break;
       case R.id.image_num_0:
            break;
       case R.id.image_num_1:
            break;
       case R.id.image_num_2:
            break;
       case R.id.image_num_3:
            break;
       case R.id.image_num_4:
            break;
       case R.id.image_num_5:
            break;
       case R.id.image_num_6:
            break;
       case R.id.image_num_7:
            break;
       case R.id.image_num_8:
            break;
       case R.id.image_num_9:
            break;
       case R.id.image_num_13:
            break;
       case R.id.image_num_14:
            break;
       case R.id.image_num_asterisk:
            break;
       case R.id.image_num_octothorpe:
            break;
		default:
			break;
		}
		
	}
	
} */
