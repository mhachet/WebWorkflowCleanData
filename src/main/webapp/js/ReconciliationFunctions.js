var inputList = [];

function InputObject(nbInput, rows, cols, contentFile, presentTags, reconcileTags){
	this.id = nbInput;
	this.rows = rows;
	this.cols = cols;
	this.file = contentFile;
	this.tags = presentTags;
	this.tagsReconcile = reconcileTags;
}

function addInput(inputObject){
	inputList.push(inputObject);
}

function getInput(nbInput){
	var inputLength = inputList.length;
	for(var i = 0 ; i < inputLength ; i++){
		var input = inputList[i];
		var inputId = input.id;
		if(nbInput == inputId){
			return input;
		}
	}
}

function getJson(nbInput){
	var inputLength = inputList.length;
	for(var i = 0 ; i < inputLength ; i++){
		var input = inputList[i];
		var inputId = input.id;
		if(nbInput == inputId){
			return input.json;
		}
	}
}

function getTags(nbInput){
	var inputLength = inputList.length;
	for(var i = 0 ; i < inputLength ; i++){
		var input = inputList[i];
		var inputId = input.id;
		if(nbInput == inputId){
			return input.tags;
		}
	}
}

function getReconcileTags(nbInput){
	var inputLength = inputList.length;
	for(var i = 0 ; i < inputLength ; i++){
		var input = inputList[i];
		var inputId = input.id;
		if(nbInput == inputId){
			return input.tagsReconcile;
		}
	}
}

function getRows(nbInput){
	var inputLength = inputList.length;
	for(var i = 0 ; i < inputLength ; i++){
		var input = inputList[i];
		var inputId = input.id;
		if(nbInput == inputId){
			return input.rows;
		}
	}
}

function getCols(nbInput){
	var inputLength = inputList.length;
	for(var i = 0 ; i < inputLength ; i++){
		var input = inputList[i];
		var inputId = input.id;
		if(nbInput == inputId){
			return input.cols;
		}
	}
}

function getFile(nbInput){
	var inputLength = inputList.length;
	for(var i = 0 ; i < inputLength ; i++){
		var input = inputList[i];
		var inputId = input.id;
		if(nbInput == inputId){
			return input.file;
		}
	}
}

function deleteInput(nbInput, newInput){
	inputList.splice(nbInput, 1, newInput);
}

function loadReconcileService(uuid, nb_input){

	var separator = "";
	var texte =  document.getElementById("csvDropdown_" + nb_input).options[document.getElementById("csvDropdown_" + nb_input).selectedIndex].value; 
	if(texte == "comma"){
		separator = ',';
	}
	else if(texte == "semiComma"){
		separator = ';';
	}
	else{
		separator = '\t';    
	}

	var tableReconcileWraper = document.getElementById("tableReconcile_" + nb_input + "_wrapper");
	var divTableReconcile = document.getElementById("divTableReconcile_" + nb_input);
	var reconcileActive = document.getElementById("reconcileActive_" + nb_input);
	var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + nb_input);

	var divMessageReconcileCancelled = document.getElementById("divMessageReconcileCancelled_" + nb_input);
	var divMessageReconcileSaved = document.getElementById("divMessageReconcileSaved_" + nb_input);

	var divButtonStartReconciliation = document.getElementById("divButtonStartReconciliation_" + nb_input);
	var divButtonCancelReconciliation = document.getElementById("divButtonCancelReconciliation_" + nb_input);
	var divButtonValidReconciliation = document.getElementById("divButtonValidReconciliation_" + nb_input);

	var tablePrepareReconcile = document.getElementById("tablePrepareReconcile_" + nb_input);
	var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + nb_input);

	if(!tablePrepareReconcile){
		if(!tableReconcileWraper){
			//console.log("doPrepare");
			doPreparationReconciliation(uuid, nb_input, separator);
		}
		else{
			//console.log("strange");
		}
	}
	else{
		if(reconcileActive){
			//console.log("display prepareTable and remove tableWrapper");
			if(tableReconcileWraper){
				divTableReconcile.removeChild(tableReconcileWraper);
			}

			divMessageReconcileCancelled.style.display = "none";
			divMessageReconcileSaved.style.display = "none";
			divButtonCancelReconciliation.style.display = "block";
			divButtonStartReconciliation.style.display = "block";
			divButtonValidReconciliation.style.display = "none";

			divReconciliationCheck.style.display = "block";
			reconcileActive.setAttribute('value', 'false');
		}
		else{
			startReconciliation(nb_input, uuid);

		}
	}


}




