package gitlet;

import java.io.File;

/** @author Lily Vittayarukskul
 */

public class InitCommand {

    /**  */
    public InitCommand() {
        try {
            if (Repository.isGitletInitialized()) {
                throw new GitletException();
            } else {
                initializeDotGitlet();
                initialCommit = new Commit();
                initializeHEAD();
                initializeMaster();
            }
        } catch (GitletException error) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
    }

    /** Initializes the subdirectories of the ./gitlet directory. */
    private void initializeDotGitlet() {
        String[] directoryNames = {Repository.COMMITS, Repository.BRANCHES,
            Repository.BLOBS, Repository.STAGING};
        File gitletDir = new File(Repository.GITLET);
        gitletDir.mkdir();
        for (String name : directoryNames) {
            File dir = new File(name);
            dir.mkdir();
        }
    }

    /** Initializes the master branch. */
    private void initializeMaster() {
        File master = new File(Repository.BRANCHES
                + File.separator + "master");
        Utils.writeContents(master, initialCommit.getID());
    }

    /** Initializes the head. */
    private void initializeHEAD() {
        File head = new File(Repository.HEAD);
        Utils.writeContents(head, "master");
    }

    /** The initial commit of this repository. */
    private Commit initialCommit;


}
