<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 

<div>
<h2>Main Menu Page</h2>

<h3>Request ID:</h3>
<p id="req-id-generated">${currentRequest == null? 'Not Generated' : currentRequest.requestId } </p>
<a class="btn btn-success" href="<spring:url value="/stream/sessionlist" /> ">View Available Sessions</a> 
<c:if test="${currentRequest == null }">
	<button class="btn btn-info" onclick="registerSession()">Register</button>
</c:if>

</div>
<c:if test="${currentRequest == null }">
	<script type="text/javascript" >
		
		function registerSession(){
			const requestObject = {};
			postReq("<spring:url value="/api/stream/register" />",
					requestObject, function(xhr) {
						infoDone();
						var response = (xhr.data);
						_byId("req-id-generated").innerHTML =  response.registeredRequest.requestId;
					});
		}
	
	</script>
</c:if>