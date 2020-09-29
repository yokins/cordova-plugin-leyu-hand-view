var exec = require("cordova/exec");

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, "LeyuHandView", "coolMethod", [arg0]);
};

// 1.提供霸屏禁用按键接口
exports.disableKeyEvent = function (arg0, success, error) {
    exec(success, error, "LeyuHandView", "disableKeyEvent", [arg0]);
};

// 2.提供霸屏禁用屏幕触摸接口
exports.disableTouchEvent = function (arg0, success, error) {
    exec(success, error, "LeyuHandView", "disableTouchEvent", [arg0]);
};

// 3.提供进入RK专用手写模式接口
exports.enterHandWriteMode = function (arg0, success, error) {
    console.log(arg0);
    exec(success, error, "LeyuHandView", "enterHandWriteMode", [arg0]);
};

exports.exitHandWriteMode = function (success, error) {
    exec(success, error, "LeyuHandView", "exitHandWriteMode");
};

// 3.提供打开RK手写进程接口
exports.runRkHandwriteProcess = function (arg0, success, error) {
    console.log(arg0);
    exec(success, error, "LeyuHandView", "runRkHandwriteProcess", [arg0]);
};
// 提供清除手写进程笔迹接口;
exports.clearScreen = function (success, error) {
    exec(success, error, "LeyuHandView", "clearScreen");
};
// 提供冻屏接口;
exports.freezeScreen = function (success, error) {
    exec(success, error, "LeyuHandView", "freezeScreen");
};
// 提供解除冻屏接口;
exports.unFreezeScreen = function (success, error) {
    exec(success, error, "LeyuHandView", "unFreezeScreen");
};

// 设置画笔
exports.setPenWidth = function (arg0, success, error) {
    exec(success, error, "LeyuHandView", "setPenWidth", [arg0]);
};

// 设置橡皮檫
exports.setRubberWidth = function (arg0, success, error) {
    exec(success, error, "LeyuHandView", "setRubberWidth", [arg0]);
};

exports.addPoint = function (arg0, success, error) {
    exec(success, error, "LeyuHandView", "addPoint", [arg0]);
};
