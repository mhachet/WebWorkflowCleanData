package fr.bird.bloom.model;

import fr.bird.bloom.beans.Finalisation;
import fr.bird.bloom.beans.InputParameters;
import fr.bird.bloom.stepresults.Step1_MappingDwc;
import fr.bird.bloom.stepresults.Step2_ReconciliationService;
import fr.bird.bloom.stepresults.Step3_CheckCoordinates;
import fr.bird.bloom.stepresults.Step4_CheckGeoIssue;
import fr.bird.bloom.stepresults.Step5_IncludeSynonym;
import fr.bird.bloom.stepresults.Step6_CheckTDWG;
import fr.bird.bloom.stepresults.Step7_CheckISo2Coordinates;
import fr.bird.bloom.stepresults.Step8_CheckCoordinatesRaster;
import fr.bird.bloom.stepresults.Step9_EstablishmentMeans;
import fr.bird.bloom.utils.BloomConfig;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.*;
public class SendMail {

	private InputParameters inputParameters;
	private Step1_MappingDwc step1;
	private Step2_ReconciliationService step2;
	private Step3_CheckCoordinates step3;
	private Step4_CheckGeoIssue step4;
	private Step5_IncludeSynonym step5;
	private Step6_CheckTDWG step6;
	private Step7_CheckISo2Coordinates step7;
	private Step8_CheckCoordinatesRaster step8;
	private Step9_EstablishmentMeans step9;
	private Finalisation finalisation;
	//private boolean errorMessage;
	private String typeEmail; //errorMessage, finalMessage, step1_mapping, step2_reconcile, step3_coordinates, step4_geoSpatialIssues, step5_synonym, step6_tdwgCode, step7_iso2, step8_raster, step9_establishment

	private String getResourcePath() {
		return BloomConfig.getResourcePath();
	}

	public SendMail(String typeMail){
		this.typeEmail = typeMail;
	}

