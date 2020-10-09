<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Public Conference</h2>
	<div class="border row">
		<div class="col-6" style="text-align: center;" >
			<video height="200" width="200" muted="muted" controls id="my-video"></video>  
			<h5>Enabled: <span class="badge badge-info" id="info-video-enabled">${videoEnabled }</span></h5>
			<div style="text-align: center;">
				<button onclick="togglePeerStream(true)" class="btn btn-outline-primary btn-sm">Enable Video</button>
				<button onclick="togglePeerStream(false)"  class="btn btn-outline-danger btn-sm">Disable Video</button>
			</div>
			<div>
			<div class="input-group mb-3">
				<div class="input-group-prepend">
				    <label class="input-group-text" for="inputGroupSelect01">Stream Type</label>
				</div>
				<select id="select-stream-type" class="custom-select">
					<option value="camera">Camera</option>
					<c:if test="${isPhone==false }"><option value="screen">Screen</option></c:if>
				</select>
				</div>
			</div>
		</div>
		<div class="col-6"> 
			<jsp:include page="partial/room-panel.jsp"></jsp:include>
		</div>
	</div>
	<div class="row">
		<div class="col-6">
			<jsp:include page="partial/conference-member-list.jsp"></jsp:include>
		</div>
		<div class="col-6"> 
			<jsp:include page="partial/conference-chat-log.jsp"></jsp:include>
		</div>
	</div>
</div>


