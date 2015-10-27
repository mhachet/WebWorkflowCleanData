

var columns = "";

//display array for mapping 
function loadMappingDwc(contentFirstLine, nb_input){
	var tableMapping = document.getElementById("mappingTable_" + nb_input);
	var divMapping = document.getElementById("divMapping_" + nb_input);
	var mappingActive = document.getElementById("mappingActive_" + nb_input);

	var divSubmitMapping = document.getElementById("divSubmitMapping_" + nb_input);
	var divSubmitMappingOK = document.getElementById("divSubmitMappingOK_" + nb_input);
	var divSubmitMappingCancel = document.getElementById("divSubmitMappingCancel_" + nb_input);

	var divMessageSaved = document.getElementById("divMessageSaved_" + nb_input);
	var divMessagedCancelled = document.getElementById("divMessageCancelled_" + nb_input);
	if(tableMapping){
		divMapping.style.display = "block";
		//tableMapping.style.display="block";
		divSubmitMapping.style.display = "block";
		divSubmitMappingOK.style.display = "block";
		divSubmitMappingCancel.style.display ="block";
		divMessageSaved.style.display = "none";
		divMessagedCancelled.style.display = "none";
		mappingActive.value = "false";
	}
	else{
		
		prepareMapping(contentFirstLine, nb_input);
		
	}

}


