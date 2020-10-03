<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="border border-primary rounded " id="member-list">
	<h3  style="text-align: center">Member List</h3>
	<div class="row">
		<c:forEach var="member" items="${members}">
			<div class="col-6" id="member-item-${member.requestId}">
				<h5>
					<i class="fas fa-user-circle"></i>&nbsp;${member.username }
					<c:if test="${true ==  member.roomCreator}">
						<small><i class="fas fa-headset"></i></small>
					</c:if>
				</h5>
				<p>${member.created }</p>
				<c:if test="${member.requestId != registeredRequest.requestId }">
					<video class="border" style="visibility: hidden" height="150"
						width="150" muted="muted" id="video-member-${member.requestId }"></video>

					<div class="btn-group" role="group"
						id="video-control-${member.requestId }">
						<button class="btn"
							onclick="toggleVideoPlay('video-member-${member.requestId }', this);">
							<i class="fas fa-pause"></i>
						</button>
						<button class="btn"
							onclick="toggleVideoMute('video-member-${member.requestId }', this);">
							<i class="fas fa-volume-down"></i>
						</button>
						<button class="btn btn-info btn-sm"
							onclick="dialPartner('${member.requestId}')">
							<i class="fas fa-phone"></i>&nbsp;Dial
						</button>

					</div>
				</c:if>
				<c:if test="${member.requestId == registeredRequest.requestId }">
					<h3 class="center-aligned bg-light">You</h3>
				</c:if>
			</div>
		</c:forEach>
	</div>
</div>
