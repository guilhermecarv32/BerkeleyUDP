import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Servidor {

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(9870);
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            long serverTime = 64800000;

            System.out.println("SERVIDOR EM AÇÃO!!!");
            System.out.println("Hora do servidor antes do ajuste: " + formatTime(serverTime));

            ArrayList<Long> clientTimes = new ArrayList<>();
            ArrayList<InetAddress> clientAddresses = new ArrayList<>();
            ArrayList<Integer> clientPorts = new ArrayList<>();

            // Recebe tempo de cada cliente
            for (int i = 0; i < 2; i++) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String receivedString = new String(receivePacket.getData()).trim();
                long clientTime = Long.parseLong(receivedString);

                clientTimes.add(clientTime);
                clientAddresses.add(receivePacket.getAddress());
                clientPorts.add(receivePacket.getPort());

                System.out.println("Hora do cliente " + (i + 1) + " antes do ajuste: " + formatTime(clientTime));
            }

            // Calcula a média das horas (incluindo a do servidor)
            long total = serverTime;
            for (long time : clientTimes) {
                total += time;
            }
            long averageTime = total / (clientTimes.size() + 1);

            // Envia tempo médio para cada cliente
            for (int i = 0; i < clientAddresses.size(); i++) {
                String averageTimeString = Long.toString(averageTime);
                sendData = averageTimeString.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddresses.get(i), clientPorts.get(i));
                serverSocket.send(sendPacket);
            }

            System.out.println("Hora média calculada: " + formatTime(averageTime));
            System.out.println("SERVIDOR ENCERRADO");

            serverSocket.close();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    private static String formatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(milliseconds));
    }
}
