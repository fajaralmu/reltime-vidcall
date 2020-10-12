<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div style="display: none" id="fullscreen-view">
	<div class="card">
		<div class="card-header"><i class="fas fa-expand"></i>&nbsp;FullScreen View</div>
		<div class="card-body" style="text-align: center">
			<canvas id="fullscreen-canvas" ></canvas>
		</div>
		<div class="card-footer">
			<button id="close-fullscreen" class="btn btn-secondary">Close</button>
			<button class="btn btn-warning" onclick="incrementCanvasSize(5)" ><i class="fas fa-search-plus"></i> Zoom In</button>
			<button class="btn btn-warning" onclick="incrementCanvasSize(-5)" ><i class="fas fa-search-minus"></i> Zoom Out</button>
		</div>
	</div>
</div>
<script>

	const canvasWidth = parseInt("${canvasWidth }");
	const canvasHeight = parseInt("${canvasHeight }");
	
	const fullscreenView = byId("fullscreen-view");
	const mainView = byId("main-view");
	const canvas = byId("fullscreen-canvas");
	const context = canvas.getContext('2d');
	
	var isFullscreen = false;
	var fullscreenVideoElement = null;
	 
	function setCanvasSize(width, height){
		canvas.width = width;
		canvas.height = height;
	}
	
	function incrementCanvasSize(value){
		const p = value/100;
		const currentWidth = canvas.width;
		const currentHeight = canvas.height;
		
		setCanvasSize(currentWidth+ (p*currentWidth), currentHeight+ (p*currentHeight) );
	}
	
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
		mainView.style.display = ""; //to keep grid view
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
	
	setCanvasSize(canvasWidth, canvasHeight);
</script>