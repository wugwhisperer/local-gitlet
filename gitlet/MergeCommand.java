package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The command that merges the branch with the given name
 * onto this branch. Processes the command merge [branch name].
 * @author Lily Vittayarukskul
 */
public class MergeCommand {

    /**Merge in GIVENBRANCH.*/
    public MergeCommand(String givenBranch) {
        currBranchName = Repository.getIDOfHead();
        givenBranchName = givenBranch;
        currHistory = Repository.getCommitHistory(currBranchName);
        givenHistory = Repository.getCommitHistory(givenBranch);
        currCommit = currHistory.get(0);
        givenCommit = givenHistory.get(0);
        splitPoint = getSplitPt();
        merge();
    }

    /**Return the most recent commit between two commits.*/
    private static Commit getSplitPt() {
        for (Commit curr : currHistory) {
            for (Commit given: givenHistory) {
                if (sameCommit(curr, given)) {
                    return curr;
                }
            }
        }
        return null;
    }

    /**Returns true if CURR ADN GIVEN are the same.*/
    private static boolean sameCommit(Commit curr, Commit given) {
        HashMap<String, String> currBlobNameToIDMap = currCommit.getBlobs();
        HashMap<String, String> givenBlobNameToIDMap = givenCommit.getBlobs();
        if (currBlobNameToIDMap.equals(givenBlobNameToIDMap)) {
            return true;
        }
        return false;
    }

