package com.wfl.leyu.handview;

import android.graphics.Rect;
import android.util.Log;

import com.leyu.android.RkHandWriteUtils;
import com.leyu.android.WflExtManager;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class LeyuHandView extends CordovaPlugin {

    private final static String TAG = "LeyuHandView";

    private RkHandWriteUtils.OnResultListener mResultListener = null;
    private Rect mHandWriteRect = null;

    @Override
    public void onResume(boolean multitasking) {
        if (mResultListener != null) {
            Log.i(TAG, "onResume:mResultListener exist!");
            runRkHandwriteProcess(mHandWriteRect, null);
        } else {
            Log.i(TAG, "onResume:mResultListener is null!");
        }
        super.onResume(multitasking);
    }

    @Override
    public void onPause(boolean multitasking) {
        if (mResultListener != null) {
            Log.i(TAG, "onPause:mResultListener exist!");
            exitHandWriteMode(null);
        } else {
            Log.i(TAG, "onPause:mResultListener exist!");
        }
        super.onPause(multitasking);
    }

    @Override
    public void onDestroy() {
        if (mResultListener != null) {
            Log.i(TAG, "onDestroy:mResultListener exist!");
            exitHandWriteMode(null);
            mResultListener = null;
        } else {
            Log.i(TAG, "onDestroy:mResultListener exist!");
        }
        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "execute:"+action);
        // 示例
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }

        //lishunbo@leyu-tech.com add 2020/8/12 for  start
        // 1.提供霸屏禁用按键接口
        if (action.equals("disableKeyEvent")) {
            boolean disabled = args.getBoolean(0);
            this.disableKeyEvent(disabled, callbackContext);
            return true;
        }

        // 2.提供霸屏禁用屏幕触摸接口
        if (action.equals("disableTouchEvent")) {
            boolean disabled = args.getBoolean(0);
            this.disableTouchEvent(disabled, callbackContext);
            return true;
        }

        // 4.提供退出RK专用手写模式接口
        if (action.equals("exitHandWriteMode")) {
            this.exitHandWriteMode(callbackContext);
            return true;
        }
        //lishunbo@leyu-tech.com add 2020/8/12 for  end

        // 5.提供打开RK手写进程接口
        if (action.equals("runRkHandwriteProcess")) {
            JSONObject rectObject = args.getJSONObject(0);
            mHandWriteRect = null;
            if (rectObject != null) {
                mHandWriteRect = new Rect();
                mHandWriteRect.left = rectObject.getInt("left");
                mHandWriteRect.top = rectObject.getInt("top");
                mHandWriteRect.right = rectObject.getInt("right");
                mHandWriteRect.bottom = rectObject.getInt("bottom");
                Log.i(TAG, "runRkHandwriteProcess-left:"+mHandWriteRect.left+"  /top:"+mHandWriteRect.top
                        +"  /right:"+mHandWriteRect.right+"  /bottom:"+mHandWriteRect.bottom);
            } else {
                Log.i(TAG, "runRkHandwriteProcess-no rect");
            }

            this.runRkHandwriteProcess(mHandWriteRect, callbackContext);
            return true;
        }

        // 6.提供清除手写进程笔迹接口
        if (action.equals("clearScreen")) {
            this.clearScreen(callbackContext);
            return true;
        }

        // 7.提供冻屏接口
        if (action.equals("freezeScreen")) {
            this.freezeScreen(callbackContext);
            return true;
        }

        // 8.提供解除冻屏接口
        if (action.equals("unFreezeScreen")) {
            this.unFreezeScreen(callbackContext);
            return true;
        }

        // 9.提供设置画笔宽度接口
        if (action.equals("setPenWidth")) {
            int width = args.getInt(0);
            this.setPenWidth(callbackContext, width);
            return true;
        }

        // 10.提供设置橡皮擦宽度接口
        if (action.equals("setRubberWidth")) {
            int width = args.getInt(0);
            this.setRubberWidth(callbackContext, width);
            return true;
        }

        // 11.提供添加 point 接口
        if (action.equals("addPoint")) {
            //int width = args.getInt(0);
            int penColor = args.getInt(0);
            JSONArray jsonArray = args.getJSONArray(1);
            JSONObject jsonObject;
            for (int i=0; i<jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                Log.i(TAG, "penColor:"+penColor+"  /jsonObject:"+jsonObject.toString());
            }
            //jsonArray.getString()
            //this.setRubberWidth(callbackContext, width);
            return true;
        }
        //lishunbo@leyu-tech.com add 2020/8/12 for  end

        return false;
    }

    // 示例
    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    //lishunbo@leyu-tech.com add 2020/8/12 for  start
    // 1.提供霸屏禁用按键接口
    private void disableKeyEvent(boolean disabled, CallbackContext callbackContext) {
        WflExtManager.getInstance().disableKeyEvent(disabled);
    }

    // 2.提供霸屏禁用屏幕触摸接口
    private void disableTouchEvent(boolean disabled, CallbackContext callbackContext) {
        WflExtManager.getInstance().disableTouchEvent(disabled);
    }

    // 4.提供退出RK专用手写模式接口
    private void exitHandWriteMode(CallbackContext callbackContext) {
        RkHandWriteUtils.getInstance().exitHandWriteMode();
    }

    // 5.提供打开RK专用手写进程接口
    private void runRkHandwriteProcess(Rect rect, final CallbackContext callbackContext) {
        if (callbackContext != null) {
            mResultListener = new RkHandWriteUtils.OnResultListener() {
                @Override
                public void onTouchResult(JSONObject jsonObject) {
                    //callbackContext.success(jsonObject);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            };
        }
        RkHandWriteUtils.getInstance().runRkHandwriteProcess(mResultListener, rect);
    }

    // 6.提供清除手写进程笔迹接口
    private void clearScreen(CallbackContext callbackContext) {
        RkHandWriteUtils.getInstance().clearScreen();
    }

    // 7.提供冻屏接口
    private void freezeScreen(CallbackContext callbackContext) {
        RkHandWriteUtils.getInstance().freezeScreen();
    }

    // 8.提供解除冻屏接口
    private void unFreezeScreen(CallbackContext callbackContext) {
        RkHandWriteUtils.getInstance().unFreezeScreen();
    }

    // 9.提供设置画笔宽度接口
    private void setPenWidth(CallbackContext callbackContext, int width) {
        RkHandWriteUtils.getInstance().setPenWidth(width);
    }

    // 10.提供设置橡皮擦宽度接口
    private void setRubberWidth(CallbackContext callbackContext, int width) {
        RkHandWriteUtils.getInstance().setRubberWidth(width);
    }
    //lishunbo@leyu-tech.com add 2020/8/12 for  end

}
