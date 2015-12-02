package navServer;




import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.util.LinkedList;
import java.util.List;

public class NavServer {

	public WebServer ws;
	private static final int port = 8080;
	
	private static LinkedList<String> jammedList = new LinkedList<String>();
	
	
	public List<String> getJammedRoads(){		
		return jammedList;
	}
	
	
	
	public Boolean setJamms(List<String> jammedRoads){
		jammedList.clear();
		
		for(int i=0;i<jammedRoads.size();i++){
			jammedList.add(jammedRoads.get(i));	
			System.out.println(jammedRoads.get(i));
		}		
		return true;
	}
	
	 public Integer mul(int x, int y) {
		    return new Integer(x*y);
		  }
	
	
	
	
	 public static void main(String[] args){
		 try {

		      WebServer webServer = new WebServer(port);

		      XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
		      PropertyHandlerMapping phm = new PropertyHandlerMapping();

		      phm.addHandler( "NavServer", NavServer.class);
		      xmlRpcServer.setHandlerMapping(phm);

		     XmlRpcServerConfigImpl serverConfig =
		              (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
		     // serverConfig.setEnabledForExtensions(true);
		      //serverConfig.setContentLengthOptional(false);

		      webServer.start();

		    } catch (Exception exception) {
		       System.err.println("JavaServer: " + exception);
		    }
	 }
 }
	
	

