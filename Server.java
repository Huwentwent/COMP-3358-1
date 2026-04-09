import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;


public class Server extends UnicastRemoteObject implements ServerFunction
{
	public Server() throws RemoteException{}
	
	private List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
            {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) 
                {
                    lines.add(trimmed);
                }
            }
        } 
        catch (IOException e) 
        {
        }
        return lines;
    }

    private void writeFile(String filename, List<String> lines) 
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) 
        {
            for (String line : lines) 
            {
                pw.println(line);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
	
	public boolean register(String username, String password) throws RemoteException
	{
		if(username == null || password == null || username.trim().isEmpty()) {
			return false;
		}
		username = username.trim();
		
		List<String> users = readFile("UserInfo.txt");
		
		for(String user:users) {
			if(user.startsWith(username + ":")) {
				return false;
			}
		}
		
		users.add(username + ":" + password);
        writeFile("UserInfo.txt", users);
        
		
		return login(username,password);
	}
	

    public boolean login(String username, String password) throws RemoteException
    {
    	if(username == null || password == null || username.trim().isEmpty()) {
			return false;
		}
    	
    	List<String> online = readFile("OnlineUser.txt");
    	if(online.contains(username)) 
    	{
    		return false;
    	}
    	
    	List<String> users = readFile("UserInfo.txt");
    	for(String user : users) {
			if(user.equals(username + ":" + password)) 
			{
				online.add(username);
				writeFile("OnlineUser.txt", online);
                return true;
			}
		}
    	
    	return false;
    }

    public boolean logout(String username) throws RemoteException{
    	if(username == null || username.trim().isEmpty()) {
			return false;
		}
    	username = username.trim();
    	
    	List<String> online = readFile("OnlineUser.txt");
        if (online.remove(username)) {
            writeFile("OnlineUser.txt", online);
            return true;
        }
        return false;
    }
    
    private static void clearFile(String filename) {
        try (FileWriter fw = new FileWriter(filename, false)) {
            // file is truncated
        } catch (IOException e) {
            System.err.println("Warning: Could not clear file " + filename);
        }
    }
	
	public static void main(String agrs[]) {
		try {
			Server app = new Server();
			System.setSecurityManager(new SecurityManager());
			Naming.rebind("Server",app);
			System.out.println("Service registered");
			try{
		        clearFile("OnlineUser.txt");
			}
			catch(Exception e) {
				System.err.println("Server Error: " + e.getMessage());
	            e.printStackTrace();
			}
		}
		catch(Exception e) 
		{
			System.err.println("Exception thrown: "+e);
		}
		
	}
}