/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var sdk = cordova.require("cordova-smartsdk.SmartsdkModule");
var isInitialized = false;
var result;
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },
    
    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
    },
    
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        init();
    }
};

app.initialize();

function init(){
    console.log("init");
    sdk.init(
        function(){
             isInitialized = true;
             console.log("sdk init return with success. Initialization OK.");
        },
        function(error){
             console.log("sdk init return with failure. Initialization KO.");
             console.log(error);
             alert(error);
             isInitialized = false;
        }
    );
}


$( "#capture" ).click(function() {
    console.log("capture")
    if(isInitialized){
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
        console.log(map);
        console.log(JSON.stringify(map));
        sdk.capture(JSON.stringify(map),
        function(results){
            console.log(results);
            result = JSON.parse(results);
            console.log(JSON.stringify(results));
            $("#name").text(result["mapDocument"]["IDENTITY_DOCUMENT"]["fields"]["FIRST_NAMES"]).text();
            $("#img").attr('src', result["mapImageCropped"]["IMAGES_RECTO"]["imageUri"]);
        },
        function(error){
            console.log(error);
            alert(error);
        });
    } else {
        init();
    }
});
