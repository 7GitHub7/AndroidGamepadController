package com.example.espcontroller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;

public class UDP_Client
{

    private InetAddress IPAddress = null;
    private String message = "Hello Android!" ;
    private AsyncTask<Void, Void, Void> async_cient;
    public byte[] Message;


    @SuppressLint("NewApi")
    public void NachrichtSenden()
    {
        async_cient = new AsyncTask<Void, Void, Void>()
        {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... params)
            {
                DatagramSocket ds = null;

                try
                {
                    byte[] ipAddr = new byte[]{ (byte) 192, (byte) 168,43, (byte) 194};
                    InetAddress addr = InetAddress.getByAddress(ipAddr);
                    ds = new DatagramSocket(4210);
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message, Message.length, addr, 4210);
                    ds.setBroadcast(true);
                    ds.send(dp);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        if (Build.VERSION.SDK_INT >= 11) async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }
}