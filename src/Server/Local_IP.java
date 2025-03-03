/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**z    
 *
 * @author Admin
 */

public class Local_IP{
    
    public String getIPAddress() {
        String ip = "";
		try {
			Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = (NetworkInterface) interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;
				Enumeration addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = (InetAddress) addresses.nextElement();

					if (addr instanceof Inet6Address)
						continue;

					ip = addr.getHostAddress();
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return ip;
    }
}
