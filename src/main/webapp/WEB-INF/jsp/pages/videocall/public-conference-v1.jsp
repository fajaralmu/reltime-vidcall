<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Public Conference ${roomId }</h2>
	<div class="border" class="row">
		<video height="200" width="200" muted="muted" controls id="my-video"></video> 
		<h3>${registeredRequest.requestId }</h3>
		<button class="btn btn-secondary" onclick="redial()"><i class="fas fa-phone"></i>&nbsp;Redial</button>
	</div>
	<div class="row">
		<div class="col-6">
			<div class="border border-primary rounded" id="member-list">
				<h3 style="text-align: center;">Member List</h3>
				<c:forEach var="member" items="${members}">
					<div id="member-item-${member.requestId}">
					<h3><i class="fas fa-user-circle"></i>&nbsp;${member.username } </h3>
					<p>${member.created }</p>
					<video class="border" style="visibility: hidden" height="150" width="150" muted="muted" id="video-member-${member.requestId }" ></video>
					</div>
				</c:forEach>
			</div>

		</div>
		<div class="col-6">
			<div class="border border-primary rounded bg-dark" id="event-log">
				<h3 style="text-align: center; color:#cccccc" class="bg-dark">Event Log</h3>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	const memberList = byId("member-list");
	const eventLog = byId("event-log");

	function prepare() {
		const _class = this;

		onConnectCallbacks.push(function(frame) {
			console.log("Connected to signaling server-", frame);
			_class.join();
		});

		const callbackMemberJoin = {
			subscribeUrl : "/wsResp/joinroom/${roomId }",
			callback : function(resp) {
				_class.initWebRtc(resp.requestId);
				if(byId("member-item-"+resp.requestId)){ 
					_class.removeMemberItem(resp.username, resp.requestId, resp.date);
				}
				_class.addMemberList(resp.username, resp.requestId, resp.date);
			}
		};
		const callbackMemberLeave = {
			subscribeUrl : "/wsResp/leaveroom/${roomId }",
			callback : function(resp) {
				_class.removeMemberItem(resp.username, resp.requestId, resp.date);
			}
		};
		const callbackWebRtcHandshake = {
				subscribeUrl : "/wsResp/webrtcpublicconference/${roomId }",
				callback : function(resp){
					_class.handleWebRtcHandshake(resp.requestId, resp.webRtcObject);
				}
			};
		connectToWebsocket(callbackMemberJoin, callbackMemberLeave, callbackWebRtcHandshake);
	}

	function addMemberList(username, requestId, date) {
		const memberElementObject = {
			tagName : 'div',
			id : "member-item-" + requestId,
			ch1 : {
				tagName : 'h3',
				innerHTML : '<i class="fas fa-user-circle"></i>&nbsp;'+username, 
			},
			ch2 : {
				tagName : 'p',
				innerHTML : 'Date: ' + date
			},
			ch3: {
				tagName: 'video',
				id: 'video-member-'+requestId,
				muted: 'muted',
				height: 150,
				width: 150,
				style: {visibility: 'hidden'}
			}

		};

		const memberElement = createHtmlTag(memberElementObject);
		memberList.appendChild(memberElement);

		updateEventLog(username  + ' Joined');
	}

	function removeMemberItem(username, requestId, date) {
		const memberElement = byId("member-item-" + requestId);
		memberElement.remove();

		updateEventLog(username  + ' Leave');
	}

	function updateEventLog(log) {
	//	log = new Date() + ' ' + log;
		const line = createHtmlTag({
			tagName: 'p',
			ch1:{
				tagName : 'code',
				innerHTML : log
			}
			
		});
		eventLog.appendChild(line);
	}
 

	function join() {
		sendToWebsocket("/app/publicconf1/join", {
			originId : "${registeredRequest.requestId}",
			roomId : "${roomId}"
		});
	}

	function leave() {
		sendToWebsocket("/app/publicconf1/leave", {
			originId : "${registeredRequest.requestId}",
			roomId : "${roomId}"
		});
	}

	prepare();