function doPreparationReconciliation(uuid, nb_input, separator){
	var sampleText = document.getElementById("text_inp_" + nb_input).value;

	if(sampleText != ""){
		var formdata = new FormData();
		formdata.append("action", "preparation");
		formdata.append("uuid", uuid);
		formdata.append("nbInput", nb_input);
		formdata.append("checkingColumn", "");
		formdata.append("separator", separator);

		var xhrPOST = new XMLHttpRequest();

		xhrPOST.open("POST","reconcileController", true);
		//xhrPOST.setRequestHeader("Content-Length", filesize);
		//xhrPOST.addEventListener("progress", updateProgress, false);
		xhrPOST.addEventListener("error", transferFailed, false);
		xhrPOST.addEventListener("abort", transferCanceled, false);

		xhrPOST.send(formdata); 

		xhrPOST.onload = function(e) {

			if (this.status == 200) {
				var firstline = this.responseText;
				var selectUrl = document.getElementById("selectUrl_" + nb_input);
				//var urlTaxo = selectUrl.options[selectUrl.selectedIndex].text;
				//var urlTaxo = document.getElementById("urlAPItaxo_" + nb_input);
				if(selectUrl == null){
					var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + nb_input);

					var divTaxo = document.createElement("div");
					divTaxo.setAttribute('class', "col-lg-12");
					divTaxo.id = "divTaxo_" + nb_input;

					var divUrlTaxo = document.createElement('div');
					divUrlTaxo.setAttribute('class', "input-field col s12 m5");
					divUrlTaxo.id = "divUrlTaxo_" + nb_input;

					var selectUrl = document.createElement('select');
					selectUrl.setAttribute('id', "selectUrl_" + nb_input);
					var optionUrl_0 = document.createElement('option');
					//optionUrl_0.setAttribute('disabled selected',"");
					optionUrl_0.innerHTML = "Choose your url";

					var optionUrl_1 = document.createElement('option');
					optionUrl_1.setAttribute('value',"1");
					optionUrl_1.innerHTML = "http://data1.kew.org/reconciliation/reconcile/IpniName";
					optionUrl_1.setAttribute('id', "optionUrl1_" + nb_input);

					var optionUrl_2 = document.createElement('option');
					optionUrl_2.setAttribute('value',"2");
					optionUrl_2.innerHTML = "http://refine.taxonomics.org/gbifchecklists/reconcile";
					optionUrl_2.setAttribute('id', "optionUrl2_" + nb_input);

					//selectUrl.setAttribute('class', "selectpicker");
					//selectUrl.setAttribute('data-style', "btn-white");
					//selectUrl.setAttribute('data-width', "100%");
					//selectUrl.style.display = "block";
					selectUrl.appendChild(optionUrl_0);
					selectUrl.appendChild(optionUrl_1);
					selectUrl.appendChild(optionUrl_2);

					divUrlTaxo.appendChild(selectUrl);

					/*
					var urlTaxo = document.createElement("input");
					urlTaxo.id = "urlAPItaxo_" + nb_input;
					urlTaxo.type = "url";
					urlTaxo.setAttribute('class', "col-lg-10");
					//urlTaxo.placeholder = "http://data1.kew.org/reconciliation/reconcile/IpniName";

					var labelUrl = document.createElement('label');
					labelUrl.id = "labelUrlTaxo_" + nb_input;
					labelUrl.setAttribute('class', "col-lg-3");
					labelUrl.value = "http://data1.kew.org/reconciliation/reconcile/IpniName";
					*/
					var divNoteReconcile = document.createElement('div');
					divNoteReconcile.setAttribute('id', 'divNoteReconcile_' + nb_input);
					divNoteReconcile.setAttribute('class', "col s12 m7 note-reconcile");
					var noteReconcile = document.createElement('h6');
					noteReconcile.innerHTML = "For more information, see the documentation about the ";
					//noteReconcile.setAttribute('class', "col s12 m7");
					var refDocReconcile = document.createElement('a');
					refDocReconcile.setAttribute('href', "DocumentationPage.html#help-reconcile-div");
					refDocReconcile.innerHTML = "taxonomic reconciliation";

					noteReconcile.appendChild(refDocReconcile);
					divNoteReconcile.appendChild(noteReconcile);
					//divUrlTaxo.appendChild(labelUrl);
					//divUrlTaxo.appendChild(urlTaxo);
					//divTaxo.appendChild(labelUrl);
					divTaxo.appendChild(divUrlTaxo);
					divTaxo.appendChild(divNoteReconcile);
					//divReconciliationCheck.appendChild(noteReconcile);
					divReconciliationCheck.appendChild(divTaxo);

					//$("#labelUrlTaxo_" + nb_input).text("Url reference");

					//$(".selectpicker").selectpicker();
					$("#selectUrl_" + nb_input).material_select();


				}
				createReconciliationPreparation(uuid, firstline.split(separator), nb_input); 


			}
			else{
				console.log("error");
			}
		}
	}


}

