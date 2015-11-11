package fr.bird.bloom.model;

import fr.bird.bloom.beans.Finalisation;
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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
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
import java.util.Map.Entry;
import java.util.Properties;

public class SendMail {

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

	private String getResourcePath() {
		return BloomConfig.getResourcePath();
	}

	public SendMail(){

	}

	public void sendMessage(String emailUser) throws AddressException, MessagingException {

		String smtpHost = "";
		String from = "";
		//String to = "melanie.hachet@gmail.com";
		String username = "";
		String password = "";
		String smtpPort = "";
		try{
			System.out.println(getResourcePath());
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
						/*case 4 : smtpPort = line.split("\t")[1];
							break;*/
					}
					count ++;
				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) {
			System.out.println("Erreur --" + ioe.toString());
		}

		System.out.println("smtpHost : " + smtpHost + "\nfrom : " + from + "\nusername : " + username + "\npassword : " + password);

		final Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("username", username);
		props.put("password", password);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(props.getProperty("username"),props.getProperty("password"));
					}
				});


		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailUser));
		message.setSubject("BLOOM results");

		message = this.setTextMimeMessage(message);

		Transport tr = session.getTransport("smtp");
		tr.connect(smtpHost, username, password);
		message.saveChanges();

		// tr.send(message);

		tr.sendMessage(message,message.getAllRecipients());
		tr.close();

	}

	public MimeMessage setTextMimeMessage(MimeMessage message) throws MessagingException {
		StringBuilder content = new StringBuilder("To download results from mapping to DarwinCore<br>");
		ArrayList<String> filenameInputs = new ArrayList<>();
		if(this.getStep1().isInvolved()){
			HashMap<Integer,MappingDwC> infos_mapping = this.getStep1().getInfos_mapping();
			for (Entry<Integer, MappingDwC> idFile : infos_mapping.entrySet()){
				MappingDwC mappingDWC = idFile.getValue();
				if(mappingDWC.getMappingInvolved()) {
					content.append("Mapped file ").append(mappingDWC.getFilename()).append(" : <a href=\"http:localhost:8080/bloom/").append(mappingDWC.getFilepath()).append("\"> Download link</a><br>");
				}
				filenameInputs.add(mappingDWC.getFilename());
			}
		}
		content.append( "<br></br>");
		//System.out.println(content);
		if(this.getStep2().isInvolved()){
			HashMap<Integer,ReconciliationService> infos_reconcile = this.getStep2().getInfos_reconcile();
			for (Entry<Integer, ReconciliationService> idFile : infos_reconcile.entrySet()){
				//ReconciliationService reconcile = infos_reconcile.get(idFile);
				content.append("Renamed file ").append(idFile.getValue().getFilename()).append(" => <a href=\"http:localhost:8080/bloom/").append(idFile.getValue().getFilepath()).append("\"> Download link</a><br>");
			}
		}
		content.append( "<br></br>");
		if(this.getStep3().isInvolved()){
			String pathWrongCoordinates = this.getStep3().getPathWrongCoordinates();
			content.append("File with wrong coordinates : <a href=\"http:localhost:8080/bloom/").append(pathWrongCoordinates).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");
		if(this.getStep4().isInvolved()) {
			String pathWrongGeoIssue = this.getStep3().getPathWrongCoordinates();
			content.append("File with wrong geo-issues : <a href=\"http:localhost:8080/bloom/").append(pathWrongGeoIssue).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");
		if(this.getStep7().isInvolved()){
			String pathWrongIso2 = this.getStep7().getPathWrongIso2();
			content.append("File with wrong iso2 code : <a href=\"http:localhost:8080/bloom/").append(pathWrongIso2).append("\"> Download link</a><br>");
		}
		content.append( "<br></br>");
		if(this.getStep8().isInvolved()){
			String pathWrongRaster = this.getStep8().getPathWrongRaster();
			String pathMatrixResultRaster = this.getStep8().getPathMatrixResultRaster();
			content.append("Wrong occurences for raster files : <a href=\"http:localhost:8080/bloom/").append(pathWrongRaster).append("\"> Download link</a><br>");
			content.append("Matrix result for raster analyse : <a href=\"http:localhost:8080/bloom/").append(pathMatrixResultRaster).append("\"> Download link</a><br>");

		}
		content.append( "<br></br>");
		if(this.getStep9().isInvolved()){
			String pathWrongEstablishmentMeans = this.getStep9().getPathWrongEstablishmentMeans();
			content.append("File with wrong establishmentMeans option : <a href=\"http:localhost:8080/bloom/").append(pathWrongEstablishmentMeans).append("\"> Download link</a><br>");
		}

		content.append( "<br></br>");
		List<File> finalOutputFile = this.getFinalisation().getFinalOutputFiles();

		for(int i = 0; i < finalOutputFile.size(); i++){
			String cleanFilePath = finalOutputFile.get(i).getAbsolutePath().replace(BloomConfig.getDirectoryPath(), "output/");
			String filenameInput = filenameInputs.get(i);
			content.append("Clean file ").append(filenameInput).append(" : <a href=\"http:localhost:8080/bloom/").append(cleanFilePath).append("\"> Download link</a><br>");
		}
		message.setText(content.toString(), "UTF-8", "html");

		return message;
	}

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
}
