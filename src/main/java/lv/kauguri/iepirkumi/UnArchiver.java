package lv.kauguri.iepirkumi;

import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;
import org.codehaus.plexus.archiver.tar.TarUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.File;

import static lv.kauguri.iepirkumi.FileOperations.visitEachSubSubDirectory;
import static lv.kauguri.iepirkumi.Iepirkumi.*;

class UnArchiver {

    static void extract() {
        visitEachSubSubDirectory(ARCHIVES_DIR, (year, month) -> {
            File archiveDir = new File(ARCHIVES_DIR + SEP + year + SEP + month);
            File xmlDir = new File(XML_DIR + SEP + year + SEP + month);
            for(File achiveFile : archiveDir.listFiles()) {
                extractFile(achiveFile, xmlDir);
            }
        });
    }

    private static void extractFile(File archiveFile, File destinationDirectory) {
        final GZipUnArchiver gZipUnArchiver = new GZipUnArchiver();

        File tarFile = new File(TMP_DIR + archiveFile.getName().replace(".gz", ""));

        gZipUnArchiver.setSourceFile(archiveFile);
        gZipUnArchiver.setDestFile(tarFile);
        gZipUnArchiver.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG, "Logger"));
        gZipUnArchiver.extract();

        final TarUnArchiver tarUnArchiver = new TarUnArchiver();
        tarUnArchiver.setSourceFile(tarFile);
        tarUnArchiver.setDestDirectory(destinationDirectory);
        tarUnArchiver.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG, "Logger"));
        tarUnArchiver.extract();
    }
}