function getContentFile(uuid, nb_input, infosColumns, separator){
	var sampleText = document.getElementById("text_inp_" + nb_input).value;
	var contentFile = "";

	if(sampleText != ""){
		var formdata = new FormData();
		formdata.append("action", "reconciliation");
		formdata.append("uuid", uuid);
		formdata.append("nbInput", nb_input);
		formdata.append("infosColumns", infosColumns);
		formdata.append("separator", separator);

		var xhrPOST = new XMLHttpRequest();

		xhrPOST.open("POST","reconcileController", true);
		//xhrPOST.setRequestHeader("Content-Length", filesize);
		//xhrPOST.addEventListener("progress", updateProgress, false);
		xhrPOST.addEventListener("error", transferFailed, false);
		xhrPOST.addEventListener("abort", transferCanceled, false);

		xhrPOST.send(formdata); 

		xhrPOST.onload = function(e) {

			if (this.status == 200) {

				contentFile = this.responseText;
				//alert("ok")
				//console.log(contentFile);

				doReconciliation(nb_input, contentFile, separator);

			}
			else{
				console.log("error")
			}
		}
	}

}

function foundColumnCheck(nb_input){

	var dropdownReconcileCheck = document.getElementById("dropdownReconcile_" + nb_input + "_0").value;
	return dropdownReconcileCheck;

}

function foundColumnsInfos(nb_input){
	var arrayColumnsInfo = [];

	for(var i =1; i < 5; i++){
		var dropdownReconcileInfo = document.getElementById("dropdownReconcile_" + nb_input + "_" + i).value;
		arrayColumnsInfo.push(dropdownReconcileInfo);
	}

	return arrayColumnsInfo;
}