<script type="text/javascript">
	//declared in conference.js
	memberList = byId("member-list");
	eventLog = byId("event-log");
	
	infoChatCount = byId("info-chat-count");
	infoLogCount = byId("info-logs-count");
	infoVideoEnabled = byId("info-video-enabled");
	
	const chatList = byId("chat-list");
	const inputChatMessage = byId("input-chat-message"); 
	const selectStreamType = byId("select-stream-type");
	
	const onloadCallbacks = [];
	
	var videoEnabled = ${!videoEnabled};
	var isJoined = ${isJoined};
	var initialPeerCount = ${members.size()};
	
	function prepare() {
		const _class = this;

		onConnectCallbacks.push(function(frame) {
			console.log("Connected to signaling server-", frame);
			_class.join();
			
			if(isJoined){
				_class.handleOnloadCallbacks();
			}
		});

		const callbackMemberJoin = {
			subscribeUrl : "/wsResp/joinroom/${roomId }",
			callback : function(response) {
				//_class.initWebRtc(resp.requestId, true);
				_class.handleMemberJoin(response);
			}
		};
		const callbackMemberLeave = {
			subscribeUrl : "/wsResp/leaveroom/${roomId }",
			callback : function(resp) {
				peerConnections[resp.requestId] = null;
				_class.removeMemberItem(resp.username, resp.requestId, resp.date);
			}
		};
		const callbackWebRtcHandshake = {
			subscribeUrl : "/wsResp/webrtcpublicconference/${roomId }/${registeredRequest.requestId}",
			callback : function(resp){
				_class.handleWebRtcHandshake(resp.eventId, resp.requestId, resp.webRtcObject);
			}
		};
		const callbackRoomInvalidated = {
			subscribeUrl : "/wsResp/roominvalidated/${roomId }",
			callback : function(resp){
				infoDialog("Room has been invalidated").then(function(e){
					window.location.reload();
				})
			}
		};
		const callbackNewChat = {
			subscribeUrl : "/wsResp/newchat/${roomId }",
			callback : function(resp){
				 _class.handleNewChat(resp);
			}
		};
		const callbackTogglePeerStream = {
			subscribeUrl : "/wsResp/togglepeerstream/${roomId }",
			callback : function(resp){
				 _class.handleTogglePeerStream(resp);
			}
		};
		const callbackPeerConfirm = {
			subscribeUrl : "/wsResp/peerconfirm/${roomId }/${registeredRequest.requestId}",
			callback : function(resp){
				 _class.handlePeerConfirmJoin(resp);
			}
		};
		const callbackRecordingTimer = {
			subscribeUrl : "/wsResp/recordingtimer/${roomId }/${registeredRequest.requestId}",
			callback : function(resp){
				 _class.handleRecordingTimer(resp);
			}
		};
		
		connectToWebsocket(callbackMemberJoin, 
				callbackMemberLeave, callbackWebRtcHandshake, 
				callbackRoomInvalidated, callbackNewChat, 
				callbackTogglePeerStream, callbackPeerConfirm, callbackRecordingTimer);
	}
	
	function handleRecordingTimer(resp){
		const peerId = resp.requestId;
		if(resp.code == "00"){
			byId("recording-timer").innerHTML = resp.message+ " CODE: "+resp.code;
		}else{
			byId("recording-timer").innerHTML = "Stopped At "+ byId("recording-timer").innerHTML;
			stopRecording(peerId);
		}
	}
	 
	
	function handlePeerConfirmJoin(resp){
		log("Peer confirmed: "+resp.requestId);
		//DEFULT: peer cannot see video
		this.togglePeerStream(false);
	}
	
	function handleTogglePeerStream(resp){
		const requestId = resp.requestId;
		const enabled = resp.streamEnabled;
		
		if(isUserRequestId(requestId)) {
			return;
		}
		
		if(enabled){
			dialPartner(requestId);
		} else if(this.videoStream != null){  }
		
		setVideoCover(requestId, enabled);
	}
	
	
	
	function handleNewChat(resp){
		const chatMessage = resp.chatMessage;
		 
		const chatItemProp = {
				tagName: 'div',
				className: 'chat-message-'+(isUserRequestId(chatMessage.requestId)? 'user':'common'),
				
				ch1: {
					tagName: 'span',
					innerHTML: isUserRequestId(chatMessage.requestId) ? 'You' : chatMessage.username
				},
				ch2: {
					tagName: 'h4',
					innerHTML: chatMessage.body
				},
				ch3: {
					tagName: 'span',
					innerHTML: new Date(chatMessage.date)
				}
		}
		const chatItemElement = createHtmlTag(chatItemProp);
		chatList.appendChild(chatItemElement);
		
		infoChatCount.innerHTML = parseInt(infoChatCount.innerHTML) + 1; 
	
	}
	
	function handleMemberJoin(resp){
		if(isUserRequestId(resp.requestId)) {
			//TODO: update...
			//window.location.reload();
			return;
		}
		
		if(byId("member-item-"+resp.requestId)){ 
			removeMemberItem(resp.username, resp.requestId, resp.date);
		}
		addMemberList(resp);
		sendPeerConfirm(resp.requestId);
		//dialPartner(resp.requestId);
		initWebRtc(resp.requestId, true);
	}

	function addMemberList(response) {
		const username = response.username;
		const requestId = response.requestId;
		const date = response.date;
		const isRoomCreator = response.roomCreator;
		const memberElementObject = {
			tagName : 'div',className: 'col-6',
			id : "member-item-" + requestId,
			ch1 : {
				tagName : 'h5',
				innerHTML : '<i class="fas fa-user-circle"></i>&nbsp;'+username + (isRoomCreator? '<small><i class="fas fa-headset"></i></small>':''), 
			},
			ch2 : {
				tagName : 'p',
				innerHTML : 'Date: ' + new Date(date)
			},
			ch3: {
				tagName: 'video',className: 'border',
				id: 'video-member-'+requestId,
				muted: 'muted', 
				controls: '',
				height: 150, width: 150,
				style: {visibility: 'hidden'}
			},
			ch4: {
				tagName: 'div', className: 'btn-group',
				id: 'video-control-'+requestId,
				role: 'group',
				ch1: {
					tagName: 'button', className: 'btn',
					innerHTML : '<i class="fas fa-pause"></i>',
					onclick: function(e){
						toggleVideoPlay('video-member-'+requestId, e.target); 
					}
				},
				ch2: {
					tagName: 'button', className: 'btn',
					innerHTML : "<i class=\"fas fa-volume-mute\"></i>",
					onclick: function(e){
						toggleVideoMute('video-member-'+requestId, e.target);
					}
				},
				ch3: {
					tagName: 'button', className: 'btn btn-info btn-sm',
					onclick: function(e){
						dialPartner(requestId);
					},
					innerHTML: '<i class="fas fa-phone"></i>&nbsp;Dial'
				},
				ch4: {
					tagName: 'button', className: 'btn btn-secondary btn-sm',
					id:'toggle-record-'+requestId,
					onclick: function(e){
						startRecording(requestId);
					},
					innerHTML: '<i class="fas fa-record-vinyl"></i> Rec'
				},
			}
		};
		
		if(requestId == "${registeredRequest.requestId}"){
			memberElementObject['ch3'] = {
					tagName:'h3', innerHTML: 'YOU', className: 'center-aligned bg-light'
			}
			memberElementObject['ch4'] = null;
		}

		const memberElement = createHtmlTag(memberElementObject);
		memberList.appendChild(memberElement);

		log(username  + ' Joined');
	}
	
	function toggleVideoPlay(videoId, button){
		const vid = byId(videoId);
		if(!vid) return;
		
		if(vid.paused){
			vid.play();
			button.innerHTML = "<i class=\"fas fa-pause\"></i>";
		}else{
			vid.pause();
			button.innerHTML = "<i class=\"fas fa-play\"></i>";
		}
	}
	
	function toggleVideoMute(videoId, button){
		const vid = byId(videoId);
		if(!vid) return;
		
		if(vid.muted){
			vid.muted = false;
			button.innerHTML = "<i class=\"fas fa-volume-mute\"></i>";
		}else{
			vid.muted = true;
			button.innerHTML = "<i class=\"fas fa-volume-down\"></i>";
		}
	}

	function removeMemberItem(username, requestId, date) {
		const memberElement = byId("member-item-" + requestId);
		memberElement.remove();

		log(username  + ' Leave');
	}
 

	function join() {
		log("Joining...");
		
		sendToWebsocket("/app/publicconf1/join", {
			originId : "${registeredRequest.requestId}",
			roomId : "${roomId}"
		});
	}

	function leave() {
		
		confirmDialog("Want to leave ?").then(function(ok){
			if(ok){
				doLeave();
			}
		})
	}
	
	function doLeave(){
		sendToWebsocket("/app/publicconf1/leave", {
			originId : "${registeredRequest.requestId}",
			roomId : "${roomId}"
		});
		window.location.href = "<spring:url value="/dashboard/" /> ";
	}
	
	function togglePeerStream(enabled){
		
		/* if(this.videoEnabled == enabled){
			infoDialog("Currently is "+enabled).then(function(e){});
			return;
		} */
		
		sendToWebsocket("/app/publicconf1/togglepeerstream", {
//			originId : requestId,
			originId : "${registeredRequest.requestId}",
			roomId: '${roomId}',
			streamEnabled: enabled
		});
		if(enabled == false){
			for ( var key in peerConnections) {
				if(isUserRequestId(key)){
					continue;
				}
				removePeerStream(key, this.videoStream);
			}
		}
		this.videoEnabled = enabled;
		infoVideoEnabled.innerHTML = enabled;
	}

	selectStreamType.onchange = function(e){
		updateStreamType(e.target.value);
	}	
	
