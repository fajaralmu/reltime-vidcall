<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
 
<div   class="content" onload="initLiveStream()">
	<h2>Video Call With</h2>
	<h3>Partner ID: ${partnerId }</h3>
	<a href="<spring:url value="/admin/home" />">Back</a>

	<h2>Live Streaming</h2>
	<p>Stream ID: ${registeredRequestId}</p> 
	<div id="live-wrapper" style="display: grid; grid-template-columns: 47% 47%">
		<div class ="camera" style="padding: 20px; border: solid 1px green; text-align: center">
			<h2>You</h2>   
			<img id="my-capture" height="350" width="350" />
			<p></p>
			<div>  
				<button id="btn-terminate" class="btn btn-danger btn-sm" onClick="terminate()">Terminate</button>
				<button id="btn-pause" class="btn btn-info btn-sm" onClick="pauseOrContinue()">Pause</button>
			</div>
			<div style="display: none"><canvas id="canvas"> </canvas></div>
			<p>Preview</p>
			<video controls id="video">Video stream not available. </video>
		</div> 
		<div class ="output-receiver" style="padding: 20px; border: solid 1px green; text-align: center;">
			<h2>Partner <small id="partner-info">Online: ${partnerInfo.active }</small></h2>
			<img width="350" height="350" id="photo-receiver"
				alt="The screen RECEIVER will appear in this box." />
		</div>
	</div>
	 
	<hr />
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

function init () {
	const _class = this;   
    window.navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then(function (stream) {
            _class.video.srcObject = stream;
           // console.log("stream:", stream); 
            _class.video.play();
           
        })
        .catch(function (err) {
            console.log("An error occurred: " + err);
        });
   
    this.video.addEventListener('canplay', function (ev) {
        if (!_class.streaming) {
            _class.height = _class.video.videoHeight /  (_class.video.videoWidth / _class.width);

            _class.video.setAttribute('width', _class.width);
            _class.video.setAttribute('height', _class.height);
            _class.canvas.setAttribute('width', _class.width );
            _class.canvas.setAttribute('height', _class.height );
            _class.streaming = true; 
           
        }
    }, false);  

    this.clearphoto();
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
	console.info("Sending video at ", new Date().toString(), " length: ", imageData.length);
	const requestObject =  {
			partnerId : "${partnerId}",
			originId : "${registeredRequestId}",
			imageData : imageData
		};
	
	const imageSent = sendToWebsocket("/app/stream", requestObject);
	if(!imageSent){
		this.sendingVideo = false
	}
	
	myCapture.setAttribute("src", imageData);
}

function handleLiveStream(response)  { 
	setSendingVideoFalse();
    if(this.terminated){
        return;
    }
    
    if(response.code == "00"){
    	partnerInfo.innerHTML = "Online: True";
    	console.info("Getting response.imageData :",response.imageData .length);
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
 

 function takepicture () {
    const _class = this;
    this.resizeWebcamImage().then(function(data){
        _class.sendVideoImage(data);
    })

}

function resizeWebcamImage () {
    const _class = this;
    return new Promise(function(resolve, reject) {
        var context = _class.canvas.getContext('2d');
        resolve(_class.canvas.toDataURL('image/png'));
       // if(paused) return;
        context.drawImage(_class.video, 0, 0, _class.width, _class.height    );
         
        // if (_class.width && _class.height) {
        //     const dividier = 1;
        //     _class.canvas.width = _class.width/ dividier;
        //     _class.canvas.height = _class.height/ dividier;
        //     context.drawImage(_class.video, 0, 0, _class.width/ dividier, _class.height/dividier);
        //     var data = _class.canvas.toDataURL('image/png');  
        //     resolve(data);
        // }else {
        //     _class.clearphoto();
        // }
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

function clearphoto () {
    // var context = this.canvas.getContext('2d');
    // context.fillStyle = "#AAA";
    // context.fillRect(0, 0, this.canvas.width, this.canvas.height);

    // var data = this.canvas.toDataURL('image/png'); 
    // var img = new Image();
    // img.src = data;
}


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
	console.info("START initLiveStream");
	
	 this.video = _byId('video');
     console.log("video:", this.video);
     this.canvas = _byId('canvas'); 
     this.photoReceiver = _byId("photo-receiver");
     this.init();
     this.initAnimation(this);
     this.initWebSocket();
     this.myCapture = _byId("my-capture");
     console.info("END initLiveStream");
     document.body.onunload = onClose;
}

function initWebSocket(){
	const _class = this;
	connectToWebsocket(null, null, null, {
		partnerId : "${partnerId}",
		callback : function(resp){
			_class.handleLiveStream(resp);
		}
		
	});
}
function onClose(){
	postReq("<spring:url value="/api/stream/disconnect" />",
			{originId : "${registeredRequestId}"}, function(xhr) {
				 
			});
}
initLiveStream();
</script>