function createReconciliationPreparation(uuid, presentTags, nbInput){
	//console.log(presentTags);
	var columnPrepare = ["Column to check","column for help 1","column for help 2","column for help 3","column for help 4"];
	var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + nbInput);
	var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + nbInput);
	var divTableReconcile = document.getElementById("divTableReconcile_" + nbInput);

	var tablePrepareReconcile = document.getElementById("tablePrepareReconcile_" + nbInput);

	if(tablePrepareReconcile == null){
		var tablePrepareReconcile = document.createElement("table");
		tablePrepareReconcile.id = "tablePrepareReconcile_" + nbInput;
		tablePrepareReconcile.name = "tablePrepareReconcile_" + nbInput;
		tablePrepareReconcile.border = "0";
		tablePrepareReconcile.setAttribute("class", "table-marges col-lg-6");
		var tbody = document.createElement("tbody");
		tablePrepareReconcile.appendChild(tbody);

		for(var i = 0 ; i < columnPrepare.length; i++){
			var row = tablePrepareReconcile.insertRow(-1);
			row.id = "rowReconcile_"+i;
			row.name = "rowReconcile_"+i;
			tbody.appendChild(row);
			var cellInput = row.insertCell(0);
			cellInput.innerHTML = columnPrepare[i];
			cellInput.style = "text-align:left";

			var dropdownReconcile = document.createElement("select");
			dropdownReconcile.name = "dropdownReconcile_" + nbInput + "_" + i;
			dropdownReconcile.setAttribute('class', "selectpicker");
			dropdownReconcile.id = "dropdownReconcile_" + nbInput + "_" + i;
			dropdownReconcile.style.display = "block";
			dropdownReconcile.setAttribute('data-style', "btn-white");
			dropdownReconcile.setAttribute('data-width', "100%");
			var cellReconcile = row.insertCell(1);

			for(var j = 0 ; j < presentTags.length; j++){
				var tag = presentTags[j];

				var optionTag = document.createElement('option');
				optionTag.value = tag;
				optionTag.text = tag;

				dropdownReconcile.appendChild(optionTag);
			}
			cellReconcile.appendChild(dropdownReconcile);

			//tablePrepareReconcile.appendChild(row);
			//tablePrepareReconcile.appendChild(cellInput);
			//tablePrepareReconcile.appendChild(cellReconcile);
		}

		divReconciliationCheck.appendChild(tablePrepareReconcile);

		$(".selectpicker").selectpicker();


		var buttonStartReconciliation = document.createElement('button');
		buttonStartReconciliation.id = "buttonStartReconciliation_" + nbInput;
		buttonStartReconciliation.name = "buttonStartReconciliation_" + nbInput;
		//buttonStartReconciliation.value = "Start reconciliation";
		buttonStartReconciliation.type = "button";
		buttonStartReconciliation.setAttribute("onclick", "startReconciliation(" + nbInput + ",'" + uuid + "')");
		buttonStartReconciliation.setAttribute('class', "btn btn-default waves-effect waves-light font-small-button");

		var divButtonStartReconciliation = document.createElement('div');
		divButtonStartReconciliation.id = "divButtonStartReconciliation_" + nbInput;
		divButtonStartReconciliation.setAttribute('class', "col-lg-6");
		divButtonStartReconciliation.appendChild(buttonStartReconciliation);

		divSubmitReconcile.appendChild(divButtonStartReconciliation);


		var buttonCancelReconciliation = document.createElement('button');
		buttonCancelReconciliation.id = "buttonCancelReconciliation_" + nbInput;
		buttonCancelReconciliation.name = "buttonCancelReconciliation_" + nbInput;
		//buttonCancelReconciliation.value = "Cancel reconciliation";
		buttonCancelReconciliation.type = "button";
		buttonCancelReconciliation.setAttribute("onclick", "cancelReconciliation(" + nbInput + ")");
		buttonCancelReconciliation.setAttribute('class', "btn btn-default waves-effect waves-light font-small-button");

		var divButtonCancelReconciliation= document.createElement('div');
		divButtonCancelReconciliation.id = "divButtonCancelReconciliation_" + nbInput;
		divButtonCancelReconciliation.setAttribute('class', "col-lg-6");
		divButtonCancelReconciliation.appendChild(buttonCancelReconciliation);


		divSubmitReconcile.appendChild(divButtonCancelReconciliation);
		$("#buttonCancelReconciliation_" + nbInput).text("Cancel reconciliation");
		$("#buttonStartReconciliation_" + nbInput).text("Start reconciliation");

		var buttonValidReconciliation = document.createElement('button');
		buttonValidReconciliation.id = "buttonValidReconciliation_" + nbInput;
		buttonValidReconciliation.name = "buttonValidReconciliation_" + nbInput;
		//buttonValidReconciliation.value = "Valid reconciliation";
		buttonValidReconciliation.type = "button";
		var activeReconcile = new Boolean(true);
		buttonValidReconciliation.setAttribute("onclick", "setReconciliation(" + activeReconcile + "," + nbInput + ")");
		buttonValidReconciliation.setAttribute('class',"btn btn-default waves-effect waves-light font-small-button");

		var divButtonValidReconciliation = document.createElement('div');
		divButtonValidReconciliation.id = "divButtonValidReconciliation_" + nbInput;
		divButtonValidReconciliation.setAttribute('class', "col-lg-6");
		divButtonValidReconciliation.appendChild(buttonValidReconciliation);

		divButtonValidReconciliation.style.display ="none";

		divSubmitReconcile.appendChild(divButtonValidReconciliation);
		$("#buttonValidReconciliation_" + nbInput).text("Valid reconciliation");

		var divMessageReconcileSaved = document.createElement('div');
		divMessageReconcileSaved.setAttribute('id', "divMessageReconcileSaved_" + nbInput);
		divMessageReconcileSaved.setAttribute('class', "col-lg-12 text-success text-center");
		divMessageReconcileSaved.style.display = "none";

		var messageReconcileSaved = document.createElement('p');
		messageReconcileSaved.innerHTML = "Reconciliation service saved";
		divMessageReconcileSaved.appendChild(messageReconcileSaved);

		var divMessageReconcileCancelled = document.createElement('div');
		divMessageReconcileCancelled.setAttribute('id', "divMessageReconcileCancelled_" + nbInput);
		divMessageReconcileCancelled.setAttribute('class', "col-lg-12 text-danger text-center");
		divMessageReconcileCancelled.style.display = "none";

		var messageReconcileCancelled = document.createElement('p');
		messageReconcileCancelled.innerHTML = "Reconciliation service cancelled";
		divMessageReconcileCancelled.appendChild(messageReconcileCancelled);

		divSubmitReconcile.appendChild(divMessageReconcileCancelled);
		divSubmitReconcile.appendChild(divMessageReconcileSaved);
	}
	else{

	}
}

