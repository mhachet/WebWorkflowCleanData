var UUID = "";

function initialise() {
	document.getElementById('compteur_inp').value = 1;
	document.getElementById('compteur_raster').value = 0;
	document.getElementById('compteur_header').value = 0;

	UUID = this.generateUUID();
}

function getUUID(){

	return UUID;
}
//--- Create temporary table with correct ISO2 ---

function generateUUID() {
	var d = new Date().getTime();
	var uuid = 'xxxxxxxx_xxxx_4xxx_yxxx_xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = (d + Math.random()*16)%16 | 0;
		d = Math.floor(d/16);
		return (c=='x' ? r : (r&0x3|0x8)).toString(16);
	});
	
	var inputUUID = document.getElementById('uuid');
	inputUUID.setAttribute('id', "formulaire_" + uuid);
	inputUUID.setAttribute('name', "formulaire");
	inputUUID.setAttribute('value', uuid);
	
	return uuid;
};

//add a new input file
function addField(compteur, idAdd, typeInput) {
	//count inputs number
	var nb_inp = document.getElementById(compteur).value;

	var divAddLoad = document.createElement('div');
	divAddLoad.setAttribute('id', "divAddLoad_" + nb_inp);
	divAddLoad.setAttribute("class", "col-lg-12 center");

	//Create a new input file
	var inp = document.createElement('input');
	inp.setAttribute('type', 'file');
	inp.setAttribute('id', typeInput + "_" + nb_inp);
	inp.setAttribute('name', typeInput + "_" + nb_inp);

	if(typeInput == "inp"){
		if(nb_inp != 0){
			var inputBefore = document.getElementById("inp_" + (nb_inp - 1));
			var uploadedInputBefore = inputBefore.getAttribute('value');
			//console.log("inp_" + (nb_inp - 1) + "  : " + uploadedInputBefore);
			if(uploadedInputBefore == 'false'){
				
				alert("Upload file before add a new file, please !");
			}
			else{
				//var uploaded = inputBefore.value;
				console.log(inputBefore);
				var globalInput = document.createElement("div");
				globalInput.setAttribute('class', "col-lg-12 global");
				globalInput.setAttribute('id', "globalInput_" + nb_inp);

				this.createInputSpecial(nb_inp, typeInput, divAddLoad);

				
				var divUpload = this.createDivUpload(nb_inp);
				var divCSV = this.createDivCSV(nb_inp);
				
				var divLoad = document.createElement('div');
				divLoad.setAttribute('id', "divLoad_" + nb_inp);
				divLoad.setAttribute('class', "col-lg-2 marges");

				var divReconcile = document.createElement('div');
				divReconcile.setAttribute('id', "divReconcile_" + nb_inp);
				divReconcile.setAttribute('class', "col-lg-3 marges");
				//divReconcile.setAttribute('role', "group");

				var bloc_inputs = document.getElementById('bloc-inputs');

				// add all new elements to the formulary
				var divMapping = document.createElement('div');
				divMapping.setAttribute('id', "divMapping_" + nb_inp);
				divMapping.setAttribute('class', "col-lg-12");

				var divSubmitMapping = document.createElement('div');
				divSubmitMapping.setAttribute('id', "divSubmitMapping_" + nb_inp);


				var divReconciliationCheck = document.createElement('div');
				divReconciliationCheck.setAttribute('id', "divReconciliationCheck_" + nb_inp);
				divReconciliationCheck.setAttribute('class', "col-lg-12");

				var divSubmitReconcile = document.createElement('div');
				divSubmitReconcile.setAttribute('id', "divSubmitReconcile_" + nb_inp);
				divSubmitReconcile.setAttribute('class', "col-lg-12 center");

				var divTableReconcile = document.createElement('div');
				divTableReconcile.setAttribute('id', "divTableReconcile_" + nb_inp);
				divTableReconcile.setAttribute('class', "col-lg-12 dataTable");
				divTableReconcile.style.display = "inline-block";

				
				divAddLoad.appendChild(divUpload)
				divAddLoad.appendChild(divCSV);
				
				divAddLoad.appendChild(divLoad);
				divAddLoad.appendChild(divReconcile);

				bloc_inputs.appendChild(globalInput);
				globalInput.appendChild(divAddLoad);	

				globalInput.appendChild(divMapping);
				globalInput.appendChild(divSubmitMapping);
				divSubmitMapping.setAttribute('class', "col-lg-12 center");

				globalInput.appendChild(divTableReconcile);
				globalInput.appendChild(divReconciliationCheck);
				globalInput.appendChild(divSubmitReconcile);
				
				//increment input counter
				document.getElementById(compteur).value ++;
			}
			
		}
		


	}
	if(typeInput == "raster"){
		var tableRaster = document.getElementById("rasterTable");
		var nb_header = document.getElementById("compteur_header").value;

		var rowInputRaster = tableRaster.insertRow(-1);
		var rowNb = parseInt(nb_header) + 2;
		rowInputRaster.id = "row_raster_" + rowNb;
		rowInputRaster.name = "row_raster_" + rowNb;

		var cellInputRaster = rowInputRaster.insertCell(0);

		this.createInputSpecial(nb_header, typeInput, cellInputRaster);

		var cellInputHeader = rowInputRaster.insertCell(1);
		this.createInputSpecial(nb_header, "header", cellInputHeader);

		document.getElementById("compteur_header").value ++;
		
		//increment input counter
		document.getElementById(compteur).value ++;
	}	

	
}

