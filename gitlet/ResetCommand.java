package gitlet;

import java.io.File;

/**
 * Processes the command reset [commit id].
 * @author Lily Vittayarukskul
 */
public class ResetCommand {

    /** Checks out all the files tracked by the commit with id COMMITID. */
    public ResetCommand(String commitID) {

        if (!Utils.dirContains(Repository.COMMITS, commitID)) {
            throw new GitletException("No commit with that id exists.");
        }

        for (File file : Utils.listFiles(Repository.WORKING)) {
            boolean trackedInGiven = Repository.isTracked
                    (file.getName(), commitID);
            boolean trackedInHead = Repository.isCurrentComitTracked(
                    file.getName());

            if (!trackedInHead && trackedInGiven) {
                throw new GitletException("There is an untracked file in the"
                        + " way; delete it or add it first.");
            } else if (!trackedInGiven && trackedInHead) {
                Utils.removeFile(file.getName(), Repository.WORKING);
            } else if (!trackedInGiven && !trackedInHead) {
                continue;
            } else {
                String givensVersion = Repository.getCommit(commitID)
                        .getBlobs().get(file.getName());
                Repository.overwriteinWD(givensVersion, file.getName());
            }
        }

        Repository.clearFiles(Repository.STAGING);
        Repository.setBranchHead(commitID);


    }
}
