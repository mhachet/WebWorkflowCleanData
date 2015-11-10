package fr.bird.bloom.model;

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
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

public class SendMail {


	private static final String SMTP_HOST1 = "smtp.mnhn.fr";
	private static final String LOGIN_SMTP1 = "mhachet";
	private static final String IMAP_ACCOUNT1 = "mhachet@mnhn.fr";

	private Step1_MappingDwc step1;
	private Step2_ReconciliationService step2;
	private Step3_CheckCoordinates step3;
	private Step4_CheckGeoIssue step4;
	private Step5_IncludeSynonym step5;
	private Step6_CheckTDWG step6;
	private Step7_CheckISo2Coordinates step7;
	private Step8_CheckCoordinatesRaster step8;
	private Step9_EstablishmentMeans step9;


	public SendMail(){

	}

	public void sendMessage() throws AddressException, MessagingException { 

		String smtpHost = "";
		String from = "";
		String to = "melanie.hachet@gmail.com";
		String username = "";
		String password = "";
		try{
			BufferedReader buff = new BufferedReader(new FileReader(BloomConfig.getResourcePath() + ".properties_mail"));
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

		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);

		MimeMessage message = new MimeMessage(session);   
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject("BLOOM results");


		message.setText("");

		Transport tr = session.getTransport("smtp");
		tr.connect(smtpHost, username, password);
		message.saveChanges();

		// tr.send(message);

		tr.sendMessage(message,message.getAllRecipients());
		tr.close();

	}

	public MimeMessage setTextMimeMessage(MimeMessage message){
		String content = "To download results from mapping to DarwinCore<br>";
		if(this.getStep1().isInvolved()){
			HashMap<Integer,MappingDwC> infos_mapping = step1.getInfos_mapping();
			for (Entry<Integer, MappingDwC> idFile : infos_mapping.entrySet()){
				MappingDwC mappingDWC = idFile.getValue();
				content += "- " + idFile.getKey() + " => " + mappingDWC.getFilepath();
			}
		}
		if(this.getStep2().isInvolved()){
			
		}
		if(this.getStep3().isInvolved()){
			
		}
		if(this.getStep4().isInvolved()){
			
		}
		if(this.getStep5().isInvolved()){
			
		}
		if(this.getStep6().isInvolved()){
			
		}
		if(this.getStep7().isInvolved()){
			
		}
		if(this.getStep8().isInvolved()){
			
		}
		if(this.getStep9().isInvolved()){
			
		}
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

	public static String getSmtpHost1() {
		return SMTP_HOST1;
	}

	public static String getLoginSmtp1() {
		return LOGIN_SMTP1;
	}

	public static String getImapAccount1() {
		return IMAP_ACCOUNT1;
	} 


}
