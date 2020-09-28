<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 

<div>
<h2>Main Menu Page</h2>

<h3>Request Information</h3>

<p>Current Request ID : <b><span id="req-id-generated">${registeredRequest == null? 'Not Generated' : registeredRequest.requestId } </span></b></p>
<a class="btn btn-success" href="<spring:url value="/stream/sessionlist" /> ">View Available Sessions</a> 
<c:if test="${registeredRequest == null }">
	<button class="btn btn-info" onclick="registerSession()">Register</button>
</c:if>
<button class="btn btn-danger" onclick="invalidate()">Invalidate</button>
</div>
<script type="text/javascript">
	function invalidate(){
		const requestObject = {};
		postReq("<spring:url value="/api/stream/invalidate" />",
				requestObject, function(xhr) {
					infoDone();
					var response = (xhr.data);
					window.location.reload();
				});
	}
</script>
<c:if test="${registeredRequest == null }">
	<script type="text/javascript" >
		
		function registerSession(){
			const requestObject = {};
			postReq("<spring:url value="/api/stream/register" />",
					requestObject, function(xhr) {
						infoDone();
						var response = (xhr.data);
						byId("req-id-generated").innerHTML =  response.registeredRequest.requestId;
						initCallbackCalling(response.registeredRequest.requestId);
					});
		} 
	
	</script>
</c:if>