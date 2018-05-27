package gitlet;

/**
 *Processes command rm branch [branchToRemove].
 * @author Lily Vittayarukskul
 */
public class RemoveBranchCommand {

    /** Removes BRANCHTOREMOVE. */
    public RemoveBranchCommand(String branchToRemove) {

        if (!Utils.dirContains(Repository.BRANCHES, branchToRemove)) {
            throw new GitletException("A branch with that "
                    + "name does not exist.");
        }
        if (Repository.getBranchFile(branchToRemove).getName().equals(Repository
                .getIDOfHead())) {
            throw new GitletException("Cannot remove the current branch.");
        }
        Utils.removeFile(branchToRemove, Repository.BRANCHES);
    }
}
