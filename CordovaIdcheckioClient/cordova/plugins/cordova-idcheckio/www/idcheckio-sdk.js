var exec = require('cordova/exec');

function IdcheckioSdk(){}
IdcheckioSdk.preload = function(extractData){
    exec(function(){}, function(){}, 'IdcheckioSdk', 'preload', [extractData]);
};
IdcheckioSdk.activate = function(licenseFilename, extractData, disableImei, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'activate', [licenseFilename, extractData, disableImei]);
};
IdcheckioSdk.start = function(parametersMap, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'start', [parametersMap]);
};
IdcheckioSdk.startOnline = function(parametersMap, licenseFilename, cisContext, disableImei, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'startOnline', [parametersMap, licenseFilename, cisContext, disableImei]);
};
module.exports = IdcheckioSdk;
