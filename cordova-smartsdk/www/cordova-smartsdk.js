var exec = require('cordova/exec');

function SmartsdkModule(){}
SmartsdkModule.init = function(onSuccess, onFail){
  exec(onSuccess, onFail, 'SmartsdkModule', 'init', []);	
};
SmartsdkModule.capture = function(content, onSuccess, onFail){
  exec(onSuccess, onFail, 'SmartsdkModule', 'capture', [content]);
};
module.exports = SmartsdkModule;
