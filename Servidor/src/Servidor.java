import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Servidor {

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(9870);
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            long serverTime = 64800000;

            System.out.println("SERVIDOR EM AÇÃO!!!");
            
            while (true) {
                System.out.println("Hora do servidor antes do ajuste: " + formatTime(serverTime));

                List<InetAddress> clientAddresses = new ArrayList<>();
                List<Integer> clientPorts = new ArrayList<>();
                List<Long> clientTimes = new ArrayList<>();

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

                serverTime = averageTime;
                System.out.println("Hora média calculada: " + formatTime(serverTime));
                System.out.println("---------------------------------------------");

                // Simula um ajuste aleatório no tempo do servidor
                Random random = new Random();
                serverTime += random.nextInt((400001) + 100000); // Ajuste aleatório entre -200000 e 200000

                Thread.sleep(1000); // Aguarda 1 segundo antes da próxima sincronização
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: " + e.getMessage());
        }
    }

    private static String formatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(milliseconds));
    }
}
