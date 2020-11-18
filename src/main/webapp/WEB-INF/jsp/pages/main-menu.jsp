<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Main Menu</h2>
	<div class="row">
		<div class="col-6">
			<form id="form-register">
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
						<input class="form-control onenter" on-enter="registerSession()"
							id="input-username" placeholder="type username"
							${registeredRequest == null? "" : "disabled" }
							value="${registeredRequest == null? null : registeredRequest.username }" />
					</div>
					<div class="card-footer">
						<c:if test="${registeredRequest == null }">
							<input type="submit" id="btn-register" class="btn btn-info"
								value="Register" />
						</c:if>
						<c:if test="${registeredRequest != null }">
							<input type="submit" class="btn btn-danger" value="Invalidate" />
						</c:if>
					</div>
				</div>
			</form>
		</div>
		<div class="col-6">
			<div class="card">
				<div class="card-header">
					<i class="fa fa-sticky-note"></i>&nbsp;README
				</div>
				<div class="card-body">
					<ol>
						<li>Register Username</li>
						<li><span>One to One Video Call</span>
							<ul>
								<li>Check Available Sessions</li>
								<li>Call The User</li>
							</ul></li>
						<li><span>Public Video Call</span>
							<ul>
								<li>Go To Dashboard</li>
								<li>Generate Room Id OR</li>
								<li>Enter Using Generated Room Id To Join</li>
								<li>Max Recording Duration: ${maxRecordingTimeString }</li>
								<li>Recording Format : ${recordingOutputFormat }</li>
							</ul></li>
						<li>Invalidate Session If You're Done</li>

					</ol>
					<p>Source Code (this web app)</p>
					<a href="https://github.com/fajaralmu/reltime-vidcall"><i
						class="fab fa-github"></i>&nbsp;Github</a>
					<p>Source Code (anrdoid mobileb app)</p>
					<a href="https://github.com/fajaralmu/android-chatting1"><i
						class="fab fa-github"></i>&nbsp;Github</a>


				</div>
			</div>
		</div>
	</div>
</div>


<script type="text/javascript">
	const inputUserName = byId("input-username");
	
</script>
<c:if test="${registeredRequest != null }">
	<script type="text/javascript">
		byId("form-register").onsubmit = function(e) {
			e.preventDefault();
			invalidate();
		}
		function invalidate() {
			confirmDialog("Do you want to invalidate session?").then(
					function(ok) {
						if (ok) {
							doInvalidate();
						}
					})
		}

		function doInvalidate() {
			const requestObject = {};
			postReq("<spring:url value="/api/stream/invalidate" />",
					requestObject, function(xhr) {
						infoDone();
						var response = (xhr.data);
						window.location.reload();
					});
		}
	</script>

</c:if>
<c:if test="${registeredRequest == null }">
	<script type="text/javascript">
		const buttonRegister = byId("btn-register");
		byId("form-register").onsubmit = function(e) {
			e.preventDefault();
			registerSession();
		}
		function registerSession() {
			confirmDialog("Do you want to Register Session?").then(
					function(ok) {
						if (ok) {
							doRegisterSession();
						}
					})
		}

		function doRegisterSession() {
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
							window.location.reload();
						}
					});
		}
	</script>
</c:if>