    /**Merge conflicting files.*/
    private static void merge() {
        failureCases();

        HashMap<String, String> currentBlobs = currCommit.getBlobs();
        HashMap<String, String> givenBlobs = givenCommit.getBlobs();
        HashMap<String, String> currModifiedBlobs =
                getModifiedBlobs(currentBlobs);
        HashMap<String, String> givenModifiedBlobs =
                getModifiedBlobs(givenBlobs);
        if ((currModifiedBlobs.size() == 0)
                && (givenModifiedBlobs.size() > 0)) {
            new CheckoutCommand(givenBranchName, false);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        if ((currModifiedBlobs.size() > 0)
                && (givenModifiedBlobs.size() == 0)) {
            System.out.println("Given branch is "
                    + "an ancestor of the current branch.");
            System.exit(0);
        }
        ArrayList<String> conflictingFiles = new ArrayList<>();
        if ((currModifiedBlobs.size() > 0) && (givenModifiedBlobs.size() > 0)) {
            conflictingFiles = createMergeConflictFiles(
                    currModifiedBlobs, givenModifiedBlobs);
            if (conflictingFiles.size() > 0) {
                System.out.println("Encountered a merge conflict.");
            }
        }
        for (String fileName : givenModifiedBlobs.keySet()) {
            if (!conflictingFiles.contains(fileName)) {
                if (givenModifiedBlobs.get(fileName).equals("deleted")) {
                    Utils.removeFile(fileName, Repository.WORKING);
                }
                if (givenModifiedBlobs.get(fileName).equals("modified")) {
                    Repository.overwriteinWD(
                            givenModifiedBlobs.get(fileName), fileName);
                    new AddCommand(fileName);
                }
                if (givenModifiedBlobs.get(fileName).equals("added")) {
                    Repository.overwriteinWD(
                            givenModifiedBlobs.get(fileName), fileName);
                    new AddCommand(fileName);
                }
            }
        }
        new Commit("Merged " + givenBranchName
                + "into " + currBranchName + ".", givenBranchName);
    }

    /** Create merge conflict files given CURRBLOBS and GIVENBLOBS. Return an
     * array of the filenames in which merge conflict files were created. */
    private static ArrayList<String> createMergeConflictFiles(
            HashMap<String, String> currBlobs, HashMap<String,
            String> givenBlobs) {
        ArrayList<String> conflictingFiles = new ArrayList<>();
        for (HashMap.Entry<String, String> currBlob : currBlobs.entrySet()) {
            for (HashMap.Entry<String, String> givenBlob
                    : givenBlobs.entrySet()) {
                if (currBlob.getKey().equals(givenBlob.getKey())) {
                    createMergeConflictFile(currBlob.getKey(),
                            currCommit.getBlobs().get(currBlob.getKey()),
                            givenCommit.getBlobs().get(givenBlob.getKey()));
                    conflictingFiles.add(currBlob.getKey());
                }
            }
        }
        return conflictingFiles;
    }

    /** Return an Hashmap of filename to modification type, given
     *  files in BLOBSMAP and comparing to splitPointBlobs.*/
    private static HashMap<String, String> getModifiedBlobs(HashMap<String,
            String> blobsMap) {
        HashMap<String, String> modifiedBlobs = new HashMap<>();
        for (String fileName : blobsMap.keySet()) {
            if (!splitPoint.getBlobs().containsKey(fileName)) {
                modifiedBlobs.put(fileName, "add");
            }
        }
        for (HashMap.Entry<String, String>
                 splitBlob : splitPoint.getBlobs().entrySet()) {
            boolean hasBlob = false;
            for (HashMap.Entry<String, String>
                     branchBlob : blobsMap.entrySet()) {
                if (splitBlob.getKey().equals(branchBlob.getKey())) {
                    hasBlob = true;
                    if (!splitBlob.getValue().equals(branchBlob.getValue())) {
                        modifiedBlobs.put(branchBlob.getKey(), "modified");
                    }
                }
            }
            if (!hasBlob) {
                modifiedBlobs.put(splitBlob.getKey(), "deleted");
            }
        }
        return modifiedBlobs;
    }

    /** Return a file created by annotating difference in two versions of the
     * FILENAME that exist in CURRBLOBID and GIVENBLOBID.*/
    private static void createMergeConflictFile(
            String fileName, String currBlobID, String givenBlobID) {
        String currContents = Utils.readContentsAsString(Utils.getFile
                (Repository.BLOBS,
                        currBlobID));
        String givenContents = Utils.readContentsAsString(
                Utils.getFile(Repository.BLOBS, givenBlobID));
        StringBuilder consolidatedContents =
                new StringBuilder("<<<<<<< HEAD\n");
        consolidatedContents.append(currContents);
        consolidatedContents.append("\n=======\n");
        consolidatedContents.append(givenContents);
        consolidatedContents.append("\n>>>>>>>\n");
        String contents = consolidatedContents.toString();
        File filePath = new File(
                Repository.WORKING + File.separator + fileName);
        Utils.writeContents(filePath, contents);
        new AddCommand(fileName);
    }

    /**Check all failure cases before attempting to merge given
     * branch onto me.*/
    private static void failureCases() {
        if (Repository.getBranchFile(givenBranchName) == null) {
            throw new GitletException("A branch with "
                    + "that name does not exist.");
        }
        if (givenBranchName.equals(currBranchName)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }
        anyUntrackedConflict();
        anyUncommitedChanges();

    }
    /** Check if any files are untracked in working directory but tracked
     * in given branch. */
    private static void anyUntrackedConflict() {
        for (File file : Utils.listFiles(Repository.WORKING)) {
            if (!Repository.isCurrentComitTracked(file.getName())
                    && (givenCommit.getBlobs
                    ().keySet().contains(file.getName()))) {
                throw new GitletException("There is an untracked file in "
                        + "the way; delete it or add it first.");
            }
        }
    }

    /** Check for uncommitted changes in current branch.*/
    private static void anyUncommitedChanges() {
        for (File file : Utils.listFiles(Repository.STAGING)) {
            if (!file.getName().equals("fileToMark")) {
                throw new GitletException("You have uncommitted changes.");
            }
        }
    }

    /**All commits current branch points to, from newest to oldest.*/
    private static ArrayList<Commit> currHistory;

    /**All commits given branch points to, from newest to oldest.*/
    private static ArrayList<Commit> givenHistory;

    /**Most recent ancestor between two commits.*/
    private static Commit splitPoint;

    /**Head commit of current branch.*/
    private static Commit currCommit;

    /**Head commit of given branch.*/
    private static Commit givenCommit;

    /**Current branch name.*/
    private static String currBranchName;

    /**Given branch name.*/
    private static String givenBranchName;
}
