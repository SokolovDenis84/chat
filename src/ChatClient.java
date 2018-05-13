import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class ChatClient extends JFrame implements Runnable {

    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final JTextArea outTextArea;
    private final JTextField inTextField;
    private final JButton sendButton;

    private ActionListener sendMessageListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                dataOutputStream.writeUTF(inTextField.getText());
                dataOutputStream.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            inTextField.setText("");
        }
    };

    public ChatClient(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        super("Client");
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;

        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setLayout(new BorderLayout());
        outTextArea = new JTextArea();
        DefaultCaret caret = (DefaultCaret) outTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane jScrollPane = new JScrollPane(outTextArea);
        jScrollPane.transferFocusDownCycle();
        add(jScrollPane, BorderLayout.CENTER);

        inTextField = new JTextField();
        add(inTextField, BorderLayout.SOUTH);

        sendButton = new JButton("Отправить");
        add(sendButton, BorderLayout.NORTH);


        sendButton.addActionListener(sendMessageListener);
        inTextField.addActionListener(sendMessageListener);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                try {
                    dataOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setVisible(true);
        inTextField.requestFocus();
        new Thread(this).start();
    }

    public static void main(String[] args) {
        String site = "localhost";
        String port = "8088";

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
//        new ChatClient(null, null, null);
        try {
            socket = new Socket(site, Integer.parseInt(port));
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            new ChatClient(socket, dataInputStream, dataOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {

            while (true) { // todo flag
                String line = dataInputStream.readUTF();
                outTextArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inTextField.setVisible(false);
            sendButton.setVisible(false);
            validate();
        }

    }


}
