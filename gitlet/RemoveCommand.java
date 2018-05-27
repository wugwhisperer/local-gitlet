package gitlet;

/**
 * The command process rm [file name].
 * @author Lily Vittayarukskul
 */
public class RemoveCommand {

    /** Marks FILENAME for untracking or unstages a file if it is currently
     * staged. Removes file from the working directory if the user has not
     * already done so. */
    RemoveCommand(String fileName) {

        if (!Utils.dirContains(Repository.STAGING, fileName)
                && !Repository.isCurrentComitTracked(fileName)) {
            throw new GitletException("No reason to remove the file.");
        }
        if (Utils.dirContains(Repository.STAGING, fileName) && Repository
                .isMarkAdd(fileName)) {
            Repository.unstage(fileName);
        }
        if (Repository.isCurrentComitTracked(fileName)) {
            Repository.putInStagingArea(fileName, "rm", Repository.BLOBS);
            if (Utils.dirContains(Repository.WORKING, fileName)) {
                Utils.removeFile(fileName, Repository.WORKING);
            }
        }
    }

}