	public void sendMessage(String emailUser){

		String smtpHost = "";
		String from = "";
		//String to = "melanie.hachet@gmail.com";
		String username = "";
		String password = "";
		String smtpPort = "";
		try{
			System.err.println(getResourcePath() + ".properties_mail");
			BufferedReader buff = new BufferedReader(new FileReader(getResourcePath() + ".properties_mail"));
			try {
				String line;
				int count = 0;
				while ((line = buff.readLine()) != null) {
					switch (count) {

						case 0: smtpHost = line.split("\t")[1];
							break;
						case 1: from = line.split("\t")[1];
							break;
						case 2: username = line.split("\t")[1];
							break;
						case 3: password = line.split("\t")[1];
							break;
						case 4 : smtpPort = line.split("\t")[1];
							break;
					}
					count ++;
				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) {
			System.out.println("Erreur --" + ioe.toString());
		}

		System.out.println("smtpHost : " + smtpHost + "\nport : " + smtpPort + "\nfrom : " + from + "\nusername : " + username + "\npassword : " + password);

		final Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("username", username);
		props.put("password", password);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(props.getProperty("password"), props.getProperty("username"));
					}
				});



		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailUser));
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		String source = "bloom.snv.jussieu.fr/bloom/";

		if(typeEmail.equals("errorMessage")){
			try {
				message.setSubject("BLOOM - error");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageError(message, source);
		}
		else if (typeEmail.equals("step1_mapping")){
			try {
				message.setSubject("BLOOM - mapping DarwinCore done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep1(message, source);
		}
		else if (typeEmail.equals("step2_reconcile")){
			try {
				message.setSubject("BLOOM - reconciliation done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep2(message, source);
		}
		else if (typeEmail.equals("step3_coordinates")){
			try {
				message.setSubject("BLOOM - checking coordinates done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep3(message, source);
		}
		else if (typeEmail.equals("step4_geoSpatialIssues")){
			try {
				message.setSubject("BLOOM - 'geospatial issues' done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep4(message, source);
		}
		else if (typeEmail.equals("step5_synonym")){
			try {
				message.setSubject("BLOOM - synonym option done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep5(message, source);
		}
		else if (typeEmail.equals("step6_tdwgCode")){
			try {
				message.setSubject("BLOOM - TDWG4 checking done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep6(message, source);
		}
		else if (typeEmail.equals("step7_iso2")){
			try {
				message.setSubject("BLOOM - iso 2 code checking done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep7(message, source);
		}
		else if (typeEmail.equals("step8_raster")){
			try {
				message.setSubject("BLOOM - Bioclimatic and habitat validation option done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep8(message, source);
		}
		else if (typeEmail.equals("step9_establishment")){
			try {
				message.setSubject("BLOOM - Status of the occurrences option done");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageStep9(message, source);
		}
		else if(typeEmail.equals("finalMessage")){
			try {
				message.setSubject("BLOOM results");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			message = setTextMimeMessageFinal(message, source);
		}

		Transport tr = null;
		try {
			tr = session.getTransport("smtp");
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		try {
			tr.connect(smtpHost, username, password);
			message.saveChanges();
			tr.sendMessage(message,message.getAllRecipients());
			tr.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public MimeMessage setTextMimeMessageError(MimeMessage message, String source){

		String uuid = this.getInputParameters().getUuid();
		StringBuilder content = new StringBuilder("An error occurred during workflow process");
		content.append( "<br></br>");
		content.append( "You're id session is : " + uuid);
		content.append( "<br></br>");
		content.append("Please, contact us with this id if you want more information, 3 days before");
		content.append( "<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public MimeMessage setTextMimeMessageStep1(MimeMessage message, String source){

		//String source = "http:localhost:8080/bloom/";
		StringBuilder content = new StringBuilder("To download results from mapping to DarwinCore<br>");
		content.append("These files will be available during 3 days.<br>");
		List<String> filenameInputs = new ArrayList<>();
		//if(this.getStep1().isInvolved()){
		Map<Integer,MappingDwC> infos_mapping = this.getStep1().getInfos_mapping();
		for (Entry<Integer, MappingDwC> idFile : infos_mapping.entrySet()){
			MappingDwC mappingDWC = idFile.getValue();
			if(mappingDWC.getMappingInvolved()) {
				content.append("Mapped file ").append(mappingDWC.getFilename()).append(" : <a href=\"" + source).append(mappingDWC.getFilepath()).append("\"> Download link</a><br>");
			}
			filenameInputs.add(mappingDWC.getFilename());
		}
		//}

		content.append("<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public MimeMessage setTextMimeMessageStep2(MimeMessage message, String source){
		StringBuilder content = new StringBuilder("Reconcile result");
		content.append("<br></br>");

		if(this.getStep2().isInvolved()){
			Map<Integer,ReconciliationService> infos_reconcile = this.getStep2().getInfos_reconcile();
			for (Entry<Integer, ReconciliationService> idFile : infos_reconcile.entrySet()){
				//ReconciliationService reconcile = infos_reconcile.get(idFile);
				if(idFile != null) {
					content.append("Renamed file ").append(idFile.getValue().getFilename()).append(" => <a href=\"" + source).append(idFile.getValue().getFilepath()).append("\"> Download link</a><br>");
				}
			}
		}

		content.append( "<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public MimeMessage setTextMimeMessageStep3(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("Coordinates checking result");
		content.append("<br></br>");

		if(this.getStep3().isInvolved()){
			String pathWrongCoordinates = this.getStep3().getPathWrongCoordinates();
			content.append("File with wrong coordinates : <a href=\"" + source).append(pathWrongCoordinates).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public MimeMessage setTextMimeMessageStep4(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("GeospatialIssues checking result");
		content.append("<br></br>");

		if(this.getStep4().isInvolved()) {
			String pathWrongGeoIssue = this.getStep4().getPathWrongGeoIssue();
			content.append("File with wrong geo-issues : <a href=\"" + source).append(pathWrongGeoIssue).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}


	public MimeMessage setTextMimeMessageStep5(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("Synonyms checking result");
		content.append("<br></br>");

		if(this.getStep5().isInvolved()){
			content.append("Synonyms option done");
		}

		content.append("<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public MimeMessage setTextMimeMessageStep6(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("TDWG level 4 checking result");
		content.append("<br></br>");

		if(this.getStep6().isInvolved()){
			content.append("TDWG level 4 option done");
		}

		content.append("<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public MimeMessage setTextMimeMessageStep7(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("Iso2 code and polygon correspondence done");
		content.append("<br></br>");

		if(this.getStep7().isInvolved()){
			String pathWrongIso2 = this.getStep7().getPathWrongIso2();
			content.append("File with wrong iso2 code : <a href=\"" + source).append(pathWrongIso2).append("\"> Download link</a><br>");

			String pathWrongPolygon = this.getStep7().getPathWrongPolygon();
			content.append("File with occurrences not included in its polygon : <a href=\"" + source).append(pathWrongPolygon).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;

	}

	public MimeMessage setTextMimeMessageStep8(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("Bioclimatic and habitat validation result");
		content.append("<br></br>");

		if(this.getStep8().isInvolved()){
			String pathWrongRaster = this.getStep8().getPathWrongRaster();
			String pathMatrixResultRaster = this.getStep8().getPathMatrixResultRaster();
			content.append("Wrong occurrences for raster files : <a href=\"" + source).append(pathWrongRaster).append("\"> Download link</a><br>");
			content.append("Matrix result for raster analyse : <a href=\"" + source).append(pathMatrixResultRaster).append("\"> Download link</a><br>");

		}
		content.append( "<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}


	public MimeMessage setTextMimeMessageStep9(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("Status of the occurrences result");
		content.append("<br></br>");

		if(this.getStep9().isInvolved()){
			String pathWrongEstablishmentMeans = this.getStep9().getPathWrongEstablishmentMeans();
			content.append("File with wrong establishmentMeans option : <a href=\"" + source).append(pathWrongEstablishmentMeans).append("\"> Download link</a><br>");
		}

		content.append( "<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}
	public MimeMessage setTextMimeMessageFinal(MimeMessage message, String source) {
		StringBuilder content = new StringBuilder("Final files");
		content.append("<br></br>");
		List<File> finalOutputFile = this.getFinalisation().getFinalOutputFiles();

		for (int i = 0; i < finalOutputFile.size(); i++) {
			String cleanFilePath = finalOutputFile.get(i).getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/");
			String filenameInput = finalOutputFile.get(i).getName();
			content.append("Clean file ").append(filenameInput).append(" : <a href=\"" + source).append(cleanFilePath).append("\"> Download link</a><br>");
		}

		content.append("<br></br>");

		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public MimeMessage setTextMimeMessageFinal(MimeMessage message) {
		String source = "bloom.snv.jussieu.fr/bloom/";
		//String source = "http:localhost:8080/bloom/";
		StringBuilder content = new StringBuilder("To download results from mapping to DarwinCore<br>");
		content.append("These files will be available during 3 days.<br>");
		List<String> filenameInputs = new ArrayList<>();
		//if(this.getStep1().isInvolved()){
			Map<Integer,MappingDwC> infos_mapping = this.getStep1().getInfos_mapping();
			for (Entry<Integer, MappingDwC> idFile : infos_mapping.entrySet()){
				MappingDwC mappingDWC = idFile.getValue();
				if(mappingDWC.getMappingInvolved()) {
					content.append("Mapped file ").append(mappingDWC.getFilename()).append(" : <a href=\"" + source).append(mappingDWC.getFilepath()).append("\"> Download link</a><br>");
				}
				filenameInputs.add(mappingDWC.getFilename());
			}
		//}

		content.append( "<br></br>");
		//System.out.println(content);
		if(this.getStep2().isInvolved()){
			Map<Integer,ReconciliationService> infos_reconcile = this.getStep2().getInfos_reconcile();
			for (Entry<Integer, ReconciliationService> idFile : infos_reconcile.entrySet()){
				//ReconciliationService reconcile = infos_reconcile.get(idFile);
				content.append("Renamed file ").append(idFile.getValue().getFilename()).append(" => <a href=\"" + source).append(idFile.getValue().getFilepath()).append("\"> Download link</a><br>");
			}
		}
		content.append( "<br></br>");
		if(this.getStep3().isInvolved()){
			String pathWrongCoordinates = this.getStep3().getPathWrongCoordinates();
			content.append("File with wrong coordinates : <a href=\"" + source).append(pathWrongCoordinates).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");
		if(this.getStep4().isInvolved()) {
			String pathWrongGeoIssue = this.getStep3().getPathWrongCoordinates();
			content.append("File with wrong geo-issues : <a href=\"" + source).append(pathWrongGeoIssue).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");
		if(this.getStep7().isInvolved()){
			String pathWrongIso2 = this.getStep7().getPathWrongIso2();
			content.append("File with wrong iso2 code : <a href=\"" + source).append(pathWrongIso2).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");
		if(this.getStep8().isInvolved()){
			String pathWrongRaster = this.getStep8().getPathWrongRaster();
			String pathMatrixResultRaster = this.getStep8().getPathMatrixResultRaster();
			content.append("Wrong occurrences for raster files : <a href=\"" + source).append(pathWrongRaster).append("\"> Download link</a><br>");
			content.append("Matrix result for raster analyse : <a href=\"" + source).append(pathMatrixResultRaster).append("\"> Download link</a><br>");

		}
		content.append( "<br></br>");
		if(this.getStep9().isInvolved()){
			String pathWrongEstablishmentMeans = this.getStep9().getPathWrongEstablishmentMeans();
			content.append("File with wrong establishmentMeans option : <a href=\"" + source).append(pathWrongEstablishmentMeans).append("\"> Download link</a><br>");
		}

		content.append( "<br></br>");
		List<File> finalOutputFile = this.getFinalisation().getFinalOutputFiles();

		for(int i = 0; i < finalOutputFile.size(); i++){
			String cleanFilePath = finalOutputFile.get(i).getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/");
			String filenameInput = filenameInputs.get(i);
			content.append("Clean file ").append(filenameInput).append(" : <a href=\"" + source).append(cleanFilePath).append("\"> Download link</a><br>");
		}
		try {
			message.setText(content.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			System.err.println(e);
		}

		return message;
	}

	public InputParameters getInputParameters() { return inputParameters; };

	public void setInputParameters(InputParameters inputParameters) { this.inputParameters = inputParameters; };

	public Step1_MappingDwc getStep1() {
		return step1;
	}

	public void setStep1(Step1_MappingDwc step1) {
		this.step1 = step1;
	}

	public Step2_ReconciliationService getStep2() {
		return step2;
	}

	public void setStep2(Step2_ReconciliationService step2) {
		this.step2 = step2;
	}

	public Step3_CheckCoordinates getStep3() {
		return step3;
	}

	public void setStep3(Step3_CheckCoordinates step3) {
		this.step3 = step3;
	}

	public Step4_CheckGeoIssue getStep4() {
		return step4;
	}

	public void setStep4(Step4_CheckGeoIssue step4) {
		this.step4 = step4;
	}

	public Step5_IncludeSynonym getStep5() {
		return step5;
	}

	public void setStep5(Step5_IncludeSynonym step5) {
		this.step5 = step5;
	}

	public Step6_CheckTDWG getStep6() {
		return step6;
	}

	public void setStep6(Step6_CheckTDWG step6) {
		this.step6 = step6;
	}

	public Step7_CheckISo2Coordinates getStep7() {
		return step7;
	}

	public void setStep7(Step7_CheckISo2Coordinates step7) {
		this.step7 = step7;
	}

	public Step8_CheckCoordinatesRaster getStep8() {
		return step8;
	}

	public void setStep8(Step8_CheckCoordinatesRaster step8) {
		this.step8 = step8;
	}

	public Step9_EstablishmentMeans getStep9() {
		return step9;
	}

	public void setStep9(Step9_EstablishmentMeans step9) {
		this.step9 = step9;
	}

	public Finalisation getFinalisation() {
		return finalisation;
	}

	public void setFinalisation(Finalisation finalisation) {
		this.finalisation = finalisation;
	}

	public String getTypeEmail() { return typeEmail; }

	public void setTypeEmail (String typeEmail) { this.typeEmail = typeEmail; }
}
