package gitlet;

/**
 * Processes the command add [fileName].
 * @author Lily Vittayarukskul
 */
public class AddCommand {

    /**Adds FILENAME to staging directory.*/
    public AddCommand(String fileName) throws GitletException {

        if (!Utils.dirContains(Repository.WORKING, fileName)) {
            throw new GitletException("File does not exist.");
        }
        String fileSHA = Repository.getFileID(Utils.getFile(Repository
                .WORKING, fileName));
        if (!Repository.isCurrentComitTracked(fileName)) {
            Repository.putInStagingArea(fileName, "add", Repository.WORKING);
        } else {
            if (!Repository.isSameVersion(fileName, fileSHA)) {
                Repository.putInStagingArea(fileName, "add",
                        Repository.WORKING);
            } else {
                if (Utils.dirContains(Repository.STAGING,
                        fileName)) {
                    Repository.unstage(fileName);
                }
                if (!Repository.isMarkAdd(fileName)) {
                    Repository.deleteMark(fileName);
                }
            }
        }
    }
}
