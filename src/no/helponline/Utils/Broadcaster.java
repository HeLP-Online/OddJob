package no.helponline.Utils;

import no.helponline.OddJob;

import java.net.*;
import java.util.Enumeration;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Broadcaster implements Runnable {
    public static final String BROADCAST_HOST = "224.0.2.60:4445";
    private int failcount = 0;
    private final DatagramSocket socket;
    private final int port;
    private final String motd;
    private final String configuredIP;
    private boolean running = true;

    public Broadcaster(DatagramSocket socket, int port, String motd, String ip) {
        this.socket = socket;
        this.port = 25564;
        this.motd = motd;
        this.configuredIP = ip;
        OddJob.getInstance().getMessageManager().console("ip:" + ip);
    }

    public static DatagramSocket createSocket() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    @Override
    public void run() {
        try {
            byte[] ad = getAd();
            String[] host = BROADCAST_HOST.split(":");
            DatagramPacket packet = new DatagramPacket(ad, ad.length, InetAddress.getByName(host[0]), Integer.parseInt(host[1]));
            broadcast(socket, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
    }

    private void broadcast(DatagramSocket socket, DatagramPacket packet) {
        try {
            while (running) {
                try {
                    socket.send(packet);
                    failcount = 0;
                } catch (Throwable ex) {
                    fail(ex);
                }
                Thread.sleep(1500);
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void fail(Throwable ex) throws InterruptedException {
        if (failcount++ == 0) {
            ex.printStackTrace();
        }
        if (failcount < 5) {
            OddJob.getInstance().getMessageManager().warning("Failed to broadcast, trying again in 10 seconds...", OddJob.getInstance().getServer().getConsoleSender(), false);
        } else if (failcount == 5) {
            OddJob.getInstance().getMessageManager().danger("Broadcasting will not work until the network is fixed. Warnings disabled.", OddJob.getInstance().getServer().getConsoleSender(), false);
        }
        Thread.sleep(8500);
    }

    private byte[] getAd() {
        String ad = getLanIP();
        String str = "[MOTD]" + motd + "[/MOTD][AD]" + ad + "[/AD]";
        return str.getBytes(UTF_8);
    }

    private String getLanIP() {
        if (!configuredIP.equals("")) return configuredIP;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
            throw new Exception("No usable IPv4 non-loopback address found");
        } catch (Exception e) {
            e.printStackTrace();
            OddJob.getInstance().getMessageManager().danger("Could not automatically detect LAN IP, please set server-ip in server.properties.", OddJob.getInstance().getServer().getConsoleSender(), false);
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
                OddJob.getInstance().getMessageManager().danger("No network interfaces found!", OddJob.getInstance().getServer().getConsoleSender(), false);
                return "End of the world";
            }
        }
    }


}
