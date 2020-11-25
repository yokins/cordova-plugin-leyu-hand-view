package com.wfl.leyu.handview;

import android.app.LeyuManager;
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
    private final static int MAX_POINT = 4000;//4000 for 100000,400 for 10000

    //parameters start
    private boolean OPTION_SEND_TOGETHER = false;
    //parameters end

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
            //Log.i(TAG, "penColor:" + penColor);
            //Log.i(TAG, "jsonObject:" + args.toString());
            JSONArray jsonArray = args.getJSONArray(1);
            //Log.i(TAG, "jsonArray.length:" + jsonArray.length());
            //Log.i(TAG, "jsonArray:" + jsonArray.toString());
            JSONObject jsonObject;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                //Log.i(TAG, "penColor:" + penColor + "  /jsonObject:" + jsonObject.toString());

                sb.append(jsonObject.getInt(RkHandWriteUtils.JSON_KEY_LAST_X)).append(",");
                sb.append(jsonObject.getInt(RkHandWriteUtils.JSON_KEY_LAST_Y)).append(",");
                sb.append(jsonObject.getInt(RkHandWriteUtils.JSON_KEY_X)).append(",");
                sb.append(jsonObject.getInt(RkHandWriteUtils.JSON_KEY_Y)).append(",");
                sb.append(jsonObject.getInt(RkHandWriteUtils.JSON_KEY_TOUCH_TYPE)+((penColor == 2) ? 4:0)).append(",");
                sb.append(jsonObject.getInt(RkHandWriteUtils.JSON_KEY_PEN_WIDTH)).append(",");
                if ((i+1) % MAX_POINT == 0) {
                    //break;
                    addPoint(callbackContext, sb.toString());
                    sb.setLength(0);
                }
            }
            if (sb.length() > 0) {
                addPoint(callbackContext, sb.toString());
            }
            //jsonArray.getString()
            //this.setRubberWidth(callbackContext, width);
            return true;
        }

        // 12.提供设置一次性传点开关接口
        if (action.equals("setSendTogetherOn")) {
            OPTION_SEND_TOGETHER = args.getBoolean(0);
            return true;
        }

        // 13.提供禁止和启用画线接口
        if (action.equals("setDisableTouch")) {
            boolean disabled = args.getBoolean(0);
            this.setDisableTouch(callbackContext, disabled);
            return true;
        }

        // 14.提供全屏刷新接口
        if (action.equals("doAppFlash")) {
            int mode = args.getInt(0);
            this.doAppFlash(mode, callbackContext);
            return true;
        }

        // 15.提供禁止和启用橡皮擦接口
        if (action.equals("setDisableRubber")) {
            boolean disabled = args.getBoolean(0);
            this.setDisableTouch(callbackContext, disabled);
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
    private JSONArray jsonArray = new JSONArray();

    private void runRkHandwriteProcess(Rect rect, final CallbackContext callbackContext) {
        if (callbackContext != null) {
            //final JSONArray jsonArray = new JSONArray();
            mResultListener = new RkHandWriteUtils.OnResultListener() {
                @Override
                public void onTouchResult(JSONObject jsonObject) {
                    //callbackContext.success(jsonObject);
                    if (OPTION_SEND_TOGETHER) {
                        //jsonArray.put(jsonObject);
                        if ("1".equals(jsonObject.optString(RkHandWriteUtils.JSON_KEY_TOUCH_UP, "0"))) {
                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonArray);
                            pluginResult.setKeepCallback(true);
                            callbackContext.sendPluginResult(pluginResult);
                            jsonArray = new JSONArray();
                        } else {
                            jsonArray.put(jsonObject);
                        }
                    } else {
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    }
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

    // 11.提供添加 point 接口
    private void addPoint(CallbackContext callbackContext, String pointInfo) {
        RkHandWriteUtils.getInstance().addPoint(pointInfo);
    }

    // 13.提供禁止和启用画线接口
    private void setDisableTouch(CallbackContext callbackContext, boolean disabled) {
        RkHandWriteUtils.getInstance().setDisableTouch(disabled);
    }

    // 14.提供全屏刷新接口
    private void doAppFlash(int mode, CallbackContext callbackContext) {
        LeyuManager.getInstance().doAppFlash(mode);
    }

    // 15.提供禁止和启用橡皮擦功能接口
    private void setDisableRubber(CallbackContext callbackContext, boolean disabled) {
        RkHandWriteUtils.getInstance().setDisableRubber(disabled);
    }
    //lishunbo@leyu-tech.com add 2020/8/12 for  end

}