function getTagsReconcileFromTable(nbInput){
	var tagsReconcile = [];

	for(var i = 0; i < 5 ; i++ ){
		var dropdownReconcile = document.getElementById("dropdownReconcile_" + nbInput + "_" + i);
		tagsReconcile.push(dropdownReconcile.value);
	}
	//console.log(tagsReconcile);
	return tagsReconcile;
}

function getColumChecked(nbInput){
	var dropdownReconcile = document.getElementById("dropdownReconcile_" + nbInput + "_0");
	var columnCheck = dropdownReconcile.value;

	return columnCheck;

}

function startReconciliation(nbInput, uuid){
	var selectUrl = document.getElementById("selectUrl_" + nbInput);
	var urlTaxo = selectUrl.options[selectUrl.selectedIndex].text;
	//var urlTaxo = document.getElementById("urlAPItaxo_" + nbInput).value;
	console.log("url : " + urlTaxo);
	if(urlTaxo != ""){
		//console.log("start reconcile");
		var separator = "";
		var texte =  document.getElementById("csvDropdown_" + nbInput).options[document.getElementById("csvDropdown_" + nbInput).selectedIndex].value; 
		if(texte == "comma"){
			separator = ',';
		}
		else if(texte == "semiComma"){
			separator = ';';
		}
		else{
			separator = '\t';    
		}
		var columnsInfo = this.foundColumnsInfos(nbInput);
		var columnCheck = this.foundColumnCheck(nbInput);
		columnsInfo.push(columnCheck);

		this.getContentFile(uuid, nbInput, columnsInfo, separator);
	}
	else{
		alert("Missing url");
	}
}

