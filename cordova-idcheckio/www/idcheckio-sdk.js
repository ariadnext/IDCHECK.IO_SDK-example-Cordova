var exec = require('cordova/exec');

function IdcheckioSdk(){}
IdcheckioSdk.preload = function(extractData){
    exec(function(){}, function(){}, 'IdcheckioSdk', 'preload', [extractData]);
};
IdcheckioSdk.activate = function(licenseFilename, extractData, disableAudioForLiveness, environment, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'activate', [licenseFilename, extractData, disableAudioForLiveness, environment]);
};
IdcheckioSdk.start = function(parametersMap, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'start', [parametersMap]);
};
IdcheckioSdk.startOnline = function(parametersMap, onlineContext, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'startOnline', [parametersMap, onlineContext]);
};
IdcheckioSdk.analyze = function(parametersMap, side1ToUpload, side2ToUpload, isOnline, onlineContext, onSuccess, onFail){
    exec(onSuccess, onFail, 'IdcheckioSdk', 'analyze', [parametersMap, side1ToUpload, side2ToUpload, isOnline, onlineContext])
};
module.exports = IdcheckioSdk;