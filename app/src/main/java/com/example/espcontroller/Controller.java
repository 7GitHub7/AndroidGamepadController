package com.example.espcontroller;

import android.content.Context;
import android.os.Build;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Controller extends View {

    public Controller(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    static public ArrayList<Integer> getGameControllerIds() {
        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();


            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        System.out.println(InputDevice.getDevice(deviceId).getMotionRanges());
                    }

                }
            }
        }
        return gameControllerDeviceIds;
    }



    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Check that the event came from a game controller
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                    InputDevice.SOURCE_JOYSTICK &&
                    event.getAction() == MotionEvent.ACTION_MOVE) {

                // Process all historical movement samples in the batch
                final int historySize = event.getHistorySize();

                // Process the movements starting from the
                // earliest historical position in the batch
//                for (int i = 0; i < historySize; i++) {
//                    // Process the event at historical position i
//                    processJoystickInput(event, i);
//                }

                // Process the current movement sample in the batch (position -1)
//                processJoystickInput(event, -1);
                System.out.println(event.getX());
                System.out.println(event.getY());
                return true;
            }
        }
        return super.onGenericMotionEvent(event);
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
                        case 1:
                            System.out.println(keyCode);

                        default:
                            if (isFireKey(keyCode)) {
                                // Update the ship object to fire lasers

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

}
