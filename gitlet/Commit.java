package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A commit object. Represents a snapshot of a working directory at a
 * particular time.
 * @author Lily Vittayarukskul
 */

public class Commit implements Serializable {

    /** The constructor for the initial commit of a repository. */
    public Commit() {
        commitMsg = "initial commit";
        date = "Thu Jan 1 00:00:00 1970 -0000";
        commitID = setID();
        save();
        Repository.initializeFileToMarkMap();
    }

    /** The constructor for a Commit with message MSG. */
    public Commit(String msg) {

        if (msg.isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }
        if (Repository.getFileToMark().size() == 0) {
            throw new GitletException("No changes added to the commit.");
        }

        commitMsg = msg;
        date = (new SimpleDateFormat(
                "EEE MMM d HH:mm:ss yyyy Z").format(new Date()));
        parents.add(Repository.getCurrentCommit().getID());
        myBlobs = Repository.getCurrentCommit().getBlobs();
        HashMap<String, String> fileToMark = Repository.getFileToMark();
        for (String f : fileToMark.keySet()) {
            if (fileToMark.get(f).equals("add")) {
                String sha = Repository.getFileID(Utils.getFile(Repository
                        .STAGING, f));
                getBlobs().put(f, sha);
                Repository.addToBlobsDir(sha, f);
            } else {
                getBlobs().remove(f);
            }
        }
        commitID = setID();
        save();
        Repository.setHeadOfBranch(Repository.getIDOfHead(), commitID);
        Repository.clearFiles(Repository.STAGING);
        Repository.initializeFileToMarkMap();
    }

    /** The constructor for a Commit resulting from a merge, with message MSG
     * and second parent with SHA-I value P.*/
    public Commit(String msg, String p) {
        this(msg);
        parents.add(p);
    }

    /** Returns my unique SHA-I value. */
    public String setID() {
        byte[] serialized = Utils.serialize(this);
        return Utils.sha1(serialized);
    }

    /** Saves me as a file into the /commits subdirectory of the ./gitlet
     * directory. */
    private void save() {
        File newCommit = new File(Repository.COMMITS
                + File.separator + commitID);
        Utils.writeObject(newCommit, this);
    }

    /** Returns my unique Sha-I hash value. */
    public String getID() {

        return commitID;
    }

    /** Returns my message. */
    public String getMessage() {

        return commitMsg;
    }

    /** Returns the timestamp of my creation. */
    public String getTimestamp() {
        return date;
    }

    /** Returns the mapping of my blob file names to file SHA-I hash values. */
    public HashMap<String, String> getBlobs() {

        return myBlobs;
    }

    /** Returns an list of my parent's SHA-I hash values. */
    public ArrayList<String> getParents() {
        return parents;
    }

    /** My SHA-I hash value. */
    private String commitID;

    /** My message. */
    private String commitMsg;

    /** The date and time of my creation. */
    private String date;

    /** List of SHA-I values of my parent commit(s). If two parents, first
     * element is the parent of the merged branch. */
    private ArrayList<String> parents = new ArrayList<>();

    /** Mapping of blob filenames to blob SHA-I values. */
    private HashMap<String, String> myBlobs = new HashMap<>();

}