function createInputSpecial(nbInput, typeInput, container){
	var divContainer = document.createElement('div');

	var divBtn = document.createElement('div');   
	divBtn.setAttribute('class', 'btn');
	var span = document.createElement('span');
	span.innerHTML = "Browse";
	divBtn.appendChild(span);

	var inputLoad = document.createElement('input');
	inputLoad.setAttribute('type', 'file');
	inputLoad.setAttribute('id', typeInput + "_" + nbInput);
	inputLoad.setAttribute('name', typeInput + "_" + nbInput);
	inputLoad.setAttribute('value', "false");//boolean for valid or not upload
	divBtn.appendChild(inputLoad);

	var divFilePathWrapper = document.createElement('div');
	divFilePathWrapper.setAttribute('class','file-path-wrapper');
	var inputText = document.createElement('input');
	inputText.setAttribute('type', 'text');
	inputText.setAttribute('id', "text_" + typeInput + '_' + nbInput);
	inputText.setAttribute('name', "text_" + typeInput + '_' + nbInput);
	inputText.setAttribute('class', 'file-path validate');

	if(typeInput == "raster" || typeInput == "header"){
		divContainer.setAttribute('class', 'file-field input-field');
		inputLoad.setAttribute('onchange', "loadInputRaster(\"" + nbInput + "\",\"" + typeInput + "\")");
	}
	else if(typeInput == "inp"){
		divContainer.setAttribute('class', "file-field input-field col-lg-3");
		divContainer.setAttribute('id', "divAdd_" + nbInput);
		divContainer.setAttribute('role', "group");
		inputLoad.setAttribute('onchange', 'loadInputFile('+nbInput+')');
	}

	divFilePathWrapper.appendChild(inputText);

	divContainer.appendChild(divBtn);
	divContainer.appendChild(divFilePathWrapper);

	container.appendChild(divContainer);
}

