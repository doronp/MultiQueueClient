import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    public static final int PORT = 5454;
    private static final String KILL_MESSAGE = "KILL_MESSAGE";
    public static final int BUFFER_SIZE = 256;

    /**
     * We run two threads each reading base tests from a file and concatenating its name to the message.
     * Because of the use of one buffer to write I added this 'protocol' of filling the buffer with spaces in
     * order to prevent residues from earlier message to be read - Just wanted to play with NIO basically.
     *
     * User == Topic in our case BTW.
     *
     * @param args
     */
    public static void main(String args[]) {

        Runnable client = () -> {
            logger.log(Level.INFO, Thread.currentThread().getName() + " Client started");
            SocketChannel c;
            ByteBuffer buffer;
            try {
                c = SocketChannel.open(new InetSocketAddress("localhost", PORT));
                BufferedReader br = new BufferedReader(new FileReader("testFile"));
                String line;

                while ((line = br.readLine()) != null) {
                    line += Thread.currentThread().getName();
                    line = String.format("%1$-" + BUFFER_SIZE + "s", line);
                    buffer = ByteBuffer.wrap(line.getBytes());
                    c.write(buffer);
                    buffer.clear();
                    c.read(buffer);
                    String msg = new String(buffer.array()).trim();
                    logger.log(Level.INFO, msg);
                }

                Thread.sleep(3000);
                String msg = KILL_MESSAGE;
                msg = String.format("%1$-" + BUFFER_SIZE + "s", msg);
                buffer = ByteBuffer.wrap(msg.getBytes());
                c.write(buffer);
                buffer.clear();
                c.read(buffer);
                c.close();
                logger.log(Level.INFO, msg);

            } catch (IOException | InterruptedException e) {
                logger.log(Level.SEVERE, "Error...", e);
            }
        };

        new Thread(client, "client-A").start();
        new Thread(client, "client-B").start();
        new Thread(client, "client-C").start();
        new Thread(client, "client-D").start();
        new Thread(client, "client-E").start();
        new Thread(client, "client-F").start();
        new Thread(client, "client-G").start();
    }
}