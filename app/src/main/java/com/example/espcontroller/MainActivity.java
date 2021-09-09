package com.example.espcontroller;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.example.udp_server.R;

import java.nio.ByteBuffer;

public class MainActivity extends Activity {

    double x,y,z,rz;
    double tempX,tempY,tempZ,tempRZ;
    int command = 100;
    boolean boost = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Controller controller = new Controller(this);
        setContentView(R.layout.main);

        final Button button = findViewById(R.id.button);
        final Button buttonGetControllers = findViewById(R.id.buttonGetControllers);
        final EditText editText = findViewById(R.id.plain_text_input);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UDP_Client Client = new UDP_Client();
                byte[] bytes = intToByteArray(Integer.parseInt(editText.getText().toString()));
                Client.Message = bytes;
                System.out.println("SendMess");
                Client.NachrichtSenden();
            }
        });
        buttonGetControllers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    System.out.println(Controller.getGameControllerIds());
//                    System.out.println(controller.onGenericMotionEvent());
                }
            }
        });
    }
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                    == InputDevice.SOURCE_GAMEPAD) {
                if (event.getRepeatCount() == 0) {
                    switch (keyCode) {

                        // Handle gamepad and D-pad button presses to
                        // navigate the ship
//                        ...
                        case 101:
                        case 100:
                        case 98:
                        case 4:
                            return true;
                        default:
                            Log.i("KeyCode",Integer.toString(keyCode));
                            if (isFireKey(keyCode)) {

                                handled = true;
                            }
                            break;
                    }
                }
                if (handled) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static boolean isFireKey(int keyCode) {
        // Here we treat Button_A and DPAD_CENTER as the primary action
        // keys for the game.
        return keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_BUTTON_A;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Check that the event came from a game controller
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                    InputDevice.SOURCE_JOYSTICK &&
                    event.getAction() == MotionEvent.ACTION_MOVE) {

                MotionEvent motionEvent = (MotionEvent) event;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
                    x = motionEvent.getAxisValue(MotionEvent.AXIS_X);
                    y = motionEvent.getAxisValue(MotionEvent.AXIS_Y);
                    z = motionEvent.getAxisValue(MotionEvent.AXIS_Z);
                    rz = motionEvent.getAxisValue(MotionEvent.AXIS_RZ);
//                    boost = motionEvent.getAxisValue(MotionEvent.AXIS_BRAKE);

                }

                if(y>0) return true;

                if (y<0){
                    y *=10;
                    y = (Math.floor(y * 4)/4) ;
//                    y = Math.round(y * 10)/10;
                    y = (y*10);
                    y *= -1;
                }


                command = (int)y;
                Log.i("Power",String.valueOf(command));
//                  command =  Math.round(y * 2) / 2.0 ;
//                System.out.println(Math.round(y * 10) /10.0 );
//                System.out.println(y );
//                up
                 if(z > 0.1 ){
                    tempRZ = (Math.floor(z * 10)) ;
                    command = command * 1000 + 90 + (int)tempRZ * 6;
                    command = command * 1000 + 90 + (int)tempRZ * 6;
                }

//                down

                else if(z < -0.1 ){
                    z *= -1;
                    tempRZ =  (Math.floor(z * 10)) ;
                    command = command * 1000 + 90 - (int)tempRZ * 6;
                    command = command * 1000 + 90 - (int)tempRZ * 6;
                }
//                turn left

                else if(rz > 0.1 ){
                    tempZ  = (Math.floor(rz * 10)) ;
                    command = command * 1000 + 90 -(int)tempZ * 6;
                    command = command * 1000 + 90 + (int)tempZ * 6;
                }
//                turn right

                else if(rz < - 0.1 ){
                    rz *= -1;
                    tempZ =  (Math.floor(rz * 10)) ;
                    command = command * 1000 + 90 + (int)tempZ * 6;
                    command = command * 1000 + 90 - (int)tempZ * 6;
                }
                else{
                     command = command * 1000 + 90;
                     command = command * 1000 + 90;
                 }
                Log.i("Command",String.valueOf(command));

//                Log.i("Power",Double.toString(y));
//                Log.i("Boost",Double.toString(boost));
//                Log.i("Left",Double.toString(z));
//                Log.i("Right",Double.toString(y));

//                System.out.println(command);

                UDP_Client Client = new UDP_Client();
                byte[] bytes = intToByteArray(command);
                Client.Message = bytes;
//                System.out.println("SendMess");
                Client.NachrichtSenden();


                return true;
            }
        }
        return super.onGenericMotionEvent(event);
    }



}