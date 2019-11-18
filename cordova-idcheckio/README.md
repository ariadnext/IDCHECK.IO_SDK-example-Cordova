# IDCheckio Sdk - Cordova plugin

## Prerequisites
Before getting started, please make sure you've installed the latest version of [`plugman`](https://www.npmjs.com/package/plugman) :
- **plugman** - `npm install -g plugman`

## Getting started

The plugin is provided locally, so you'll need to have it on your local machine, and *install* it using plugman to add it to your project.

#### Install the plugin in your project
> ⚠️ &nbsp; You have to provide **absolute** paths to project and plugin folders for `plugman` to effectively install the plugin in your project.  \
> ⚠️ &nbsp; You have to launch the following command **from the PROJECT_FOLDER**, even if you use absolute paths, for `plugman` to effectively install the plugin in your project.

- Android
```shell
$ plugman install --platform android --project ABSOLUTE_PATH_TO_YOUR_PROJECT_FOLDER/platforms/android/ --plugin ABSOLUTE_PATH_TO_PLUGIN_FOLDER
```

- iOS
```shell
$ plugman install --platform ios --project ABSOLUTE_PATH_TO_YOUR_PROJECT_FOLDER/platforms/ios/ --plugin ABSOLUTE_PATH_TO_PLUGIN_FOLDER
```

## Platform's specific configuration

### ios

1. Before starting, to download the sdk dependency, you will need to have git lfs configured in your project. If you have not, use : `git lfs install`
2. The min ios version supported by the sdk is 10.0, to the min ios version of your project you can add this line in your config.xml: `<preference name="deployment-target" value="10.0" />`
3. In the IdcheckioSdk.m file, you will need to change the following header with your project name : `#import "YOUR_PROJECT_NAME-Swift.h"`
4. Add your SDK's license file in your app's source folder (containing your app's `Info.plist`):
  ```
  PATH_TO_YOUR_PROJECT_FOLDER/platforms/ios/SOURCE_FOLDER/YOUR_LICENCE_NAME.axt
  ```
  > ✅ &nbsp; Don't forget to add the licence file to your app bundle (check it in project navigator: `APP_TARGET` ➜ `Build Phases` ➜ `Copy Bundle Resources` ➜ Add `YOUR_LICENCE_NAME.axt` if not present in the list ).
5. Go to YOUR_TARGET > Build Settings > Swift Language Version > set it to at least 4.2.
6. Go to YOUR_TARGET > Build Settings > Enable Bitcode > set it to Yes.
7. Run your project (`Cmd+R`), you're done !  &nbsp; 🎉

### Android

1. In order to access our external nexus for retrieving the latest version of the IDCheck.io SDK, you have to update the gradle file from the **plugin** project `PATH_TO_PLUGIN_FOLDER/android/sdk.gradle`, and replace `$YOUR_USERNAME` and `$YOUR_PASSWORD` with the credentials given by our support team.
2. For the SDK activation to be successful, you must provide a valide license. Add your SDK's licence file in the *assets* folder of the **main** project :
  ```
  PATH_TO_YOUR_PROJECT_FOLDER/platforms/android/app/src/main/assets/YOUR_LICENCE_NAME.axt
  ```
  > ✅ &nbsp; Don't forget to update the *signingConfig* of the project's gradle file with the certificate for which you gave the fingerprint when our team created the licence. If the final application's fingerprint is different, the activation will fail ! &nbsp; ⚠️
3. Run your project (`cordova run android`), you're done ! &nbsp; 🎉

## Usage

1. In your html, add the following script :
```html
<script type="text/javascript" src="plugins/cordova-idcheckio/www/idcheckio-sdk.js"></script>
```
2. In your js, to import the module use :
```javascript
var sdk = cordova.require("cordova-idcheckio.IdcheckioSdk");
```
3. Before doing any call to the sdk, you can use the `preload()` method. It will accelerate the future call the to the capture process. You won't receive any callback when calling this method.
```javascript
function preload(){
    sdk.preload("true");
}
```
4. Before capturing any document, you need to activate the licence. To do so, you have to use the `activate()` method.
```javascript
function activate(){
    sdk.activate(
        "license",
        "true",
        "false",
        function(){
            this.isInitialized = true;
        },
        function(error){
            this.isInitialized = false;
            alert(error);
        }
    );
}
```
5. To start the capture of a document, you have to call the start method with your wanted parameters. You will receive the result in a string that can be parse into a json object.
```javascript
function start(){
    if(isInitialized){
        var map = {
            'DocumentType': 'ID',
            'Orientation': 'PORTRAIT',
            'ConfirmType': 'DATA_OR_PICTURE',
            'Side1Extraction': {
                'DataRequirement': 'DECODED',
                'FaceDetection': 'ENABLED'
            },
           'ScanBothSides': 'ENABLED'
        };
        sdk.start(
            JSON.stringify(map),
            function(results){
                result = JSON.parse(results);
                $("#name").text("SDK Success").text();
            },
            function(error){
                alert(error);
            }
        );
    } else {
        alert("The sdk is not activated.");
    }
}
```
6. To start an online capture of a document, you have the method the `startOnline()` method. You will receive the result in a string that can be parse into a json object.
```javascript
function startOnline(){
    var map = {
        'DocumentType': 'ID',
        'Orientation': 'LANDSCAPE',
        'ConfirmType': 'DATA_OR_PICTURE',
        'Side1Extraction': {
            'DataRequirement': 'DECODED',
            'FaceDetection': 'ENABLED'
        },
       'ScanBothSides': 'ENABLED'
    };
    var cisContext = {};
    sdk.startOnline(
        JSON.stringify(map),
        "license",
        JSON.stringify(cisContext),
        "false",
        function(results){
            result = JSON.parse(results);
            $("#name").text("ID Scan Success").text();
        },
        function(error){
            alert(error);
        }
    );
}
```    
  > ✅ &nbsp; To learn more informations on those methods and theirs parameters. Please refer to the official [IDCheck.io sdk](https://support.ariadnext.com) documentation.
