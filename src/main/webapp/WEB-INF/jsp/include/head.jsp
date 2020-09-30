
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height: auto">
	<style>
.menu-spoiler {
	text-align: left;
	font-size: 0.7em;
	background-color: gray;
	z-index: 1;
	position: absolute;
}

.menu-spoiler>a {
	color: white;
}

#header-wrapper {
	height: 100%;
}
</style>
	<div class="page-header" style="color: blue">
		<h1>Nuswantoro Conference</h1>
		<ul class="navbar-nav mr-auto">
			<li class="nav-item "><a
				href="<spring:url value="/app/"></spring:url>">
						<i class="fas fa-home"></i>
					 &nbsp;Main Menu</a></li>
			<c:if test="${registeredRequest != null && inActiveCall}">
				<li class="nav-item "><a href="#" onclick="handleLeaveCalling()"><span id="active-call-info">
							<i class="fas fa-phone"></i>
						&nbsp;Click to Enable Calling</span></a></li>
			</c:if>
		</ul>
	</div>

	<div></div>
</div>
<script>
	const activeInfo = byId("active-call-info");
	var activeCall = ${inActiveCall};
	
	function handleLeaveCalling(){
		if(!activeInfo || activeCall == false){
			return;
		}
		leaveCalling(function(response){
			activeInfo.innerHTML = "<i class=\"fas fa-phone\"></i>&nbsp;Calling Enabled";
			activeCall = false;
		});
	}

</script>