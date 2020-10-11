<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div>
	<div class="card">
		<div class="card-header">
			<i class="fas fa-user-shield"></i>&nbsp;Admin Data
		</div>
		<div class="card-body">
			<ol>
				<li>Runtime Data: ${runtimeData } </li>
				<c:forEach items="${quickLinks }" var="linkItem">
					<li>
						<p>${linkItem.label } <i>Link: ${linkItem.link }</i></p>
						<c:if test="${linkItem.hasPathVariable == true}">
							<c:forEach items="${linkItem.pathVariableNameList }" var="pathVariable">
							 	<label>${pathVariable.value }</label><input id="input-${pathVariable.value }-${linkItem.id }" type="text" class="form-control" placeholder="${pathVariable.value }" />
							</c:forEach>
						</c:if>
						<button id="${linkItem.id }" class="btn btn-info btn-sm quick-link" link="${linkItem.link }" variablename="${linkItem.pathVariableName }" hasvariable="${linkItem.hasPathVariable }" >Action</button>
					</li>
				</c:forEach>
			</ol>
		</div>
	</div>
	<div class="card">
		<div class="card-header">
			<i class="fas fa-user-shield"></i>&nbsp;Response
		</div>
		<div class="card-body bg-dark">
			<code id="action-response"></code>
		</div>
	</div>
</div>

<script type="text/javascript">

	const actionButtons = document.getElementsByClassName("quick-link");
	const actionResponse = byId("action-response");
	
	const baseUrl = '<spring:url value="/" />';
	function initActionButtonEvents(){
		for (var i = 0; i < actionButtons.length; i++) {
			const button = actionButtons[i];
			
			button.onclick = function(e){
				
				const hasVariable = button.getAttribute("hasvariable") == "true";
				var link = button.getAttribute("link");
				
				confirmDialog("Continue?").then(function(accepted){
					if(accepted){ 
						var valid = true;
						if(hasVariable){
							const variables = button.getAttribute("variablename").split(",");
							for (var j = 0; j < variables.length; j++) {
								
								const varName = variables[j];
								if(null == varName || "" == varName){
									continue;
								}
								const variableValue = byId("input-"+varName+"-"+button.id).value;
								if(variableValue == null || variableValue == ""){
									infoDialog(varName+" must be present!").then(function(e){});
									valid = false;
									break;
								}
								link = link.replace("{"+varName+"}", variableValue);
							}
						}
						
						if(valid){
							const finalUrl = baseUrl+link;
							postReqEmptyBody(finalUrl, function(xhr) {
										infoDone();
										console.log(xhr.data);
										actionResponse.innerHTML = JSON.stringify(xhr.data, null, "\t");
									});
						}
						
					}
				})
				
				
				
			}
		}
	}
	
	initActionButtonEvents();
</script>