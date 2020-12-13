<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type"
	content="text/html; charset=windows-1256">
<meta name="description" content="${applicationDescription }">
<meta property="og:title" content="${applicationHeaderLabel }" >
<meta property="og:url" content="https://realtime-videocall.herokuapp.com/" >
<meta property="og:description" content="${applicationDescription }">
<meta property="og:site_name" content="${applicationHeaderLabel }">
<meta property="og:image" itemprop="image" content="https://realtime-videocall.herokuapp.com/res/img/Flag_of_Indonesia_200.png" >
<meta property="og:type" content="website" >

<title>${title}</title>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico"></c:url >"
	type="image/x-icon">

<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/css/shop.css?version=1"></c:url>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/res/fa/css/all.css" />" />
<link rel="stylesheet"
	href="<c:url value="/res/css/bootstrap/bootstrap.min.css" />" />
<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js" />"></script>
<script src="<c:url value="/res/js/popper.min.js" />"></script>
<script src="<c:url value="/res/js/bootstrap/bootstrap.min.js"  />"></script>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>
<script src="<c:url value="/res/js/ajax.js?v=1"></c:url >"></script>
<script src="<c:url value="/res/js/util.js?v=1"></c:url >"></script> 
<script src="<c:url value="/res/js/dialog.js?v=1"></c:url >"></script>

<script src="<c:url value="/res/fa/js/all.js?v=1"></c:url >"></script>

<c:forEach var="stylePath" items="${additionalStylePaths }">
	<link rel="stylesheet"
		href="<c:url value="/res/css/pages/${ stylePath.value}.css?version=1"></c:url >" />
</c:forEach>
<c:forEach var="scriptPath" items="${additionalScriptPaths }">
	<script
		src="<c:url value="/res/js/pages/${scriptPath.value }.js?v=1"></c:url >"></script>
</c:forEach>



<script type="text/javascript">

	var ipAndPort = "${ipAndPort}";
	var ctxPath = "${contextPath}";
	var websocketUrl = "<spring:url value="/realtime-app" />";
	var websocketUrlv2 = "${ipAndPort}<spring:url value="/socket" />";
	var inCalling = false;
</script>
<style>  
.centered-align {
	text-align: center;
	width: 100%;
}
</style>
</head>
<body>
	<div id="progress-bar-wrapper" onclick="hide('progress-bar-wrapper');"
		class="box-shadow"
		style="display: none; height: 50px; padding: 10px; background-color: white; margin: auto; position: fixed; width: 100%">
		<div class="progress">
			<div id="progress-bar"
				class="progress-bar progress-bar-striped bg-info" role="progressbar"
				aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
		</div>
	</div>
	  <input id="token-value" value="${pageToken }" type="hidden" />
	<input id="request-id" value="${requestId }" type="hidden" />
	<input id="registered-request-id" value="${registeredRequestId }" type="hidden" />  
	<div id="loading-div"></div>
	<div class="container-fluid ">
		<jsp:include page="include/head.jsp"></jsp:include>
		<div class="container-fluid row">
			<div class="col col-md-2 sidebar-custom">
				<jsp:include page="include/sidebar.jsp"></jsp:include>
			</div>
			<div class="col">
				<jsp:include page="${pageUrl == null? 'error/notfound': pageUrl}.jsp"></jsp:include>
			</div>
		</div>
		<jsp:include page="include/foot.jsp"></jsp:include>
		
	</div>
	<script type="text/javascript">
		document.body.onload = function() {
			//console.log("init progress websocket");
			//initProgressWebsocket();
			performWebsocketConnection();

		}
		
		function initCallbackCalling(requestId){
			const callbackNofityCall = {
					subscribeUrl : "/wsResp/notifycall/"+requestId,
					callback : function(resp){
						const caller = resp.requestId;
						const username = resp.username;
						const url = "<spring:url value="/stream/videocallv2/" />"+caller+"?referrer=calling";
						
						if(inCalling){
							sendToWebsocket("/app/acceptcall", { accept:false, destination: caller, message: 'busy', originId: requestId }); 
							
						}else{
							confirmDialog("&nbsp;<h4>"+username+"("+caller+")</h4> want to call you.. ", {dialogIcon:"fa fa-user-circle", yesIcon:"fa fa-phone", yesText:"Accept", noIcon:"fa fa-phone", noText:"Decline"})
							.then(function(ok){ 
									sendToWebsocket("/app/acceptcall", { accept:ok, destination: caller, originId: requestId }); 
									if(ok){
										window.location.href = url; 
									}
							})
						}
					}					
				};
			connectToWebsocket( callbackNofityCall); 
		}
		
		function leaveCalling(callback){
			postReq("<spring:url value="/api/webrtc2/leavecall" />",
					{}, function(xhr) { if(callback){callback(xhr.data);} });
		}
		
		
	</script>
	<c:if test="${registeredRequest != null }">
		<script type="text/javascript">
			initCallbackCalling("${registeredRequest.requestId }");
		</script>
	</c:if>
	<script type="text/javascript">
		const elementHavingOnEnters = document.getElementsByClassName("onenter");
		
		function initOnEnterListener(){
			for (var i = 0; i < elementHavingOnEnters.length; i++) {
				const element = elementHavingOnEnters[i];
				const onEnter = element.getAttribute("on-enter");
				
				if(onEnter) {
					const originalKeyup = element.onkeyup;
					element.onkeyup = function(event){
						
						if(originalKeyup){
							originalKeyup(event);
						}
						
						if (event.keyCode === 13) { //when key is 'Enter'
						    event.preventDefault(); 
						    eval(onEnter);
						}
					}
				}
			}
		}
		
		initOnEnterListener();
	</script>
</body>
</html>