//delete a input file 
function deleteField(compteur, typeInput){
	// count inputs number
	var nb_inp = document.getElementById(compteur).value;

	// if inputs exist => delete all
	var min = 0;
	if(typeInput == "inp"){
		min = 1;
	}
	if(nb_inp > min){
		var id = nb_inp - 1;
		var inp = document.getElementById(typeInput + "_" + id);

		if(typeInput == 'raster'){
			var tableRaster = document.getElementById("rasterTable");
			tableRaster.deleteRow(-1);
			document.getElementById('compteur_header').value --;
		}
		if(typeInput == 'inp'){
			var bloc_inputs = document.getElementById("bloc-inputs");
			var divAddLoad = document.getElementById("divAddLoad_" + id);
			var divAdd = document.getElementById("divAdd_" + id);
			var divMapping = document.getElementById("divMapping_" + id);
			var divSubmitMapping = document.getElementById("divSubmitMapping_" + id);

			var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + id);
			var divTableReconcile = document.getElementById("divTableReconcile_" + id);
			var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + id);

			var globalInput = document.getElementById("globalInput_" + id);
			bloc_inputs.removeChild(globalInput);

		}
		//decrement input counter
		document.getElementById(compteur).value --;
	}
}

//add or delete raster files
function addDeleteRasterFile(){
	var rasterOption = document.getElementById('raster').checked;
	var nb_raster = document.getElementById('compteur_raster').value;
	var divRasterFiles = document.getElementById("divRasterFiles");

	if(rasterOption){
		divRasterFiles.style.display = "block";
		if(nb_raster == 0){
			var tableRaster = document.getElementById("rasterTable");

			if(tableRaster == null){

				//divRasterFiles.id = "divRasterFiles";
				var addRasterButton = document.createElement("input");
				addRasterButton.id = "addRaster";
				addRasterButton.name = "addRaster";
				addRasterButton.type = "button";
				addRasterButton.setAttribute('class',"btn btn-default waves-effect waves-light font-small-button dropdown-toggle");
				//addRasterButton.style = "color:rgb(204,0,153); border-color:rgb(255,255,255); border-style:solid;";
				addRasterButton.size="51";
				addRasterButton.value = "Add bioclimatic and habitat data";
				addRasterButton.setAttribute("onclick", "addField(\'compteur_raster\',\'tdwg4\',\'raster\')");

				var delRasterButton = document.createElement("input");
				delRasterButton.id = "delRaster";
				delRasterButton.name = "delRaster";
				delRasterButton.type = "button";
				delRasterButton.size = "51";
				delRasterButton.value = "Delete bioclimatic and habitat data";
				delRasterButton.setAttribute('class', 'btn btn-default waves-effect waves-light font-small-button');
				delRasterButton.setAttribute("onclick","deleteField(\'compteur_raster\',\'raster\')");

				var tableRaster = document.createElement("table");
				tableRaster.id = "rasterTable";
				tableRaster.name = "rasterTable";
				tableRaster.setAttribute("class", "table table-marges");
				divRasterFiles.appendChild(tableRaster);

				this.addLine(addRasterButton,delRasterButton, "row_raster_0");
				this.addLine("Raster file","Header file","row_raster_1");				


			}
		}
	}
	else{
		divRasterFiles.style.display = "none";
	}
}

function addLine(val1,val2, idLine){

	// On créé le corps du tableau
	TBODY = document.createElement ("tbody");
	THEAD = document.createElement('thead');

	// On créé la ligne
	TR = document.createElement ("tr");
	TR.setAttribute("id", idLine);

	// On créé la premiere cellule
	TD1  = document.createElement ("td");
	if(idLine == "row_raster_1"){
		TXT1 = document.createTextNode (val1);
		TD1.appendChild (TXT1);
	}
	else{
		TD1.appendChild(val1);
	}


	// On créé la deuxieme cellule
	TD2  = document.createElement ("td");
	if(idLine == "row_raster_1"){
		TXT2 = document.createTextNode (val2);
		TD2.appendChild (TXT2);
	}
	else{
		TD2.appendChild(val2);
	}


	// On assemble les cellules a la ligne
	TR.appendChild(TD1);
	TR.appendChild(TD2);

	if(idLine != "row_raster_0"){
		// On assemble la ligne au corps du tableau
		TBODY.appendChild(TR);

		// On assemble le corps du tableau au tableau
		document.getElementById ('rasterTable').appendChild (TBODY);
	}
	else{
		THEAD.appendChild(TR);

		// On assemble le corps du tableau au tableau
		document.getElementById ('rasterTable').appendChild (THEAD);
	}



}

