<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="fr.bird.bloom.servlets.MainController"%>
<%@page import="fr.bird.bloom.beans.*"%>


<!DOCTYPE html PUBLIC "-//W4C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet"	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <!--<link href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/css/materialize.min.css" rel="stylesheet" >-->
    <link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"	rel="stylesheet" type="text/css">
    <link href='http://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800'	rel='stylesheet' type='text/css'>
    <link href="css/clean-blog.css" rel="stylesheet">
    <link href="http://cdn.datatables.net/1.10.7/css/jquery.dataTables.css" rel="stylesheet" type="text/css" >
    <link href="css/materialize.css" rel="stylesheet">
    <link href="css/clean-blog.css" rel="stylesheet">
    <link href="css/bootstrap-select.css" rel="stylesheet">

    <title>Biodiversity Linked Organisms Occurrences Megadatasets</title>
</head>
<body onload="javascript:initialiseFinalPage()">
<nav>
    <div class="nav-wrapper #4db6ac teal lighten-2">
        <ul id="nav" class="right hide-on-med-and-down">
            <li><a href="HomePage.html">Home</a></li>
            <li><a href="LaunchPage.html">Start</a></li>
            <li><a href="AboutPage.html">About</a></li>
            <li><a href="DocumentationPage.html">Documentation</a></li>
            <li><a href="WebServiceDownload.html">Download</a></li>
            <li><a href="Contact.html">Contact</a></li>
        </ul>
    </div>
</nav>
<header>
    <div class="container section no-pad-bot">
        <div class="row">
            <div class="col-lg-12 col-md-10">
                <div class="post-heading">
                    <h1>BLOOM</h1>
                    <h2 class="subheading">Workflow for open data curation</h2>
                </div>
            </div>
        </div>
    </div>
