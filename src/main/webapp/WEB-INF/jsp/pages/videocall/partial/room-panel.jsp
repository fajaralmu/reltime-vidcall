<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div style="width:100%">
	<h3>Room : ${roomId }</h3>
	<h3>User : ${registeredRequest.requestId }</h3>
	<c:if test="${isRoomOwner == true }">
		<h3>You Are Room Admin</h3>
	</c:if>
	<c:if test="${isRoomOwner == false }">
		<h3>Admin : ${roomAdmin.username }</h3>
	</c:if>

<!-- 	<button class="btn btn-info  " onclick="redial()"><i class="fas fa-phone"></i>&nbsp;Redial</button> -->
	<div style="display: grid; grid-template-columns: auto auto; grid-row-gap: 3px; grid-column-gap: 3px">
		<button class="btn btn-danger  " onclick="leave()">
			<i class="fas fa-sign-out-alt"></i>&nbsp;Leave
		</button>
		<button onclick="clearLog()" class="btn btn-secondary">
			<i class="fas fa-trash-alt"></i>&nbsp;Clear Log
		</button>
	
		<c:if test="${isRoomOwner == true }">
			<button class="btn btn-danger  " onclick="invalidateRoom()">
				<i class="fas fa-times-circle"></i>&nbsp;Invalidate Room
			</button>
		</c:if>
	
		<a style="" id="btn-download-recorded" class="btn btn-warning"><i
			class="fas fa-file-download"></i> Recorded Capture</a>
	</div>
	<p><span id="recording-timer"></span></p>
</div>