//initialise all establishmentMeans option to "false" if main checkbox doesn't checked
function established() {
	var establishmentIsChecked = document.getElementById("establishment").checked;
	if(!establishmentIsChecked){

		document.getElementById("native").checked = false;
		document.getElementById("introduced").checked = false;
		document.getElementById("naturalised").checked = false;
		document.getElementById("invasive").checked = false;
		document.getElementById("managed").checked = false;
		document.getElementById("uncertain").checked = false;
		document.getElementById("others").checked = false;
	}
	else{
		document.getElementById("native").checked = true;
		document.getElementById("introduced").checked = true;
		document.getElementById("naturalised").checked = true;
		document.getElementById("invasive").checked = true;
		document.getElementById("managed").checked = true;
		document.getElementById("uncertain").checked = true;
		document.getElementById("others").checked = true;
	}
}

//check option establishmentMeans
function checkEstablishment(){

	var native = document.getElementById("native").checked;
	var introduced = document.getElementById("introduced").checked;
	var naturalised = document.getElementById("naturalised").checked;
	var invasive = document.getElementById("invasive").checked;
	var managed = document.getElementById("managed").checked;
	var uncertain = document.getElementById("uncertain").checked;
	var others = document.getElementById("others").checked;

	if(native || introduced || naturalised || invasive || managed || uncertain || others){
		document.getElementById("establishment").checked = true;
	}
	if(!native && !introduced && !naturalised && !invasive && !managed && !uncertain && !others){
		document.getElementById("establishment").checked = false;
	}
}

//add a new element with all parameters
function addElement(id, typeElement, type, onclick, style, value, elementAfter){
	var element = document.createElement(typeElement);
	element.setAttribute('type', type);
	element.setAttribute('id', id);
	element.setAttribute('name', id);
	element.setAttribute('onclick', onclick);
	element.setAttribute('style', style );
	element.setAttribute('value', value);

	var after = document.getElementById(elementAfter);
	var formulaire = document.getElementById('formulaire');
	formulaire.insertBefore(element, after);
}


function createDivUpload(nb_input){
	var divUpload = document.createElement('div');
	divUpload.setAttribute('id', "divUpload_" + nb_input);
	divUpload.setAttribute('name', "divUpload_" + nb_input);
	divUpload.setAttribute('class', "col-lg-2 marges");

	var okUpload = document.createElement('div');
	okUpload.setAttribute('id', "divOKUpload_" + nb_input);
	okUpload.setAttribute('name', "divOKUpload_" + nb_input);
	okUpload.setAttribute('class', "");

	var cancelUpload = document.createElement('div');
	cancelUpload.setAttribute('id', "divCancelUpload_" + nb_input);
	cancelUpload.setAttribute('name', "divCancelUpload_" + nb_input);
	cancelUpload.setAttribute('class', "");

	divUpload.appendChild(okUpload);
	divUpload.appendChild(cancelUpload);

	return divUpload;
}

function createDivCSV(nb_input){
	var divCSV = document.createElement('div');
	divCSV.setAttribute('id', "divCSV_" + nb_input);
	divCSV.setAttribute('class', "col-lg-2 marges");

	return divCSV;
}

//when click on "convert" checkbox
function loadInputFile(nb_input, action){
	//action=change ou cancel
	var fileExist = document.getElementById('inp_' + nb_input);
	var filename = fileExist.value;
	var inputText = document.getElementById("text_inp_" + nb_input);
	inputText.value = filename;
	if(fileExist.value != null){
		displayButtonsUpload(nb_input, action);
		//var columns = this.mappingDWC(counter, change_loadMapping_reconcile);
		//taxonReconciliation(columns, counter, change_loadMapping_reconcile);
	}	
}

