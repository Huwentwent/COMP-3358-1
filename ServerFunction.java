import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerFunction extends Remote {

    boolean register(String username, String password) throws RemoteException;

    boolean login(String username, String password) throws RemoteException;

    boolean logout(String username) throws RemoteException;
}