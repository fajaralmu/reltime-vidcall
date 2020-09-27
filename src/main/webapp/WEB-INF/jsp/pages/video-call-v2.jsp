<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>

<div class="content">
	<h2>Video Call With</h2>
	<h3>Partner ID: ${partnerId }</h3>
	<a href="<spring:url value="/admin/home" />">Back</a>

	<h2>Live Streaming</h2>
	<p>Stream ID: ${registeredRequest.requestId}</p>
	<div class="row" style="grid-row-gap: 5px">
		<div class="col-6 camera"
			style="padding: 10px; border: solid 1px green; text-align: center">
			<h2>You</h2>
			<video height="200" width="200" muted="muted" controls id="my-video"></video>

		</div>
		<div class="col-6 output-receiver"
			style="padding: 10px; border: solid 1px green; text-align: center;">
			<h2>
				Partner <small id="partner-info">Online:
					${partnerInfo.active }</small>
			</h2>
			<video style="visibility: hidden" height="200" width="200" controls
				id="video"></video>
		</div>
		<div>
			<button class="btn btn-info btn-lg" onclick="createOffer()">
				<i class="fas fa-phone"></i>&nbsp;Call
			</button>
		</div>
	</div> 

</div>
<script type="text/javascript">

var paused = false;
var video;
var myVideo; 
 
var width = 70;
var height = 70;   

var MIN_DELTA_TIME = 500; 

function init () {
	const app = this;   
    window.navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then(function (stream) {
        	 console.debug("START getUserMedia"); 
        	 peerConnection.addStream(stream);
        	 app.myVideo.srcObject = stream;
			 
            console.debug("END getUserMedia"); 
        }).catch(function (err) {
            //console.log("An error occurred: " + err);
        });
   
    this.video.addEventListener('canplay', function (ev) { 
    		app.updateVideoDom();  
         
    }, false);  
 
}  

function updateVideoDom(){
	if ( this.streaming) { 
		return; 
	} 
} 

function showVideoElement(){
	if(this.video){
		this.video.style.visibility = 'visible';
	}
}
   
function initLiveStream(){ 
	this.myVideo = _byId("my-video");
	this.video = _byId('video'); 
    this.initWebSocket();
}

//////////////////////////// websocket stuff //////////////////////////
var conn = null;
var peerConnection = null;
var dataChannel = null;


function initWebSocket(){
	const _class = this;
	
	onConnectCallbacks.push(function(frame){ 
		console.log("Connected to signaling server-", frame);
		_class.initWebRtc(); 
	});
			
		 
	const callbackWsMsg = {
			subscribeUrl : "/wsResp/webrtc/${registeredRequest.requestId }",
			callback : function(resp){
				_class.handleWsMsg(resp.webRtcObject);
			}
			
		};
	connectToWebsocket( callbackWsMsg); 
	
}

function handleWsMsg(webRtcObject){ 
	console.debug("handleWsMsg : ", webRtcObject);
    var data =  (webRtcObject.data);
    switch (webRtcObject.event) {
    // when somebody wants to call us
    case "offer":
        handleOffer(data);
        break;
    case "answer":
        handleAnswer(data);
        break;
    // when a remote peer sends an ice candidate to us
    case "candidate":
        handleCandidate(data);
        break;
    default:
        break;
    }
	 
}

function initWebRtc(){
	var configuration = {
		    "iceServers" : [ 
		    	{
		    	//	"url":"stun:stun2.1.google.com:19302"
		      		"urls" : "stun:127.0.0.1:3478"
		    	},
		    	{
			      'urls': 'turn:127.0.0.1:8888',
			      'credential': 'superpwd',
			      'username': 'testuser'
			    } 
		    ]
		};
	/* peerConnection = new RTCPeerConnection(configuration, {
	    optional : [ {
	        RtpDataChannels : true
	    } ]
	}); */
	peerConnection = new RTCPeerConnection(null, {
	    optional : [ {
	        RtpDataChannels : true
	    } ]
	} );
	peerConnection.onaddstream = function(event) {
	    video.srcObject = event.stream;
	};
	peerConnection.onicecandidate = function(event) {
		console.debug("peerConnection onICE_Candidate: ", event.candidate)
	    if (event.candidate) {
	        send({
	            event : "candidate",
	            data : event.candidate
	        }); 
	    }else{
	    	console.warn("Candiate is NULL: ", event);
	    }
	};
	peerConnection.onsignalingstatechange = function(e){
		console.debug("PEER CONNECTION Signaling state: ", peerConnection.signalingState);
	}
	
	peerConnection.ondatachannel = function(ev){
		console.debug("ondatachannel: ", ev);
		initDataChannel(ev);
	}
	init(); 
	
}

function initDataChannel(ev){
	dataChannel = peerConnection.createDataChannel("dataChannel", { reliable: true }); 

	dataChannel.onopen = function(event){
		console.debug("DATA CHANNEL ON OPEN ", event);
	}
	
	dataChannel.onmessage = function(event) {
	    console.debug("#####dataChannel Message:", event );
	};
	dataChannel.onerror = function(error) {
	    console.debug("#####dataChannel Error:", error);
	};
	dataChannel.onclose = function(closed) {
	    console.debug("#####Data channel is closed ", closed);
	};
	 
}

function sendInputMessage(){
	dataChannelSend(_byId("input-msg").value);
	_byId("input-msg").value = "";
}

function dataChannelSend(msg){
	if(dataChannel){
		try{
			dataChannel.send(msg);
		}catch(e){
			console.warn("Error send message: ", e);
		}
		
	}
}

function createOffer(){
	if(!peerConnection){
		alert("please try again...");
		return;
	}
	peerConnection.createOffer(function(offer) {
	    send({
	        event : "offer",
	        data : offer
	    });
	    peerConnection.setLocalDescription(offer);
	}, function(error) {
	    console.error("Error create offer...", error);
	});
}

function handleOffer(offer){
	console.debug("handleOffer: ", offer);
	peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
	
	console.debug("Will create answer");
	peerConnection.createAnswer(function(answer) {
		console.debug("createAnswer: ", answer);
	    peerConnection.setLocalDescription(answer);
	        send({
	            event : "answer",
	            data : answer
	        });
	}, function(error) {
	    console.error("error handle offer: ", error);
	});
}
function handleCandidate(candidate){
	console.debug("handleCandidate: ", candidate);
	peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
	showVideoElement();
}

function handleAnswer(answer){
	console.debug("handleAnswer: ", answer);
	peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
}

function send(msg){
	 console.debug("SEND WEBSOCKET, event: ", msg.event);
	//console.info("Send Audio Data");
	sendToWebsocket("/app/webrtc", {
		partnerId : "${partnerId}",
	 	webRtcObject:	 (msg) 
	});
}

function sendToWebsocketV2(message){
	 if(!conn){
		 return;
	 }
	 conn.send(JSON.stringify(message));
}

 
initLiveStream();
</script>