function displayButtonsUpload(nb_input, action){
	var divUpload = document.getElementById("divUpload_" + nb_input);
	var divOkUpload = document.getElementById("divOKUpload_" + nb_input);
	var divCancelUpload = document.getElementById("divCancelUpload_" + nb_input);
	var okButton = document.getElementById("okButtonUpload_" + nb_input);
	
	if(okButton == null){
		var buttonOkUpload = document.createElement('button');
		buttonOkUpload.setAttribute('type', "button");
		buttonOkUpload.setAttribute('id', "okButtonUpload_" + nb_input);
		buttonOkUpload.setAttribute('class', "btn btn-default waves-effect waves-light font-medium-button");
		buttonOkUpload.setAttribute('onclick', "uploadInputFile('" + nb_input + "')");

		var buttonCancelUpload = document.createElement('button');
		buttonCancelUpload.setAttribute('type', "button");
		buttonCancelUpload.setAttribute('id', "cancelButtonUpload_" + nb_input);
		buttonCancelUpload.setAttribute('class', "btn btn-default waves-effect waves-light font-medium-button");
		buttonCancelUpload.setAttribute('onclick', "cancelInputFile('" + nb_input + "','cancel')");

		divOkUpload.appendChild(buttonOkUpload);
		divCancelUpload.appendChild(buttonCancelUpload);
		
		divUpload.appendChild(divOkUpload);
		divUpload.appendChild(divCancelUpload);
		
		$("#okButtonUpload_" + nb_input).text('Upload');
		$("#cancelButtonUpload_" + nb_input).text("Cancel");	
	}
	else{

		cancelInputFile(nb_input, action);
	}
	
}

