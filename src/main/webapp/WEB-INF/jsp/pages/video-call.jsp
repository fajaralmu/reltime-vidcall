<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>

<div class="content" onload="initLiveStream()">
	<h2>Video Call With</h2>
	<h3>Partner ID: ${partnerId }</h3>
	<a href="<spring:url value="/admin/home" />">Back</a>

	<h2>Live Streaming</h2>
	<p>Stream ID: ${registeredRequest.requestId}</p>
	<div id="live-wrapper"
		style="display: grid; grid-template-columns: 47% 47%">
		<div class="camera"
			style="padding: 20px; border: solid 1px green; text-align: center">
			<h2>You</h2>
			<img id="my-capture" height="350" width="350" />
			<p></p>
			<div>
				<button id="btn-terminate" class="btn btn-danger btn-sm"
					onClick="terminate()">Terminate</button>
				<button id="btn-pause" class="btn btn-info btn-sm"
					onClick="pauseOrContinue()">Pause</button>
					<p>Audio Playing: <span id="audio-play-info">False</span></p>
				<button id="btn-play-audio" class="btn btn-info btn-sm"
					onClick="playAudio()">Play Audio</button>
				<button id="btn-stop-audio" class="btn btn-info btn-sm"
					onClick="stopAudio()">Stop And Send Audio</button>
			</div>
			<div style="display: none">
				<canvas id="canvas"> </canvas>
			</div>
			<p>Preview</p>
			<video controls id="video">Video stream not available.
			</video>
		</div>
		<div class="output-receiver"
			style="padding: 20px; border: solid 1px green; text-align: center;">
			<h2>
				Partner <small id="partner-info">Online:
					${partnerInfo.active }</small>
			</h2>
			<img width="350" height="350" id="photo-receiver"
				alt="The screen RECEIVER will appear in this box." />
		</div>
	</div>

	<hr />
	<h3>Audio</h3>
	
	<p>Duration: <span id="duration-info"></span></p>
	<audio autoplay="autoplay"  controls="controls" id="audio"></audio>
	<p id="info-audio"></p>
</div>
<script type="text/javascript">

var paused = false;
var video;
var canvas;
var photoReceiver;
var myCapture;
var terminated = false;
var receiver = "${partnerId}";
var latestImageResponse = {};
var width = 90;
var height = 90;
const theCanvas = document.createElement("canvas");
var btnTerminate = _byId("btn-terminate");
var btnPause = _byId("btn-pause");
var partnerInfo = _byId("partner-info");
//AUDIO
var audioContext; 
var mediaSource;
var analyser;
var mySoundData;
var mediaRecorder;
var chunks = [];
var _blob;
var audio = _byId("audio");
var base64Datas = new Array();
var audioMetadataLoaded = false;
var durationInfo = _byId("duration-info");  

function initVideo(){
	  this.video.onloadedmetadata = function(e) {
      	video.play();
      	//_class.video.muted = true;
  	};
}

function init () {
	const _class = this;   
    window.navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then(function (stream) {
        	 console.debug("START getUserMedia");
        	 
        	 //------ video
            _class.video.srcObject = stream; 
            _class.initVideo();
             
            //------- analyzer
            const audioCtx = new AudioContext();
            const source = audioCtx.createMediaStreamSource(stream);
            const analyser = audioCtx.createAnalyser();
        	source.connect(analyser);
        	analyser.connect(audioCtx.destination); 
            _class.initAudio(source, audioCtx, analyser);
             
            //------- media recorder
            const mediaRecorder = new MediaRecorder(stream);  
			_class.initMediaRecorder(mediaRecorder);
			 
            console.debug("END getUserMedia"); 
        })
        .catch(function (err) {
            //console.log("An error occurred: " + err);
        });
   
    this.video.addEventListener('canplay', function (ev) { 
			_class.updateVideoDom();  
         
    }, false);  

    this.clearphoto();
}  

function updateVideoDom(){
	if (this.streaming) { 
		return;
		
	}
	this.height = this.video.videoHeight /  (this.video.videoWidth / this.width);
	this.video.setAttribute('width', this.width);
	this.video.setAttribute('height', this.height);
	this.canvas.setAttribute('width', this.width );
	this.canvas.setAttribute('height', this.height );
	this.streaming = true; 
}

