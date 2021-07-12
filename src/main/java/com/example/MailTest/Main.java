package com.example.MailTest;

import java.io.IOException;
import java.util.Properties;

import com.sun.mail.util.BASE64DecoderStream;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import com.sun.mail.pop3.POP3Store;

import org.apache.commons.io.IOUtils;
import org.apache.commons.codec.binary.Base64;


public class Main {

    public static void main(String[] args) {
        String host = "pop.gmail.com";
        String mailStoreType = "pop3";
        String username= "**";
        String password= "**";
        receiveEmail(host, mailStoreType, username, password);
    }

    private static void receiveEmail(String pop3Host, String storeType, String user, String password) {
        try{
            Properties properties = new Properties();
            //properties.setProperty("mail.store.protocol", "pop3s");
            properties.setProperty("mail.pop3.host", pop3Host);
            properties.setProperty("mail.pop3.user", user);
            properties.setProperty("mail.pop3.password", password);
            properties.setProperty("mail.pop3.port", "995");
            properties.setProperty("mail.pop3.auth", "true");
            //properties.setProperty("mail.debug", "true");
            properties.setProperty("mail.pop3s.ssl.trust", "*");
//            properties.setProperty("mail.pop3s.socketFactory.class",
//                    "javax.net.ssl.SSLSocketFactory" );
            properties.setProperty("mail.pop3.ssl.enable", "true");

            Session session = Session.getDefaultInstance(properties,
                    new jakarta.mail.Authenticator(){
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    user, password);// Specify the Username and the PassWord
                        }
                    });


            POP3Store emailStore = (POP3Store) session.getStore(storeType);
            emailStore.connect();

            while(true) {
                Folder emailFolder = emailStore.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                jakarta.mail.Message[] messages = emailFolder.getMessages();
                System.out.println("count: " + messages.length);

                int temp = 7;
                if(messages.length == temp) {
                    System.out.println("-------------V--------------");
                    System.out.println("Subject: " + messages[messages.length-1].getSubject());
                    System.out.println("From: " + messages[messages.length-1].getFrom()[0]);
                    MimeMultipart mimeMultipart = (MimeMultipart) messages[messages.length-1].getContent();
                    MimeBodyPart mimeBodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(1);

                    String[] base64Content = new String[2];

                    if (mimeBodyPart.getContent() instanceof BASE64DecoderStream) {
                        BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream) mimeBodyPart.getContent();
                        byte[] byteArray = IOUtils.toByteArray(base64DecoderStream);
                        byte[] encodeBase64 = Base64.encodeBase64(byteArray);
                        base64Content[0] = new String(encodeBase64, "UTF-8");
//                        base64Content[1] = getContentTypeString(part);
                    }


                    System.out.println("Text: " +base64Content);
                    System.out.println("Text: " +mimeBodyPart.getContent());


                    System.out.println("-------------V--------------");
                    emailFolder.close(false);
                    emailStore.close();
                    break;
                }
                System.out.println("next");
            }

//            for (int i = 0; i < messages.length; i++) {
//                Message message = messages[i];
//                System.out.println("---------------------------------");
//                System.out.println("Email Number " + (i + 1));
//                System.out.println("Subject: " + message.getSubject());
//                System.out.println("From: " + message.getFrom()[0]);
//                System.out.println("Text: " + message.getContent().toString());
//            }



        }catch(NoSuchProviderException e){
            e.printStackTrace();
        } catch (MessagingException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }


}
