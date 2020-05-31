<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>

<div>
<h2>Available Sessions</h2>
<c:forEach var="session" items="${sesions }">
<div>
	<p>${session.requestId }</p>
	<a class="btn btn-success" href="<spring:url value="/stream/videocall" />/${session.requestId }">Video Call</a> 
</div>
</c:forEach>
</div>