function cancelInputFile(nb_input, action){
	var textinput = document.getElementById("text_inp_" + nb_input);
	console.log("text_inp_" + nb_input);
	var sampleText = textinput.value;

	var inputFile = document.getElementById("inp_" + nb_input);
	var sampleFile = inputFile.files[0];
	
	var filesize = sampleFile.size;

	if(sampleText != ""){
		var formdata = new FormData();
		formdata.append("uuid", this.getUUID());
		formdata.append("nbInput", nb_input);
		formdata.append("action", "cancel");
		formdata.append("sampleFile", sampleFile);

		var xhrPOST = new XMLHttpRequest();

		xhrPOST.open("POST","uploadController", true);
		xhrPOST.setRequestHeader("Content-Length", filesize);
		//xhrPOST.addEventListener("progress", updateProgress, false);
		xhrPOST.addEventListener("error", transferFailed, false);
		xhrPOST.addEventListener("abort", transferCanceled, false);

		xhrPOST.send(formdata); 

		xhrPOST.onload = function(e) {

			if (this.status == 200) {
				actionSeparatorCSV(nb_input, "cancel");
				actionMappingButton(nb_input, "cancel");
				actionReconcileButton(nb_input, "cancel");
				console.log(this.responseText);
				//readInputFile(this.responseText, nbInput);
			}
		}
	}
	
	var tableMapping = document.getElementById("mappingTable_" + nb_input);
	var divMapping = document.getElementById("divMapping_" + nb_input);
	var infosMapping = document.getElementById("infosMapping_" + nb_input);
	var mappingActive = document.getElementById("mappingActive_" + nb_input);
	
	var divSubmitMapping = document.getElementById("divSubmitMapping_" + nb_input);
	var divSubmitMappingOK = document.getElementById("divSubmitMappingOK_" + nb_input);
	var divSubmitMappingCancel = document.getElementById("divSubmitMappingCancel_" + nb_input);
	
	var divMessageSaved = document.getElementById("divMessageSaved_" + nb_input);
	var divMessagedCancelled = document.getElementById("divMessageCancelled_" + nb_input);
	
	var tablePrepareReconcile = document.getElementById("tablePrepareReconcile_" + nb_input);
	var divTableReconcile = document.getElementById("divTableReconcile_" + nb_input);
	var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + nb_input);
	var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + nb_input);
	var divUrlTaxo = document.getElementById("divUrlTaxo_" + nb_input);
	var divMessageReconcileCancelled  = document.getElementById("divMessageReconcileCancelled_" + nb_input);
	var divMessageReconcileSaved = document.getElementById("divMessageReconcileSaved_" + nb_input);
	var divButtonCancelReconciliation = document.getElementById("divButtonCancelReconciliation_" + nb_input);
	var divButtonValidReconciliation = document.getElementById("divButtonValidReconciliation_" + nb_input);
	var tableReconcile = document.getElementById("tableReconcile_" + nb_input + "_wrapper");
	var divButtonStartReconciliation = document.getElementById("divButtonStartReconciliation_" + nb_input);
	
	var globalInput = document.getElementById("globalInput_" + nb_input);
	var divCSV = document.getElementById("divCSV_" + nb_input);
	var divLoad = document.getElementById("divLoad_" + nb_input);
	var divReconcile = document.getElementById("divReconcile_" + nb_input);
	var selectCSV = document.getElementById("csvDropdown_" + nb_input);
	var buttonConvert = document.getElementById("convert_" + nb_input);
	var buttonReconcile = document.getElementById("reconcileButton_" + nb_input);
	
	var divOkUpload = document.getElementById("divOKUpload_" + nb_input);
	var divCancelUpload = document.getElementById("divCancelUpload_" + nb_input);
	var okUploadButton = document.getElementById("okButtonUpload_" + nb_input);
	var cancelUploadButton = document.getElementById("cancelButtonUpload_" + nb_input);
	
	if(action == "cancel"){
		var inputText = document.getElementById("text_inp_" + nb_input);
		inputText.value = "";
		
		inputFile.setAttribute('value', false);
	}
	
	
	if(tableMapping){
		divMapping.removeChild(infosMapping);
		divMapping.removeChild(tableMapping);
		divMapping.removeChild(mappingActive);

		divSubmitMapping.removeChild(divSubmitMappingOK);
		divSubmitMapping.removeChild(divSubmitMappingCancel);
		divSubmitMapping.removeChild(divMessageSaved);
		divSubmitMapping.removeChild(divMessagedCancelled);
	}

	if(tablePrepareReconcile){
		divReconciliationCheck.removeChild(tablePrepareReconcile);
		divReconciliationCheck.removeChild(divUrlTaxo);
	}

	if(divMessageReconcileSaved){
		divMessageReconcileSaved.style.display = "none";
	}
	if(divMessageReconcileCancelled){
		divMessageReconcileCancelled.style.display = "none";
	}

	if(divButtonCancelReconciliation){
		divSubmitReconcile.removeChild(divButtonCancelReconciliation);
	}

	if(divButtonStartReconciliation){
		divSubmitReconcile.removeChild(divButtonStartReconciliation);
	}

	if(divButtonValidReconciliation){
		divSubmitReconcile.removeChild(divButtonValidReconciliation);
	}

	if(tableReconcile){
		divTableReconcile.removeChild(tableReconcile);
	}
}

