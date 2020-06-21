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
			<div class="session-item" id="${session.requestId }">
				<h3>ID: ${session.requestId }</h3>
				<c:if test="${registeredRequest.requestId == session.requestId }">
					<b>Your session</b>
				</c:if>
				<p>Created: ${session.created }</p>
				<p>Active: <span id="status-${registeredRequest.requestId }">${registeredRequest.active}</span></p>
				
				<c:if test="${registeredRequest.requestId != session.requestId }">
					<a class="btn btn-success"
						href="<spring:url value="/stream/videocall" />/${session.requestId }">Video
						Call</a>
				</c:if> 
				 
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
			const htmlTag = generateHtmlTextForSession(registeredRequest);
			sessionList.appendChild(htmlTag);
		}else{
			removeElementById(registeredRequest.requestId);
		}
	}

	function generateHtmlTextForSession(newRegisteredRequest) {
		const urlStream = "<spring:url value="/stream/videocall" />";
		const requestId = newRegisteredRequest.requestId;
		const isActive = newRegisteredRequest.active;
		const createdDate = newRegisteredRequest.created;
		const videoCallUrl = urlStream+"/"+requestId;
		
		/* const html = `<div class=\"session-item\" id=\"${requestId}\"><h3>ID: `
				+ requestId
				+ `</h3><p>Created: `+createdDate+`</p>`
				+ `<p>Active:<span id=\"status-`+requestId+`\">`+isActive+`</span></p>`
				+ `<a class=\"btn btn-success\" href=\"`+videoCallUrl+`\">Video Call</a></div>`;
				console.log("html", html); */
		const htmlv2 = createHtmlTag({
			'tagName':"div",
			'class': "session-item",
			'id':requestId,
			'ch1':{
				'tagName': "h3",
				'innerHTML': "ID: "+requestId
			},
			'ch2':{
				'tagName': "p",
				'innerHTML': "Created: "+createdDate
			},
			'ch3':{
				'tagName': "p",
				'innerHTML' :"Active:",
				'ch1':{
					'tagName': "span",
					'id': "status-"+requestId,
					'innerHTML': isActive
				}
			},
			'ch4':{
				'tagName': "a",
				'class': "btn btn-success",
				'href': videoCallUrl,
				'innerHTML': "Video Call"
			}
		});
		return htmlv2;

	}
	
	function buildHtmlTag(tagObj){
		
	}

	initWebSocket();
</script>