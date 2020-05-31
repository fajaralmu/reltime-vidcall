const tempComponent = document.createElement("div");
function _byId(id){
	return document.getElementById(id);
}

function infoLoading() {
	document.getElementById("loading-div").innerHTML = 
		"<img width='60px'  src=\""+ctxPath+"/res/img/loading-disk.gif\" />";
}

function infoDone() {
	document.getElementById("loading-div").innerHTML = "";
}

/** ***************COMPONENT*************** */
function createAnchor(id, html, url){
	var a = document.createElement("a");
	a.id = id;
	a.innerHTML = html;
	a.href = url
	return a;
}

function createNavigationButton(id, html, callback){
	var btn= createAnchor(id,html, "#");
	btn.className = "page-link";
	if(callback != null)
		btn.onclick = function(){
			callback(id);
		}
	var li = document.createElement("li");
	li.className = "page-item";
	li.append(btn);
	return li;
}

function createButton(id, html){
	var button = document.createElement("button");
	button.id = id;
	button.innerHTML = html;
	return button;
}
 

function createCell(val){
	let column = document.createElement("td");
	column.innerHTML = val;
	return column;
}

function createRow(val){
	let column = document.createElement("tr");
	column.innerHTML = val;
	return column;
}

function createInputText(id, className){
	let input = document.createElement("input");
	input.id = id;
	input.setAttribute("class",className);
	return input;
}

function hide(id){
	document.getElementById(id).style.display = "none";
}

function show(id){
	document.getElementById(id).style.display = "block";
}
	 

function toDateInput(date){
	let dateStr  ="";
	let yearStr=date.getFullYear();
	let monthStr =( date.getMonth()+1) >=10?( date.getMonth()+1):"0"+( date.getMonth()+1);
	let dayStr =( date.getDate()+1) >=10?( date.getDate()+1):"0"+( date.getDate()+1);
	dateStr = yearStr+"-"+monthStr+"-"+dayStr;
	
	return dateStr;
	
};

function toBase64(file, callback){
	const reader = new FileReader();
    reader.readAsDataURL(file.files[0]);
    reader.onload = () => callback(reader.result);
    reader.onerror = error => {
    	alert("Error Loading File");
    }
}

function createDiv(id, className){
	let div = createElement("div", id, className); 
	return div;
}

function createInput(id, className, type){
	let div = createElement("input", id, className); 
	div.type = type; 
	return div;
}

function createOption(value, html){
	let option = createElement("option", null, null);
	option.value = value;
	option.innerHTML = html;
	return option;
}

function createLabel(text){
	let element = document.createElement("label");
	element.innerHTML = text;
	return element;
}

function createHeading(tag ,id, className, html){
	let option = createElement(tag,id, className);
	option.innerHTML = html;
	return option;
}

function createElement(tag, id, className){
	let div = document.createElement(tag);
	if(className!=null)
		div.className = className;
	if(id != null)
		div.id  = id;
	return div;
}

function createImgTag(id, className, w, h, src){
	let img = createElement("img", id, className);
	img.width = w;
	img.height = h;
	
	img.src = src;
	
	return img;
}

function createGridWrapper(cols, width){
	let div = document.createElement("div");
	div.style.display = "grid";
	
	if(width == null){
		div.style.gridTemplateColumns = "auto ".repeat(cols);
	}else{
		div.style.gridTemplateColumns = (width+" ").repeat(cols);
	}
	return div;
}
function createHtmlTag(tagName, object){
	var tag = document.createElement(tagName);
	
	for(let key in object){
		if(key == "innerHTML" || key == "child"){
			continue;
		}
		tag.setAttribute(key, object[key]);
	}
	if(object["innerHTML"])
		tag.innerHTML = object["innerHTML"];
	if(object["child"])
		tag.appendChild(object["child"]);
	return tag;
}