function initMediaRecorder(_mediaRecorder){
	console.debug("Will init media recorder");
	
	this.mediaRecorder = _mediaRecorder;
	mediaRecorder.onstart = function(e){ 
    	setAudioInfo("true");
    }
	this.mediaRecorder.ondataavailable = function(e) {
		//console.debug("ondataavailable");
	      chunks.push(e.data);
	   }
	this.mediaRecorder.onstop = function(e){ 
		_blob = new Blob(chunks, { 'type' : 'audio/ogg; codecs=opus' }); 
	  	chunks = []; 
	    setAudioInfo("False");
	   	processAudioData(_blob);  
	}
	
	console.debug("End init media recorder");
	 
	console.debug("INIT MEDIA RECORDER END");
}

function processAudioData(_blob){
 	if(this.paused){
 		return;
 	}
	const _class = this;
	 
	_class.blobToBase64(_blob, function(base64data){ 
		_class.sendAudio(base64data);
		_class.addBase64Data(base64data); 
	}); 
}

function sendAudio(base64data){
	if(this.sendingVideo == true || this.terminated || this.paused){
        return;
    }
	//this.sendingVideo = true;   
	//console.info("Sending video at ", new Date().toString(), " length: ", imageData.length);
	const requestObject =  {
			partnerId : "${partnerId}",
			originId : "${registeredRequest.requestId}",
			audioData : base64data
		};
	console.info("Send Audio Data");
	const audioSent = sendToWebsocket("/app/audiostream", requestObject);
	 
}
 

function blobToBase64(blob, onloadCallback){ 
	 
	 var reader = new FileReader();
	 reader.readAsDataURL(blob); 
	 reader.onloadend = function() { 
	     var base64data = reader.result;                
	     onloadCallback(base64data);
	 }
}

function initAudio(_mediaSource, _audioContext, _analyser){ 
	audioContext = _audioContext;
	mediaSource = _mediaSource;
	analyser = _analyser;   
}

function getSoundData() {
	   var sample = new Float32Array(analyser.frequencyBinCount);
	   analyser.getFloatFrequencyData(sample); 
	  /*  console.debug("analyser.frequencyBinCount: ",analyser.frequencyBinCount);
	   console.debug("soundData: ",sample); */
	   return sample;
	}

function terminate (){
    this.terminated = true;
    
    btnTerminate.innerHTML = "Reload to continue";
    btnTerminate.setAttribute("class", "btn btn-info btn-sm");
    btnTerminate.onclick = function(){
    	window.location.reload();
    }
}

function pauseOrContinue(){
	paused = !paused;
	if(paused){
		btnPause.innerHTML = "Continue";
	}else{
		btnPause.innerHTML = "Pause";
	}
}

function setSendingVideoFalse () {
   this.sendingVideo = false;
}
 

function sendVideoImage(imageData ){
	if(this.sendingVideo == true || this.terminated || this.paused){
	        return;
	    }
	this.sendingVideo = true;   
	//console.info("Sending video at ", new Date().toString(), " length: ", imageData.length);
	const requestObject =  {
			partnerId : "${partnerId}",
			originId : "${registeredRequest.requestId}",
			imageData : imageData
		};
	
	const imageSent = sendToWebsocket("/app/stream", requestObject);
	if(!imageSent){
		this.sendingVideo = false
	}
	
	myCapture.setAttribute("src", imageData);
}

function handleAudioStream(response){
//	console.debug("handleAudioStream");
	if(response.code == "00"){
    	partnerInfo.innerHTML = "Online: True "+ (new Date().getMilliseconds());
    	playAudioByBase64Data(response.audioData);
         
        _byId("info-audio").innerHTML = response.audioData;
    }else{
    	partnerInfo.innerHTML = "Online: False";
    } 
}

function addBase64Data(audioData){
	base64Datas.push(audioData);
}

function clearBase64Data(){
	base64Datas = new Array();
}

var audioIndex = 0;

function playAllAudioData(){
	for (var i = 0; i < base64Datas.length; i++) {
		playAudioByBase64Data(base64Datas[i]);
	} 
}

function playAllAudioDatav2(_audioIndex){
	const theAudioIndex = _audioIndex ? _audioIndex : 0; 
	
	audio.src = base64Datas[_audioIndex];
	const _class = this;
	/* console.debug("base64Datas[audioIndex]: ",base64Datas[audioIndex]);
	console.debug("audio.src: ", audio.src); */
	
	audio.onloadedmetadata = function(e){
		console.debug("on loaded metadata ", _audioIndex); 
		_class.audio.play(); 
	}
	audio.onended = function(e){
		console.debug("on ended");
		_class.audioIndex = theAudioIndex + 1;
		if(theAudioIndex >= _class.base64Datas.length - 1){
			_class.audioIndex = 0;
			return;
		}
		_class.playAllAudioDatav2(_class.audioIndex);
	}
	
	audio.onerror = audio.onended;
}

