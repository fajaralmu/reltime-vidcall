<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2><i class="fas fa-comments"></i>&nbsp;Chatting With: ${partner.username}</h2>
	<div class="row">
		<div class="col-6">
			<form id="message-form">
				<div class="form-group">
					<label for="chat-message">Message</label> <input type="text"
						class="form-control" id="chat-message" placeholder="message">
				</div>
				<button type="submit" class="btn btn-primary">Send</button>
			</form>
			<p id="is-typing"></p>
		</div>
		<div class="col-6">
			<div id="chatting-message" style="overflow: scroll; height: 500px; padding: 10px">
				<c:forEach items="${messages }" var="message">
					<div class="alert ${registeredRequest.requestId == message.requestId ? 'alert-success':'alert-secondary' }" >  
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
						chatMessage.value = "";
						addChattingMessage(response);
					}else if(response){
					}else{
						alert("Server Error");
					}
				});
	}
	
	function subscribeWebsocket(){
		const _class = this;
		const callbackIncomingMessage = {
				subscribeUrl : "/wsResp/newchatting/${registeredRequest.requestId}",
				callback : function(resp) {
					_class.addChattingMessage(resp);
				}

			};
		const callbackPartnerIsTyping = {
				subscribeUrl : "/wsResp/typingstatus/${partner.requestId}/${registeredRequest.requestId}",
				callback : function(resp) {
					_class.infoPartnerIsTyping(resp);
				}

			};
		connectToWebsocket(callbackIncomingMessage, callbackPartnerIsTyping);
	}
	
	function infoPartnerIsTyping(response){
		if(response.typing){
			byId("is-typing").innerHTML = "${partner.username} is typing";
		} else {
			byId("is-typing").innerHTML = "";
		}
	}
	
	function addChattingMessage(response){
		const chatElement = generateChatMessageElement(response.chatMessage);
		byId("chatting-message").appendChild(chatElement);
		
	}
	
	function generateChatMessageElement(message){
		const alertType = message.requestId == "${registeredRequest.requestId}" ? "alert-success" : "alert-secondary";
		const htmlv2 = createHtmlTag({ 'tagName':"div", 'class': "alert "+alertType,
			'ch1':{
				'tagName': "b",
				'innerHTML': message.requestId == "${partner.requestId}" ? "${partner.username}" : "You"
			},
			'ch2':{
				'tagName': "p", 'innerHTML': message.body
			},
			'ch3':{
				'tagName': "p", 'innerHTML': new Date(message.date)
			},
		});
		return htmlv2;
	}
	
	function sendTypingInfoViaWebsocket(isTyping){
		sendToWebsocket("/app/chatting/typingstatus", {
			originId : "${registeredRequest.requestId}",
			destination : "${partner.requestId}",
			typing: isTyping
		});
	}
	
	byId("chat-message").onkeydown = function(e){
		sendTypingInfoViaWebsocket(true);
	}
	
	byId("chat-message").onkeyup = function(e){
		sendTypingInfoViaWebsocket(false);
	}
	
	byId("message-form").onsubmit = function(e){
		e.preventDefault();
		if(chatMessage.value == null || chatMessage.value == ""){
			return;
		}
		sendTypingInfoViaWebsocket(false);
		sendMessage();
	}
	
	subscribeWebsocket();
	
</script>
