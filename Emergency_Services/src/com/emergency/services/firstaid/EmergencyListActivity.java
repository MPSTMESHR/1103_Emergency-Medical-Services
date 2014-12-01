package com.emergency.services.firstaid;

import java.util.ArrayList;
import java.util.Collection;

import com.emergency.services.R;
import com.emergency.services.R.id;
import com.emergency.services.R.layout;
import com.emergency.services.R.string;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyListActivity extends ListActivity implements OnClickListener, RecognitionListener {

    private static final int REQUEST_RECOGNIZE = 1337;

    EmergencyEntry[] mEmergencies = new EmergencyEntry[] {
	    new EmergencyEntry(R.string.emergencyCallGuide, R.layout.activity_emergency_call_guide),
	    new EmergencyEntry(R.string.bleeding, R.layout.activity_bleeding),
	    new EmergencyEntry(R.string.noseBleeding, R.layout.activity_nose_bleeding),
	    new EmergencyEntry(R.string.sink, R.layout.activity_sink),
	    new EmergencyEntry(R.string.looseConsciousness, R.layout.activity_loose_consciousness),
	    new EmergencyEntry(R.string.shock, R.layout.activity_shock),
	    new EmergencyEntry(R.string.electricShock, R.layout.activity_electric_shock),
	    new EmergencyEntry(R.string.smallBurns, R.layout.activity_small_burns),
	    new EmergencyEntry(R.string.burnDressing, R.layout.activity_burn_dressing),
	    new EmergencyEntry(R.string.chemicalsPoisoning, R.layout.activity_chemicals_poisoning),
	    new EmergencyEntry(R.string.drugsAndFoodPoisoning, R.layout.activity_drugs_and_food_poisoning),
	    new EmergencyEntry(R.string.anaphylacticShock, R.layout.activity_anaphylactic_shock),
	    new EmergencyEntry(R.string.carAccident, R.layout.activity_car_accident),
	    new EmergencyEntry(R.string.frostbite, R.layout.activity_frostbite),
	    new EmergencyEntry(R.string.hypothermia, R.layout.activity_hypothermia), };

    private SpeechRecognizer mSpeechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_emergency_list);
	setListAdapter(new EmergencyEntryAdapter(this, mEmergencies));

	findViewById(R.id.micButton).setOnClickListener(this);
	findViewById(R.id.locationButton).setOnClickListener(this);
    }

    public void onClick(View v) {
	final int id = v.getId();
	if (id == R.id.locationButton) {
	    showMap();
	} else if (id == R.id.micButton) {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
	    startActivityForResult(intent, REQUEST_RECOGNIZE);
	}
    }

    void showMap() {
	LocationManager location = null;

	location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	Criteria criteria = new Criteria();
	criteria.setAccuracy(Criteria.NO_REQUIREMENT);
	criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

	Location loc = location.getLastKnownLocation(location.getBestProvider(criteria, true));
try{
	final String geoURI = String.format("geo: %f,%f?zoom=20&q=" + loc.getLatitude() + "," + loc.getLongitude(), loc.getLatitude(),
		loc.getLongitude());
	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(geoURI)));
	
}
catch (Exception e){
	e.printStackTrace();
}

	
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);

	showEmergencyActivity(mEmergencies[position]);

    }

    void showEmergencyActivity(EmergencyEntry ee) {
	Intent intent = new Intent(this, EmergencyActivity.class);
	intent.putExtra(EmergencyActivity.EMERGENCY_NAME, getString(ee.nameId));
	intent.putExtra(EmergencyActivity.EMERGENCY_LAYOUT_ID, ee.layoutId);
	startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == REQUEST_RECOGNIZE && resultCode == RESULT_OK) {
	    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

	    int[] wordsMatched = new int[mEmergencies.length];

	    for (String result : matches) {
		String[] recognizedWords = result.toLowerCase().split(" ");

		for (int i = 0; i < mEmergencies.length; i++) {
		    EmergencyEntry ee = mEmergencies[i];
		    final String emergencyName = getString(ee.getNameId()).toLowerCase();
		    for (String word : recognizedWords) {
			if (emergencyName.contains(word)) {
			    wordsMatched[i]++;
			}
		    }
		}
	    }

	    // find highest value
	    int max = 0;
	    int maxId = -1;
	    for (int i = 0; i < wordsMatched.length; i++) {
		if (wordsMatched[i] > max) {
		    max = wordsMatched[i];
		    maxId = i;
		}
	    }

	    if (maxId == -1) {
		Toast.makeText(this, "No result found", Toast.LENGTH_LONG).show();
	    } else {
		showEmergencyActivity(mEmergencies[maxId]);
	    }

	}
	super.onActivityResult(requestCode, resultCode, data);
    }

    static class EmergencyEntry {
	public EmergencyEntry(int nameId, int layoutId) {
	    this.nameId = nameId;
	    this.layoutId = layoutId;
	}

	private int nameId;
	int layoutId;

	// TODO: icon?
	public int getNameId() {
	    return nameId;
	}

    }

    public static class EmergencyEntryAdapter extends ArrayAdapter<EmergencyEntry> {

	private LayoutInflater mInflater;

	public EmergencyEntryAdapter(Context context, EmergencyEntry[] entries) {
	    super(context, android.R.id.text1, entries);
	    mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    ViewHolder holder;

	    if (convertView == null) {
		convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

		holder = new ViewHolder();

		holder.name = (TextView) convertView.findViewById(android.R.id.text1);

		convertView.setTag(holder);
	    } else {
		holder = (ViewHolder) convertView.getTag();
	    }

	    String name = getContext().getString(((EmergencyEntry) getItem(position)).getNameId());
	    holder.name.setText(name);

	    return convertView;
	}

	static class ViewHolder {
	    TextView name;
	}
    }

    public void onBeginningOfSpeech() {
	// TODO Auto-generated method stub

    }

    public void onBufferReceived(byte[] buffer) {
	// TODO Auto-generated method stub

    }

    public void onEndOfSpeech() {
	// TODO Auto-generated method stub

    }

    public void onError(int error) {
	// TODO Auto-generated method stub

    }

    public void onEvent(int eventType, Bundle params) {
	// TODO Auto-generated method stub

    }

    public void onPartialResults(Bundle partialResults) {
	// TODO Auto-generated method stub
	Log.d("TAGGGG", partialResults.toString());

    }

    public void onReadyForSpeech(Bundle params) {
	// TODO Auto-generated method stub

    }

    public void onResults(Bundle results) {
	// TODO Auto-generated method stub
	Log.d("TAGGGG", results.toString());

    }

    public void onRmsChanged(float rmsdB) {
	// TODO Auto-generated method stub

    }

}
