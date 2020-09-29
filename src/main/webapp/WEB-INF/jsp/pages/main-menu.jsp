<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Main Menu Page</h2>

	<div class="card">
		<div class="card-header">
			<i class="fas fa-address-card"></i>&nbsp;Request Information
		</div>
		<div class="card-body">
			<p>
				Request ID : <span class="font-weight-bold" id="req-id-generated">${registeredRequest == null? 'Not Generated' : registeredRequest.requestId }
				</span>
			</p>
			<p>Alias:</p>
			<input class="form-control" id="input-username"
				placeholder="type username"
				value="${registeredRequest == null? null : registeredRequest.username }" />

		</div>
		<div class="card-footer">
			<a class="btn btn-success"
				href="<spring:url value="/stream/sessionlist" /> "><i
				class="fa fa-list-ul" aria-hidden="true"></i> Available Sessions</a>
			<c:if test="${registeredRequest == null }">
				<button class="btn btn-info" onclick="registerSession()">Register</button>
			</c:if>
			<button class="btn btn-danger" onclick="invalidate()">Invalidate</button>
		</div>
	</div>
</div>


<script type="text/javascript">
	function invalidate() {
		const requestObject = {};
		postReq("<spring:url value="/api/stream/invalidate" />", requestObject,
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					window.location.reload();
				});
	}
</script>
<c:if test="${registeredRequest == null }">
	<script type="text/javascript">
		function registerSession() {
			if (byId("input-username").value == null
					|| byId("input-username").trim().value == "") {
				infoDialog("Please specify username!").then(function(E) {
				});
				return;
			}
			const requestObject = {
				username : byId("input-username").value
			};
			postReq(
					"<spring:url value="/api/stream/register" />",
					requestObject,
					function(xhr) {
						infoDone();
						var response = (xhr.data);
						byId("req-id-generated").innerHTML = response.registeredRequest.requestId;
						initCallbackCalling(response.registeredRequest.requestId);
					});
		}
	</script>
</c:if>