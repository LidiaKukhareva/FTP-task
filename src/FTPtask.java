
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
 
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
 
 
public class FTPtask {
 
    FTPClient ftp = null;
 
 
    public FTPtask(String host, String username, String password) throws Exception {
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftp.connect(host);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftp.login(username, password);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }
 
 
    public void downloadFile(String remoteFilePath, String localFilePath) {
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            this.ftp.retrieveFile(remoteFilePath, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
 
    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
            }
        }
    }
 
    public void PrintListOfFiles() throws IOException{
    	 FTPFile[] ftpFiles = ftp.listFiles();  
         if (ftpFiles != null && ftpFiles.length > 0) {
             for (FTPFile file : ftpFiles) {
                 if (!file.isFile()) {
                     continue;
                 }
                 System.out.println("File is " + file.getName());
             }
         }
         else
        	 System.out.println("No files found...");
    }
    
    public void uploadFile(String remoteFilePath, String localFilePath) {
    	 try (FileInputStream fis = new FileInputStream(localFilePath)) {
             boolean done = ftp.storeFile(remoteFilePath, fis);
             fis.close();
             if (done) {
                 System.out.println("The file is uploaded successfully.");
             }
             else
            	 System.out.println("Uploading failed");
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
    
    public static void main(String[] args) {
        try {
            FTPtask ftptask =
                new FTPtask("ftp.mirror.nl", "anonymous", "lida_ft9@yahoo.com");  
            ftptask.PrintListOfFiles();     
            ftptask.downloadFile("robots.txt", "E:/tmp/ftptask.txt");
            ftptask.uploadFile("uploaded.txt", "E:/tmp/uploaded.txt");
            ftptask.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
}
