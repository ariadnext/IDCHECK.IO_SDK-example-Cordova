var exec = require('cordova/exec');

function IdcheckioSdk(){}
IdcheckioSdk.preload = function(extractData){
    exec(function(){}, function(){}, 'IdcheckioSdk', 'preload', [extractData]);
};
IdcheckioSdk.activate = function(licenseFilename, extractData, disableImei, disableAudioForLiveness, environment, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'activate', [licenseFilename, extractData, disableImei, disableAudioForLiveness, environment]);
};
IdcheckioSdk.start = function(parametersMap, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'start', [parametersMap]);
};
IdcheckioSdk.startOnline = function(parametersMap, cisContext, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'startOnline', [parametersMap, cisContext]);
};
IdcheckioSdk.analyze = function(parametersMap, side1ToUpload, side2ToUpload, isOnline, cisContext, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'analyze', [parametersMap, side1ToUpload, side2ToUpload, isOnline, cisContext])
};
module.exports = IdcheckioSdk;
