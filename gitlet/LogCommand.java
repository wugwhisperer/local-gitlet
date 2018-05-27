package gitlet;

import java.util.ArrayList;

/**
 * Processes command log.
 * @author Lily Vittayarukskul
 */
public class LogCommand {

    /** */
    public LogCommand() {

        Commit c = Repository.getCurrentCommit();
        ArrayList<String> p = Repository.getCurrentCommit().getParents();
        Repository.printCommit(c);
        while (p.size() != 0) {
            for (String id : p) {
                Commit parentCommit = Repository.getCommit(id);
                Repository.printCommit(parentCommit);
                c = Repository.getCommit(id);
            }
            p = c.getParents();
        }
    }

}