</script>
<script type="text/javascript">
	inCalling = true;
	var paused = false;
	var video;
	var myVideo = byId("my-video"); 
	var streamType = "camera"; //"screen"
	
	function isUserRequestId(requestId){
		return requestId == "${registeredRequest.requestId}";
	}
	
	function updateVideoEvent() {
		const app = this;   
		if(app.videoStream){
			log("VideoStream IS EXIST");
			var peerCount = 0;
			var totalPeer = 0;
			
			for (var key in peerConnections ) {
       			const entry = peerConnections[key];
       			if(null == entry) continue;
       			
	       		if(isUserRequestId(key)){
	       		}else{ 
		       		const peerConnection = entry['connection'];
		       		if(!peerConnection.getLocalStreams() || peerConnection.getLocalStreams().length == 0){
		       			peerConnections[key]['connection'].addStream(this.videoStream); 
		       			peerCount++;
		       		} 
		       		totalPeer++; 
	       		}
			}  
			log("Updated Peer Count: "+peerCount);
			log("Total Peer Count: "+totalPeer);
			return;
		}
		const config = { video: true, audio: true };
		
		var mediaStream; 
	   	
	   	if (streamType == "camera") {
	   		mediaStream = window.navigator.mediaDevices.getUserMedia(config)
	   	} else {
	   		mediaStream = window.navigator.mediaDevices.getDisplayMedia(config)
	   	}
	   	mediaStream
	   		.then(function (stream) { app.handleStream (stream) })
	   		.catch(function (error) { console.error(error) });	    	
	}   
	
	function handleStream(stream){ 
    		this.videoStream = stream;
	       	console.debug("START getUserMedia"); 
	       	log("Start handle user media");
	       	
	       	this.myVideo.srcObject = stream;
	       	var peerCount = 0;
	       	for (var key in peerConnections ) {

       			const entry = peerConnections[key];
       			if(null == entry) continue;
	       		if(isUserRequestId(key)){
	       			 
	       		}else{
	       			 
		       		const peerConnection = entry['connection'];
		       		if(!peerConnection.getLocalStreams() || peerConnection.getLocalStreams().length == 0){
		       			peerConnections[key]['connection'].addStream(stream); 
		       		}
		       		//updatePeerConnection(key, peerConnection);
		       		peerCount++;
	       		} 
	       		
			}  
            console.debug("END getUserMedia"); 
            log("End HandleMedia peerCount: "+peerCount);
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
	
	function updateStreamType(type){
		if(type != "screen" && type != "camera") {
			return;
		}
		
		this.streamType = type;
		this.togglePeerStream(false);
		this.videoStream = null;
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
		updateVideoEvent();
	}
	 
	
	//////////////////////////// WebRTC stuff ////////////////////////// 
	
	function handleWebRtcHandshake(eventId, requestId, webRtcObject){
		console.debug("handleWebRtcHandshake from ",requestId,": ", webRtcObject);
		
		if(isUserRequestId(requestId)){
			log("## HANDSHAKE ABORTED "+eventId+"|"+webRtcObject.event.toUpperCase()+"|"+requestId);
   			return;
   		}
		log("## HANDSHAKE "+eventId+"|"+webRtcObject.event.toUpperCase()+"|"+requestId);
		
	    const data =  (webRtcObject.data); 
	    handleHandshake(webRtcObject.event, requestId, data);
	    
	}
	  
	function initWebRtc(requestId, handleNewMemberJoin){ 
		
		if(isUserRequestId(requestId)){
			updateVideoEvent(); 
			return;
		}
		
		const peerConnection = generatePeerConnection(requestId);
		
		var mustUpdate = false;
		if(this.videoStream){
			peerConnection.addStream(this.videoStream);
			mustUpdate = true;
		}
		
		updatePeerConnection(requestId, peerConnection); 
		if(mustUpdate){
			log("# Will Update Video Event ");
			updateVideoEvent(); 
		}
		//updateVideoEvent(); 
		if(handleNewMemberJoin){
			createOffer(requestId); 
		}
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
		if(isUserRequestId(requestId)){
   			return;
   		}
		log("doCreateOffer to "+requestId);
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
		
		log(requestId+" handleOffer");
		if(!peerConnection){
			log("Aborted bacause peer is null");
			return;
		}
		
		console.debug(requestId, "handleOffer: ", offer);
		peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
		
		console.debug("Will create answer");
		peerConnection.createAnswer(function(answer) {
			
			const peerConnection2 = getPeerConnection(requestId);
			console.debug("createAnswer: ", answer);
			log(requestId+" createAnswer");
			
			peerConnection2.setLocalDescription(answer);
	        send(requestId, {
	            event : "answer",
	            data : answer
	        });
	        _class.updatePeerConnection(requestId,peerConnection2 );
		}, function(error) {
		    console.error("error handle offer: ", error);
		});
		
		updatePeerConnection(requestId,peerConnection );
	}
	
	function handleCandidate(requestId, candidate){
		const peerConnection = getPeerConnection(requestId);
		
		log(requestId+" handleCandidate");
		if(!peerConnection){
			log("Aborted bacause peer is null");
			return;
		}
		
		console.debug(requestId, "handleCandidate: ", candidate);
		
		peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
		updatePeerConnection(requestId,peerConnection );
		showVideoElement(requestId);
	}
	
	function handleAnswer(requestId, answer) {
		const peerConnection = getPeerConnection(requestId);
		
		log(requestId+" handleAnswer");
		if(!peerConnection){
			log("Aborted bacause peer is null");
			return;
		}

		console.debug(requestId, "handleAnswer: ", answer);
		  if(peerConnection.signalingState == "stable"){ // && this.videoStream) {
			log("WILL ERROR? handle answer beacuse state is stable");
			//peerConnections[requestId]['connection'].addStream(this.videoStream); 
			//return;
		} 
		
		peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
		updatePeerConnection(requestId,peerConnection );
	}
		  
	function handlePartnerDial(requestId) {
		removePeerConnection(requestId);
		
		initWebRtc(requestId, true);
		updateVideoEvent(); 
	}
	
	function send(requestId, msg) {
		const eventId = randomNumber();
		console.debug("SEND WEBSOCKET, event: ", msg.event);
		log(">> SEND WEBSOCKET to "+requestId+" | "+eventId+" :"+msg.event);
		//console.info("Send Audio Data");
		sendToWebsocket("/app/publicconf1/webrtc", {
//			originId : requestId,
			originId : "${registeredRequest.requestId}",
			roomId: '${roomId}',
			eventId: eventId,
			destination:requestId,
		 	webRtcObject: (msg) 
		});
	}
	
	function sendChat(){
		const body = inputChatMessage.value;
		if(!body || body == ""){
			infoDialog("Please specify messge body!").then(function(e){ });
			return;
		}
		sendToWebsocket("/app/publicconf1/newchat", { 
			originId : "${registeredRequest.requestId}",
			roomId: '${roomId}',
			message: body
		});
		inputChatMessage.value = "";
	}
	
	function sendPeerConfirm(requestId){
		sendToWebsocket("/app/peerconfirm", { 
			originId : "${registeredRequest.requestId}",
			roomId: '${roomId}',
			destination: requestId
		}); 
	}
	
	function startRecordPeer(requestId, callback){
		postReq("<spring:url value="/api/webrtcroom/startrecording" />", {
			roomId : "${roomId}",
			originId : "${registeredRequest.requestId}",
			destination: requestId
		}, function(xhr) {
			infoDone();
			if(xhr.data && xhr.data.code == "00"){
				callback();
			}
			
		});
	}
	
	function sendToWebsocketV2(message){
		 if(!conn){
			 return;
		 }
		 conn.send(JSON.stringify(message));
	}
	
	function dialPartner(requestId){
		removePeerConnection(requestId);
		send(requestId, {
			event: 'dial',
			data: {}
		});
	}
	
	function redial(){
		for (var key in peerConnections) {
			if(isUserRequestId(key)){
	   			continue;
	   		}
			initWebRtc(key, true);
		}
	}
	
	function handleOnloadCallbacks(){
		console.debug("handleOnloadCallbacks: ", onloadCallbacks.length);
		log("handleOnloadCallbacks "+onloadCallbacks.length);
	
		for (var i = 0; i < onloadCallbacks.length; i++) {
			const callback = onloadCallbacks[i];
			const param = callback.param;
			const handler = callback.handler;
			handler(param);
		}
	}
	
	
</script>
<c:forEach var="member" items="${members}">
	<script>
	if("${member.requestId}" == "${registeredRequest.requestId}"){
		
	}else{
		onloadCallbacks.push({
		 	param: "${member.requestId}",
		 	handler: function(param){
		 		dialPartner(param);
		 	}
		}); 
	}
		
	</script>
</c:forEach>
<c:if test="${isRoomOwner == true }">
	<script type="text/javascript">
	function invalidateRoom() {
		confirmDialog("Invalidate Room ?").then(function(ok) {
			if (ok) {
				doInvalidateRoom();
			}
		});
	}

	function doInvalidateRoom() {
		postReq("<spring:url value="/api/webrtcroom/invalidate" />", {
			roomId : "${roomId}"
		}, function(xhr) {
			infoDone();
		});
	}
</script>
</c:if>
<script>
prepare();
initLiveStream();

if(isJoined == false){
	handleOnloadCallbacks();
	
}else {
	this.videoEnabled = !this.videoEnabled;
}

window.addEventListener('beforeunload', function(event) {
	doLeave();
});
window.addEventListener('unload', function(event) {
  
});

</script>