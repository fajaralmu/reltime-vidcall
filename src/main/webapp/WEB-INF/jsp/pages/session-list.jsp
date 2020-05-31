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
				<h3>ID:${session.requestId } </h3>
				<p>Created: ${session.created }</p>
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
		const callbackObject = {
			subscribeUrl : "/wsResp/sessions",
			callback : function(resp) {
				_class.addSessionList(resp);
			}

		};
		connectToWebsocket(callbackObject);
	}

	function addSessionList(response) {
		sessionList.innerHTML += generateHtmlTextForSession(response.registeredRequest);
	}

	function generateHtmlTextForSession(regisreredRequest) {
		const urlStream = "<spring:url value="/stream/videocall" />";
		const html = "<div id=\""+regisreredRequest.requestId+"\"><h3>ID:"
				+ regisreredRequest.requestId
				+ " <p>Created:"
				+ regisreredRequest.created
				+ "</p></h3> \r\n"
				+ "<a class=\"btn btn-success\" href=\"" + urlStream+"/"+ regisreredRequest.requestId+"\">"
				+ "Video Call</a>" + "<hr/></div>";

		return html;

	}

	initWebSocket();
</script>