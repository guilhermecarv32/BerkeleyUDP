import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Cliente1 {

    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");

        byte[] sendData;
        byte[] receiveData = new byte[1024];

        long clientTime = 64200000;

        while (true) {
            System.out.println("Cliente 1 - Hora ANTES do ajuste: " + formatTime(clientTime));
        
            // Envia tempo atual para o servidor
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

            System.out.println("Cliente 1 - Hora DEPOIS do ajuste: " + formatTime(clientTime));

            // Simula um ajuste aleatório no tempo do cliente
            Random random = new Random();
            clientTime += random.nextInt((400001) + 100000); // Ajuste aleatório entre -200000 e 200000

            System.out.println("----------------------------------------");

            Thread.sleep(1000); // Aguarda 1 segundo antes da próxima sincronização
        }
    }

    private static String formatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(milliseconds));
    }
}