function doReconciliation(nbInput, contentFile, separator){

	var lines = contentFile.split("\n");
	//console.log(contentFile);
	var firstLine = lines[0];
	var headers = lines[0].split(separator);
	var rows = createDataJSON(headers, lines, separator);
	//console.log(headers);
	var cols = createTitleJSON(headers);
	var input = new InputObject(nbInput, rows, cols, contentFile, firstLine, []);
	//console.log("before : " );
	//console.log(input);
	deleteInput(nbInput, input);

	var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + nbInput);
	divReconciliationCheck.style.display = "none";

	var divButtonStartReconciliation = document.getElementById("divButtonStartReconciliation_" + nbInput);
	divButtonStartReconciliation.style.display = "none";

	var divButtonValidReconciliation = document.getElementById("divButtonValidReconciliation_" + nbInput);
	divButtonValidReconciliation.style.display = "block";

	var divTableReconcile = document.getElementById("divTableReconcile_" + nbInput);
	var tableReconcile = document.getElementById("tableReconcile_" + nbInput);
	var tableReconcileWrapper = document.getElementById("tableReconcile_" + nbInput + "_wrapper");


	if(tableReconcile == null && tableReconcileWrapper == null){
		//console.log("tableReconcile and wrapper are null");
		//var tableReconcile = document.createElement("div");
		var tableReconcile = document.createElement("table");
		tableReconcile.id = "tableReconcile_" + nbInput;
		tableReconcile.name = "tableReconcile_" + nbInput;
		tableReconcile.border = "0";
		tableReconcile.setAttribute("class", "table-mapping table-marges");

		var reconcile = $("#reconcileActive_" + nbInput);
		if(reconcile.length != 1){
			var reconcileNew = document.createElement("input");
			reconcileNew.id = "reconcileActive_" + nbInput;
			reconcileNew.name = "reconcileActive_" + nbInput;
			reconcileNew.value = "false";
			reconcileNew.type = "hidden";
			divTableReconcile.appendChild(reconcileNew);
		}


		divTableReconcile.appendChild(tableReconcile);
		divTableReconcile.style.display = "inline-block";
	}
	else if(tableReconcileWrapper != null){
		console.log("already table reconcile exist and must be deleted");
		//var reconcileOld = document.getElementById("reconcileActive_" + nbInput);
		divTableReconcile.removeChild(tableReconcileWrapper);
		//divTableReconcile.removeChild(reconcileOld);
		var tableReconcile = document.createElement("table");
		tableReconcile.id = "tableReconcile_" + nbInput;
		tableReconcile.name = "tableReconcile_" + nbInput;
		tableReconcile.border = "0";
		tableReconcile.setAttribute("class", "table-mapping table-marges compact");

		divTableReconcile.appendChild(tableReconcile);
		divTableReconcile.style.display = "inline-block";
	}
	var tagsReconcile = this.getTagsReconcileFromTable(nbInput);
	var columnCheck = this.getColumChecked(nbInput);
	var inputObj = this.getInput(nbInput);
	//console.log(inputList);
	//console.log("nbInput : " + nbInput);
	inputObj.tagsReconcile = tagsReconcile;
	//console.log(inputObj);
	var inputElement = this.getInput(nbInput);
	var rows = this.getRows(nbInput);
	var cols = this.getCols(nbInput);

	//console.log(cols);
	var table = $(tableReconcile).DataTable({
		"data": rows,
		"columns": cols,
		"bAutoWidth" : true,
		"sScrollX" : '50%',
		"bFilter" : false, 
		"bInfo" : false,
		"ordering": false,
		"info":     false,
		"dom": '<"top"i>rt<"bottom"flp><"clear">',
		"bLengthChange" : false,
		"columnDefs": [
		               {
		            	   targets: -1,
		            	   className: 'details-control',
		            	   orderable: false,
		            	   data: null,
		            	   defaultContent: '',
		            	   cursor: 'pointer'
		               }
		               ],
		               "fnInitComplete": function() {
		            	   this.fnAdjustColumnSizing();
		               }
	});


	/*this.adjustJSON(nbInput);
	$(window).bind('resize', function () {
		$("#tableReconcile_" + nbInput).fnAdjustColumnSizing();
	} );*/

	// Add event listener for opening and closing detailsfunctions.js
	$('#tableReconcile_' + nbInput + ' tbody').on('click', 'td.details-control', function () {
		var tr = $(this).closest('tr');
		var td = $(this).closest('td');
		var row = table.row(tr);
		rowIndex = row.index();

		var id = "tableInput_" + nbInput + "_row_" + rowIndex;
		var child = row.child();

		if(child){
			var tableDetailsControl = document.getElementById(id);
			// This row is already open - close it
			if($('#' + id).css('display') == 'none'){ 
				$('#' + id).css("display", "block"); 
				tr.addClass('shown');
			} else { 
				$('#' + id).css("display", "none");
				tr.removeClass('shown');
			}
		}
		else {

			// Open this row
			//console.log("create child");
			addTableForReconcile(nbInput, columnCheck, row);
			tr.addClass('shown');
		}


	} );
}

