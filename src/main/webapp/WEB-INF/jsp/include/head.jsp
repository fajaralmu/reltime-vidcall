
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height: auto">
  
	<div class="page-header" style="color: blue">
		<h1>Nuswantoro.conf</h1>
		<nav class="navbar  navbar-expand-lg ">
			<div>
				<ul class="navbar-nav ">
					<li class="nav-item "><a
						href="<spring:url value="/app/"></spring:url>"><i
							class="fas fa-home"></i>&nbsp;Main Menu </a></li>
					<c:if test="${registeredRequest != null && inActiveCall}">
						<li class="nav-item "><a href="#"
							onclick="handleLeaveCalling()"><span id="active-call-info">
									<i class="fas fa-phone"></i>&nbsp;Click to Enable Calling
							</span></a></li>
					</c:if>
					<c:if test="${registeredRequest != null }">
						<li class="nav-item "><a
							href="<spring:url value="/dashboard/"></spring:url>"> <i
								class="fas fa-cog"></i>&nbsp;Dashboard
						</a></li>
						<li class="nav-item "><a
							href="<spring:url value="/dashboard/sessionlist" /> "><i
								class="fa fa-list-ul" aria-hidden="true"></i>&nbsp;Available
								Sessions</a></li>
					</c:if>
				</ul>
			</div>
		</nav>
	</div>

	<div></div>
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