<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div style="display: none" id="fullscreen-view">
	<div class="card">
		<div class="card-header">FullScreen View <small></small></div>
		<div class="card-body">
			<canvas id="fullscreen-canvas" width="400" height="300"></canvas>
		</div>
		<div class="card-footer">
			<button id="close-fullscreen" class="btn btn-secondary">Close</button>
		</div>
	</div>


</div>
<script>
	const canvasHeight = 300;
	const canvasWidth = 400;
	
	const fullscreenView = byId("fullscreen-view");
	const mainView = byId("main-view");
	const canvas = byId("fullscreen-canvas");
	const context = canvas.getContext('2d');
	
	var isFullscreen = false;
	var fullscreenVideoElement = null;
	 
	function hideFullscreen(){
		fullscreenView.style.display = "none"; 
		canvasClear();
		
		isFullscreen = false;
		fullscreenVideoElement = null;
	}

	function canvasClear(){
		context.clearRect(0,0, canvas.width, canvas.height);
	}
	
	function hideMainView(){
		mainView.style.display = "none";
	}
	
	function showFullScreen(videoId){
		const vid = byId(videoId);
		if(vid == null){
			infoDialog("Video Not Found").then(function(e){});
			return false;
		}
		fullscreenView.style.display = "block";
		
		fullscreenVideoElement = vid;
		isFullscreen = true;
		
		drawVideo();
		return true;
	}
	
	function drawVideo(){
		
		context.drawImage(fullscreenVideoElement, 0, 0, canvas.width, canvas.height);
		
		if(fullscreenVideoElement && isFullscreen){
			setTimeout(drawVideo, 1);
		}
		
	}
	
	function showMainView(){
		mainView.style.display = "block";
	}
	
	function showMemberFullscreen(videoId){
		if(showFullScreen( videoId) == true){
			hideMainView();
		}
	}
	
	byId("close-fullscreen").onclick = function(e){
		hideFullscreen();
		showMainView();
	}
</script>