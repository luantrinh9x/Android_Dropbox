package com.assignment1;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import controller.MainActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TwoSteps extends Activity {

	public String a = "";
	public TextView tv;
	public EditText et;
	public static int choice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_steps);
		
		Button ok = (Button) findViewById(R.id.okStep);
		Button cancel = (Button) findViewById(R.id.cancelStep);
		Button getPIN = (Button) findViewById(R.id.getPIN);
		tv = (TextView) findViewById(R.id.error);
		et = (EditText) findViewById(R.id.etStep);
		
		getPIN.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final CharSequence[] items = {"Email", "SMS"};
				AlertDialog.Builder builder = new AlertDialog.Builder(TwoSteps.this);
				builder.setTitle("Options");
				builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				          public void onClick(DialogInterface dialog, int item) {
				        	  if(item == 0)
				        	  {
				        		  a = generateEmail();
				        		  dialog.dismiss();
				        	  }
				        	  else if(item == 1)
				        	  {
				        		  a = generatePIN();
				        		  dialog.dismiss();
				        	  }
				            }
				        });
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText et = (EditText) findViewById(R.id.etStep);
				
				if(et.getText().toString().equalsIgnoreCase(""))
				{
					tv.setText("Enter PIN");
					tv.setTextColor(Color.RED);
				}
				else if(!et.getText().toString().equalsIgnoreCase(a))
				{
					tv.setText("Wrong PIN");
					tv.setTextColor(Color.RED);
					et.setText("");
				}
				else if(et.getText().toString().equalsIgnoreCase(a))
				{
					a = "";
					tv.setText("Successful");
					tv.setTextColor(Color.GREEN);
					Intent int1 = new Intent(TwoSteps.this, MainActivity.class);
					int1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(int1);
					finish();
				}
			}
		});
	}
	
	public String generateEmail()
	{
		String alphabet = 
		        new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		int n = alphabet.length();
		String result = new String(); 
		Random r = new Random();
		for (int i=0; i < 5; i++){
		    result = result + alphabet.charAt(r.nextInt(n));
		}

		sendMail("luketrinh90@gmail.com", "DropBox PIN", result);
		
		return result;
	}
	
	public String generatePIN()
	{
		String alphabet = 
		        new String("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()");
		int n = alphabet.length();
		String result = new String(); 
		Random r = new Random();
		for (int i=0; i < 10; i++){
		    result = result + alphabet.charAt(r.nextInt(n));
		}

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage("01688887799", null, result, null, null);

		return result;
	}
	
	private Session createSessionObject() {
	    Properties properties = new Properties();
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.smtp.starttls.enable", "true");
	    properties.put("mail.smtp.host", "smtp.gmail.com");
	    properties.put("mail.smtp.port", "587");
	 
	    return Session.getInstance(properties, new javax.mail.Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication("luketrinh90@gmail.com", "Luke2711");
	        }
	    });
	}
	
	private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
	    Message message = new MimeMessage(session);
	    message.setFrom(new InternetAddress("anonymous@gmail.com", "Anonymous"));
	    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
	    message.setSubject(subject);
	    message.setText(messageBody);
	    return message;
	}
	
	private class SendMailTask extends AsyncTask<Message, Void, Void> {
	    private ProgressDialog progressDialog;
	 
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        progressDialog = ProgressDialog.show(TwoSteps.this, "Please wait", "Sending mail...", true, false);
	    }
	 
	    @Override
	    protected void onPostExecute(Void aVoid) {
	        super.onPostExecute(aVoid);
	        progressDialog.dismiss();
	    }
	 
	    @Override
	    protected Void doInBackground(Message... messages) {
	        try {
	            Transport.send(messages[0]);
	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	}
	private void sendMail(String email, String subject, String messageBody) {
	    Session session = createSessionObject();
	 
	    try {
	        Message message = createMessage(email, subject, messageBody, session);
	        new SendMailTask().execute(message);
	    } catch (AddressException e) {
	        e.printStackTrace();
	    } catch (MessagingException e) {
	        e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
	}
}
