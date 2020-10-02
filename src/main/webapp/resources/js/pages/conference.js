var videoStream = null;

const peerConnections = {};

var conn = null; 
var dataChannel = null;

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
		updateEventLog("Error when handling handshake "+e);
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
	var configuration2 = {
		    "iceServers" : [ 
		    	{ "url":"stun:stun2.1.google.com:19302"  } 
		    ]
		};
	const peerConnection = new RTCPeerConnection( configuration2, {//configuration2, {
	    optional : [ {
	        RtpDataChannels : true
	    } ]
	} );
	//TODO: onaddstream is deprecated, change to ontrack
	peerConnection.onaddstream  = function(event) {
		updateEventLog("PeerConnection Start Add Stream => "+ requestId);
		const vid = byId("video-member-"+requestId);
		if(vid){
			vid.srcObject = event.stream;
			vid.style.visibiity = "visible";
			vid.addEventListener('canplay', function (ev) { 
				vid.play();
	    	}, false);
		}
		updateEventLog("PeerConnection End Add Stream => "+ requestId+" vid: "+(vid!=null));
		
	}; 
	peerConnection.onicecandidate = function(event) {
		console.debug("peerConnection on ICE Candidate: ", event.candidate);
		updateEventLog("Peer IceCandidate ("+ requestId +")");
	    if (event.candidate) {
	        send(requestId, {
	            event : "candidate",
	            data : event.candidate
	        }); 
	    }else{
	    	console.warn("Candiate is NULL: ", event);
	    	updateEventLog("Peer IceCandidate IS NULL ("+ requestId +")");
	    }
	};
	peerConnection.onsignalingstatechange = function(e){
		const state = peerConnection.signalingState;
		console.debug("PEER CONNECTION Signaling state: ", state);
		updateEventLog("Peer SignalingState ("+ requestId +") | "+state);
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