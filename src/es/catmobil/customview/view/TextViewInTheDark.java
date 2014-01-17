package es.catmobil.customview.view;

import es.catmobil.customview.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 
 * @author ofernandez
 *Very simple sample about extending an SDK view implementation, in this case a TextView
 *This TextView will adjust its text size according to light sensor.
 */
public class TextViewInTheDark extends TextView {
	private SensorManager mySensorManager;
	private Sensor myLightSensor;
	private float minSize = 10f;//Attribute read from xml
	private float maxSize = 10f;//Attribute read from xml

	/*
	 * This constructor is called if the view is instantiated from code
	 */
	public TextViewInTheDark(Context context) {
		super(context);
		init(context);

	}
	/*
	 * This constructor is called from the ADT plugin if we create an XML layout 
	 */
	public TextViewInTheDark(Context context, AttributeSet attrs) {
		super(context, attrs);

		initAttributes(context, attrs);
		init(context);
	}
	/*
	 * This constructor is called from the ADT plugin if we create an XML layout and some style is used 
	 */
	public TextViewInTheDark(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		initAttributes(context, attrs);
		init(context);
	}

	private void initAttributes(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TextViewInTheDarkView, 0, 0);
		try {
			// get the text and colors specified using the names in attrs.xml
			minSize = a.getFloat(R.styleable.TextViewInTheDarkView_minTextSize,
					10);
			maxSize = a.getFloat(R.styleable.TextViewInTheDarkView_maxTextSize,
					40);
		} finally {
			a.recycle();
		}

	}
	/*
	 * Instantiation of all objects we need to avoid create them in the onDraw or other frequently called methods.
	 */
	private void init(Context context) {
		if (isInEditMode()) {
			//In case of being in edit mode we just adjust the size of text to the average size between max and minus values 
			changeSizeOfText((minSize + maxSize) / 2f);
			return;
		}
		mySensorManager = (SensorManager) (context
				.getSystemService(Context.SENSOR_SERVICE));
		myLightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		if (myLightSensor == null) {
			//Nothing to be done here
			return;
		} else {

			mySensorManager.registerListener(new SensorEventListener() {

				@Override
				public void onSensorChanged(SensorEvent event) {
					if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
						float sensorSize = map(event.values[0], 800, 0,
								minSize, maxSize);
						Log.v("SENSOR", "in:" + event.values[0] + " out:"
								+ sensorSize + "  nim:" + minSize + " max:"
								+ maxSize);
						changeSizeOfText(sensorSize);
					}
				}

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// Nothing to be done

				}
			}, myLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	/*
	 * Call to the TextView.setTextSize method who will requestLayout and invalidate the view.
	 */
	private void changeSizeOfText(float size) {
		this.setTextSize(size);
	}

	/**
	 * Helper method to provide an equivalent value according to input-output range of values 
	 * @param x
	 * @param in_min
	 * @param in_max
	 * @param out_min
	 * @param out_max
	 * @return
	 */
	private float map(float x, float in_min, float in_max, float out_min,
			float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}