</header>
	<div class="container">
		<div id="divBody" class="row">
			<div class="col-lg-12">
                <div class="post-preview" id="bloc-intro">
					<h2 class="post-title">Summary workflow</h2>
					<h3 class="post-subtitle">All results can be downloaded</h3>
				</div>
                <div id="hiddenResults">
                	<input type="hidden" id="nbTotalInput" value="${step1.nbInputs}" />
                    <c:set var="count0" value="0" scope="page"/>
                    <c:forEach var="info" items="${step1.infos_mapping}">
                        <c:if test="${info.value.mappingInvolved == false}">
                            <c:set var="count0" value="${count0 + 1}" scope="page"/>
                        </c:if>
                    </c:forEach>
                    <c:if test="${count0 > 0}">
                        <input type="hidden" id="step0_involved" value="true"/>
                    </c:if>
                    <c:if test="${count0 == 0}">
                        <input type="hidden" id="step0_involved" value="false"/>
                    </c:if>
                    <input type="hidden" id="step1_involved" value="${step1.involved}" />
                    
                    <input type="hidden" id="step2_involved" value="${step2.involved}" />
                    
                    <input type="hidden" id="step3_involved" value="${step3.involved}" />
                    <input type="hidden" id="step3_ok" value="${step3.step3_ok}" />
                    <input type="hidden" id="step3_nbFound" value="${step3.nbFound}" />
                    <input type="hidden" id="step3_path" value="${step3.pathWrongCoordinates}" />

                    <input type="hidden" id="step4_involved" value="${step4.involved}" />
                    <input type="hidden" id="step4_ok" value="${step4.step4_ok}" /> <input
                        type="hidden" id="step4_nbFound" value="${step4.nbFound}" /> <input
                        type="hidden" id="step4_path" value="${step4.pathWrongGeoIssue}" />

                    <input type="hidden" id="step5_involved" value="${step5.involved}" />
                    <input type="hidden" id="step5_ok" value="${step5.step5_ok}" /> 

                    <input
                        type="hidden" id="step5_involved" value="${step5.involved}" />
                    <input
                        type="hidden" id="step5_ok" value="${step5.step5_ok}" />
                    <input
                        type="hidden" id="step5_nbFound" value="${step5.nbFound}" />
                    <input
                        type="hidden" id="step6_involved" value="${step6.involved}" />
                    <input
                        type="hidden" id="step6_ok" value="${step6.step6_ok}" />
                    <input
                        type="hidden" id="step7_involved" value="${step7.involved}" />
                    <input
                        type="hidden" id="step7_ok" value="${step7.step7_ok}" />
                    <input
                        type="hidden" id="step7_nbFound_polygon" value="${step7.nbFoundPolygon}" />
                    <input
                        type="hidden" id="step7_path_polygon" value="${step7.pathWrongPolygon}" />
                    <input
                        type="hidden" id="step7_nbFound_polygon" value="${step7.nbFoundIso2}" />
                    <input
                        type="hidden" id="step7_path_iso2" value="${step7.pathWrongIso2}" />
                   	<input
                        type="hidden" id="step8_involved" value="${step8.involved}" />
                    <input
                        type="hidden" id="step8_ok" value="${step8.step8_ok}" />
                    <input
                        type="hidden" id="step8_nbFound" value="${step8.nbFound}" />
                    <input
                        type="hidden" id="step8_path" value="${step8.pathWrongRaster}" />
                    <input
                        type="hidden" id="step9_involved" value="${step9.involved}" />
                    <input
                        type="hidden" id="step9_ok" value="${step9.step9_ok}" />
                    <input
                        type="hidden" id="step9_nbFound" value="${step9.nbFound}" />
                    <input
                        type="hidden" id="nbOutputClean" value="${fn:length(finalisation.listPathsOutputFiles)}">
                    <input
                        type="hidden" id="workflowSuccess" value="${finalisation.successWorkflow}">
                </div>

                <hr class="col-lg-11 hr-result">
                <div id="headerStep0_involved" class="post-preview col-lg-11">
                    <h4 class="post-meta">Checking input files format </h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" data-height="299" data-response-handler="responseHandler" id=divStep0>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner ">Filename</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner ">Checking format</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="count0" value="0" scope="page" />
                            <c:forEach var="info" items="${step1.infos_mapping}">
                                <tr class="tr-result" data-index=<c:out value="${count0}"/>>
                                   <c:if test="${info.value.mappingInvolved == false}">
                                        <td colspan="6" class="td-result">
                                            <div class="card-view marges-span">
                                                <span class="title">Filename</span>
                                                <span class="value"><c:out value='${info.value.filename}'/></span>
                                            </div>
                                            <div id="p_ok_step0_inp<c:out value='${count0}'/>" class="card-view marges-span">
                                                <span id="span_0_success_inp<c:out value='${count0}'/>" class="title">Success</span>
                                                <span id="step0_ok_inp<c:out value='${count0}'/>" class="value"><c:out value="${info.value.successMapping}"/></span>
                                            </div>
                                        </td>
                                    </c:if>
                                </tr>
                                <c:set var="count0" value="${count0 + 1}" scope="page"/>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">
                <!-- <a href="output/test_ipt_ok.csv"/>Test download</a>
				<a href="test_ipt_ok.csv"/>Test download 2</a> -->
                <div id="headerStep1_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 1 : Mapping to DarwinCore format</h4>
                </div>
                <div id="headerStep1_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 1 : Mapping to DarwinCore format isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" data-height="299" data-response-handler="responseHandler" id=divStep1>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner ">Filename</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner ">Success</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner ">Download Link</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="count1" value="0" scope="page" />
                            <c:forEach var="info" items="${step1.infos_mapping}">
                                <tr class="tr-result" data-index=<c:out value="${count1}" />>
                                    <c:if test="${info.value.mappingInvolved == true}">
                                        <td colspan="6" class="td-result">
                                            <div class="card-view marges-span">
                                                <span class="title">Filename</span>
                                                <span class="value"><c:out value='${info.value.filename}'/></span>
                                            </div>
                                            <div id="p_ok_step1_inp<c:out value='${count1}'/>" class="card-view marges-span">
                                                <span  id="span_1_success_inp<c:out value='${count1}'/>" class="title">Success</span>
                                                <span id="step1_ok_inp<c:out value='${count1}'/>" class="value"><c:out value="${info.value.successMapping}"/></span>
                                            </div>
                                            <div class="card-view marges-span">
                                                <span class="title">Download link</span>
                                                <span class="value"><a href=<c:out value='${info.value.filepath}'/>>Download</a></span>
                                            </div>
                                        </td>
                                    </c:if>
                                </tr>
                                <c:set var="count1" value="${count1 + 1}" scope="page"/>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">

                <div id="headerStep2_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 2 : Reconciliation Service</h4>
                </div>
                <div id="headerStep2_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 2 : Reconciliation Service isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStep2>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner">Filename</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Success</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Download link</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="count2" value="0" scope="page" />
                            <c:forEach items="${step2.infos_reconcile}" var="info">
                                <tr class="tr-result" data-index=<c:out value="${count2}" />>
                                    <c:if test="${info.value.successReconcile == true}">
                                        <td colspan="6" class="td-result">
                                            <div class="card-view marges-span">
                                                <span class="title">Filename</span>
                                                <span class="value"><c:out value='${info.value.filename}'/></span>
                                            </div>
                                            <div id="p_ok_step2_inp<c:out value='${count2}'/>" class="card-view marges-span">
                                                <span class="title">Success</span>
                                                <span id="step2_ok_inp<c:out value='${count2}'/>" class="value"> <c:out value="${info.value.successReconcile}"/></span>
                                            </div>
                                            <div class="card-view marges-span">
                                                <span class="title">Download link</span>
                                                <span class="value">
                                                    <a href=<c:out value='${info.value.filepath}'/>>Download</a>
                                                </span>
                                            </div>
                                        </td>
                                    </c:if>
                                </tr>
                                <c:set var="count2" value="${count2 + 1}" scope="page"/>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">
                <div id="headerStep3_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 3 : Check coordinates</h4>
                </div>
                <div id="headerStep3_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 3 : Check coordinates isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStep3>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner">Success</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Occurrences number deleted</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Download link</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="tr-result" data-index="0">
                                <td colspan="6" class="td-result">
                                    <div id="p_ok_step3_inp" class="card-view marges-span">
                                        <span class="title">Success</span>
                                        <span id="spanSuccessStep3" class="value">${step3.step3_ok}</span>
                                    </div>
                                    <div class="card-view marges-span">
                                        <span class="title">Occurrences number deleted</span>
                                        <span id="nbOccurrencesStep3" class="value"><c:out value='${step3.nbFound}'/></span>
                                    </div>
                                    <div id="cardDownloadLink_step3" class="card-view marges-span">
                                        <span class="title">Download link</span>
                                        <span class="value"><a href=<c:out value='${step3.pathWrongCoordinates}'/>>Wrong coordinates file</a></span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">

                <div id="headerStep4_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 4 : Check geospatial issue</h4>
                </div>
                <div id="headerStep4_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 4 : Check geospatial issue isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStep4>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner">Success</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Occurrences number deleted</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Download link</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="tr-result" data-index="0">
                                <td colspan="6" class="td-result">
                                    <div id="p_ok_step4_inp" class="card-view marges-span">
                                        <span class="title">Success</span>
                                        <span id="spanSuccessStep4" class="value">${step4.step4_ok}</span>
                                    </div>
                                    <div class="card-view marges-span">
                                        <span class="title">Occurrences number deleted</span>
                                        <span id="nbOccurrencesStep4" class="value"><c:out value='${step4.nbFound}'/></span>
                                    </div>
                                    <div id="cardDownloadLink_step4" class="card-view marges-span">
                                        <span class="title">Download link</span>
                                        <span class="value"><a href=<c:out value='${step4.pathWrongGeoIssue}'/>>Wrong geospatial issue file</a></span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">

                <div id="headerStep5_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 5 : Check synonym</h4>
                </div>
                <div id="headerStep5_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 5 : Check synonym isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id="divStep5">
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner">Success</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="tr-result" data-index="0">
                                <td colspan="6" class="td-result">
                                    <div id="p_ok_step5_inp" class="card-view marges-span">
                                        <span class="title">Success</span>
                                        <span id="spanSuccessStep5" class="value">${step5.step5_ok}</span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">

                <div id="headerStep6_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 6 : Check TDWG code</h4>
                </div>
                <div id="headerStep6_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 6 : Check TDWG code isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStep6>
                        <thead style="display: none;">
                        <tr>
                            <th style="">
                                <div class="th-inner">Success</div>
                                <div class="fht-cell"></div>
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                            <tr class="tr-result" data-index="0">
                                <td colspan="6" class="td-result">
                                    <div id="p_ok_step6_inp" class="card-view marges-span">
                                        <span class="title">Success</span>
                                        <span id="spanSuccessStep6" class="value"><c:out value='${step6.step6_ok}'/></span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">

                <div id="headerStep7_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 7 : Iso2 code and polygon correspondence were checked</h4>
                </div>
                <div id="headerStep7_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 7 : Iso2 code and polygon correspondence isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStep7>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner">Success</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Occurrences number deleted</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Download link</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="tr-result" data-index="0">
                                <td colspan="6" class="td-result">
                                    <div id="p_ok_step7_inp" class="card-view marges-span">
                                        <span class="title">Success</span>
                                        <span id="spanSuccessStep7" class="value"><c:out value='${step7.step7_ok}'/></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="tr-result" data-index="1">
                                <td colspan="6" class="td-result">
                                    <div class="card-view marges-span">
                                        <span class="title">Occurrences number deleted<br>that don't have iso2 code</span>
                                        <span id="nbOccurrencesIso2Step7" class="value"><c:out value='${step7.nbFoundIso2}'/></span>
                                    </div>
                                    <div id="cardDownloadLinkIso2_step7" class="card-view marges-span">
                                        <span class="title">Download link</span>
                                        <span class="value"><a href=<c:out value='${step7.pathWrongIso2}'/>>Wrong ISO2 code file</a></span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="tr-result" data-index="2">
                                <td colspan="6" class="td-result">
                                    <div class="card-view marges-span">
                                        <span class="title">Occurrences number deleted without<br>correspondence between the Iso2 code<br>and its polygon</span>
                                        <span id="nbOccurrencesPolygonStep7" class="value"><c:out value='${step7.nbFoundPolygon}'/></span>
                                    </div>
                                    <div id="cardDownloadLinkPolygon_step7" class="card-view marges-span">
                                        <span class="title">Download link</span>
                                        <span class="value"><a href=<c:out value='${step7.pathWrongPolygon}'/>>Wrong polygon file</a></span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">
                    
                <div id="headerStep8_involved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 8 : Check coordinates in raster cells</h4>
                </div>
                <div id="headerStep8_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 8 : Check coordinates in raster cells isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                   <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStep8>
                       <thead style="display: none;">
                           <tr>
                               <th style="">
                                   <div class="th-inner">Success</div>
                                   <div class="fht-cell"></div>
                               </th>
                               <th style="">
                                   <div class="th-inner">Number of wrong occurrences</div>
                                   <div class="fht-cell"></div>
                               </th>
                               <th style="">
                                   <div class="th-inner">Download link</div>
                                   <div class="fht-cell"></div>
                               </th>
                           </tr>
                       </thead>
                       <tbody>
                            <tr class="tr-result" data-index="0">
                                <td colspan="6" class="td-result">
                                    <div id="p_ok_step8_inp" class="card-view marges-span">
                                        <span class="title">Success</span>
                                        <span id="spanSuccessStep8" class="value"><c:out value='${step8.step8_ok}'/></span>
                                    </div>
                                    <div id="cardDownloadLink_step8" class="card-view marges-span">
                                        <span class="title">Download link</span>
                                        <span class="value"><a href=<c:out value='${step8.pathMatrixResultRaster}'/>>Matrix results file</a></span>
                                    </div>
                                </td>
                            </tr>
                       </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">

                <div class="post-preview col-lg-8">
                    <h4 id="headerStep9_involved" class="post-meta">Step 9 : EstablishmentMeans check</h4>
                </div>
                <div id="headerStep9_NotInvolved" class="post-preview col-lg-8">
                    <h4 class="post-meta">Step 9 : EstablishmentMeans check isn't involved in process</h4>
                </div>
                <div class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStep9>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner">Success</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Occurrences number deleted</div>
                                    <div class="fht-cell"></div>
                                </th>
                                <th style="">
                                    <div class="th-inner">Download link</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="tr-result" data-index="0">
                                <td colspan="6" class="td-result">
                                    <div id="p_ok_step9_inp" class="card-view marges-span">
                                        <span class="title">Success</span>
                                        <span id="spanSuccessStep9" class="value">${step9.step9_ok}</span>
                                    </div>
                                    <div class="card-view marges-span">
                                        <span class="title">Occurrences number deleted</span>
                                        <span id="nbOccurrencesStep9" class="value"><c:out value='${step9.nbFound}'/></span>
                                    </div>
                                    <div id="cardDownloadLink_step9" class="card-view marges-span">
                                        <span class="title">Download link</span>
                                        <span class="value"><a href=<c:out value='${step9.pathWrongEstablishmentMeans}'/>>Wrong establishmentMeans file</a></span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <hr class="col-lg-11 hr-result">
                <div class="post-preview col-lg-8">
                    <h4 class="post-meta">Download clean data</h4>
                </div>
                <div id="downloadFinalFiles" class="col-lg-11 fixed-table-body">
                    <table data-toggle="table" data-card-view="true" class="table table-hover" style="margin-top: 0px;" id=divStepFinal>
                        <thead style="display: none;">
                            <tr>
                                <th style="">
                                    <div class="th-inner">Download link</div>
                                    <div class="fht-cell"></div>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="countFinal" value="0" scope="page" />
                            <c:forEach items="${finalisation.listPathsOutputFiles}" var="path">
                                <c:set var="name" value="${fn:split(path, '/')}" />
                                <c:set var="length" value="${fn:length(name)}"/>
                                <tr class="tr-result" data-index=<c:out value="${countFinal}" />>
                                    <td colspan="6" class="td-result">
                                        <div class="card-view marges-span">
                                            <span class="title">Download link</span>
                                            <span class="value">
                                                <a href=<c:out value='${path}'/>><c:out value='${name[length-1]}'/></a>
                                            </span>
                                        </div>
                                    </td>
                                </tr>
                                <c:set var="countFinal" value="${countFinal + 1}" scope="page"/>
                            </c:forEach>
                            <tr id="noCleanData" class="tr-result" data-index=<c:out value="${countFinal}"/>>
                                <td colspan="6" class="td-result">
                                    <div class="card-view marges-span">
                                        <span class="title">No clean data to download</span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
