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