function createDataJSON(headers, lines, separator){
	var rows = [];
	for(var i = 0 ; i < lines.length ; i++){
		var line = lines[i];
		var row = [];

		if(i != 0 && line != ""){
			for(var j = 0 ; j < headers.length ; j++){
				var value = line.split(separator);
				row.push(value[j]);
			}

			rows.push(row);
		}

	}
	return rows;
}

function createTitleJSON(headers){
	var cols = [];
	for(var i = 0 ; i <= headers.length ; i++){
		var col = new Object();
		if(i < headers.length){
			var header = headers[i];	
			col['title'] = header;

		}
		else{
			col["title"] = "Show all or mask all";
		}
		cols.push(col);
	}

	return cols;
}

function adjustJSON(nbInput){
	var tableReconcile = $("#tableReconcile_" + nbInput).dataTable();
	var tagsReconcile = this.getReconcileTags(nbInput);
	var tagsAll = this.getTags(nbInput);
	for(var i = 0 ; i < tagsAll.length; i++){
		var tag = tagsAll[i];
		if(jQuery.inArray(tag, tagsReconcile) != -1){
			tableReconcile.fnSetColumnVis(i, true);
		}
		else{
			tableReconcile.fnSetColumnVis(i, false);
		}

	}		
}

function createTableReconciliationService(resultsReconcile, nbInput, row){
	var indexRow = row.index();
	var subTableReconcile = '<table id=tableInput_' + nbInput + '_row_' + indexRow + ' cellpadding="5" cellspacing="0" border="0" style="display:block; padding-left:50px;" >';
	var size = 0;
	for(i in resultsReconcile){
		size ++;
	}
	if(size > 0){
		subTableReconcile +=
			'<tr>'+
			//'<td style="font-weight : bold">check</td>'+
			'<td style="font-weight : bold">name</td>' +
			'<td style="font-weight : bold">score</td>' +
			'</tr>';
		for(i in resultsReconcile){
			var result = resultsReconcile[i];
			var name = result[0];
			var score = result[1];
			var nameRadio = "radio_" + nbInput + "_" + indexRow;
			var idRadio = "radio_" + nbInput + "_" + indexRow + "_" + i;
			subTableReconcile += 
				'<tr>'+
				'<td><input type="radio" name="' + 'group_' + nbInput + "_" + indexRow + '" value="' + name + '" id="' + idRadio + '"/>' +
				'<label for="' + idRadio + '">' + name +'</label></td>' +
				'<td>' + score +'</td>' +
				'</tr>';
		}

	}
	else{
		subTableReconcile += '<tr><td>No match found</td></tr>';
	}
	subTableReconcile += '</table>';
	return subTableReconcile;
}



function addTableForReconcile(nbInput, columnCheck, row){
	var rows = this.getRows(nbInput);
	var cols = this.getCols(nbInput);
	var idColumnCheck = "";
	var indexRow = row.index(); 
	for(i in cols){
		var col = cols[i];
		if(col['title'] == columnCheck){
			idColumnCheck = i;
		}

	}
	var selectUrl = document.getElementById("selectUrl_" + nbInput);
	var urlTaxo = selectUrl.options[selectUrl.selectedIndex].text;

	//var urlTaxo = $('#urlAPItaxo_' + nbInput).val();
	//http://data1.kew.org/reconciliation/reconcile/IpniName
	//http://refine.taxonomics.org/gbifchecklists/reconcile
	var scientificName = rows[indexRow][idColumnCheck];
	callRestService(urlTaxo, scientificName, nbInput, row);

}

