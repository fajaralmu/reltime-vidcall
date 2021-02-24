var stompClient = null;
var wsConnected = false;
const onConnectCallbacks = new Array();
const subscriptionCallbacks = new Array();

function sendToWebsocket(url, requestObject){
	if(!wsConnected){
		console.info("Connecting");
		return false;
	}
	stompClient.send(url, {}, JSON.stringify(requestObject));
	return true;
}

function performWebsocketConnection(){
	console.debug("performWebsocketConnection");
	var socket = new SockJS(websocketUrl);
	const stompClients = Stomp.over(socket);
	stompClients.connect({}, function(frame) {
		wsConnected = true;
		// setConnected(true);
		console.log('Websocket CONNECTED: ' ,websocketUrl ,'frame :', frame, stompClients.ws._transport.ws.url);
		console.debug("subscriptionCallbacks :" ,subscriptionCallbacks.length);
		// document.getElementById("ws-info").innerHTML =
		// stompClients.ws._transport.ws.url;
		for(let i =0; i < subscriptionCallbacks.length; i++){
			const callBackObject = subscriptionCallbacks[i];
			
			if(callBackObject){ 
				
				stompClients.subscribe(callBackObject.subscribeUrl, function(response) {
					 
					console.log("Websocket Updated...");
					
					var respObject = JSON.parse(response.body);
					 
					callBackObject.callback(respObject);
					 
				});
			}
		}
		
		for (var i = 0; i < onConnectCallbacks.length; i++) {
			const callback = onConnectCallbacks[i];
			callback(frame);
		}
		 
	}, function(e) {
		console.warn("Error connection websocket, reconnect");
		doItLater(performWebsocketConnection, 2000);
	});

	this.stompClient = stompClients;
}
function doItLater(callback, intervalMs) {
    const timeout = setTimeout(function(){
       callback();
        clearTimeout(timeout);
      }, intervalMs)
}
/**
 * 
 * @param callBackObject
 *            video call
 * @returns
 */
function connectToWebsocket( ... callBackObjects) { 
	 
	if(null == callBackObjects) {
		return;
	}
	for (var i = 0; i < callBackObjects.length; i++) {
		subscriptionCallbacks.push(callBackObjects[i]);
	} 
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
