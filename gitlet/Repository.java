package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles all requests that require access to the .gitlet
 * and working directory. q
 * @author Lily Vittayarukskul
 */
public class Repository {


    /** Path to working directory.*/
    static final String WORKING = System.getProperty("user.dir");
    /** Path to .gitlet directory.*/
    static final String GITLET = System.getProperty("user.dir") + File
            .separator + ".gitlet";
    /** Path to commits directory inside .gitlet.*/
    static final String COMMITS = GITLET + File.separator + "commits";
    /** Path to branches directory inside .gitlet.*/
    static final String BRANCHES = GITLET + File.separator + "branches";
    /** Path to staging directory inside .gitlet.*/
    static final String STAGING = GITLET + File.separator + "staging";
    /** Path to blobs directory inside .gitlet.*/
    static final String BLOBS = GITLET + File.separator + "blobs";
    /** Path to HEAD file inside .gitlet.*/
    static final String HEAD = GITLET + File.separator +  "HEAD";
    /** Path to FILETOMARKMAP file inside staging directory.*/
    static final String FILETOMARKMAP = STAGING + File.separator
            + "fileToMark";
    /**Length of a COMMITIDLENGTH.*/
    static final int COMMITIDLENGTH = 40;


    /**Returns true IFF .gitlet directory already exists.*/
    static boolean isGitletInitialized() {
        return new File(GITLET).exists();
    }

    /** Adds a copy of the FILENAME given FILESHA from the staging directory
     * to the blobs directory. */
    static void addToBlobsDir(String fileSha, String fileName) {
        File stagedFile = new File(STAGING + File.separator + fileName);
        File newFile = new File(BLOBS + File.separator + fileSha);
        String contents = Utils.readContentsAsString(stagedFile);
        Utils.writeContents(newFile, contents);
    }

