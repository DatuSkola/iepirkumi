package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.DateRange;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static lv.kauguri.iepirkumi.files.FileOperations.createIfNeeded;
import static lv.kauguri.iepirkumi.Iepirkumi.*;

class DownloadFromIUB {
    private FTPClient ftp = new FTPClient();
    private FTPClientConfig config = new FTPClientConfig();

    public void download(String destinationDir, DateRange dateRange) {
        ftp.configure(config);
        try {
            connectFTP(ftp);
            downloadFiles(destinationDir, dateRange, ftp);

            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }
    }

    private void connectFTP(FTPClient ftp) throws IOException {
        String server = "open.iub.gov.lv";
        ftp.connect(server, 21);
        ftp.user("anonymous");
        ftp.pass("");
        ftp.enterLocalPassiveMode();

        System.out.println("Connected to " + server + ".");
        System.out.print(ftp.getReplyString());

        int reply = ftp.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("FTP server refused connection.");
        }
    }

    private void downloadFiles(String archivesDir, DateRange dateRange, FTPClient ftp) throws IOException {
        for (int year = dateRange.fromYear; year <= dateRange.toYear; year++) {
            String yearDirectory =  archivesDir + year + SEP;
            createIfNeeded(yearDirectory);

            for (int month = dateRange.getFromMonth(year); month <= dateRange.getToMonth(year); month++) {
                String monthYear = getMonthStr(month) + "_" + year;
                String ftpDirectory = "/" + year + "/" + monthYear + "/";
                ftp.changeWorkingDirectory(ftpDirectory);

                String destinationDir = archivesDir + year + SEP + month + SEP;
                createIfNeeded(destinationDir);

                for (FTPFile ftpFile : ftp.listFiles()) {
                    retrieveAndSaveFile(destinationDir, ftp, ftpFile);
                }
            }
        }
    }

    private String getMonthStr(int month) {
        String monthStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        return monthStr;
    }

    private void retrieveAndSaveFile(String directory, FTPClient ftp, FTPFile ftpFile) {
        File file = new File(directory + ftpFile.getName());

        try (FileOutputStream fop = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }

            System.out.println("download and save " + file.getCanonicalPath());

            ftp.retrieveFile(file.getName(), fop);

            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void debug(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
}
