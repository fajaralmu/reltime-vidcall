<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="content" style="width: 100%">
	<c:if test="${message == null }">
		<h3>Page Not Found</h3>
	</c:if>
	<c:if test="${message != null }">
		<h3>Error Occured</h3>
		<p>${message }</p>
	</c:if>
</div>