function uploadInputFile(nb_input){
	var sampleText = document.getElementById("text_inp_" + nb_input).value;

	var sampleFile = document.getElementById("inp_" + nb_input).files[0];

	var filesize = sampleFile.size;
	var uuid = this.getUUID();
	if(sampleText != ""){
		var divLoadingIcon = document.createElement("div");
		divLoadingIcon.setAttribute('class', "preloader-wrapper small active");
		divLoadingIcon.setAttribute('id', "loadIcon_" + nb_input);
		var divSpinnerIcon = document.createElement('div');
		divSpinnerIcon.setAttribute('class', "spinner-layer spinner-red-only");
		var divCircleLeft = document.createElement("div");
		divCircleLeft.setAttribute('class', "circle-clipper left");
		var divCircleOne = document.createElement("div");
		divCircleOne.setAttribute('class', "circle");
		var divGapPatch = document.createElement("div");
		divGapPatch.setAttribute('class', "gap-patch");
		var divCircleTwo = document.createElement("div");
		divCircleTwo.setAttribute('class', "circle");
		var divCircleThree = document.createElement("div");
		divCircleThree.setAttribute('class', "circle");
		var divClipperRight = document.createElement("div");
		divClipperRight.setAttribute('class',"circle-clipper right");
		
		divCircleLeft.appendChild(divCircleOne);
		divGapPatch.appendChild(divCircleTwo);
		divClipperRight.appendChild(divCircleThree);
		divSpinnerIcon.appendChild(divCircleLeft);
		divSpinnerIcon.appendChild(divGapPatch);
		divSpinnerIcon.appendChild(divClipperRight);
		divLoadingIcon.appendChild(divSpinnerIcon);
		
		var divAddLoad = document.getElementById("divAddLoad_" + nb_input);
		divAddLoad.appendChild(divLoadingIcon);
		
		var formdata = new FormData();
		formdata.append("uuid", uuid);
		formdata.append("nbInput", nb_input);
		formdata.append("action", "upload");
		formdata.append("sampleFile", sampleFile);
		console.log(formdata);
		var xhrPOST = new XMLHttpRequest();

		xhrPOST.open("POST","uploadController", true);
		xhrPOST.setRequestHeader("Content-Length", filesize);
		//xhrPOST.addEventListener("progress", updateProgress, false);
		xhrPOST.addEventListener("error", transferFailed, false);
		xhrPOST.addEventListener("abort", transferCanceled, false);

		xhrPOST.send(formdata); 

		xhrPOST.onload = function(e) {

			if (this.status == 200) {
				
				divAddLoad.removeChild(divLoadingIcon);
				var inputLoad = document.getElementById("inp_" + nb_input);
				inputLoad.setAttribute('value', true);//boolean for valid upload
				
				actionSeparatorCSV(nb_input, "upload");
				actionMappingButton(nb_input, "upload");
				actionReconcileButton(nb_input, "upload");
				
				var buttonConvert = document.getElementById("convert_" + nb_input);
				buttonConvert.setAttribute("onclick" , "loadMappingDwc('" + this.responseText + "'," + nb_input + ")");
				
				var reconcileButton = document.getElementById("reconcileButton_" + nb_input);
				reconcileButton.setAttribute("onclick" , "loadReconcileService('" + uuid + "','" + nb_input + "')");
			}
		}
	}
	
}

function transferFailed(){
	alert("Transfert error ");
}

function transferCanceled(){
	alert("Transfert cancelled");
}

function actionMappingButton(nb_input, action){
	var convertButton = document.getElementById('convert_'+ nb_input);
	var divAddLoad = document.getElementById("divAddLoad_" + nb_input);
	var buttonConvert = null;
	var divLoad = document.getElementById("divLoad_" + nb_input);
	
	if(action == "upload"){
		if(!convertButton){

			buttonConvert = document.createElement('button');
			buttonConvert.type = "button";
			buttonConvert.id = "convert_" + nb_input;
			buttonConvert.name = "convert_" + nb_input;
			buttonConvert.setAttribute("onclick" , "loadMappingDwc(" + nb_input + ")");
			buttonConvert.setAttribute('class', "btn btn-default waves-effect waves-light font-medium-button");

			divLoad.appendChild(buttonConvert);
			$("#convert_" + nb_input).text('Map to Dwc');
			divAddLoad.appendChild(divLoad);

		}
	}
	else if(action == "cancel"){
		if(convertButton){
			divLoad.removeChild(convertButton);
		}
	}
	
	
}