function mappingDWC(counter, change_mapping_reconcile){
	var convertButton = document.getElementById('convert_'+ counter);
	var divAddLoad = document.getElementById("divAddLoad_" + counter);
	var buttonConvert = null;
	if(!convertButton){
		var divLoad = document.getElementById("divLoad_" + counter);

		buttonConvert = document.createElement('button');
		buttonConvert.type = "button";
		buttonConvert.id = "convert_" + counter;
		buttonConvert.name = "convert_" + counter;
		buttonConvert.setAttribute("onclick" , "loadInputFile(" + counter + ",\"loadMapping\")");
		buttonConvert.setAttribute('class', "btn btn-default waves-effect waves-light btn-large font-medium-button");

		divLoad.appendChild(buttonConvert);
		$("#convert_" + counter).text('Map to Dwc');
		divAddLoad.appendChild(divLoad);

	}
	else{
		buttonConvert = document.getElementById("convert_" + counter);
		var divMapping = document.getElementById("divMapping_" + counter);
		var tableMapping = document.getElementById("mappingTable_" + counter);
		var infosMapping = document.getElementById("infosMapping_" + counter);
		var divTableReconcile = document.getElementById("divTableReconcile_" + counter);
		var divSubmitMapping = document.getElementById("divSubmitMapping_" + counter);
		var divSubmitMappingOK = document.getElementById("divSubmitMappingOK_" + counter);
		var divSubmitMappingCancel = document.getElementById("divSubmitMappingCancel_" + counter);
		var mappingActive = document.getElementById("mappingActive_" + counter);
		var divMessageSaved = document.getElementById("divMessageSaved_" + counter);
		var divMessagedCancelled = document.getElementById("divMessageCancelled_" + counter);

		var divReconciliationCheck = document.getElementById("divReconciliationCheck_" + counter);
		var divSubmitReconcile = document.getElementById("divSubmitReconcile_" + counter);

		var divButtonStartReconciliation = document.getElementById("divButtonStartReconciliation_" + counter);
		var divButtonCancelReconciliation = document.getElementById("divButtonCancelReconciliation_" + counter);
		var divButtonValidReconciliation = document.getElementById("divButtonValidReconciliation_" + counter);


		var divTableReconcile = document.getElementById("divTableReconcile_" + counter);
		var tableReconcile = document.getElementById("tableReconcile_" + counter + "_wrapper");
		var tablePrepareReconcile = document.getElementById("tablePrepareReconcile_" + counter);
		var divUrlTaxo = document.getElementById("divUrlTaxo_" + counter);

		var divMessageReconcileCancelled  = document.getElementById("divMessageReconcileCancelled_" + counter);
		var divMessageReconcileSaved = document.getElementById("divMessageReconcileSaved_" + counter);

		if(change_mapping_reconcile == "loadMapping"){
			if(tableMapping){
				divMapping.style.display = "block";
				//tableMapping.style.display="block";
				divSubmitMapping.style.display = "block";
				divSubmitMappingOK.style.display = "block";
				divSubmitMappingCancel.style.display ="block";
				divMessageSaved.style.display = "none";
				divMessagedCancelled.style.display = "none";
				mappingActive.value = "false";
			}
		}
		if(change_mapping_reconcile == "change"){
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

			if(divTableReconcile){
				divMessageReconcileSaved.style.display = "none";
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

	}

	var fileInput = document.querySelector('#inp_' + counter);
	var result = "";
	var count = 0;
	buttonConvert.addEventListener('click', function() {


		//performAjaxSubmit(counter, isChange);
	}, false);

}





function loadInputRaster(counter, typeInput){
	var fileExist = document.getElementById(typeInput + '_' + counter);
	//console.log(typeInput + '_' + counter);
	var filename = fileExist.value;
	//console.log("filename : " + filename);
	var inputText = document.getElementById("text_" + typeInput + "_" + counter);
	inputText.value = filename;
}



function prepareMapping(contentFirstFile, nbInput){
	var separator = "";//this.findSeparator(contentFile);
	var divMapping = document.getElementById("divMapping_" + nbInput);
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

	var length = contentFirstFile.split(separator).length;

	var firstLine = contentFirstFile.split(separator);
	//console.log(contentFile.split('\n')[0].split(','));
	//firstLine[length] = " ";
	var dwcTags = [" ","abstract","acceptedNameUsage","acceptedNameUsageID","accessRights","accrualMethod","accrualPeriodicity","accrualPolicy","alternative","associatedMedia","associatedOccurrences","associatedOrganisms","associatedReferences","associatedSequences","associatedTaxa","audience","available","basisOfRecord","bed","behavior","bibliographicCitation","catalogNumber","class","classKey","collectionCode","collectionID","conformsTo","continent","contributor","coordinateAccuracy","coordinatePrecision","coordinateUncertaintyInMeters","country","countryCode","county","coverage","created","creator","dataGeneralizations","datasetID","datasetKey","datasetName","date","dateAccepted","dateCopyrighted","dateIdentified","dateSubmitted","day","decimalLatitude","decimalLongitude","depth","depthAccuracy","description","disposition","distanceAboveSurface","distanceAboveSurfaceAccuracy","dynamicProperties","earliestAgeOrLowestStage","earliestEonOrLowestEonothem","earliestEpochOrLowestSeries","earliestEraOrLowestErathem","earliestPeriodOrLowestSystem","educationLevel","elevation","elevationAccuracy","endDayOfYear","establishmentMeans","event","eventDate","eventID","eventRemarks","eventTime","extent","family","familyKey","fieldNotes","fieldNumber","footprintSpatialFit","footprintSRS","footprintWKT","format","formation","gbifID","genericName","genus","genusKey","geodeticDatum","geologicalContext","geologicalContextID","georeferencedBy","georeferencedDate","georeferenceProtocol","georeferenceRemarks","georeferenceSources","georeferenceVerificationStatus","group","habitat","hasCoordinate","hasFormat","hasGeospatialIssues","hasPart","hasVersion","higherClassification","higherGeography","higherGeographyID","highestBiostratigraphicZone","identification","identificationID","identificationQualifier","identificationReferences","identificationRemarks","identificationVerificationStatus","identifiedBy","identifier","idFile","individualCount","individualID","informationWithheld","infraspecificEpithet","institutionCode","institutionID","instructionalMethod","isFormatOf","island","islandGroup","isPartOf","isReferencedBy","isReplacedBy","isRequiredBy","issue","issued","isVersionOf","kingdom","kingdomKey","language","lastCrawled","lastInterpreted","lastParsed","latestAgeOrHighestStage","latestEonOrHighestEonothem","latestEpochOrHighestSeries","latestEraOrHighestErathem","latestPeriodOrHighestSystem","license","lifeStage","lithostratigraphicTerms","livingSpecimen","locality","locationAccordingTo","locationID","locationRemarks","lowestBiostratigraphicZone","machineObservation","materialSample","materialSampleID","maximumDepthinMeters","maximumDistanceAboveSurfaceInMeters","maximumElevationInMeters","measurementAccuracy","measurementDeterminedBy","measurementDeterminedDate","measurementID","measurementMethod","measurementOrFact","measurementRemarks","measurementType","measurementUnit","mediator","mediaType","medium","member","minimumDepthinMeters","minimumDistanceAboveSurfaceInMeters","minimumElevationInMeters","modified","month","municipality","nameAccordingTo","nameAccordingToID","namePublishedIn","namePublishedInID","namePublishedInYear","nomenclaturalCode","nomenclaturalStatus","occurrence","occurrenceDetails","occurrenceID","occurrenceRemarks","occurrenceStatus","order","orderKey","organism","organismID","organismName","organismRemarks","organismScope","originalNameUsage","originalNameUsageID","otherCatalogNumbers","ownerInstitutionCode","parentNameUsage","parentNameUsageID","phylum","phylumKey","pointRadiusSpatialFit","preparations","preservedSpecimen","previousIdentifications","protocol","provenance","publisher","publishingCountry","recordedBy","recordNumber","references","relatedResourceID","relationshipAccordingTo","relationshipEstablishedDate","relationshipRemarks","relation","replaces","reproductiveCondition","requires","resourceID","resourceRelationship","resourceRelationshipID","rights","rightsHolder","samplingEffort","samplingProtocol","scientificName","scientificNameAuthorship","scientificNameID","sex","source","spatial","species","speciesKey","specificEpithet","startDayOfYear","stateProvince","subgenus","subgenusKey","subject","tableOfContents","taxon","taxonConceptID","taxonID","taxonKey","taxonomicStatus","taxonRank","taxonRemarks","temporal","title","type","typeStatus","typifiedName","valid","verbatimCoordinates","verbatimCoordinateSystem","verbatimDate","verbatimDepth","verbatimElevation","verbatimEventDate","verbatimLatitude","verbatimLocality","verbatimLongitude","verbatimSRS","verbatimTaxonRank","vernacularName","waterBody","year"];
	var presentTags = [];
	for(var i = 0; i < firstLine.length; i++){
		if(jQuery.inArray(firstLine[i], dwcTags)!=-1){
			presentTags[presentTags.length] = firstLine[i];
		}

	}
	createMapping(firstLine, dwcTags, presentTags, nbInput);

	//divMapping.removeChild(progressbar);

	return firstLine;
}


function createMapping(firstLineInput, dwcTags, presentTags, nbInput){
	var mappingTable = document.getElementById("mappingTable_" + nbInput);
	var divSubmitMapping = document.getElementById("divSubmitMapping_" + nbInput);
	var divMapping = document.getElementById("divMapping_" + nbInput);
	divMapping.style.display = "block";
	if(mappingTable == null){

		var mappingTable = document.createElement("table");
		mappingTable.id = "mappingTable_" + nbInput;
		mappingTable.name = "mappingTable_" + nbInput;
		mappingTable.border = "0";

		var tbody = document.createElement("tbody");
		mappingTable.appendChild(tbody);
		for(var i = 0 ; i < firstLineInput.length; i++){
			var tagInput = firstLineInput[i];
			var row = mappingTable.insertRow(-1); // insert last line
			row.id = "row_" + i;
			row.name = "row_" + i;
			tbody.appendChild(row);
			var cellInput = row.insertCell(0);
			cellInput.innerHTML = tagInput;

			var dropdownDwC = document.createElement("select");
			dropdownDwC.style.display = "block";
			dropdownDwC.name = "dropdownDwC_" + i;
			var cellDwC = row.insertCell(1);

			//tbody.appendChild(cellDwC);
			for(var j = 0 ; j <  dwcTags.length ; j++){
				var dwcTag = dwcTags[j];
				var optionDwC = document.createElement('option');
				optionDwC.value = dwcTags[j];
				optionDwC.text = dwcTags[j];

				if(tagInput == dwcTag){
					optionDwC.selected = "selected";
				}
				dropdownDwC.appendChild(optionDwC);
			}



			cellDwC.appendChild(dropdownDwC);


		}

		var infosMapping = document.createElement('h6');
		infosMapping.setAttribute('id', "infosMapping_" + nbInput);
		infosMapping.innerHTML = "Note : If you don't choose map one of your column, this one will be deleted.";
		divMapping.appendChild(infosMapping);
		divMapping.appendChild(mappingTable);
		mappingTable.setAttribute('class', "centered table-marges");

		var mapping = document.createElement("input");
		mapping.id = "mappingActive_" + nbInput;
		mapping.name = "mappingActive_" + nbInput;
		mapping.value = "false";
		mapping.type = "hidden";
		divMapping.appendChild(mapping);

		var buttonOK = document.createElement('button');
		buttonOK.id = "okMapping_" + nbInput;
		buttonOK.name = "okMapping_" + nbInput;
		//buttonOK.value = "OK";
		buttonOK.type = "button";
		var activeMapping = new Boolean(true);
		buttonOK.setAttribute("onclick", "activeMapping(" + activeMapping +"," + nbInput +")");
		buttonOK.setAttribute('class', "btn btn-default waves-effect waves-light font-small-button");

		var buttonCancel = document.createElement('button');
		buttonCancel.id = "cancelMapping_" + nbInput;
		buttonCancel.name = "cancelMapping_" + nbInput;
		//buttonCancel.value = "Cancel conversion";
		buttonCancel.type = "button";
		buttonCancel.setAttribute('onclick', "deleteMapping("+ nbInput +")");
		buttonCancel.setAttribute('class', "btn btn-default waves-effect waves-light font-small-button");

		var divSubmitOK = document.createElement('div');
		divSubmitOK.setAttribute('id', "divSubmitMappingOK_" + nbInput);
		divSubmitOK.setAttribute('class', "col-lg-6");
		// divSubmitOK.setAttribute('role',"group");	
		divSubmitOK.setAttribute('value', 'false');
		divSubmitOK.appendChild(buttonOK);


		var divSubmitMappingCancel = document.createElement('div');
		divSubmitMappingCancel.setAttribute('id', "divSubmitMappingCancel_" + nbInput);
		divSubmitMappingCancel.setAttribute('class', "col-lg-6");
		//divSubmitMappingCancel.setAttribute('role',"group");	
		divSubmitMappingCancel.setAttribute('value', "false");
		divSubmitMappingCancel.appendChild(buttonCancel);


		var divMessageSaved = document.createElement('div');
		divMessageSaved.setAttribute('id', "divMessageSaved_" + nbInput);
		divMessageSaved.setAttribute('class', "col-lg-12 text-success text-center");
		divMessageSaved.style.display = "none";
		var messageSaved = document.createElement('p');
		messageSaved.innerHTML = "Mapping saved";
		divMessageSaved.appendChild(messageSaved);

		var divMessageCancelled = document.createElement('div');
		divMessageCancelled.setAttribute('id', "divMessageCancelled_" + nbInput);
		divMessageCancelled.setAttribute('class', "col-lg-12 text-danger text-center");
		divMessageCancelled.style.display = "none";
		var messageCancelled = document.createElement('p');
		//messageCancelled.setAttribute('class', "has-warning");
		messageCancelled.innerHTML = "Mapping cancelled";
		divMessageCancelled.appendChild(messageCancelled);

		divSubmitMapping.appendChild(divSubmitOK);
		$("#okMapping_" + nbInput).text('Valid mapping DarwinCore');
		divSubmitMapping.appendChild(divSubmitMappingCancel);
		$("#cancelMapping_" + nbInput).text('Cancel mapping DarwinCore');
		divSubmitMapping.appendChild(divMessageSaved);
		divSubmitMapping.appendChild(divMessageCancelled);

		divSubmitMapping.setAttribute('class', "col-lg-12 center");
		//divSubmitMapping.setAttribute('role', "group");
	}


}

function activeMapping(mappingIsActive, nbInput){
	var mappingActive = document.getElementById("mappingActive_" + nbInput);
	var divSubmitMappingOK = document.getElementById("divSubmitMappingOK_" + nbInput);
	var divSubmitMappingCancel = document.getElementById("divSubmitMappingCancel_" + nbInput);
	var divMessageSaved = document.getElementById("divMessageSaved_" + nbInput);
	var divMessageCancelled = document.getElementById("divMessageCancelled_" + nbInput);
	var divMapping = document.getElementById("divMapping_" + nbInput);

	if(mappingIsActive){
		mappingActive.value = "true";
		divSubmitMappingOK.value = "true";
		divSubmitMappingCancel.value = "true";
		divMessageSaved.style.display = "block";
		divMessageCancelled.style.display = "none";
		divMapping.style.display = "none";
		divSubmitMappingCancel.style.display = "none";
		divSubmitMappingOK.style.display = "none";
	}
	else{
		mappingActive.value = "false";
		divSubmitMappingOK.value = "false";
		divSubmitMappingCancel.value = "false";
		divMessageSaved.style.display = "none";
		divMessageCancelled.style.display = "block";
		divSubmitMappingCancel.style.display = "none";
		divSubmitMappingOK.style.display = "none";
	}
}

function deleteMapping(nbInput){

	var divMapping = document.getElementById("divMapping_" + nbInput);
	divMapping.style.display="none";

	var divSubmitMapping = document.getElementById("divSubmitMapping_" + nbInput);
	var divSubmitMappingOK = document.getElementById("divSubmitMappingOK_" + nbInput);
	var divSubmitMappingCancel = document.getElementById("divSubmitMappingCancel_" + nbInput);
	var divMessageCancelled = document.getElementById("divMessageCancelled_" + nbInput);
	//divSubmitMapping.style.display = "none";
	divSubmitMappingOK.style.display = "none";
	divSubmitMappingCancel.style.display = "none";
	divMessageCancelled.style.display = "block";
	activeMapping(false, nbInput);
}
