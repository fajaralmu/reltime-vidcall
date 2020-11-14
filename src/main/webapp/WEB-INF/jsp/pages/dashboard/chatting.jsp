<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Chat Message With: ${partner.username}</h2>
	<div class="row">
		<div class="col-6">
			<form id="message-form">
				<div class="form-group">
					<label for="chat-message">Message</label> <input type="text"
						class="form-control" id="chat-message" placeholder="message">
				</div>
				<button type="submit" class="btn btn-primary">Send</button>
			</form>
		</div>
		<div class="col-6">
			<div>
				<h3>Message</h3>
				<c:forEach items="${messages }" var="message">
					<div class="session-item" >  
						<c:if test="${registeredRequest.requestId == message.requestId }">
							<b>You</b>
						</c:if>
						<c:if test="${partner.requestId == message.requestId }">
							<b>${partner.username }</b>
						</c:if>
						<p>${message.body }</p>
						<p>${message.date }</p> 
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</div>
<script>
	const chatMessage= byId("chat-message");
	function sendMessage(){
		postReq("<spring:url value="/api/chatting/send/${partner.requestId}"/>", { message: chatMessage.value},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if(response && response.code == "00"){  
						 
					}else if(response){
						//infoDialog(response.message).then(function(e){});
					}else{
						alert("Server Error");
					}
				});
	}
	
	byId("message-form").onsubmit = function(e){
		e.preventDefault();
		sendMessage();
	}
	
</script>