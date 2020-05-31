<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>

<div>
	<h2>Available Sessions</h2>
	<c:forEach var="session" items="${sessions }">
	<% int i = 1; %>
	<c:if test="${registeredRequest.requestId != session.requestId }"></c:if>
		<div>
			<p>(<%=i %>) ${session.requestId }</p>
			<p>Created Date: ${session.created }</p>
			<c:if test="${registeredRequest.requestId != session.requestId }">
				<a class="btn btn-success"
				href="<spring:url value="/stream/videocall" />/${session.requestId }">Video
				Call</a>
			</c:if>
			<c:if test="${registeredRequest.requestId == session.requestId }">
				<b>Your session</b>
			</c:if>
			<% i++; %>
		</div>
	</c:forEach>
</div>