<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<h2>Public Conference ${roomId }</h2>
	<div class="row">
		<div class="col-6">
			<div class="border border-primary rounded" id="member-list">
				<h3>Member List</h3>
				<c:forEach var="member" items="${members}">
					<div id="member-item-{member.requestId}">
					<h3>${member.username }<small>${member.requestId }</small></h3>
					<p>${member.created }</p>
					</div>
				</c:forEach>
			</div>

		</div>
		<div class="col-6">
			<div class="border border-primary rounded" id="event-log">
				<h3>Log</h3>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	const memberList = byId("member-list");
	const eventLog = byId("event-log");

	function prepare() {
		const _class = this;

		onConnectCallbacks.push(function(frame) {
			console.log("Connected to signaling server-", frame);
			_class.init();
		});

		const callbackMemberJoin = {
			subscribeUrl : "/wsResp/joinroom/${roomId }",
			callback : function(resp) {
				_class.addMemberList(resp.username, resp.requestId, resp.date);
			}
		};
		const callbackMemberLeave = {
			subscribeUrl : "/wsResp/leaveroom/${roomId }",
			callback : function(resp) {
				_class.removeMemberItem(resp.username, resp.requestId,
						resp.date);
			}
		};

		connectToWebsocket(callbackMemberJoin, callbackMemberLeave);
	}

	function addMemberList(username, requestId, date) {
		const memberElementObject = {
			tagName : 'div',
			id : "member-item-" + requestId,
			ch1 : {
				tagName : 'h3',
				innerHTML : username,
				ch1: {
					tagName: 'small',
					innerHTML : requestId
				}
			},
			ch2 : {
				tagName : 'p',
				innerHTML : 'Date: ' + date
			}

		};

		const memberElement = createHtmlTag(memberElementObject);
		memberList.appendChild(memberElement);

		updateEventLog(username + '_' + requestId + ' Joined');
	}

	function removeMemberItem(username, requestId, date) {
		const memberElement = byId("member-item-" + requestId);
		memberElement.remove();

		updateEventLog(username + '_' + requestId + ' Leave');
	}

	function updateEventLog(log) {
		log = new Date() + ' ' + log;
		const p = createHtmlTag({
			tagName : 'p',
			innerHTML : log
		});
		eventLog.appendChild(p);
	}

	function init() {
		join();
	}

	function join() {
		sendToWebsocket("/app/publicconf1/join", {
			originId : "${registeredRequest.requestId}",
			roomId : "${roomId}"
		});
	}

	function leave() {
		sendToWebsocket("/app/publicconf1/leave", {
			originId : "${registeredRequest.requestId}",
			roomId : "${roomId}"
		});
	}

	prepare();
</script>