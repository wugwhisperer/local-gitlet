package gitlet;

import java.util.HashMap;

/** @author Lily Vittayarukskul
 */
public class CheckoutCommand {
    /**Processes the commands checkout -- [NAME], checkout [COMMITID] --
     * [SEPMARK] and checkout [branch name].*/
    public CheckoutCommand(String commitID, String sepMark, String name) {
        Commit c = Repository.getCommit(commitID);

        if (c == null) {
            throw new GitletException("No commit with that id exists.");
        }
        String fileID = c.getBlobs().get(name);
        if (fileID == null) {
            throw new GitletException("File does not exist in that commit.");
        }
        if (!sepMark.equals("--")) {
            throw new GitletException("Incorrect operands.");
        }
        Repository.overwriteinWD(c.getBlobs().get(name), name);
    }

    /**Processes the commands checkout -- [NAME] --
     *and checkout [branch name], given ISFILE.*/
    public CheckoutCommand(String name, boolean isFile) {
        if (isFile) {
            Commit currentCommit = Repository.getCurrentCommit();
            String fileID = currentCommit.getBlobs().get(name);
            if (fileID == null) {
                throw new GitletException(
                        "File does not exist in that commit.");
            }
            Repository.overwriteinWD(fileID, name);

        } else {
            String commitID = Repository.getBranchID(name);
            if (commitID == null) {
                throw new GitletException("No such branch exists.");
            }
            if (Repository.getIDOfHead().equals(name)) {
                throw new GitletException(
                        "No need to checkout the current branch.");
            }
            Commit given = Repository.getCommit(Repository
                    .getBranchID(name));
            for (String file
                    : Repository.getCurrentCommit().getBlobs().keySet()) {
                if (!given.getBlobs().keySet().contains(file)) {
                    Utils.removeFile(file, Repository.WORKING);
                } else {
                    if (!Repository.isCurrentComitTracked(file)) {
                        if (given.getBlobs().keySet().contains(file)) {
                            throw new GitletException(
                                    "There is an untracked file in the way;"
                                            + " delete it or add it first.");
                        }
                    }
                }

            }
            for (HashMap.Entry<String, String>
                     givenBlob : given.getBlobs().entrySet()) {
                Repository.overwriteinWD(
                         givenBlob.getValue(), givenBlob.getKey());
            }
            Repository.setHead(name);
        }
    }
}
