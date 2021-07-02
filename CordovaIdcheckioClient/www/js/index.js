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
import * as Dictionnary from './dictionnary.js';
var AdvancedImagePicker = cordova.require("cordova-plugin-advanced-imagepicker.AdvancedImagePicker");
var Idcheckio = cordova.require("cordova-idcheckio.IdcheckioSdk");
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
        $("#capture").prop("disabled",true);
        $("#capture").css("background-color", "grey");
        $("#activate").css("background-color", "#0000de");
        $("#activate").css("color", "white");
        $("#activate").css("text-shadow", "none");
    }
};

app.initialize();

function preload(){
    Idcheckio.preload("true");
}

function activate(){
    Idcheckio.activate("license", true, true, "DEMO",
        function(){
            isInitialized = true;
            $("#activation").text("SDK activated!").text();
            $("#activate").text("SDK already activated").text();
            $("#capture").text("Capture Document").text();
            $("#activate").prop("disabled",true);
            $("#capture").prop("disabled",false);
            $("#activate").css("background-color", "grey");
            $("#capture").css("background-color", "#0000de");
            $("#capture").css("color", "white");
            $("#capture").css("text-shadow", "none");
        },
        function(error){
            isInitialized = false;
            console.error(error)
        }
    );
}

function capture(){
    let key = $( "#params" ).val();
    let param = Dictionnary.paramsList[key]
    //Retrieve the online context from last session, it will be used as parameter for the next session
    let onlineContext
    if(result != null && result.onlineContext != null) {
        onlineContext = result.onlineContext
    } else { 
        onlineContext = null 
    } 
    if(param.isUpload) {
        analyze(param, onlineContext)
    } else if (param.isOnline){
        Idcheckio.startOnline(
            param.params,
            onlineContext,
            function(results){
                console.log(results)
                result = JSON.parse(results)
                updateResult()
            },
            function(error){
                console.error(error)
            }
        )
    } else {
        Idcheckio.start(
            param.params,
            function(results){
                console.log(results)
                result = JSON.parse(results)
                updateResult()
            },
            function(error){
                console.error(error)
            }
        )
    }
}

function analyze(param, onlineContext){
    window.AdvancedImagePicker.present({
        max: 1
    }, function(success) {
        Idcheckio.analyze(param.params, success[0].src, "", true, onlineContext,
            function(results){
                console.log(results)
                result = JSON.parse(results)
                updateResult()
            },
            function(error){
                console.error(error)
            }
        )
    }, function (error) {
        console.error(error);
    });
}

function updateResult(){
    var resultText
    if(result != null){
        if(result.document != null && result.document.type == "IdentityDocument"){
            resultText = "Howdy".concat(' ', result.document.fields.firstNames.value.split(' ')[0]).concat(' ', result.document.fields.lastNames.value).concat('', "!")
        } else {
            resultText = "Capture OK"
        }
    } else {
        resultText = "Please first scan an ID"
    }
    $("#result").text(resultText).text();
}

$( "#activate" ).click(function(){
    activate();
})

$( "#capture" ).click(function() {
    capture();
})