package mxc.android.picker;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * A simple layout group that provides a numeric text area with two buttons to
 * increment or decrement the value in the text area. Holding either button
 * will auto increment the value up or down appropriately. 
 * 
 * @author Jeffrey F. Cole
 *
 */
public class OrderNumberPicker extends LinearLayout {

	private final long REPEAT_DELAY = 20;
	
	private final int INC = 1;
	
	private int MINIMUM = 0;
	private int MAXIMUM = 99999;
	
	public Integer value;
	
	Button decrement;
	Button increment;
	public EditText valueText;
	
	private Handler repeatUpdateHandler = new Handler();
	
	private boolean autoIncrement = false;
	private boolean autoDecrement = false;

	/**
	 * This little guy handles the auto part of the auto incrementing feature.
	 * In doing so it instantiates itself. There has to be a pattern name for
	 * that...
	 * 
	 * @author Jeffrey F. Cole
	 *
	 */
	class RepetetiveUpdater implements Runnable {
		public void run() {
			if( autoIncrement ){
				increment();
				repeatUpdateHandler.postDelayed( new RepetetiveUpdater(), REPEAT_DELAY );
			} else if( autoDecrement ){
				decrement();
				repeatUpdateHandler.postDelayed( new RepetetiveUpdater(), REPEAT_DELAY );
			}
		}
	}
	
	public OrderNumberPicker( Context context, AttributeSet attributeSet ) {
		super(context, attributeSet);
		
		this.setLayoutParams( new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ) );
		LayoutParams elementParams = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
		LayoutParams elementParamst = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
		elementParamst.weight = 1.0f;
		// init the individual elements
		initDecrementButton( context );
		initValueEditText( context );
		initIncrementButton( context );
		
		// Can be configured to be vertical or horizontal
		// Thanks for the help, LinearLayout!	
		if( getOrientation() == VERTICAL ){
			addView( increment, elementParams );
			addView( valueText, elementParamst );
			addView( decrement, elementParams );
		} else {
			addView( decrement, elementParams );
			addView( valueText, elementParamst );
			addView( increment, elementParams );
		}
	}
	
	public void setMax(int value) {
		MAXIMUM = value;
	}

	public void setMin(int value) {
		MINIMUM = value;
	}
	
	private void initIncrementButton( Context context){
		increment = new Button( context );
		increment.setTextSize( 16 );
		increment.setText( "  +  " );
		
		increment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	increment();
            }
        });
		
		increment.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoIncrement = true;
						repeatUpdateHandler.post(new RepetetiveUpdater());
						return false;
					}
				}
		);
		
		increment.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoIncrement ){
					autoIncrement = false;
				}
				return false;
			}
		});
	}
	
	private void initValueEditText( Context context){
		
		value = new Integer( 0 );
		
		valueText = new EditText( context );
		valueText.setTextSize(25);		
		
		valueText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int arg1, KeyEvent event) {
				int backupValue = value;
				try {
					value = Integer.parseInt( ((EditText)v).getText().toString() );
				} catch( NumberFormatException nfe ){
					value = backupValue;
				}				
				return false;
			}
		});
		
		valueText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ){
					((EditText)v).selectAll();
				}
			}
		});
		valueText.setGravity( Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL );
		valueText.setText( value.toString() );
		valueText.setInputType( InputType.TYPE_CLASS_PHONE );
	}
	
	private void initDecrementButton( Context context){
		decrement = new Button( context );
		decrement.setTextSize( 16 );
		decrement.setText( "  -  " );
		

		decrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	decrement();
            }
        });
		
		
		decrement.setOnLongClickListener( 
				new View.OnLongClickListener(){
					public boolean onLongClick(View arg0) {
						autoDecrement = true;
						repeatUpdateHandler.post( new RepetetiveUpdater() );
						return false;
					}
				}
		);
		
		decrement.setOnTouchListener( new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_UP && autoDecrement ){
					autoDecrement = false;
				}
				return false;
			}
		});
	}
	
	public void increment(){
		if( value < MAXIMUM ){
			fixValue();
			value = value + INC;
			valueText.setText( value.toString() );
		}
	}

	public void decrement(){
		if( value > MINIMUM ){
			fixValue();
			value = value - INC;
			valueText.setText( value.toString() );
		}
	}
	
	public void fixValue() {
		int backupValue = value;
		try {
			value = Integer.parseInt( valueText.getText().toString() );
		} catch( NumberFormatException nfe ){
			value = backupValue;
		}
	}
	
	public int getValue(){
		fixValue();
		
		return value;
	}
	
	public void setValue( int value ){
		if( value > MAXIMUM ) value = MAXIMUM;
		if( value >= 0 ){
			this.value = value;
			valueText.setText( this.value.toString() );
		}
	}
	
}
