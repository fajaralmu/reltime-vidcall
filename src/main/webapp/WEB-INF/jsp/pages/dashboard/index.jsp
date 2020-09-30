<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Dashboard</h2>
	<div class="row">
		<div class="col-6">
			<div class="card">
				<div class="card-header">Generate Room Id</div>
				<div class="card-body">
					<p>
						Current Room Id: <span id="room-id">${roomId}</span>
						
					</p>
					<p>Link <span id="room-link"></span></p>
					<button class="btn btn-primary" onclick="generateRoomId()">Generate</button>
				</div>
				<div class="card-footer">
					<span id="footer-info">${roomId != null? 'Update Room Id' : 'Generate Room Id'}</span>
				</div>
			</div>
		</div>
	</div>
</div>
<script> 
	
	const footerInfo = byId("footer-info");
	const roomId = byId("room-id");
	const roomLink = byId("room-link");
		
	function generateRoomId(){
		postReq("<spring:url value="/api/webrtcroom/generateroomid" />", {},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if(response && response.code == "00"){
						footerInfo.innerHTML = 'Update Room Id';
						roomId.innerHTML = response.message;
						roomLink.innerHTML = ipAndPort+ctxPath +"/stream/publicconference/"+(response.message);
					}else if(response){
						infoDialog(response.message).then(function(e){});
					}else{
						alert("Server Error");
					}
				});
	}
	
</script>
<c:if test="${roomId != null }" >
<script>
	roomLink.innerHTML = ipAndPort+ctxPath +"/stream/publicconference/${roomId}";
</script>
</c:if>