function callRestService(urlAPI, scientificName, nbInput, row){
	//xhr.open('GET', 'http://data1.kew.org/reconciliation/reconcile/IpniName?query={"query":"Lamiaceae+Congea+chinensis+Moldenke"}');
	var url = urlAPI + '?query={"query":"' + scientificName.replace(" ", "+") + '"}';
	var htmlResult = "";
	$.ajax({
		url : url,
		type: 'GET',
		dataType: 'jsonp',
		data : {symbol : "AAPL"},
		success: function(data) {
			var result = {};
			for (var i = 0; i < data.result.length; i++) {
				result[i] = [data.result[i].name, data.result[i].score];
			}
			htmlResult = createTableReconciliationService(result, nbInput, row);
			row.child(htmlResult).show();
		},
		error: function(xhr, sts, err) {
			console.log('Erreur !!');
		}
	});

	return htmlResult;
}


function cancelReconciliation(nbInput){
	var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + nbInput);
	divReconciliationCheck.style.display ="none";

	var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + nbInput);
	divSubmitReconcile.style.display = "none";

	var divButtonStartReconciliation = document.getElementById("divButtonStartReconciliation_" + nbInput);
	divButtonStartReconciliation.style.display = "block";

	var divButtonValidReconciliation = document.getElementById("divButtonValidReconciliation_" + nbInput);
	divButtonValidReconciliation.style.display = "none";

	var divTableReconcile = document.getElementById("divTableReconcile_" + nbInput);
	divTableReconcile.style.display = "none";

	var tableReconcile = document.getElementById("tableReconcile_" + nbInput + "_wrapper");
	if(tableReconcile != null){
		divTableReconcile.removeChild(tableReconcile);
	}

	setReconciliation(false, nbInput);

}

function setReconciliation(reconcileActive, nbInput){

	var divTableReconcile = document.getElementById("divTableReconcile_" + nbInput);
	var divMessageReconcileCancelled  = document.getElementById("divMessageReconcileCancelled_" + nbInput);
	var divMessageReconcileSaved = document.getElementById("divMessageReconcileSaved_" + nbInput);
	var divButtonStartReconciliation = document.getElementById("divButtonStartReconciliation_" + nbInput);
	var divButtonCancelReconciliation = document.getElementById("divButtonCancelReconciliation_" + nbInput);
	var divButtonValidReconciliation = document.getElementById("divButtonValidReconciliation_" + nbInput);
	var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + nbInput);
	var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + nbInput);

	//var reconcileActive = document.getElementById("reconcileActive_" + nbInput);

	if(reconcileActive){
		divSubmitReconcile.style.display = "block";
		divTableReconcile.style.display = "none";
		divMessageReconcileCancelled.style.display = "none";
		divMessageReconcileSaved.style.display = "block";
		divButtonStartReconciliation.style.display = "none";
		divButtonCancelReconciliation.style.display = "none";
		divButtonValidReconciliation.style.display = "none";
		divReconciliationCheck.style.display = "none";

		var idReconcileActive = "#reconcileActive_" + nbInput;
		var reconcileActiveJquery = $(idReconcileActive),val = reconcileActiveJquery.val();
		reconcileActiveJquery.val(val === "true" ? "false" : "true");
		//console.log("reconcileActive value :" );
		//console.log(reconcileActiveJquery.val());

	}
	else{
		divSubmitReconcile.style.display = "block";
		divTableReconcile.style.display = "none";
		divMessageReconcileCancelled.style.display = "block";
		divMessageReconcileSaved.style.display = "none";
		divButtonStartReconciliation.style.display = "none";
		divButtonCancelReconciliation.style.display = "none";
		divButtonValidReconciliation.style.display = "none";
		divReconciliationCheck.style.display = "none";

		reconcileActive.value = "false";
	}
}

