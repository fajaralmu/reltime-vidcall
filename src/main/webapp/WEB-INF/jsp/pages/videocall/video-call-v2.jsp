<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>

<div class="content">
	<h2>Video Call With ${partnerUsername }</h2>
	<h3>Partner ID: ${partnerId }</h3>
	<a class="btn btn-outline-secondary" href="<spring:url value="/dashboard/sessionlist" /> ">Back</a> 
	<p>Your Id: ${registeredRequest.requestId}</p>
	<p><i>*begin Call to start video call</i></p>
	
	<div class="row" style="grid-row-gap: 5px">
		<div class="col-lg-6 camera"
			style="padding: 10px; border: solid 1px green; text-align: center">
			<h2>You</h2>
			<video height="200" width="200" muted="muted" controls id="my-video"></video>

		</div>
		<div class="col-lg-6 output-receiver"
			style="padding: 10px; border: solid 1px green; text-align: center;">
			<h2>Partner <small id="partner-is-online">Online:
					${partnerInfo.active }</small></h2>
			<video style="visibility: hidden" height="200" width="200" controls
				id="video"></video>
		</div>
		<div>
			<button class="btn btn-info btn-lg" onclick="createOffer()">
				<i class="fas fa-phone"></i>&nbsp;Begin Call
			</button>
			<button class="btn btn-danger btn-lg" onclick="closePeerConnection()">
				<i class="fas fa-phone"></i>&nbsp;End Call
			</button>
		</div>
	</div> 

</div>
<script type="text/javascript">
inCalling = true;
var paused = false;
var video;
var myVideo; 
    
var partnerIsOnline = ${partnerInfo.active };
const partnerOnlineInfo = byId("partner-is-online");

function init() {
	const app = this;   
    window.navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then(function (stream) {
		       	console.debug("START getUserMedia"); 
		       
		       	peerConnection.addStream(stream);
		       	app.myVideo.srcObject = stream;
				 
	            console.debug("END getUserMedia"); 
	        }).catch(function (err) {  });
   
    	this.video.addEventListener('canplay', function (ev) { 
    		app.updateVideoDom();  
    	}, false);
}  

function closePeerConnection(){
	confirmDialog("Leave the call?").then(function(ok){
		if(ok){
			if(peerConnection){
				peerConnection.close();
				send({ 	event: "leave", data: {} });
			} 
		}
	});	
}

function updateVideoDom(){ } 

function showVideoElement(){
	if(this.video){
		this.video.style.visibility = 'visible';
	}
}
   
function initLiveStream(){ 
	this.myVideo = byId("my-video");
	this.video = byId('video'); 
    this.initWebSocket();
}

function reCallPartner(){
	const requestObject = {destination: "${partnerId }"};
	postReq("<spring:url value="/api/webrtc2/callpartner" />", requestObject, function(xhr) { });
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
		 
	const callbackWebRtcHandshake = {
			subscribeUrl : "/wsResp/webrtc/${registeredRequest.requestId }",
			callback : function(resp){
				_class.handleWebRtcHandshake(resp.webRtcObject);
			}
		};
	const callbackPartnerOnline = {
			subscribeUrl : "/wsResp/partneronlineinfo/${partnerId }",
			callback : function(resp){
				if(resp && resp.onlineStatus == true){
					partnerOnlineInfo.innerHTML = "Online: true";
					partnerIsOnline = true;
				} else if(resp){
					partnerIsOnline = false;
				}
			}
		};
	const callbackPartnerAcceptCall = {
			subscribeUrl : "/wsResp/partneracceptcall/${registeredRequest.requestId }/${partnerId }",
			callback : function(resp){
				if(resp && resp.accept == true){
					partnerOnlineInfo.innerHTML = "Online: Please Wait...."; 
				} else if(resp){
					partnerOnlineInfo.innerHTML = "Call rejected <button class=\"btn btn-info\" onclick=\"reCallPartner()\">Call again</button>"; 
				}
				partnerIsOnline = false;
			}
		};
	connectToWebsocket( callbackWebRtcHandshake, callbackPartnerOnline, callbackPartnerAcceptCall);  
}


function handleWebRtcHandshake(webRtcObject){
	console.debug("handleWebRtcHandshake : ", webRtcObject);
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
    case "leave":
        handlePartnerLeave(data);
        break;
    default:
        break;
    }
}

function initWebRtc(){
	/* var configuration = {
		    "iceServers" : [ 
		    	{ //	"url":"stun:stun2.1.google.com:19302" "urls" : "stun:127.0.0.1:3478" },
		    	{  'urls': 'turn:127.0.0.1:8888',  'credential': 'superpwd',  'username': 'testuser' } 
		    ]
		}; */
	var configuration2 = {
		    "iceServers" : [ 
		    	{ "url":"stun:stun2.1.google.com:19302"  } 
		    ]
		};
	/* peerConnection = new RTCPeerConnection(configuration, {
	    optional : [ {
	        RtpDataChannels : true
	    } ]
	}); */
	peerConnection = new RTCPeerConnection(configuration2, {
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

	dataChannel.onopen = function(event){ console.debug("DATA CHANNEL ON OPEN ", event); } 
	dataChannel.onmessage = function(event) { console.debug("#####dataChannel Message:", event ); };
	dataChannel.onerror = function(error) { console.debug("#####dataChannel Error:", error); };
	dataChannel.onclose = function(closed) { onsole.debug("#####Data channel is closed ", closed); };
}

function handlePartnerLeave(data){
	infoDialog("partner left the call").then(function(e){});
}

function sendInputMessage(){
	dataChannelSend(byId("input-msg").value);
	byId("input-msg").value = "";
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

function createOffer() {
	if(!peerConnection){
		infoDialog("peerConnection not created please try again...").then(function(e){});
		return;
	}
	if(!partnerIsOnline){
		infoDialog("partner is not online...").then(function(e){});
		return;
	}
	doCreateOffer();
}

function doCreateOffer(){
	  
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

function handleAnswer(answer) {
	console.debug("handleAnswer: ", answer);
	peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
}

function send(msg) {
	console.debug("SEND WEBSOCKET, event: ", msg.event);
	//console.info("Send Audio Data");
	sendToWebsocket("/app/webrtc", {
		partnerId : "${partnerId}",
	 	webRtcObject: (msg) 
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

