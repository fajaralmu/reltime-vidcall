var stompClient = null;
var wsConnected = false;

function updateMovement() {
	stompClient.send("/app/move", {}, JSON.stringify({
		'entity' : {
			'id' : entity.id * 1,
			'life' : entity.life,
			'active' : true,
			'physical' : {
				'x' : entity.physical.x,
				'y' : entity.physical.y,
				'direction' : entity.physical.direction,
				'color' : entity.physical.color,
				'lastUpdated' : new Date()
			},
			'missiles' : entity.missiles
		}
	}));
}

function sendToWebsocket(url, requestObject){
	if(!wsConnected){
		console.info("Connecting");
		return false;
	}
	stompClient.send(url, {}, JSON.stringify(requestObject));
	return true;
}

/**
 *  
 * @param callbackObject4 video call
 * @returns
 */
function connectToWebsocket(  callbackObject4) {

	const requestIdElement = document.getElementById("request-id");
	 
	var socket = new SockJS('/universal-good-shop/shop-app');
	const stompClients = Stomp.over(socket);
	stompClients.connect({}, function(frame) {
		wsConnected = true;
		// setConnected(true);
		console.log('Connected -> ' + frame, stompClients.ws._transport.ws.url);

		// document.getElementById("ws-info").innerHTML =
		// stompClients.ws._transport.ws.url; 
		
		if(callbackObject4){
			stompClients.subscribe("/wsResp/videostream/"+callbackObject4.partnerId, function(response) {
				 
				console.log("Websocket Updated...");
				
				var respObject = JSON.parse(response.body);
				 
				callbackObject4.callback(respObject);
				 
			});
		}

	});

	this.stompClient = stompClients;
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	//wsConnected = (false);
	console.log("Disconnected");
}

function leaveApp(entityId) {
	stompClient.send("/app/leave", {}, JSON.stringify({
		'entity' : {
			'id' : entityId * 1
		}
	}));
}
