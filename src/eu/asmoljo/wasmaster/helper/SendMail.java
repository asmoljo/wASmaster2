package eu.asmoljo.wasmaster.helper;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	
	
	public static void send(String subject, String text, String from, String to, String mailServer){    


	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", mailServer);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	                                 
	         // Set Subject: header field
	         message.setSubject(subject);

	         // Now set the actual message
	         message.setText(text);

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent mail successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	   }
	

        
	
	

}
