var maxRecordingTime;
var currentCounter = 0; //0
var lastDuration = 0;
var deltaTime = 1000;
var isRecording = false;

function initClientSideRecordingTimer(){
	currentCounter = 0;
	lastDuration = currentCounter;
	
	setTimeout(updateRecordingTime, deltaTime);
}

function updateRecordingTime(){
	currentCounter++;
	if(lastDuration < currentCounter){
		lastDuration = currentCounter;
	}
	 
	if(currentCounter <= maxRecordingTime && isRecording){
		handleRecordingTimer({
			code:"00", 
			requestId: recordingId,
			message: secondToTimeString(lastDuration)
			});
		setTimeout(updateRecordingTime, deltaTime);
		lastDuration = currentCounter;
		
	} else if(isRecording){
		
		handleRecordingTimer({
			code:secondToTimeString(lastDuration), 
			requestId: recordingId,  
			message: "Exceeds Max Limit"
				});
		currentCounter = 0;
		
	} else if(!isRecording) {
		
		handleRecordingTimer({
			code:secondToTimeString(lastDuration), 
			requestId: recordingId,  
			message: "Stopped by User"
				});
		currentCounter = 0;
	}
}


function secondToTimeString(rawSecond) {
	
	var minute = 0;
	var second = 0;
	var hour = 0;
	
	for (var s = 1; s <= rawSecond; s++) {
		second++;
		if(second >  59) {
			minute++;
			second = 0;
		}
		
		if(minute > 59) {
			hour++;
			minute = 0;
		}
		
	}
	
	return (hour > 9 ? hour: "0"+hour)+":"+(minute > 9 ? minute: "0"+minute)+":"+(second > 9 ? second: "0"+second);
	
}