<!--<%--
                        <tbody>
                        <tr class="tr-result" data-index="0">
                            <td colspan="6" class="td-result">
                                <div id="p_ok_step_final" class="card-view marges-span">
                                    <span class="title">Success</span>
                                    <span id="spanSuccessStepFinal" class="value">${finalisation.successWorkflow}</span>
                                </div>
                                <div class="post-preview col-lg-8">
                                    <h4 class="post-meta">Download clean data</h4>
                                </div>

                                <c:forEach items="${finalisation.listPathsOutputFiles}" var="path">
                                    <c:set var="name" value="${fn:split(path, '/')}" />
                                    <c:set var="length" value="${fn:length(name)}"/>
                                    <a href=<c:out value='${path}'/>><c:out value='${name[length-1]}'/></a>
                                    <br>
                                </c:forEach>
-->--%>
    <!--	<hr></hr>
     Footer -->
    <footer>
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                    <ul class="list-inline text-center">
                        <li>
                            <a href="http://www.cnrs.fr" target="_blank">
                                <img src="images/logo_cnrs.jpg" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="https://www.mnhn.fr/fr" target="_blank">
                                <img src="images/logo_mnhn.jpg" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="http://isyeb.mnhn.fr/" target="_blank">
                               <img src="images/logo_ISYEB.png" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="http://www.upmc.fr/" target="_blank">
                                <img src="images/logo_upmc.png" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                        <li>
                            <a href="http://www.ephe.fr/" target="_blank">
                                <img src="images/logo_ephe.png" height="50" width="50" class="img-circle" style="cursor:pointer;">
                            </a>
                        </li>
                    </ul>
                    <p class="copyright text-muted">Copyright &copy; BLOOM 2015</p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
	<script src="bootstrap/js/bootstrap.min.js"></script>
	<script src="js/jquery.watable.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript"></script>
    <script type="text/javascript" src="js/functionsFinal.js"></script>
</body>
</html>