package gitlet;

import java.io.File;

/**
 *
 * @author Lily Vittayarukskul
 */
public class GlobalLogCommand {

    /**  */
    public GlobalLogCommand() {

        for (File file : Utils.listFiles(Repository.COMMITS)) {
            Repository.printCommit(Repository.getCommit(file.getName()));

        }

    }

}