function playAudioByBase64Data(audioData){
	if(audioMetadataLoaded == true){
		return;
	}
	audioMetadataLoaded = true;
	audio.src = audioData;
	audio.onloadedmetadata = function(e){
		
		audio.play(); 
		
	}
	/* audio.onplay = function(e){
		
	} */
	
	audio.onended = function(e){
		//console.warn("AURIO DURATION:",audio.duration);
		durationInfo.innerHTML = audio.duration;
		audioMetadataLoaded = false;
	}
	
}

function handleLiveStream(response)  { 
	setSendingVideoFalse();
    if(this.terminated){
        return;
    }
    
    if(response.code == "00"){
    	partnerInfo.innerHTML = "Online: True";
    	//console.info("Getting response.imageData :",response.imageData .length);
        photoReceiver.setAttribute('src', response.imageData );
    }else{
    	partnerInfo.innerHTML = "Online: False";
    } 
    
    
    //_byId("base64-info").innerHTML = response.imageData;
   /*  const _class = this;
    this.populateCanvas().then(function(base64) {
        _class.photoReceiver.setAttribute('src', base64 );
    }); */
}
 
 function playAudio(){
	 if(mediaRecorder && mediaRecorder.state != "recording"){
	    	mediaRecorder.start(); 
	 }
	 
 }
 
 function stopAudio(){
	  if( mediaRecorder && !deltaTimeLessThan(70)){
      	mediaRecorder.stop();
      	updateCurrentTime();
	  }
 }
 
 function setAudioInfo(info){
	 _byId("audio-play-info").innerHTML = info;
 }

 function takepicture () {
    const _class = this;
   	playAudio();
    this.resizeWebcamImage().then(function(data){
        _class.sendVideoImage(data);
       	_class.stopAudio(); 
    })

}

function resizeWebcamImage () {
    const _class = this;
    return new Promise(function(resolve, reject) {
        var context = _class.canvas.getContext('2d');
        resolve(_class.canvas.toDataURL('image/png'));
       // if(paused) return;
        context.drawImage(_class.video, 0, 0, _class.width, _class.height    ); 
    })

   
}

function imageToDataUri (img, width, height)   {

    var ctx = theCanvas.getContext('2d');

    // set its dimension to target size
    theCanvas.width = width;
    theCanvas.height = height;

    // draw source image into the off-screen canvas:
    ctx.drawImage(img, 0, 0, width, height);

    // encode image to data-uri with base64 version of compressed image
    return theCanvas.toDataURL('image/png');
}

function clearphoto () {  }


/**
* ==================================================
*                  Frame Loop
* ================================================== 
*/

function initAnimation () {
    this.isAnimate = !this.isAnimate;
    const _class = this;
    window.requestAnimationFrame(function () {
        _class.animate()
    });
}
function animate(){
    
    this.clearphoto();
    this.takepicture(); 
    const _class = this;
    if (this.isAnimate) {
        window.requestAnimationFrame(function () {
            _class.animate();
        });
    }
}

function initLiveStream(){
	//console.info("START initLiveStream");
	
	 this.video = _byId('video');
     //console.log("video:", this.video);
     this.canvas = _byId('canvas'); 
     this.photoReceiver = _byId("photo-receiver");
     this.init();
     this.initAnimation(this);
     this.initWebSocket();
     this.myCapture = _byId("my-capture");
     //console.info("END initLiveStream");
     document.body.onunload = onClose;
}

function initWebSocket(){
	const _class = this;
	const callbackObjectVideo = {
			subscribeUrl : "/wsResp/videostream/${partnerId}",
			callback : function(resp){
				_class.handleLiveStream(resp);
			}
			
		};
	const callbackObjectAudio = {
			subscribeUrl : "/wsResp/audiostream/${partnerId}",
			callback : function(resp){
				_class.handleAudioStream(resp);
			}
			
		};
	connectToWebsocket(callbackObjectVideo, callbackObjectAudio);
}
function onClose(){
	/* postReq("<spring:url value="/api/stream/disconnect" />",
			{originId : "${registeredRequestId}"}, function(xhr) {
				 
			}); */
}
initLiveStream();
</script>

