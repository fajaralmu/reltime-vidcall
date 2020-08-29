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
 * @param callBackObject
 *            video call
 * @returns
 */
function connectToWebsocket( ... callBackObjects) {

	const requestIdElement = document.getElementById("request-id");
	 
	var socket = new SockJS(websocketUrl);
	const stompClients = Stomp.over(socket);
	stompClients.connect({}, function(frame) {
		wsConnected = true;
		// setConnected(true);
		console.log('Connected -> ' + frame, stompClients.ws._transport.ws.url);

		// document.getElementById("ws-info").innerHTML =
		// stompClients.ws._transport.ws.url;
		for(let i =0;i<callBackObjects.length;i++){
			const callBackObject = callBackObjects[i];
			
			if(callBackObject){ 
				
				stompClients.subscribe(callBackObject.subscribeUrl, function(response) {
					 
					console.log("Websocket Updated...");
					
					var respObject = JSON.parse(response.body);
					 
					callBackObject.callback(respObject);
					 
				});
			}
		}

	});

	this.stompClient = stompClients;
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	// wsConnected = (false);
	console.log("Disconnected");
}

function leaveApp(entityId) {
	stompClient.send("/app/leave", {}, JSON.stringify({
		'entity' : {
			'id' : entityId * 1
		}
	}));
}