function domToString(dom){
	tempComponent.innerHTML = "";
	tempComponent.appendChild(dom);
	
	return tempComponent.innerHTML;
}

/** BEGIN ENTITY DETAIL* */
function createTableHeaderByColumns(columns, ignoreNumber){
	console.log("Headers", columns);
	
	let row = createElement("tr","th-header-detail",null);
	if(!ignoreNumber)
		row.append(createCell("<b>No</b>"));
	for (var i = 0; i < columns.length; i++) {
		var column = columns[i];
		column = column.toUpperCase();
		column = column.replace("."," ");
		row.append(createCell("<b>"+column+"</b>"));
	}
	
	return row;
}

// return array of TR !!!!
function createTableBody(columns, entities ,ignoreNumber){
	 createTableBody(columns, entities, 0,ignoreNumber);
}

function createTableFromRows(rows, id){
	let table = createElement	("table", id, "table");
	for (var i = 0; i < rows.length; i++) {
		table.appendChild(rows[i]);
	}
	return table;
}

function createTableBody(columns, entities, beginNumber,ignoreNumber){
	if(beginNumber == null){
		beginNumber = 0;
		 
	}
	// let tbody = createElement("tbody", "tbody-detail", "tbody-detail");
	let rows = [];
	for (let j = 0; j < entities.length; j++) {
		let entity = entities[j];
		
		let row = createElement("tr","tr-body-detail-"+j,null);
		if(!ignoreNumber)
			row.append(createCell(beginNumber+1)); 
		beginNumber++;
		for (let i = 0; i < columns.length; i++) {
			let column = columns[i];
			let isUrl = false;
			if(column.includes("setting=")){
				let setting = column.split("setting=")[1];
				column = column.split("setting=")[0].trim();
				
				if(setting.includes("link")){
					isUrl = true;	
				}
			}
			let refField = column.split(".");
			let entityValue = entity[column];
			
			let cell = createCell("");
			cell.setAttribute("name",column); 
			
			if(refField.length>1 && entity[refField[0]] !=null){
			 	entityValue = entity[refField[0]][refField[1]];
			 	
				cell.setAttribute("name", refField[1]);
				 
			}
			let notNull = entityValue!=null;
			if(notNull && typeof(entityValue) == "number"){
		 		entityValue = beautifyNominal(entityValue);
		 	}
			isUrl = typeof (entityValue) == "string" && (entityValue.trim().startsWith("http://") || entityValue.trim().startsWith("https://"));
			
			if(notNull && isUrl){
				entityValue  ="<a href=\""+entityValue+"\">"+entityValue+"</a>";
			}
			cell.innerHTML = entityValue;
			row.append(cell);
		}
		rows.push(row);
	}
	return rows;
}

/** END ENTITY DETAIL* */

function createFilterInputDate(inputGroup, fieldName, callback){
	// input day
	let inputDay = createInputText(
			"filter-" + fieldName + "-day", "filter-field form-control"); 
	inputDay.setAttribute("field", fieldName + "-day");
	inputDay.style.width = "30%";
	inputDay.onkeyup = function() {
		callback();
	}
	// input month
	let inputMonth = createInputText("filter-" + fieldName
			+ "-month", "filter-field form-control");
	inputMonth.setAttribute("field", fieldName + "-month");
	inputMonth.style.width = "30%"; 
	inputMonth.onkeyup = function() {
		callback();
	}
	// input year
	let inputYear = createInputText(
			"filter-" + fieldName + "-year", "filter-field form-control");
	inputYear.setAttribute("field", fieldName + "-year"); 
	inputYear.style.width = "30%";
	inputYear.onkeyup = function() {
		callback();
	}
	inputGroup.append(inputDay);
	inputGroup.append(inputMonth);
	inputGroup.append(inputYear);
	return inputGroup;
}

