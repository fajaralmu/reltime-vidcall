<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>

<div>
	<h2>Available Sessions</h2>
	<div id="session-list">
		<c:forEach var="session" items="${sessions }">

			<c:if test="${registeredRequest.requestId != session.requestId }"></c:if>
			<div id="${session.requestId }">
				<h3>ID:${session.requestId }</h3>
				<p>Created: ${session.created }</p>
				<p>Active: <span id="status-${registeredRequest.requestId }">${registeredRequest.active}</span></p>
				<c:if test="${registeredRequest.requestId != session.requestId }">
					<a class="btn btn-success"
						href="<spring:url value="/stream/videocall" />/${session.requestId }">Video
						Call</a>
				</c:if>
				<c:if test="${registeredRequest.requestId == session.requestId }">
					<b>Your session</b>
				</c:if>
				<hr />
			</div>

		</c:forEach>
	</div>
</div>
<script type="text/javascript">
	const sessionList = _byId("session-list");

	function initWebSocket() {
		const _class = this;
		const callbackObject1 = {
			subscribeUrl : "/wsResp/sessions",
			callback : function(resp) {
				_class.updateSessionList(resp);
			}

		};
		const callbackObject2 = {
			subscribeUrl : "/wsResp/sessionstatus",
			callback : function(resp) {
				_class.updateSessionStatus(resp);
			}

		};
		connectToWebsocket(callbackObject1, callbackObject2);
	}

	function updateSessionStatus(response) {
		const status = response.registeredRequest.active;
		const requestId = response.registeredRequest.requestId;
		
		_byId("status-"+requestId).innerHTML = status;
	}

	function updateSessionList(response) {
		const registeredRequest = response.registeredRequest;
		if(!registeredRequest){
			return;
		}
		
		if(registeredRequest.exist){
			sessionList.innerHTML += generateHtmlTextForSession(registeredRequest);
		}else{
			removeElementById(registeredRequest.requestId);
		}
	}

	function generateHtmlTextForSession(registeredRequest) {
		const urlStream = "<spring:url value="/stream/videocall" />";
		const html = "<div id=\""+ registeredRequest.requestId +"\"><h3>ID:"
				+ registeredRequest.requestId
				+ "</h3><p>Created:"
				+ registeredRequest.created
				+ "</p><p>Active:<span id=\"status-"+registeredRequest.requestId+"\">"
				+ registeredRequest.active
				+ "</span></p>"
				+ "<a class=\"btn btn-success\" href=\"" + urlStream+"/"+ registeredRequest.requestId+"\">"
				+ "Video Call</a>" + "<hr/></div>";

		return html;

	}

	initWebSocket();
</script>