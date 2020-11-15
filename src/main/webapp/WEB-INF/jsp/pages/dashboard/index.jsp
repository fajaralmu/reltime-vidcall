<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Dashboard</h2>
	<div class="row">
		<div class="col-6">
			<form onsubmit="return false">
				<div class="card">
					<div class="card-header">Generate Room Id</div>
					<div class="card-body">
						<p><i class="fas fa-key"></i>&nbsp;Room Id <span id="room-id">${roomId == null ? "Not Generated" : roomId}</span></p>
						<p><i class="fas fa-link"></i>&nbsp;Link <a  id="room-link"></a></p> 
					</div>
					<div class="card-footer">
						<button class="btn btn-primary" onclick="generateRoomId()" id="footer-info">
							${roomId != null? 'Update Room Id' : 'Generate Room Id'}</button>
						<button class="btn btn-danger" onclick="invalidateRoom()" >Invalidate Room</button>
					</div>
				</div>
			</form>
		</div>
		<div class="col-6">
			<form onsubmit="goToExistingRoom(); return false;">
				<div class="card">
					<div class="card-header">Go To Existing Room</div>
					<div class="card-body">
						<p>Enter Existing Room</p>
						<input type="text" class="form-control" placeholder="Existing Room Id" id="input-existing-room" />
						
					</div>
					<div class="card-footer">
						<input type="submit" class="btn btn-primary" value="Submit"/>
					</div>
				</div>
			</form>
			<form onsubmit="goToChattingPage(); return false;">
				<div class="card">
					<div class="card-header">Go To Chatting Page</div>
					<div class="card-body">
						<p>Enter Partner Id</p>
						<input type="text" class="form-control" placeholder="Existing Room Id" id="input-message-partner" />
						
					</div>
					<div class="card-footer">
						<input type="submit" class="btn btn-primary" value="Submit"/>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<script> 
	
	const footerInfo = byId("footer-info");
	const roomId = byId("room-id");
	const roomLink = byId("room-link");
	const roomIdExisting = byId("input-existing-room");
	const partnerIdForChatting = byId("input-message-partner");
	var currentRoomId = "${roomId}";
	
	function goToExistingRoom(){
		if(null == roomIdExisting.value){
			infoDialog("Please specify room ID!").then(function(e){});
			return;
		}
		window.location.href = "<spring:url value="/stream/publicconference/" />"+roomIdExisting.value;
	}
	
	function goToChattingPage(){
		if(null == partnerIdForChatting.value){
			infoDialog("Please specify partnerIdForChatting!").then(function(e){});
			return;
		}
		window.location.href = "<spring:url value="/dashboard/chatting/" />"+partnerIdForChatting.value;
	}
	
	function generateRoomId(){
		confirmDialog("Do You want to generate/update Room? The existing room will be invalidated... ")
		.then(function(ok){  
			if(ok){
				doGenerateRoomId();
			}
		})
	}
		
	function doGenerateRoomId(){
		postReq("<spring:url value="/api/webrtcroom/generateroomid" />", {},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if(response && response.code == "00"){
						
						updateRoomInfo(response.message);
						
						const link = "<spring:url value="/stream/publicconference/"/>"+(response.message);
						updateLink(link);
					}else if(response){
						infoDialog(response.message).then(function(e){});
					}else{
						alert("Server Error");
					}
				});
	}
	
	function updateRoomInfo(id){
		footerInfo.innerHTML = id == null ? 'Generate Room Id' : 'Update Room Id';
		roomId.innerHTML = id;
		currentRoomId = id;
	}
	
	function updateLink(link){
		if(null == link){
			roomLink.setAttribute("href", "");
			roomLink.innerHTML = "";
		}else{
			roomLink.setAttribute("href", link);
			roomLink.innerHTML = "Go To Room";
		}
		
	}
	
</script>
<script type="text/javascript">
	function invalidateRoom() {
		if(null == currentRoomId || "" == currentRoomId){ return; }
		confirmDialog("Invalidate Room ?").then(function(ok) {
			if (ok) {
				doInvalidateRoom();
			}
		});
	}

	function doInvalidateRoom() {
		postReq("<spring:url value="/api/webrtcroom/invalidate" />", {
			roomId : currentRoomId
		}, function(xhr) {
			infoDone();
			updateLink(null);
			updateRoomInfo(null);
		});
	}
</script>
<c:if test="${roomId != null }" >
<script>
	updateLink("<spring:url value="/stream/publicconference/"/>${roomId}");
</script>
</c:if>