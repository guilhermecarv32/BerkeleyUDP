import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cliente1 {

    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");

        byte[] sendData;
        byte[] receiveData = new byte[1024];

        long clientTime = 64200000;
        System.out.println("Cliente 1 - Hora antes do ajuste: " + formatTime(clientTime));

        String timeString = Long.toString(clientTime);
        sendData = timeString.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9870);
        clientSocket.send(sendPacket);

        // Recebe tempo médio do servidor
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String averageTimeString = new String(receivePacket.getData()).trim();
        long averageTime = Long.parseLong(averageTimeString);
        clientTime = averageTime;

        System.out.println("Cliente 1 - Hora após o ajuste: " + formatTime(clientTime));

        clientSocket.close();
        System.out.println("CLIENTE 1 ENCERRADO");
    }

    private static String formatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(milliseconds));
    }
}
