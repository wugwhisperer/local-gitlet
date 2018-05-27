package gitlet;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Lily Vittayarukskul
 */
public class StatusCommand {

    /***/
    public StatusCommand() {

        ArrayList<String> displayAsStaged = new ArrayList<>();
        ArrayList<String> displayAsRemoved = new ArrayList<>();
        HashMap<String, String> fileToMark = Repository.getFileToMark();
        Repository.displayBranches();
        for (String f : fileToMark.keySet()) {
            if (fileToMark.get(f).equals("add")) {
                displayAsStaged.add(f);
            } else {
                displayAsRemoved.add(f);
            }
        }
        System.out.println();
        Repository.displayStagedFiles(displayAsStaged);
        System.out.println();
        Repository.displayRemovedFiles(displayAsRemoved);
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");

    }
}
