<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Main Menu Page</h2>
	<div class="row">
		<div class="col-6">
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
						${registeredRequest == null? "" : "disabled" }
						value="${registeredRequest == null? null : registeredRequest.username }" />

				</div>
				<div class="card-footer">
					<a class="btn btn-success"
						href="<spring:url value="/stream/sessionlist" /> "><i
						class="fa fa-list-ul" aria-hidden="true"></i> Available Sessions</a>
					<c:if test="${registeredRequest == null }">
						<button id="btn-register" class="btn btn-info"
							onclick="registerSession()">Register</button>
					</c:if>
					<button class="btn btn-danger" onclick="invalidate()">Invalidate</button>
				</div>
			</div>
		</div>
		<div class="col-6">
			<div class="card">
				<div class="card-header">
					<i class="fa fa-sticky-note"></i>&nbsp;README
				</div>
				<div class="card-body">
					<ol>
						<li>Register Username</li>
						<li>Check Available Sessions</li>
						<li>Call The User</li>
					</ol>
				</div>
			</div>
		</div>
	</div>
</div>


<script type="text/javascript">
	const inputUserName = byId("input-username");
	const buttonRegister = byId("btn-register");

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
			if (inputUserName.value == null || inputUserName.value.trim() == "") {
				infoDialog("Please specify username!").then(function(E) {
				});
				return;
			}
			const requestObject = {
				username : inputUserName.value
			};
			postReq(
					"<spring:url value="/api/stream/register" />",
					requestObject,
					function(xhr) {
						infoDone();
						var response = (xhr.data);
						if (response.code == "00") {
							byId("req-id-generated").innerHTML = response.registeredRequest.requestId;
							initCallbackCalling(response.registeredRequest.requestId);
							buttonRegister.style.display = "none";
							inputUserName.setAttribute("disabled", "disabled");
						}

					});
		}
	</script>
</c:if>