function confirmDialog(msg) {
	return new Promise(function(resolve, reject) {
		const dialog = createHtmlTag({
			tagName : 'div',
			style:{ 'z-index': 6 },
			ch0 : modalBackdropJson(),
			ch1 : {
				tagName : 'div',
				className : 'modal fade show',
				role : 'dialog',
				style : { display : 'block', 'text-align' : 'center', 'margin-top' : '30vh', },
				ch0 : {
					tagName : 'div',
					className : 'modal-dialog',
					role : "document",
					ch1 : {
						tagName : 'div',
						className : 'modal-content',
						ch1 : modalHeader("Confirmation"),
						ch2 : modalBody('<i class="fas fa-exclamation-triangle"></i>&nbsp;' + msg),
						ch3 : {
							tagName : 'div',
							className : 'modal-footer',
							ch1 : {
								style : { margin : 'auto', style:{width:'max-content'} },
								ch1:{
									tagName : 'button',
									innerHTML : 'Yes',
									className : 'btn btn-primary',
									style: {margin: '3px'},
									onclick : function(e) {
										resolve(true);
										dialog.parentNode.removeChild(dialog);
									}
								},
								ch2:{
									tagName : 'button',
									innerHTML : 'No',
									style: {margin: '3px'},
									className : 'btn btn-secondary',
									onclick : function(e) {
										resolve(false);
										dialog.parentNode.removeChild(dialog);
									}
								},
							}
						}
					}
				},
			}
		})

		document.body.prepend(dialog);
	});
}

function promptDialog(msg, inputType) {
	return new Promise(function(resolve, reject) {
		const dialog = createHtmlTag({
			tagName : 'div',
			style:{ 'z-index': 6 },
			ch0 : modalBackdropJson(),
			ch1 : {
				tagName : 'div',
				className : 'modal fade show',
				role : 'dialog',
				style : { display : 'block', 'text-align' : 'center', 'margin-top' : '30vh', },
				ch0 : {
					tagName : 'div',
					className : 'modal-dialog',
					role : "document",
					ch1 : {
						tagName : 'div',
						className : 'modal-content',
						ch1 : modalHeader("Prompt"),
						ch2 :  modalBody({
							tagName:'div',
							ch0:{
								tagName:'p',
								innerHTML: msg
							},
							ch1: {
								tagName: 'input',
								type:inputType?inputType:'text',
								id: 'prompt-input-val',
								className: 'form-control'
							}
						}),
						ch3 : {
							tagName : 'div',
							className : 'modal-footer',
							ch1 : {
								style : { margin : 'auto', style:{width:'max-content'} },
								ch1:{
									tagName : 'button',
									innerHTML : 'Yes',
									className : 'btn btn-primary',
									style: {margin: '3px'},
									onclick : function(e) {
										let val = byId('prompt-input-val').value;
										if(!val){
											alert("Invalid input!");
											return;
										}
										console.log("Prompt Val: ", val);
										resolve({ok:true, value:val} );
										dialog.parentNode.removeChild(dialog);
									}
								},
								ch2:{
									tagName : 'button',
									innerHTML : 'No',
									style: {margin: '3px'},
									className : 'btn btn-secondary',
									onclick : function(e) {
										resolve({ok:false, value: null});
										dialog.parentNode.removeChild(dialog);
									}
								},
							}
						}
					}
				},

			}
		})

		document.body.prepend(dialog);
	});
}


function infoDialog(msg) {
	return new Promise(function(resolve, reject) {
		const dialog = createHtmlTag({
			tagName : 'div',
			style:{ 'z-index': 6 },
			ch0 : modalBackdropJson(),
			ch1 : {
				tagName : 'div',
				className : 'modal fade show',
				role : 'dialog',
				style : { display : 'block', 'text-align' : 'center', 'margin-top' : '30vh', },
				ch0 : {
					tagName : 'div',
					className : 'modal-dialog',
					role : "document",
					ch1 : {
						tagName : 'div',
						className : 'modal-content',
						ch1 : modalHeader("Info"),
						ch2 : modalBody('<i class="fa fa-info-circle"></i>&nbsp'+ msg),
						ch3 : {
							tagName : 'div',
							className : 'modal-footer',
							ch1 : {
								style : { margin : 'auto' },
								tagName : 'button',
								innerHTML : 'Ok',
								className : 'btn btn-primary',
								onclick : function(e) {
									resolve(true);
									dialog.parentNode.removeChild(dialog);
								}
							}
						}
					}
				},

			}
		})

		document.body.prepend(dialog);
	});
}


function modalHeader(text){
	return {
		tagName : 'div', className : 'modal-header',
		ch1 : {
			tagName : 'h5',
			className : 'modal-title',
			innerHTML : text,
			style:{margin:'auto'}
		},
	};
}

function modalBackdropJson(){
	return {
		tagName : 'div',
		className : 'modal-backdrop', 
		style:{ 'background-color': 'rgba(150,150,150,0.5)' }
	}
}

function modalBody(html){
	const obj = {
		tagName : 'div',
		className : 'modal-body',
		
	}
	
	if(typeof(html) == "string"){
		obj.innerHTML = html;
	}else{
		obj.ch0 = html;
	}
	
	return obj;
}