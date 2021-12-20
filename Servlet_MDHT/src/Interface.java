
import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Interface extends Remote {
	public byte[] fileContents(String filePath) throws RemoteException, IOException;

	public void readFile(String srcFileName, String socketIP, int socketPort, String remoteFileName)
			throws RemoteException;

	public List<File> getFiles(String extension) throws RemoteException;

	public void sendFile(File file) throws IOException;

	public void toServer(byte[] entireFile, String fileName, String fileName2) throws IOException;

	public void toServer(byte[] fileContents, String string, String fileName, String mergeType);
}
