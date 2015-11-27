function initialiseFinalPage() {
	for (var i = 0; i <= 9 ; i++) {
		var step_involved = document.getElementById("step" + i + "_involved").value;
		console.log(i + " => " + step_involved);
		if(step_involved == "true"){
            this.initialiseStep(i, true);
		}
		else{
            
			this.initialiseStep(i, false);
		}
		
	}
	var nbOutputClean = document.getElementById("nbOutputClean").value;
	if(nbOutputClean == 0){
		document.getElementById("downloadFinalFiles").style.display = "none";
	}
}

function checkErrorOk(step_ok, p_ok, nbStep){
	//console.log(nbStep + " : " + step_ok);
	if(step_ok == "true"){
		//console.log("true : " + "p_ok" + nbStep);
		p_ok.setAttribute("style", "color:#089A4C");
		p_ok.innerHTML = "Everything went well";
	}
	else{
		//console.log("false : " + step_ok);
		p_ok.setAttribute("style", "color:#FF0000");
		p_ok.innerHTML = "Error during process";
	}
}

function initialiseStep(nbStep, involved){
	var divStep = document.getElementById("divStep" + nbStep);
	var nbInput = document.getElementById("nbTotalInput").value;
	
    divStep.setAttribute('style', "margin-left: 40px; visibility: visible");
    
    if(nbStep == 0 || nbStep == 1 || nbStep == 2){

    	if(involved){

    		for(var i = 0; i < nbInput; i++){
        		var step_ok_id = document.getElementById("step" + nbStep + "_ok_inp" + i);
				console.log(involved + " - " + nbStep+ " - " + step_ok+ " - " + p_ok_step_inp);
        		if(step_ok_id){
        			var step_ok = step_ok_id.innerHTML;
        			var p_ok_step_inp = document.getElementById("p_ok_step" + nbStep + "_inp" + i);

            		formatResult(involved, nbStep, step_ok, p_ok_step_inp);
        		}
        		
        	}
    	}
    	else{
    		
    		formatResult(involved, nbStep, "", "");
    	}
    	
        
    }
    else{
    	var step_ok = document.getElementById("step" + nbStep + "_ok").value;
    	var p_ok_step_inp = document.getElementById("p_ok_step" + nbStep + "_inp");
    	
    	formatResult(involved, nbStep, step_ok, p_ok_step_inp);
    }
}

function formatResult(involved, nbStep, step_ok, p_ok_step_inp){
	var headerStep_involved = $("#headerStep" + nbStep + "_involved");
    var headerStep_notInvolved = $("#headerStep" + nbStep + "_NotInvolved");

	if(involved){
		var divStepInvolved = document.getElementById("headerStep" + nbStep + "_involved");
        headerStep_involved.show();
        headerStep_notInvolved.hide();
        this.checkErrorOk(step_ok, p_ok_step_inp, nbStep);
		if(nbStep == 3 || nbStep == 4 || nbStep == 7 || nbStep == 9) {
			console.log(nbStep + " formatDownloadLink");
			this.formatDownloadLink(nbStep);
		}
		else if(nbStep == 8){
			var step8_ok = document.getElementById("step8_ok").value;
			if(!step8_ok){
				document.getElementById("cardDownloadLink_step8").style.display = "none";
			}
		}
		//divStepInvolved.setAttribute('style', "margin-left: 40px; visibility: visible");
	}
	else{
        
        headerStep_involved.hide();
        headerStep_notInvolved.show();
        
	    var notInvolved = document.getElementById("headerStep" + nbStep + "_NotInvolved");
	    var divStep = document.getElementById("divStep" + nbStep);
        divStep.style.display = "none";
        //console.log(notInvolved);
	    //notInvolved.setAttribute('style', "margin-left: 40px; visibility: visible");
	    
	}
	
}

function formatDownloadLink(nbStep){
	var nbOccurrencesStep = document.getElementById("nbOccurrencesStep" + nbStep).innerHTML;
	var cardDownloadLink_step = document.getElementById("cardDownloadLink_step" + nbStep);
	if(nbOccurrencesStep == 0){
		cardDownloadLink_step.style.display = 'none';
	}

}

/*
function initialiseStep1(){
	var divStep1 = document.getElementById("divStep1");
	var step1_ok = document.getElementById("step1_ok").value;
	var p_ok = document.getElementById("p_ok1");
	
	this.checkErrorOk(step1_ok, p_ok);
	
	divStep1.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep2(){
	var divStep2 = document.getElementById("divStep2");
	var step2_ok = document.getElementById("step2_ok").value;
	var p_ok = document.getElementById("p_ok2");
	
	this.checkErrorOk(step2_ok, p_ok);
	
	divStep2.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep3(){
	var divStep3 = document.getElementById("divStep3");
	var step3_ok = document.getElementById("step3_ok").value;
	var p_ok = document.getElementById("p_ok2");
	
	this.checkErrorOk(step3_ok, p_ok);
	
	divStep3.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep4(){
	var divStep4 = document.getElementById("divStep4");
	var step4_ok = document.getElementById("step4_ok").value;
	var p_ok = document.getElementById("p_ok3");
	
	this.checkErrorOk(step4_ok, p_ok);
	
	divStep4.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep5(){
	var divStep5 = document.getElementById("divStep5");
	var step5_ok = document.getElementById("step5_ok").value;
	var p_ok = document.getElementById("p_ok4");
	
	this.checkErrorOk(step5_ok, p_ok);
	
	divStep5.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep6(){
	var divStep6 = document.getElementById("divStep6");
	var step6_ok = document.getElementById("step6_ok").value;
	var p_ok = document.getElementById("p_ok5");
	
	this.checkErrorOk(step6_ok, p_ok);
	
	divStep6.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep7(){
	var divStep7 = document.getElementById("divStep7");
	var step7_ok = document.getElementById("step7_ok").value;
	var p_ok = document.getElementById("p_ok6");
	
	this.checkErrorOk(step7_ok, p_ok);
	
	divStep7.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep8(){
	var divStep8 = document.getElementById("divStep8");
	var step8_ok = document.getElementById("step8_ok").value;
	var p_ok = document.getElementById("p_ok7");
	
	this.checkErrorOk(step8_ok, p_ok);
	
	divStep8.setAttribute('style', "margin-left: 40px; visibility: visible");
}

function initialiseStep9(){
	var divStep9 = document.getElementById("divStep9");
	var step9_ok = document.getElementById("step9_ok").value;
	var p_ok = document.getElementById("p_ok8");
	console.log(step9_ok);
	this.checkErrorOk(step9_ok, p_ok);
	
	divStep9.setAttribute('style', "margin-left: 40px; visibility: visible");
}
*/