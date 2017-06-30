package com.wezom.ulcv2.net;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

public abstract class WebSocketChannelHandler implements WebSocketListener {

    private static final int TIMEOUT = 0; //seconds
    protected String TAG;
    protected boolean isConnected;
    private WebSocket webSocket;
    private OkHttpClient client;
    private String url;

    WebSocketChannelHandler(String TAG) {
        this.TAG = TAG;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.v("SOCKET_WS_" + TAG, "onOpen");
        this.webSocket = webSocket;
        isConnected = true;
    }

    @Override
    public void onFailure(IOException e, Response response) throws IllegalStateException {
        Log.v("SOCKET_WS_" + TAG, "onFailure");
        try {
            if (webSocket != null) {
                webSocket.close(1002, e.getMessage());
            }
        } catch (IOException e1) {
            Log.v("SOCKET_WS_" + TAG, "disconnect error " + e1.getMessage());
            //nothing to do
        }
        isConnected = false;
        Log.v("SOCKET_WS_" + TAG, "isConnected = " + isConnected);
    }

    @Override
    public void onPong(Buffer payload) {
        Log.v("SOCKET_WS_" + TAG, "onPong " + payload.toString());
    }

    @Override
    public void onClose(int code, String reason) {
        isConnected = false;
        webSocket = null;

        Log.v("SOCKET_WS_" + TAG, "onClose " + "reason: " + reason);
    }

    public void connect(String url) {
        this.url = url;
        Log.v("SOCKET_WS_" + TAG, "connect " + this.url);
        createSocket(this.url);
        isConnected = true;
    }

    public void disconnect() throws IllegalStateException {
        Log.v("SOCKET_WS_" + TAG, "disconnect()");
        isConnected = false;
        try {
            if (webSocket != null) {
                webSocket.close(1000, "Normal close socket");
            }
        } catch (Exception e) {
            Log.v("SOCKET_WS_" + TAG, "disconnect error " + e.getMessage());
            //nothing to do
        }
    }

    public boolean isConnected() {
        Log.v("SOCKET_WS_" + TAG, "isConnected() = " + isConnected);
        return isConnected;
    }

    public void destroySocket() {
        Log.v("SOCKET_WS_" + TAG, "destroySocket()");
        webSocket = null;
        client = null;
        isConnected = false;
    }

    protected void sendMessage(RequestBody requestBody) throws IllegalStateException {
        try {
            if (webSocket != null) {
                webSocket.sendMessage(requestBody);
            }
        } catch (IOException e) {
            isConnected = false;
            Log.v("SOCKET_WS_" + TAG, "sendSystemMessage() IOException " + e.getMessage());
        }
    }

    protected void sendPing() throws IllegalStateException {
        try {
            if (webSocket != null) {
                webSocket.sendPing(new Buffer().writeUtf8("Hello, I`m," + TAG + ",and I still alive!"));
            }
        } catch (IOException e) {
            isConnected = false;
            Log.v("SOCKET_WS_" + TAG, "sendPing()");
        }
    }

    private void createSocket(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        webSocket = null;
        client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        WebSocketCall webSocketCall = WebSocketCall.create(client, request);
        webSocketCall.enqueue(this);
        Log.v("SOCKET_WS_" + TAG, "WebSocketCall.createSocket(" + url + ")");

    }

// deprecated
//    public void reconnectSocket() {
//        if (client == null) {
//            connect(url);
//        }
//        client = client.newBuilder()
//                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//                .build();
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        WebSocketCall webSocketCall = WebSocketCall.create(client, request);
//        webSocketCall.enqueue(this);
//        Log.v("SOCKET_WS_" + TAG, "reconnectSocket(" + url + ")");
//    }


}
