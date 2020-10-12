<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="border border-primary rounded " id="member-list">
	<h3 style="text-align: center">Member List</h3>
	<div class="row">
		<c:forEach var="member" items="${members}">
			<div class="col-6" id="member-item-${member.requestId}">
				<span>
					<c:if test="${true ==  member.roomCreator}">
						 <i class="fas fa-headset"></i> 
					</c:if>
					<i class="fas fa-user-circle"></i>&nbsp;${member.username } 
				</span>
				<%-- <p>${member.created }</p> --%>
				<c:if test="${member.requestId != registeredRequest.requestId }">
					<video class="border"
						style="visibility: hidden; display: ${member.conferenceMemberData.streamEnabled? 'block' : 'none'}"
						height="150" width="150" muted="muted"
						id="video-member-${member.requestId }"></video>
					<c:if test="${member.conferenceMemberData.streamEnabled == false}">
						<div id="video-cover-${member.requestId }"
							class="video-cover rounded align-middle">
							<h1>
								<i class="fas fa-video-slash"></i>
							</h1>
						</div>
					</c:if>
					<div style="display: ${member.conferenceMemberData.streamEnabled? 'block' : 'none'}"
						 id="video-control-${member.requestId }">
						<div class="btn-group"role="group">
							<button class="btn btn-secondary btn-sm" onclick="toggleVideoPlay('video-member-${member.requestId }', this);">
								<i class="fas fa-pause"></i>
							</button>
							<button class="btn btn-secondary btn-sm" onclick="toggleVideoMute('video-member-${member.requestId }', this);">
								<i class="fas fa-volume-down"></i>
							</button>
							<button class="btn btn-secondary btn-sm" onclick="showMemberFullscreen('video-member-${member.requestId }');">
								<i class="fas fa-expand"></i>
							</button>
						</div>
					
						<div class="btn-group" role="group" id="group-dial-record">
							<button class="btn btn-info btn-sm" onclick="dialPartner('${member.requestId}')">
								<i class="fas fa-phone"></i>&nbsp;Dial
							</button>
							<button class="btn btn-secondary btn-sm" onclick="startRecording('${member.requestId}')" id="toggle-record-${member.requestId}">
								<i class="fas fa-record-vinyl"></i> Rec
							</button>
						</div>
					</div>
					
				</c:if>
				<c:if test="${member.requestId == registeredRequest.requestId }">
					<h3 class="center-aligned bg-light">You</h3>
				</c:if>
			</div>
		</c:forEach>
	</div>
</div>


<script type="text/javascript">
	const lengthInMS = 10000;
	const downloadButton = byId("btn-download-recorded");
	const recordingTimer = byId("recording-timer");
	
	isRecording = false;
	
	var recorder = null;
	var recordingId = null; //peerID
	var schedulerId = null;

	function startRecording(requestId){
		
		if(isRecording){
			infoDialog("Currently Recording ID: "+recordingId+", please stop it first!").then(function(e){});
			return;
		}
		
		confirmDialog("Start Recording Peer "+requestId+" ?").then(function(ok){
			if(ok){
				startRecordPeer(requestId, function(response){	
					prepareAndRecord(requestId);
					
					//Timer Client Side
					initClientSideRecordingTimer();
					schedulerId = response.message;
				});
			}
		});
	}
	
	function prepareAndRecord(requestId){
		log("Start Recording");
		
		clearDownloadButtonProps();
		recordingId = null;
		updateToggleRecordButton(requestId, false);
		
		recordVideo(requestId).then(function(recordedChunks){
			isRecording = false;
			
			log("END Recording");
			
			infoDialog("Recording End, Click Download Button").then(function(e){
				
				const recordedBlob = new Blob(recordedChunks, { type: "${recordingOutputFormat}" });
			    downloadButton.href = URL.createObjectURL(recordedBlob);
			    downloadButton.download = "RecordedVideo_"+requestId+ getDateString() + "${recordingOutputExtension}";
			    
			});
		});
	}
	
	function getDateString(){
		const date = new Date();
		return date.toISOString().replaceAll(":", "");
	}
	
	function updateToggleRecordButton(requestId, enableRecording){
		const btn = byId("toggle-record-"+requestId);
		if(btn == null){
			return;	
		}
		if(enableRecording){
			
			btn.innerHTML = "<i class=\"fas fa-record-vinyl\"></i> Rec";
			btn.onclick = function(e){
				startRecording(requestId);
			}
			
		} else {
			 
			btn.innerHTML = "<i class=\"fas fa-stop\"></i>&nbsp;<span class=\"spinner-grow spinner-grow-sm\" role=\"status\" aria-hidden=\"true\"></span> Rec";
			btn.onclick = function(e){
				stopRecordingOnClick(requestId);
			}
			 
		}
	}
	
	function recordVideo(requestId) {
		
		const vid = byId("video-member-"+requestId);
		
		this.recordingId = requestId;
		this.isRecording = true;
		this.recorder = new MediaRecorder(vid.srcObject);
		
		const data = [];
		log("Will Start Recording");
		
		recorder.ondataavailable = function(event) { data.push(event.data); }
		recorder.start();
		 
		const stopped = new Promise(function(resolve, reject){
			
			/* while(recorder.state != "inactive"){
				byId("recording-timer").innerHTML = new Date();
			} */
			
		    recorder.onstop = function (event){  
				currentCounter = 0;
				resolve(event);
			}
		    recorder.onerror = function (event) { 
		    	reject(event.name); 
		    }
		});
		
		/* const recorded = wait(lengthInMS).then(
		    () => recorder.state == "recording" && recorder.stop()
		); */
		
		return Promise.all([
		    stopped, //  recorded
		  ])
		.then(function() { return data; });
	}
	
	function clearDownloadButtonProps(){
		downloadButton.href = "";
		downloadButton.download = "";
	}
	
	function forceStopRecording(requestId, message){
		infoDialog(message).then(function(e){ });
		
		doStopRecording(requestId);
	}
	
	function stopRecordingOnClick(requestId) {

		confirmDialog("Stop recording ?").then(function(ok) {
			if(ok){
				 stopRecording(requestId);
			}
		});
	}
	
	function stopRecording(requestId){
		if(requestId == recordingId){
			notifyStopRecording(function(resp) {
				doStopRecording(requestId);
			});
			
		}
	}
	
	function doStopRecording(requestId) {
		isRecording = false;
		recorder.stop();
		updateToggleRecordButton(requestId, true);
	}
	
	/**
		notify scheduler to stop counting
	*/
	function notifyStopRecording(callback) {
		if(schedulerId == null){
			return;
		}
		const url = "<spring:url value="/api/webrtcroom/stoprecording" />/"+schedulerId;
		postReqEmptyBody(url, function(xhr) {
			infoDone();
			if(xhr.data && xhr.data.code == "00"){
				callback(xhr.data);
			} else{
				alert("Error");
			}
			
		});
	}

</script>
