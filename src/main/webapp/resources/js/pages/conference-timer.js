var maxRecordingTime;
var currentCounter = 0; //0
var deltaTime = 1000;

function initRecordingTimer(){
	setTimeout(updateRecordingTime, deltaTime);
}

function updateRecordingTime(){
	currentCounter++;
	
	if(currentCounter <= maxRecordingTime){
		handleRecordingTimer({
			code:"00", 
			requestId: recordingId,
			message: secondToTimeString(currentCounter)
			});
		setTimeout(updateRecordingTime, deltaTime);
		
	} else {
		
		handleRecordingTimer({
			code:secondToTimeString(currentCounter), 
			requestId: recordingId,  
			message: "Exceeds Max Limit"
				});
		currentCounter = 0;
	}
}

//function handleRecordingTimer(resp){
//	const peerId = resp.requestId;
//	if(resp.code == "00"){
//		recordingTimer.innerHTML = "Recording Time: "+ resp.message;
//	}else{
//		recordingTimer.innerHTML = "Stopped At "+ resp.code +" cause: "+resp.message;
//		forceStopRecording(peerId, resp.message);
//	}
//}


function secondToTimeString(rawSecond) {
	
	var minute = 0;
	var second = 0;
	
	for (var i = 1; i <= rawSecond; i++, second++) {
		if(second >= 60) {
			minute++;
			second = 0;
		}
	}
	
	return (minute > 9 ? minute: "0"+minute )+":"+(second > 9 ? second: "0"+second );
	
}