package gitlet;

import java.io.File;

/**
 * Processes command find [message].
 * @author Lily Vittayarukskul
 */
public class FindCommand {

    /** Prints out all commit with the given MESSAGE.*/
    public FindCommand(String message) {

        boolean doesMsgExist = false;
        if (Utils.listFiles(Repository.COMMITS) == null) {
            throw new GitletException("Found no commit with that message.");
        }
        for (File file : Utils.listFiles(Repository.COMMITS)) {
            Commit commit = Repository.getCommit(file.getName());
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getID());
                doesMsgExist = true;
            }
        }
        if (!doesMsgExist) {
            throw new GitletException("Found no commit with that message.");
        }
    }

}
