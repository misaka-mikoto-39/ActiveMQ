package server;

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

public class Server {
    private static String url = "failover://tcp://192.168.1.58:61616";
    private static String mainsubject = "MESSAGEQUEUE";

    public static void main(String[] args) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination recieveDestination = session.createQueue(mainsubject);
        MessageConsumer consumer = session.createConsumer(recieveDestination);
        while (true) {

            System.out.println("Waiting");
            // get data
            Message recieveMessage = consumer.receive();
            if (recieveMessage instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) recieveMessage;
                String connectionID = textMessage.getText().split(" ", 2)[0];
                String data = textMessage.getText().split(" ", 2)[1];
                WebCrawler crawler = new WebCrawler();
                String[] output = crawler.getPageLinks(data);
                Destination sendDestination = session.createQueue(connectionID);
                MessageProducer producer = session.createProducer(sendDestination);
                for (int i = 0; i < output.length; i++) {
                    if (output[i].equals("END")) {
                        TextMessage sendMessage = session.createTextMessage(output[i]);
                        producer.send(sendMessage);
                        break;
                    } else {
                        TextMessage sendMessage = session.createTextMessage(output[i]);
                        producer.send(sendMessage);
                    }
                }
                producer.close();
                System.out.println("Done: '" + connectionID + "'");
            }
        }
    }
}
