
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height: auto">
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<a class="navbar-brand" href="#">${applicationHeaderLabel }</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup"
			aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarNavAltMarkup">
			<div class="navbar-nav">
				<a class="nav-link " href="<spring:url value="/app/"></spring:url>">
					<i class="fas fa-home" style="margin-right: 5px"></i>Main Menu
				</a>

				<c:if test="${registeredRequest != null && inActiveCall}">
					<a class="nav-link " href="#" onclick="handleLeaveCalling()"> <span
						id="active-call-info"><i class="fas fa-phone"
							style="margin-right: 5px"></i></span>Click to Enable Calling
					</a>
				</c:if>
				<c:if test="${registeredRequest != null }">
					<a class="nav-link "
						href="<spring:url value="/dashboard/"></spring:url>"> <i
						class="fas fa-cog" style="margin-right: 5px"></i>Dashboard
					</a>
					<a class="nav-link "
						href="<spring:url value="/dashboard/sessionlist"></spring:url>">
						<i class="fas fa-list-ul" style="margin-right: 5px"></i>Available
						Sessions
					</a>
				</c:if>
			</div>
		</div>
	</nav>
</div>
<script>
	const activeInfo = byId("active-call-info");
	var activeCall = "${inActiveCall}" == "true";

	function handleLeaveCalling() {
		if (!activeInfo || activeCall == false) {
			return;
		}
		leaveCalling(function(response) {
			activeInfo.innerHTML = "<i class=\"fas fa-phone\"></i>&nbsp;Calling Enabled";
			activeCall = false;
		});
	}
</script>