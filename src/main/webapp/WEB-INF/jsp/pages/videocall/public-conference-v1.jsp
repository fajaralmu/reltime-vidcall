<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Public Conference</h2>
	<div class="border row">
		<div class="col-6">
			<video height="200" width="200" muted="muted" controls id="my-video"></video>  
		</div>
		<div class="col-6"> 
		    <h3>Room : ${roomId }</h3>
		    <h3>User :${registeredRequest.requestId } <c:if test="${isRoomOwner == true }"><small>You Are Room Admin</small></c:if></h3> 
			
			<!-- 	<button class="btn btn-info  " onclick="redial()"><i class="fas fa-phone"></i>&nbsp;Redial</button> -->
			<button class="btn btn-danger  " onclick="leave()"><i class="fas fa-sign-out-alt"></i>&nbsp;Leave</button>
			<button onclick="clearLog()" class="btn btn-secondary"><i class="fas fa-trash-alt"></i>&nbsp;Clear Log</button>
			<c:if test="${isRoomOwner == true }">
				<button class="btn btn-danger  " onclick="invalidateRoom()"><i class="fas fa-times-circle"></i>&nbsp;Invalidate Room</button>
			</c:if>
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
	memberList = byId("member-list");
	eventLog = byId("event-log");
	const chatList = byId("chat-list");
	const inputChatMessage = byId("input-chat-message");
 
	
	function prepare() {
		const _class = this;

		onConnectCallbacks.push(function(frame) {
			console.log("Connected to signaling server-", frame);
			_class.join();
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
		connectToWebsocket(callbackMemberJoin, callbackMemberLeave, callbackWebRtcHandshake, callbackRoomInvalidated, callbackNewChat);
	}
	
	function handleNewChat(resp){
		const chatMessage = resp.chatMessage;
		//<div class="chat-message border-secondary rounded">
		//<p>${message.body }</p>
		//<p><i>${message.username }</i></p>
	//</div>
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
		initWebRtc(resp.requestId, true);
	}

	function addMemberList(response) {
		const username = response.username;
		const requestId = response.requestId;
		const date = response.date;
		const isRoomCreator = response.roomCreator;
		const memberElementObject = {
			tagName : 'div',
			id : "member-item-" + requestId,
			className: 'col-6',
			ch1 : {
				tagName : 'h5',
				innerHTML : '<i class="fas fa-user-circle"></i>&nbsp;'+username + (isRoomCreator? '<small><i class="fas fa-headset"></i></small>':''), 
			},
			ch2 : {
				tagName : 'p',
				innerHTML : 'Date: ' + date
			},
			ch3: {
				tagName: 'video',
				id: 'video-member-'+requestId,
				muted: 'muted',
				className: 'border',
				controls: '',
				height: 150,
				width: 150,
				style: {visibility: 'hidden'}
			},
			ch4: {
				tagName: 'div',
				id: 'video-controls-'+requestId,
				className: 'btn-group',
				role: 'group',
				ch1: {
					tagName: 'button',
					innerHTML : '<i class="fas fa-pause"></i>',
					className: 'btn',
					onclick: function(e){
						toggleVideoPlay('video-member-'+requestId, e.target); 
					}
				},
				ch2: {
					tagName: 'button',
					innerHTML : "<i class=\"fas fa-volume-mute\"></i>",
					className: 'btn',
					onclick: function(e){
						toggleVideoMute('video-member-'+requestId, e.target);
					}
				},
				ch3: {
					tagName: 'button',
					className: 'btn btn-info btn-sm',
					onclick: function(e){
						dialPartner(requestId);
					},
					innerHTML: '<i class="fas fa-phone"></i>&nbsp;Dial'
				}
			
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

		updateEventLog(username  + ' Joined');
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

		updateEventLog(username  + ' Leave');
	}
 

	function join() {
		sendToWebsocket("/app/publicconf1/join", {
			originId : "${registeredRequest.requestId}",
			roomId : "${roomId}"
		});
	}

	function leave() {
		
		confirmDialog("Want to leave ?").then(function(ok){
			if(ok){
				sendToWebsocket("/app/publicconf1/leave", {
					originId : "${registeredRequest.requestId}",
					roomId : "${roomId}"
				});
				window.location.href = "<spring:url value="/dashboard/" /> ";
			}
		})
		
		
	}

	prepare();
</script>
<script type="text/javascript">
	inCalling = true;
	var paused = false;
	var video;
	var myVideo = byId("my-video"); 
	
	function isUserRequestId(requestId){
		return requestId == "${registeredRequest.requestId}";
	}
	
	function updateVideoEvent() {
		const app = this;   
		if(app.videoStream){
			updateEventLog("VideoStream IS EXIST");
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
			updateEventLog("Updated Peer Count: "+peerCount);
			updateEventLog("Total Peer Count: "+totalPeer);
			return;
		}
		
		
	    window.navigator.mediaDevices.getUserMedia({ video: true, audio: true })
	        .then(function (stream) {
	        		app.videoStream = stream;
			       	console.debug("START getUserMedia"); 
			       	updateEventLog("Start handle user media");
			       	
			       	app.myVideo.srcObject = stream;
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
		            updateEventLog("End HandleMedia peerCount: "+peerCount);
		        }).catch(function (err) { console.error(error); });	    	
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
	 
	
	//////////////////////////// WebRTC stuff //////////////////////////
	
	
	function initWebSocketConference(){
		updateVideoEvent();
		const _class = this; 
		
	/* 	const callbackWebRtcHandshake = {
				subscribeUrl : "/wsResp/webrtcpublicconference/${roomId }",
				callback : function(resp){
					_class.handleWebRtcHandshake(resp.requestId, resp.webRtcObject);
				}
			};
		 
		connectToWebsocket( callbackWebRtcHandshake );   */
	}
	
	
	function handleWebRtcHandshake(eventId, requestId, webRtcObject){
		console.debug("handleWebRtcHandshake from ",requestId,": ", webRtcObject);
		
		if(isUserRequestId(requestId)){
			updateEventLog("## HANDSHAKE ABORTED "+eventId+"|"+webRtcObject.event.toUpperCase()+"|"+requestId);
   			return;
   		}
		updateEventLog("## HANDSHAKE "+eventId+"|"+webRtcObject.event.toUpperCase()+"|"+requestId);
		
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
			updateEventLog("# Will Update Video Event ");
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
		updateEventLog("doCreateOffer to "+requestId);
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
		if(!peerConnection){
			updateEventLog("Aborted bacause peer is null");
			return;
		}
		console.debug(requestId, "handleOffer: ", offer);
		
		peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
		
		console.debug("Will create answer");
		peerConnection.createAnswer(function(answer) {
			
			const peerConnection2 = getPeerConnection(requestId);
			console.debug("createAnswer: ", answer);
			updateEventLog(requestId+" createAnswer");
			
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
		
		
		updateEventLog(requestId+" handleCandidate");
		if(!peerConnection){
			updateEventLog("Aborted bacause peer is null");
			return;
		}
		
		console.debug(requestId, "handleCandidate: ", candidate);
		
		peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
		updatePeerConnection(requestId,peerConnection );
		showVideoElement(requestId);
	}
	
	function handleAnswer(requestId, answer) {
		const peerConnection = getPeerConnection(requestId);
		
		updateEventLog(requestId+" handleAnswer");
		if(!peerConnection){
			updateEventLog("Aborted bacause peer is null");
			return;
		}

		console.debug(requestId, "handleAnswer: ", answer);
		  if(peerConnection.signalingState == "stable"){ // && this.videoStream) {
			updateEventLog("WILL ERROR? handle answer beacuse state is stable");
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
		updateEventLog(">> SEND WEBSOCKET to "+requestId+" | "+eventId+" :"+msg.event);
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
//			originId : requestId,
			originId : "${registeredRequest.requestId}",
			roomId: '${roomId}',
			message: body
		});
		inputChatMessage.value = "";
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
	
	 
	initLiveStream(); 
</script>
<c:forEach var="member" items="${members}">
	<script>
	if("${member.requestId}" == "${registeredRequest.requestId}"){
		
	}else{
		initWebRtc("${member.requestId}", true);
	}
		
	</script>
</c:forEach>
<c:if test="${isRoomOwner == true }">
	<script>
		function invalidateRoom(){
	 		confirmDialog("Invalidate Room ?").then(function(ok){
	 			if(ok){
	 				doInvalidateRoom();
	 			}
	 		});
		}
		
		function doInvalidateRoom(){
			postReq("<spring:url value="/api/webrtcroom/invalidate" />", {roomId: "${roomId}"},
					function(xhr) {
						infoDone();
						 
					});
		}
	</script>
</c:if>