#IDCheckio Sdk - Cordova plugin

## Prerequisites
Before started, please make sure you've installed :
- **plugman** - `npm install -g plugman`

## Getting started

> The plugin is provided locally, so you'll need to have it on your local machine, and *install* it using plugman to add it to your project.

#### Install the plugin in your project
- Android
```shell
$ plugman install --platform android --project PATH_TO_YOUR_PROJECT_FOLDER/platforms/android/ --plugin PATH_TO_PLUGIN_FOLDER
```

- iOS
```shell
$ plugman install --platform ios --project PATH_TO_YOUR_PROJECT_FOLDER/platforms/ios/ --plugin PATH_TO_PLUGIN_FOLDER
```

## Platform's specific configuration

### ios

1. The min ios version supported by the sdk is 10.0, to the min ios version of your project you can add this line in your config.xml: `<preference name="deployment-target" value="10.0" />`
2. Add your SDK's license file in your app's source folder (containing your app's `Info.plist`):
```
PATH_TO_YOUR_PROJECT_FOLDER/platforms/ios/SOURCE_FOLDER/YOUR_LICENCE_NAME.axt
```
- ✅ &nbsp; Don't forget to add the licence file to your app bundle (check it in project navigator: `APP_TARGET` ➜ `Build Phases` ➜ `Copy Bundle Resources` ➜ Add `YOUR_LICENCE_NAME.axt` if not present in the list ).
3. Go to YOUR_TARGET > Build Settings > Swift Language Version > set it to at least 4.2.
4. Go to YOUR_TARGET > Build Settings > Enable Bitcode > set it to Yes.
5. Run your project (`Cmd+R`), you're done ! 🎉

### Android

1. In the file `PATH_TO_PLUGIN_FOLDER/android/sdk.gradle`, you have to replace `$YOUR_USERNAME` and `$YOUR_PASSWORD` with the credentials we give to access our external nexus.
2. Add your SDK's licence file in your assets folder :
```
PATH_TO_YOUR_PROJECT_FOLDER/platforms/android/app/src/main/assets/YOUR_LICENCE_NAME.axt
```
    - ✅  &nbsp; Don't forget to change your signingConfig with the certificate you give us to create the licence. If you do not, the activation will fail ! ⚠️
3. Run your project, you're done ! 🎉

## Usage

1. In your html, add the following script :
```
<script type="text/javascript" src="plugins/cordova-idcheckio/www/idcheckio-sdk.js"></script>
```
2. In your js, to import the module use :
```
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
    - ✅  &nbsp; To learn more informations on those methods and theirs parameters. Please refers to the official IDCheck.io sdk documentation.  ⚠️
