package gitlet;

import java.io.File;

/** @author Lily Vittayarukskul
 */
public class BranchCommand {

    /**Create a new branch with given name BRANCHNAME.*/
    public BranchCommand(String branchName) throws GitletException {
        if (Utils.dirContains(Repository.BRANCHES, branchName)) {
            throw new GitletException("A branch with that name already exists"
                    + ".");
        }
        File newBranch = new File(Repository.BRANCHES + File.separator
                + branchName);
        String currentCommitID = Repository.getCurrentCommit().getID();
        Utils.writeContents(newBranch, currentCommitID);
    }
}