function createTBodyWithGivenValue(rowList){
	let tbody = createElement("tbody","id",null);
	for (var i = 0; i < rowList.length; i++) {
		var columns = rowList[i];
		let row = document.createElement("tr");
		for (var j = 0; j < columns.length; j++) {
			var cell = columns[j];
			let column = document.createElement("td");
			if(null!=cell && typeof(cell) == "string" && cell.includes("setting=")){
				var setting = cell.split("setting=")[1];
				// colspan
				if(setting.includes("<colspan>")){
					var collspan = setting.split("<colspan>")[1];
					column.setAttribute("colspan",collspan.split("</colspan>")[0]);
				}
				// style
				if(setting.includes("<style>")){
					var style = setting.split("<style>")[1].split("</style>")[0];
					column.setAttribute("style",style);
				}
				
				cell = cell.split("setting=")[0];
			}
			if(cell!=null && typeof(cell) == "number"){
				cell = beautifyNominal(cell);
			}
			column.innerHTML = cell;
			row.append(column);
		}
		tbody.append(row);
	}
	return tbody;
	
}

function beautifyNominal(val) {
	let nominal = ""+val;
	let result = "";
	if (nominal.length > 3) {
		let zero = 0;
		for (let i = nominal.length - 1; i > 0; i--) {
			zero++;
			result = nominal[i] + result;
			if (zero == 3) {
				result = "." + result;
				zero = 0;
			}

		}
		result = nominal[0] + result;
	} else {
		result = val;
	}
	return result;
}

/** ******NAVIGATION******loadEntity** */
function createNavigationButtons(navigationPanel,currentPage,totalData,limit,buttonClickCallback) {
	navigationPanel.innerHTML = "";
	var buttonCount = Math.ceil(totalData / limit);
	let prevPage = currentPage == 0 ? 0 : currentPage - 1;
	// prev and first button
	navigationPanel.append(createNavigationButton(0, "|<",buttonClickCallback));
	navigationPanel.append(createNavigationButton(prevPage, "<",buttonClickCallback));

	/* DISPLAYED BUTTONS */
	let displayed_buttons = new Array();
	let min = currentPage - 2;
	let max = currentPage + 2;
	for (let i = min; i <= max; i++) {
		displayed_buttons.push(i);
	}
	let firstSeparated = false;
	let lastSeparated = false;

	for (let i = 0; i < buttonCount; i++) {
		let buttonValue = i * 1 + 1;
		let included = false;
		for (let j = 0; j < displayed_buttons.length; j++) {
			if (displayed_buttons[j] == i && !included) {
				included = true;
			}
		}
		if (!lastSeparated && currentPage < i - 2
				&& (i * 1 + 1) == (buttonCount - 1)) {
			// console.log("btn id",btn.id,"MAX",max,"LAST",(jumlahTombol-1));
			lastSeparated = true;
			var lastSeparator = document.createElement("span");
			lastSeparator.innerHTML = "...";
	// navigationPanel.appendChild(lastSeparator);

		}
		if (!included && i != 0 && !firstSeparated) {
			firstSeparated = true;
			var firstSeparator = document.createElement("span");
			firstSeparator.innerHTML = "...";
		// navigationPanel.appendChild(firstSeparator);

		}
		if (!included && i != 0 && i != (buttonCount - 1)) {
			continue;
		}

		let button = createNavigationButton(i, buttonValue,buttonClickCallback);
		if (i == page) {
			button.className = button.className.replace("active", "");
			button.className = button.className + " active ";
		}
		navigationPanel.append(button);
	}

	let nextPage = currentPage == buttonCount - 1 ? currentPage : currentPage + 1;
	// next & last button
	navigationPanel.append(createNavigationButton(nextPage, ">",buttonClickCallback));
	navigationPanel.append(createNavigationButton(buttonCount - 1, ">|",buttonClickCallback));
	return navigationPanel;
}

/*********** CONSTANTS ***********/
const monthNames = ["January", "February", "March", "April", "May", "June",
	  "July", "August", "September", "October", "November", "December"
	];
