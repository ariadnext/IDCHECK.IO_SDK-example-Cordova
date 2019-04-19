# AriadNEXT's SmartSDK - Cordova plugin

## Prerequisites
Before started, please make sure you've installed :
- **plugman** - `npm install -g plugman

## Getting started

> The SmartSDK's cordova plugin is provided locally, so you'll need to have it on your local machine, and *install* it using plugman to add it to your project.

#### Install the plugin in your project
- Android
```shell
$ plugman install --platform android PATH_TO_YOUR_PROJECT_FOLDER/platforms/android/ --plugin PATH_TO_PLUGIN_FOLDER/
```
- iOS
```shell
$ plugman install --platform ios PATH_TO_YOUR_PROJECT_FOLDER/platforms/ios/ --plugin PATH_TO_PLUGIN_FOLDER/
```

## Platform's specific configuration

### iOS

#### 📱 &nbsp; Application's configuration (your project)

1. From your Finder, add the `SmartsdkKit.framework` in your iOS project folder 
2. Add the `SmartsdkKit.framework` to your iOS app's target.
- Via Xcode's project navigator, select your target, then go to `General` ➜ `Embedded Binaries` ➜ `+`, and select `SmartsdkKit.framework`
- *If it's not visible in the list, select `Add Other...` and go find it on your disk.*
3. Add your SDK's license file in your app's source folder (containing your app's `Info.plist` and `AppDelegate{.h,.m,.swift}`):
```
PATH_TO_YOUR_PROJECT_FOLDER/platforms/ios/SOURCE_FOLDER/licence.axt
```
- ⚠️  &nbsp; Please be sure to rename the license file "**licence.axt**" ⚠️
- ✅ &nbsp; Don't forget to add the licence file to your app bundle (check it in project navigator: `APP_TARGET` ➜ `Build Phases` ➜ `Copy Bundle Resources` ➜ Add `licence.axt` if not present in the list ).
4. Run your project (`Cmd+R`), you're done ! 🎉

#### Android

1. From your Finder, add the `smartsdk-release.aar` in your libs folder :
```
PATH_TO_YOUR_PROJECT_FOLDER/platforms/android/app/libs/smartsdk-release.aar'
```
2. In your `android/app/build.gradle` file :
- Insert the following lines inside the repositories block :
```
flatDir{
    dirs 'libs'
}
```
- Insert the following lines inside the dependencies block :
```
implementation (name: 'smartsdk-release', ext: 'aar')
```
3. Add your SDK's license file in your assets folder :
```
PATH_TO_YOUR_PROJECT_FOLDER/platforms/android/app/src/main/assets/licence.axt
```
    - ⚠️  &nbsp; Please be sure to rename the license file "**licence.axt**" ⚠️
    - ✅  &nbsp; Don't forget to change your signingConfig with the certificate you give us to create the licence. I you do not, the activation will fail ! ⚠️
4. Run your project, you're done ! 🎉

## Usage

1. In your html, add the following script : 
```
<script type="text/javascript" src="plugins/cordova-smartsdk/www/cordova-smartsdk.js"></script>
```
2. In your js, to import the module use : 
```
var sdk = cordova.require("cordova-smartsdk.SmartsdkModule")
```
3. Before capturing any document, you need to initialize the sdk. To do so, you can use the method `init()` of the `SmartsdkModule` :
```javascript
function init(){
    sdk.init(
        function(){
            console.log("sdk init return with success. Initialization OK.");
        },
        function(error){
            console.log("sdk init return with failure. Initialization KO.");
            alert(error);
        }
    );
}
```
4. To start the capture of a document, you have to call the capture method with a map of parameters. After the capture you receive a string that can be parse into a json object containing the capture results.
```javascript
function() {
    var map = {
        'ExtractData': 'true',
        'UseHD': 'false',
        'DisplayResult': 'true',
        'UseFrontCamera': 'false',
        'ScanBothSide': 'true',
        'DocType':'ID',
        'ExtractRequirement':'MRZ_FOUND',
        'ExtraParameters': {
            'AXT_MANUAL_BUTTON_TIMER': 10,
            'AXT_DISPLAY_CROPPED_IMAGE': 'true'
        }
    }
    sdk.capture(JSON.stringify(map),
        function(results){
            result = JSON.parse(results);
            $("#name").text(result["mapDocument"]["IDENTITY_DOCUMENT"]["fields"]["FIRST_NAMES"]).text();
            $("#img").attr('src', result["mapImageCropped"]["IMAGES_RECTO"]["imageUri"]);
        },
        function(error){
            console.log(error);
            alert(error);
        }
    );
}
```
