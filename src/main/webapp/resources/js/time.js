var currentTime = new Date().getTime();
var timeUpdated = 0;

function getDeltaTime(){
	const now = getNowDateMilis();
	//console.warn("currentTime:",currentTime,", now:",now);
	return now - currentTime;
}

function deltaTimeLessThan(value){
	return getDeltaTime() < value;
}

function updateCurrentTime(){
	//console.warn("getDeltaTime: ", getDeltaTime());
	currentTime = getNowDateMilis();
	timeUpdated = timeUpdated + 1;
}

function getNowDateMilis(){
	return new Date().getTime();
}