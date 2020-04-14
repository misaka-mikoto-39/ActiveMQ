package client;

import java.util.Scanner;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Client {
	private static String url = "failover://tcp://192.168.1.161:61616";
	private static String mainsubject = "MESSAGEQUEUE";

	public static void main(String[] args) throws JMSException {
		boolean check = true;
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		String connectionID = "queue-" + connection.getClientID();
		Destination sendDestination = session.createQueue(mainsubject);
		MessageProducer producer = session.createProducer(sendDestination);
		Destination recieveDestination = session.createQueue(connectionID);
		MessageConsumer consumer = session.createConsumer(recieveDestination);
		while (check) {
			System.out.println("Enter an URL: ");
			String msg;
			msg = new Scanner(System.in).nextLine();
			// send msg
			msg = connectionID + " " + msg;
			TextMessage sendMessage = session.createTextMessage(msg);
			producer.send(sendMessage);
			System.out.println("Sent: '" + msg + "'");
			// wait answer
			Message recieveMessage = consumer.receive();
			if (recieveMessage instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) recieveMessage;
				System.out.println("Ket qua: '" + textMessage.getText() + "'");
			}
			System.out.println("Tiep tuc? (Y/N)");
			msg = new Scanner(System.in).next();
			if (msg == "N") {
				check = false;
			}

		}
	}
}