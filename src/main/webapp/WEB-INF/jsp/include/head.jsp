
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
	<div class="page-header" style="color:blue">
		<h1>Real Time Video Conference</h1>
		<a href="<spring:url value="/livestream/app"></spring:url>">Main Page</a>
	</div>

	<div>
		<!-- <ul class="nav nav-tabs"> -->
		<ul class="nav  flex-column">

			<%-- <!-- Account Menu -->
			<c:if test="${loggedUser == null  }">
				<li class="nav-item "><a
					class="nav-link  ${page == 'login' ? 'active':'' }"
					href="<spring:url value="/account/login"/>">Log In </a></li>
			</c:if>
			<c:if test="${loggedUser != null }">
				<div class="dropdown">
					<button class="btn btn-primary dropdown-toggle" type="button"
						data-toggle="dropdown">
						${loggedUser.displayName }<span class="caret"></span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item"
							href="<spring:url value="/management/profile"/>">Profile</a> <a
							class="dropdown-item" href="#" onclick="logout()">Logout</a>
					</div>
				</div>
			</c:if>

			 
			<c:if test="${loggedUser != null }">
				<li class="nav-item"><a
					class="nav-link ${page == 'dashboard' ? 'active':'' }"
					href="<spring:url value="/admin/home"/>">Dashboard</a></li> 
			</c:if>

			<c:forEach var="pageItem" items="${pages}">
				<li class="nav-item" style="position: relative;"><a
					class="nav-link pagelink" id="${pageItem.code }"
					menupage="${pageItem.isMenuPage() }"
					href="<spring:url value="${pageItem.link }"/>">${pageItem.name }</a></li>

			</c:forEach> --%>

		</ul>
	</div>
</div>
 