</script>
<script type="text/javascript">
	inCalling = true;
	var paused = false;
	var video;
	var myVideo = byId("my-video"); 
	     
	const peerConnections = {};
	
	function matchCurrentReqId(requestId){
		return requestId == "${registeredRequest.requestId}";
	}
	
	function updateVideoEvent() {
		const app = this;   
	    window.navigator.mediaDevices.getUserMedia({ video: true, audio: true })
	        .then(function (stream) {
			       	console.debug("START getUserMedia"); 
			       	updateEventLog("Start handle user media");
			       	
			       	app.myVideo.srcObject = stream;
			       	
			       	for (var key in peerConnections ) {
			       		
			       		if(matchCurrentReqId(key)){
			       			 
			       		}else{
			       			updateEventLog("Add Track" +key);
			       			const entry = peerConnections[key];
				       		const peerConnection = entry['connection'];
				       		peerConnection.addStream(stream);
				       		updateEventLog("End Add Track" +key);
				       		updatePeerConnection(key, peerConnection);
			       		} 
					}  
		            console.debug("END getUserMedia"); 
		            updateEventLog("End handle user media");
		        }).catch(function (err) {  });
	   
	    	
	}  
	
	function getPeerConnection(requestId){
		if(!peerConnections[requestId]){
			return null;
		}
		return peerConnections[requestId]['connection'];
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
	
	function closePeerConnection(requestId){
		const peerConnection = getPeerConnection(requestId);
		if(!peerConnection){
			return;
		}
		
		confirmDialog("Leave the call?").then(function(ok){
			if(ok){
				if(peerConnection){
					peerConnection.close();
					send(requestId, { 	event: "leave", data: {} });
					updatePeerConnection(requestId,peerConnection );
				} 
			}
		});	
	}
	
	function updateVideoDom(){ } 
	
	function showVideoElement(requestId){
		const vid = byId("video-member-"+requestId)
		if(vid){
			vid.style.visibility = 'visible';
		}
	}
	   
	function initLiveStream(){ 
		this.myVideo = byId("my-video");
		this.video = byId('video'); 
	    this.initWebSocketConference();
	}
	 
	
	//////////////////////////// websocket stuff //////////////////////////
	
	var conn = null; 
	var dataChannel = null;
	
	function initWebSocketConference(){
		const _class = this; 
		
	/* 	const callbackWebRtcHandshake = {
				subscribeUrl : "/wsResp/webrtcpublicconference/${roomId }",
				callback : function(resp){
					_class.handleWebRtcHandshake(resp.requestId, resp.webRtcObject);
				}
			};
		 
		connectToWebsocket( callbackWebRtcHandshake );   */
	}
	
	
	function handleWebRtcHandshake(requestId, webRtcObject){
		console.debug("handleWebRtcHandshake from ",requestId,": ", webRtcObject);
		updateEventLog(webRtcObject.event.toUpperCase()+" HANDSHAKE "+requestId);
		
		if(matchCurrentReqId(requestId)){
   			return;
   		}
	    var data =  (webRtcObject.data); 
	    switch (webRtcObject.event) {
	    // when somebody wants to call us
	    case "offer":
	        handleOffer(requestId, data);
	        break;
	    case "answer":
	        handleAnswer(requestId, data);
	        break;
	    // when a remote peer sends an ice candidate to us
	    case "candidate":
	        handleCandidate(requestId, data);
	        break;
	    case "leave":
	        handlePartnerLeave(data);
	        break;
	    default:
	        break;
	    }
	}
	
	function initWebRtc(requestId){ 
		
		if(matchCurrentReqId(requestId)){
			updateVideoEvent(); 
			return;
		}
		
		var configuration2 = {
			    "iceServers" : [ 
			    	{ "url":"stun:stun2.1.google.com:19302"  } 
			    ]
			}; 
		const peerConnection = new RTCPeerConnection(configuration2, {
		    optional : [ {
		        RtpDataChannels : true
		    } ]
		} );
		peerConnection.onaddstream  = function(event) {
			updateEventLog("PeerConnection Start Add Track => "+ requestId);
			const vid = byId("video-member-"+requestId);
			if(vid){
				vid.srcObject = event.stream;
			}
			updateEventLog("PeerConnection End Add Track => "+ requestId);
			
		};
		peerConnection.onicecandidate = function(event) {
			console.debug("peerConnection onICE_Candidate: ", event.candidate)
		    if (event.candidate) {
		        send(requestId, {
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
			//initDataChannel(ev);
		}

		updatePeerConnection(requestId,peerConnection ); 
		updateVideoEvent(); 
		createOffer(requestId);
	}
	
	function initDataChannel(ev){
	/* 	dataChannel = peerConnection.createDataChannel("dataChannel", { reliable: true }); 
	
		dataChannel.onopen = function(event){ console.debug("DATA CHANNEL ON OPEN ", event); } 
		dataChannel.onmessage = function(event) { console.debug("#####dataChannel Message:", event ); };
		dataChannel.onerror = function(error) { console.debug("#####dataChannel Error:", error); };
		dataChannel.onclose = function(closed) { onsole.debug("#####Data channel is closed ", closed); }; */
	}
	
	function handlePartnerLeave(data){
	//	infoDialog("partner left the call").then(function(e){});
	}
	 
	function createOffer(requestId) {
		const peerConnection = getPeerConnection(requestId);
		if(!peerConnection){
			infoDialog("peerConnection not created please try again...").then(function(e){});
			return;
		}
		 
		doCreateOffer(requestId);
	}
	
	function doCreateOffer(requestId){
		if(matchCurrentReqId(requestId)){
   			return;
   		}
		
		const peerConnection = getPeerConnection(requestId);
		const _class = this;
		peerConnection.createOffer(function(offer) {
		    send(requestId, {
		        event : "offer",
		        data : offer
		    });
		    peerConnection.setLocalDescription(offer);
		    _class.updatePeerConnection(requestId,peerConnection );
		}, function(error) {
		    console.error("Error create offer...", error);
		});
		
		updatePeerConnection(requestId,peerConnection );
	}
	
	function handleOffer(requestId, offer){
		const peerConnection = getPeerConnection(requestId);
		const _class = this;
		
		updateEventLog(requestId+" handleOffer");
		console.debug(requestId, "handleOffer: ", offer);
		
		peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
		
		console.debug("Will create answer");
		peerConnection.createAnswer(function(answer) {
			
			console.debug("createAnswer: ", answer);
			updateEventLog(requestId+" createAnswer");
			
		    peerConnection.setLocalDescription(answer);
	        send(requestId, {
	            event : "answer",
	            data : answer
	        });
	        _class.updatePeerConnection(requestId,peerConnection );
		}, function(error) {
		    console.error("error handle offer: ", error);
		});
		
		updatePeerConnection(requestId,peerConnection );
	}
	
	function handleCandidate(requestId, candidate){
		const peerConnection = getPeerConnection(requestId);
		
		console.debug(requestId, "handleCandidate: ", candidate);
		updateEventLog(requestId+" handleCandidate");
		
		peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
		updatePeerConnection(requestId,peerConnection );
		showVideoElement(requestId);
	}
	
	function handleAnswer(requestId, answer) {
		const peerConnection = getPeerConnection(requestId);
		
		console.debug(requestId, "handleAnswer: ", answer);
		updateEventLog(requestId+" handleAnswer");
		
		peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
		updatePeerConnection(requestId,peerConnection );
	}
	
	function send(requestId, msg) {
		console.debug("SEND WEBSOCKET, event: ", msg.event);
		updateEventLog(requestId+" SEND WEBSOCKET "+msg.event);
		//console.info("Send Audio Data");
		sendToWebsocket("/app/publicconf1/webrtc", {
			originId : requestId,
			roomId: '${roomId}',
		 	webRtcObject: (msg) 
		});
	}
	
	function sendToWebsocketV2(message){
		 if(!conn){
			 return;
		 }
		 conn.send(JSON.stringify(message));
	}
	
	function redial(){
		for (var key in peerConnections) {
			if(matchCurrentReqId(key)){
	   			continue;
	   		}
			initWebRtc(key);
		}
	}
	
	 
	initLiveStream(); 
</script>
<c:forEach var="member" items="${members}">
	<script>
	if("${member.requestId}" == "${registeredRequest.requestId}"){
		
	}else{
		initWebRtc("${member.requestId}");
	}
		
	</script>
</c:forEach>