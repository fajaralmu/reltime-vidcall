<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div>
	<ul class="nav nav-tabs" id="tab-event-log-chat" role="tablist">
		<li class="nav-item"><a class="nav-link active" id="chat-tab"
			data-toggle="tab" href="#chat-panel" role="tab"
			aria-controls="chat-panel" aria-selected="true">Chat <span class="badge badge-secondary" id="info-chat-count">${chatMessages.size() }</span></a></li>
		<li class="nav-item"><a class="nav-link" id="log-tab"
			data-toggle="tab" href="#log-panel" role="tab"
			aria-controls="log-panel" aria-selected="false">Log <span class="badge badge-secondary" id="info-logs-count">0</span></a></li>
	</ul>
	<div class="tab-content" id="myTabContent">
		<div class="tab-pane fade show active" id="chat-panel" role="tabpanel"
			aria-labelledby="chat-panel">
			<div class="border border-primary rounded">
				<h3 style="text-align: center; color: #cccccc" class="bg-dark">Chat
					</h3>
				<div style="padding: 3px">
					<div class="input-group mb-3">
						<input type="text" class="form-control onenter" on-enter="sendChat()" id="input-chat-message" />
						<div class="input-group-append">
							<button class="btn btn-info" onclick="sendChat()"><i class="fas fa-paper-plane"></i></button>
						</div>
					</div>
				</div>
				<div id="chat-list">
					<c:forEach var="message" items="${chatMessages }">
						<div class="chat-message-${message.requestId == registeredRequest.requestId ? 'user' : 'common' }">

							<span>${message.requestId == registeredRequest.requestId ? 'You' : message.username }</span>
							<h4>${message.body }</h4>
							<span>${message.date }</span>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="tab-pane fade" id="log-panel" role="tabpanel"
			aria-labelledby="log-panel">
			<div class="border border-primary rounded bg-dark">
				<h3 style="text-align: center; color: #cccccc" class="bg-dark">Event
					Log </h3>
				<div id="event-log"></div>
			</div>
		</div>
	</div>

</div>