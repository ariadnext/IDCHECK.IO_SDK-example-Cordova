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
var sdk = cordova.require("cordova-idcheckio.IdcheckioSdk");
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
        console.log('Received Event: ' + id);
        preload();
    }
};

app.initialize();

function preload(){
    console.log("preload");
    sdk.preload("true");
}

function activate(){
    console.log("activate");
    sdk.activate(
        "license",
        "true",
        "false",
        "true",
        "DEMO",
        function(){
            this.isInitialized = true;
            console.log("Activation done with success.");
            alert("Activation done with success.");
        },
        function(error){
            this.isInitialized = false;
            console.log("Activation done with error.");
            alert(error);
        }
    );
}

function start(){
    console.log("start");
    if(isInitialized){
        var map = {
            'DocumentType': 'ID',
            'Orientation': 'PORTRAIT',
            'ConfirmType': 'DATA_OR_PICTURE',
            'Side1Extraction': {
                'DataRequirement': 'DECODED',
                'FaceDetection': 'ENABLED'
            },
            'ScanBothSides': 'ENABLED',
            'ExtraParams': {
                'Language': "fr",
                'AdjustCrop': 'true'
            }
        };
        sdk.start(
            JSON.stringify(map),
            function(results){
                console.log(results);
                alert(results);
                result = JSON.parse(results);
                console.log(JSON.stringify(results));
                $("#name").text("SDK Success").text();
            },
            function(error){
                console.log(error);
                alert(error);
            }
        );
    } else {
        alert("The sdk is not activated.");
    }
}

function startOnline(){
    console.log("startOnline");
        var map = {
            'DocumentType': 'ID',
            'Orientation': 'LANDSCAPE',
            'ConfirmType': 'DATA_OR_PICTURE',
            'Side1Extraction': {
                'DataRequirement': 'DECODED',
                'FaceDetection': 'ENABLED'
            },
           'ScanBothSides': 'ENABLED',
           'ExtraParams': {
                'SdkEnvironment': "DEMO"
            }
        };
        var cisContext = {};
        sdk.startOnline(
            JSON.stringify(map),
            JSON.stringify(cisContext),
            function(results){
                console.log(results);
                alert(results);
                result = JSON.parse(results);
                console.log(JSON.stringify(results));
                $("#name").text("ID Scan Success").text();
            },
            function(error){
                console.log(error);
                alert(error);
            }
        );
}

function startLiveness(){
   console.log("startLiveness");
   var map = {
        'DocumentType': 'LIVENESS',
        'Orientation': 'PORTRAIT',
        'ExtraParams': {
             'SdkEnvironment': "DEMO"
         }
    };
    var cisContext = {
        'referenceDocUid': result.documentUid,
        'referenceTaskUid': result.taskUid,
        'folderUid': result.folderUid
    };
    sdk.startOnline(
        JSON.stringify(map),
        JSON.stringify(cisContext),
        function(results){
            console.log(results);
            alert(results);
            result = JSON.parse(results);
            console.log(JSON.stringify(results));
            $("#name").text("ID Scan Success").text();
        },
        function(error){
            console.log(error);
            alert(error);
        }
    );
}

function analyze(){
    console.log("analyze");
    var map = {
        'DocumentType': 'ID',
        'Side1Extraction': {
            'DataRequirement': 'DECODED',
            'FaceDetection': 'ENABLED'
        },
        'ExtraParams': {
             'SdkEnvironment': "DEMO"
         }
    };
    var cisContext = {};
    window.imagePicker.getPictures(
        function(results) {
            sdk.analyze(
                JSON.stringify(map),
                results[0],
                "",
                "true",
                JSON.stringify(cisContext),
                function(results){
                    console.log(results);
                    alert(results);
                    result = JSON.parse(results);
                    console.log(JSON.stringify(results));
                    $("#name").text("ID Scan Success").text();
                },
                function(error){
                    console.log(error);
                    alert(error);
                }
            );
	      }, function (error) {
		        console.log('Error: ' + error);
	      }, {
		        maximumImagesCount: 1
	      }
    );
}

$( "#activate" ).click(function(){
    console.log("activate");
    activate();
})

$( "#start" ).click(function() {
    console.log("start");
    start();
})

$( "#startOnline" ).click(function() {
    console.log("startOnline");
    startOnline();
})

$( "#startLiveness" ).click(function() {
    console.log("startLiveness");
    startLiveness();
})

$( "#analyze" ).click(function() {
    console.log("analyze");
    analyze();
})