function actionReconcileButton(nb_input, action){
	var divAddLoad = document.getElementById("divAddLoad_" + nb_input);
	var reconcileButton = document.getElementById("reconcileButton_" + nb_input);
	var buttonReconcile = null;
	var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + nb_input);
	var divTableReconcile = document.getElementById("divTableReconcile_" + nb_input);
	var divReconcile = document.getElementById("divReconcile_" + nb_input);
	
	if(action == "upload"){
		if(!reconcileButton){
			buttonReconcile = document.createElement('button');
			buttonReconcile.type = "button";
			buttonReconcile.id = "reconcileButton_" + nb_input;
			buttonReconcile.name = "reconcileButton_" + nb_input;
			buttonReconcile.setAttribute("onclick" , "loadReconcileService(" + nb_input + ")");
	        buttonReconcile.setAttribute('class', "btn btn-default waves-effect waves-light font-medium-button");
			divReconcile.appendChild(buttonReconcile);
	        $("#reconcileButton_" + nb_input).text("Taxonomic validation");
			divAddLoad.appendChild(divReconcile);
		}
	}
	else if(action == "cancel"){
		if(reconcileButton){
			divReconcile.removeChild(reconcileButton);
		}
		
	}
	
	
}

function actionSeparatorCSV(nb_inp, action){
	var divCSV = document.getElementById("divCSV_" + nb_inp);
	var select = document.getElementById("csvDropdown_" + nb_inp);
	var divAddLoad = document.getElementById('divAddLoad_' + nb_inp);
	
	if(action == "upload"){
		if(!select){
			var selectCSV = document.createElement('select');
			selectCSV.setAttribute('id', "csvDropdown_" + nb_inp);
			selectCSV.setAttribute('name', "csvDropdown_" + nb_inp);
			selectCSV.setAttribute('class', "csvDropdown");
	
			var optionComma = document.createElement('option');
			optionComma.setAttribute('id', "optionComma_" + nb_inp);
			optionComma.setAttribute('name', "optionComma_" + nb_inp);
			optionComma.setAttribute('value', "comma");
			optionComma.innerHTML = "Comma";
	
			var optionTab = document.createElement('option');
			optionTab.setAttribute('id', "optionTab_" + nb_inp);
			optionTab.setAttribute('name', "optionTab_" + nb_inp);
			optionTab.setAttribute('value', "tab");
			optionTab.innerHTML = "Tabulation";
	
			var optionSemiComma = document.createElement('option');
			optionSemiComma.setAttribute('id', "optionSemiComma_" + nb_inp);
			optionSemiComma.setAttribute('name', "optionSemiComma_" + nb_inp);
			optionSemiComma.setAttribute('value', "semiComma");
			optionSemiComma.innerHTML = "Semi comma";
	
			selectCSV.appendChild(optionComma);
			selectCSV.appendChild(optionSemiComma);
			selectCSV.appendChild(optionTab);
	
			divCSV.appendChild(selectCSV);
			
			divAddLoad.appendChild(divCSV);
		}
	}
	else if(action == "cancel"){
		if(select){
			divCSV.removeChild(select);
		}
	}
}


function activeRunning(){
	var checkInputs = checkingInputs();
	//var checkRasters = checkRasterFiles();
	//alert("raster : " + checkRasters);
	if(checkInputs == false){
		alert("File(s) aren't uploaded, please upload it/them");
	}
	else{
		var submitButton = document.getElementById("workflowLaunch");
		submitButton.setAttribute('type', 'submit');
		var divRunning = document.getElementById('running');   
		var divBody = document.getElementById('divBody');
		divRunning.style.display = 'block';
		divBody.style.display = 'none';
	}
	
	
}

function checkingInputs(){
	var all_ok = new Boolean(true);
	var nbInput = document.getElementById("compteur_inp").value;
	
	for(var i = 1; i <= nbInput; i++){
		var input = document.getElementById("inp_" + (i - 1));
		var upload = input.getAttribute('value');
		
		if(upload == 'false'){
			all_ok = false;
		}
	}
	
	return all_ok;
}

function checkRasterFiles(){
	var all_ok = new Boolean(true);
	var nbRaster = document.getElementById("compteur_raster").value;
	var nbHeader = document.getElementById("compteur_header").value;
	var checkboxRaster = document.getElementById("raster").checked;
	if(checkboxRaster){
		if(nbRaster == 0){
			all_ok = false;
		}
	}
	
	return all_ok;
}

