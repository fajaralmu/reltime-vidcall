var videoStream = null;

const peerConnections = {};

var conn = null; 
var dataChannel = null;

//MUST BE DECLARED
var memberList;
var eventLog;
var infoChatCount;
var infoLogCount; 
var rtcConfiguration;

const HandshakeHandler = {
	"offer" : function(requestId, data) {
		handleOffer(requestId, data);
	},
	"answer" : function(requestId, data) {
		handleAnswer(requestId, data);
	},
	"candidate" : function(requestId, data) {
		handleCandidate(requestId, data);
	},
	"leave" : function(requestId, data) {
		handlePartnerLeave(data);
	},
	"dial" : function(requestId, data) {
		handlePartnerDial(requestId);
	}
};

function handleHandshake(event, requestId, data){
	try{
		const handler = HandshakeHandler[event];
		if(handler){
			handler(requestId, data);
		}
	}catch(e){
		log("Error when handling handshake "+e);
	}
}


function getPeerConnection(requestId){
	if(!peerConnections[requestId]){
		return null;
	}
	return peerConnections[requestId]['connection'];
}

function removePeerConnection(requestId){
	 const peerConnection = generatePeerConnection(requestId);
	 updatePeerConnection(requestId, peerConnection); 
}

function updatePeerConnection(requestId, obj){
	if(!getPeerConnection(requestId)) {
		peerConnections[requestId] = {
				'date' : new Date()
		};
	}
	peerConnections[requestId]['connection'] = obj;
	peerConnections[requestId]['updated'] = new Date();
}

function generatePeerConnection(requestId) {
	
	const peerConnection = new RTCPeerConnection( rtcConfiguration, {//configuration2, {
	    optional : [ {
	        RtpDataChannels : true
	    } ]
	} );
	//TODO: onaddstream is deprecated, change to ontrack
	peerConnection.onaddstream  = function(event) {
		log("PeerConnection Start Add Stream => "+ requestId);
		const vid = byId("video-member-"+requestId);
		if(vid){
			vid.srcObject = event.stream;
			vid.style.visibiity = "visible";
			vid.addEventListener('canplay', function (ev) { 
				vid.play();
	    	}, false);
		}
		log("PeerConnection End Add Stream => "+ requestId+" vid: "+(vid!=null));
		
	}; 
	peerConnection.onicecandidate = function(event) {
		console.debug("peerConnection on ICE Candidate: ", event.candidate);
		log("Peer IceCandidate ("+ requestId +")");
	    if (event.candidate) {
	        send(requestId, {
	            event : "candidate",
	            data : event.candidate
	        }); 
	    }else{
	    	console.warn("Candiate is NULL: ", event);
	    	log("Peer IceCandidate IS NULL ("+ requestId +")");
	    }
	};
	peerConnection.onsignalingstatechange = function(e){
		const state = peerConnection.signalingState;
		console.debug("PEER CONNECTION Signaling state: ", state);
		log("Peer SignalingState ("+ requestId +") | "+state);
	}
	
	peerConnection.ondatachannel = function(ev){
		console.debug("ondatachannel: ", ev);
		initDataChannel(ev);
	}
	
	return peerConnection;
}

function initDataChannel(ev){
	//dataChannel = peerConnection.createDataChannel("dataChannel", { reliable: true });  
}

function removePeerStream(requestId, stream){
	const peerConnection = getPeerConnection(requestId); 
	
	if(!peerConnection){
		return;
	}
	if(peerConnection.getSenders() == null || stream.getTracks() == null){
		return;
	}
	
	peerConnection.getSenders().forEach(function(sender){
		stream.getTracks().forEach(function(track) {
	      if(sender.track != null && track.kind == sender.track.kind) {
	    	  peerConnection.removeTrack(sender);
	    	  const identical = track == sender.track;
	    	  console.debug("track removed : ", track.kind ," identical: ", identical);
	      }
	    })
	});
	
	
	updatePeerConnection(requestId,peerConnection );
} 

/////////////////////////// DOM MANIPULATIONS ///////////////////////////////////////

function clearLog(){
	eventLog.innerHTML = "";
	infoLogCount.innerHTML = 0;
}

function log(content) {
	//	log = new Date() + ' ' + log;
	const line = createHtmlTag({
		tagName: 'p',
		ch1:{
			tagName : 'code',
			innerHTML : content
		}
		
	});
	eventLog.appendChild(line);
	if(infoLogCount)
		infoLogCount.innerHTML = parseInt(infoLogCount.innerHTML) + 1;
}

function setVideoCover(requestId, hideCover){
	const videoElement = byId("video-member-"+requestId);
	const videoControl = byId("video-control-"+requestId);
	if(videoElement == null){
		return;
	}
	
	if(hideCover){
		const coverElement = byId('video-cover-'+requestId);
		if(coverElement) coverElement.remove();
		videoElement.style.display = 'block';
		videoControl.style.display = 'block';
		
	} else {
		if(byId('video-cover-'+requestId)){
			byId('video-cover-'+requestId).remove();
		}
		const cover = {
				tagName: 'div', className: 'video-cover rounded align-middle', 
				id:'video-cover-'+requestId, innerHTML: '<h1><i class="fas fa-video-slash"></i></h1>'
		}
		
		insertAfter(createHtmlTag(cover), videoElement);
		videoElement.style.display = 'none';
		videoControl.style.display = 'none';
	}
}

function wait(delayInMS) {
	return new Promise(resolve => setTimeout(resolve, delayInMS));
}