    /** Prints some information about COMMIT. */
    static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getID());
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /** Overwrites FILENAME in the working directory to be the version
     * whose sha is FILEID.*/
    static void overwriteinWD(String fileID, String fileName) {
        File newFile = new File(Repository.WORKING + File.separator + fileName);
        String contents = Utils.readContentsAsString(Utils.getFile(BLOBS,
                fileID));
        Utils.writeContents(newFile, contents);
    }

    /** Make filetomarkmap.*/
    public static void initializeFileToMarkMap() {
        HashMap<String, String> stagedFileToMark = new HashMap<>();
        File file  = new File(STAGING + File
                .separator + "fileToMark");
        Utils.writeObject(file, stagedFileToMark);
    }

    /** Adds a copy of FILENAME, located in DIRECTORY, to the staging
     * directory and updates the mapping of staged files to their MARK.
     *  */
    static void putInStagingArea(String fileName, String mark,
                                 String directory) {

        if (mark.equals("rm")) {
            updateMark(fileName, mark);
        }
        if (mark.equals("add")) {
            File file = new File(STAGING + File.separator + fileName);
            String contents = Utils.readContentsAsString(Utils.getFile
                    (directory, fileName));
            Utils.writeContents(file, contents);
            updateMark(fileName, mark);
        }
    }

    /**Returns FileToMark hashmap.*/
    @SuppressWarnings("unchecked")
    static HashMap<String, String> getFileToMark() {
        File file = new File(FILETOMARKMAP);
        return Utils.readObject(file, HashMap.class);
    }

    /** Returns true iff FILENAME is marked as "add" in the fileToMark map of
     *  the staging directory. **/
    static boolean isMarkAdd(String fileName) {
        String entry = getFileToMark().get(fileName);
        if (entry == null) {
            return false;
        }
        return getFileToMark().get(fileName).equals("add");
    }

    /** Update the mapping of File FILENAME to its MARK in the staging
     * directory. Occurs when a file is being staged. */
    public static void updateMark(String fileName, String mark) {
        File file = new File(FILETOMARKMAP);
        HashMap<String, String> fileToMark = getFileToMark();
        fileToMark.put(fileName, mark);
        Utils.writeObject(file, fileToMark);
    }

    /** Returns the SHA-I hash value of FILE. **/
    static String getFileID(File file) {
        return Utils.sha1(Utils.readContents(file));
    }

    /** Removes the file with name NAME from the staging directory.  */
    static void unstage(String name) {
        deleteMark(name);
        File file = new File(STAGING + File.separator + name);
        file.delete();
    }

    /** Removes all files from DIRECTORY.*/
    static void clearFiles(String directory) {
        for (File file : Utils.listFiles(directory)) {
            file.delete();
        }
    }

    /** Returns the file that has name BRANCHNAME from
     * the branches directory. */
    static File getBranchFile(String branchName) {
        for (File file : Utils.listFiles(BRANCHES)) {
            if (file.getName().equals(branchName)) {
                return file;
            }
        }
        return null;
    }

    /** Delete a file given NAME from fileToMark in Staging directory.*/
    static void deleteMark(String name) {
        File file = new File(FILETOMARKMAP);
        HashMap<String, String> fileToMark = getFileToMark();
        fileToMark.remove(name);
        Utils.writeObject(file, fileToMark);
    }

    /** Set head of BRANCHNAME to point to new COMMITID.*/
    static void setHeadOfBranch(String branchName, String commitID) {
        File branchFile = getBranchFile(branchName);
        Utils.writeContents(branchFile, commitID);
    }

    /** Return commit at head of BRANCH.*/
    static String getBranchID(String branch) {
        File branchFile = getBranchFile(branch);
        if (branchFile == null) {
            return null;
        }
        return Utils.readContentsAsString(
                Repository.getBranchFile(branch));
    }

    /** Returns the name of the branch that references the current commit.
    Gitlet does not support detatched heads. */
    static String getIDOfHead() {
        return Utils.readContentsAsString(new File(HEAD));
    }

    /** Returns the commit who is at the the head of the current branch. */
    static Commit getCurrentCommit() {
        String branch = getIDOfHead();
        File branchFile = getBranchFile(branch);
        return getCommit(Utils.readContentsAsString(branchFile));
    }

    /** Returns the commit object whose unique id is SHA. Returns null if the
     commit does not exist. Accepts either full commitID's or abbreviated
     ones. */
    static Commit getCommit(String sha) {

        if (Repository.isSmallID(sha)) {
            sha = getLongId(sha);
        }

        for (File file : Utils.listFiles(COMMITS)) {
            if (file.getName().equals(sha)) {
                Commit c = Utils.readObject(file, Commit.class);
                return c;
            }
        }
        return null;
    }

    /** Returns true iff COMMITID is abbreviated. */
    static boolean isSmallID(String commitID) {
        return commitID.length() < COMMITIDLENGTH;
    }

    /** Returns the long version of the given commit SMALL.
     * Returns null if the commit does not exist. */
    static String getLongId(String small) {
        for (File f : Utils.listFiles(COMMITS)) {
            if (f.getName().substring(0, small.length()).equals(small)) {
                return f.getName();
            }
        }
        return null;
    }

    /** Returns a mapping of the current commit's blob fileNames to SHAs. */
    static HashMap<String, String> getTrackedFiles() {
        return Repository.getCurrentCommit().getBlobs();
    }

    /** Returns true iff FILENAME is the same version of the FILEID with the
     * same name in the current comment. */
    static boolean isSameVersion(String fileName, String fileID) {
        return getTrackedFiles().get(fileName).equals(fileID);
    }

    /** Returns true iff FILENAME is included by the current commit. */
    static boolean isCurrentComitTracked(String fileName) {
        return getTrackedFiles().containsKey(fileName);
    }

    /** Returns true iff FILE is included by the commit with COMMITID
     * or in the staging area. */
    static boolean isTracked(String file, String commitID) {
        Commit commit = getCommit(commitID);
        return commit.getBlobs().containsKey(file);
    }

    /** Sets the commit that the current branch references to COMMITID. */
    static void setBranchHead(String commitID) {
        File currBranch = getBranchFile(getIDOfHead());
        Utils.writeContents(currBranch, commitID);
    }

    /** Changes the HEAD (denoted by *) to be the head of BRANCH. */
    static void setHead(String branch) {
        File file = new File(HEAD);
        Utils.writeContents(file, branch);
    }

    /**Return a commit history of a branch given BRANCHNAME.*/
    static ArrayList<Commit> getCommitHistory(String branchName) {
        ArrayList<Commit> commitHistory = new ArrayList<Commit>();
        String commitID = getBranchID(branchName);
        Commit commit = getCommit(commitID);
        ArrayList<String> parents = commit.getParents();
        commitHistory.add(commit);
        while (parents.size() != 0) {
            for (String parentID : parents) {
                Commit parentCommit = Repository.getCommit(parentID);
                commitHistory.add(parentCommit);
                commit = parentCommit;
            }
            parents = commit.getParents();
        }

        return commitHistory;
    }

    /**Show branches in branch directory.*/
    static void displayBranches() {
        System.out.println("=== Branches ===");

        for (File file : Utils.listFiles(Repository.BRANCHES)) {
            if (file.getName().equals(Repository.getIDOfHead())) {
                System.out.println("*" + file.getName());
            } else {
                System.out.println(file.getName());
            }
        }
    }

    /**Show added FILES in staging directory.*/
    static void displayStagedFiles(ArrayList<String> files) {
        System.out.println("=== Staged Files ===");
        for (String file : files) {
            System.out.println(file);
        }
    }

    /**Show deleted FILES in staging directory.*/
    static void displayRemovedFiles(ArrayList<String> files) {
        System.out.println("=== Removed Files ===");
        for (String file : files) {
            System.out.println(file);
        }
    }


}
