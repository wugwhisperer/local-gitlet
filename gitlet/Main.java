package gitlet;

/** Driver class for Gitlet, the mini Git-inspired version-control system.
 *  @author Lily Vittayarukskul
 *  collaborator: Meital Avitan
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        try {
            checkFailureCases(args);
            String command = args[0];
            if (command.equals("add")) {
                check(2, args);
                new AddCommand(args[1]);
                abort();
            } else if (command.equals("branch")) {
                check(2, args);
                new BranchCommand(args[1]);
                abort();
            } else if (command.equals("checkout")) {
                checkoutCases(args);
                abort();
            } else if (command.equals("commit")) {
                check(2, args);
                new Commit(args[1]);
                abort();
            } else if (command.equals("find")) {
                check(2, args);
                new FindCommand(args[1]);
                abort();
            } else if (command.equals("global-log")) {
                check(1, args);
                new GlobalLogCommand();
                abort();
            } else if (command.equals("log")) {
                check(1, args);
                new LogCommand();
                abort();
            } else if (command.equals("merge")) {
                check(2, args);
                new MergeCommand(args[1]);
                abort();
            } else if (command.equals("rm-branch")) {
                check(2, args);
                new RemoveBranchCommand(args[1]);
                abort();
            } else if (command.equals("rm")) {
                check(2, args);
                new RemoveCommand(args[1]);
                abort();
            } else if (command.equals("status")) {
                check(1, args);
                new StatusCommand();
                abort();
            } else if (command.equals("reset")) {
                check(2, args);
                new ResetCommand(args[1]);
                abort();
            } else {
                throw new GitletException("No command with that name "
                        + "exists.");
            }
        } catch (GitletException error) {
            System.out.println(error.getMessage());
            abort();
        }
    }

    /**Process more than one ARGS situation.*/
    private static void checkoutCases(String...args) {
        if (args.length == 2) {
            new CheckoutCommand(args[1], false);
        } else if (args.length == 3) {
            new CheckoutCommand(args[2], true);
        } else if (args.length == 4) {
            new CheckoutCommand(args[1], args[2], args[3]);
        } else {
            throw new GitletException("Incorrect operands.");
        }
    }

    /**Exit system.*/
    private static void abort() {
        System.exit(0);
    }

    /**Check if N ARGS are valid.*/
    private static void check(int n, String...args) {
        if (args.length != n) {
            throw new GitletException("Incorrect operands.");
        }
    }

    /**Try to run string of ARGS.*/
    private  static void checkFailureCases(String... args) {
        if (args.length == 0) {
            throw new GitletException("Please enter a command.");
        }
        if (args[0].equals("init")) {
            if (args.length != 1) {
                throw new GitletException("Incorrect operands.");
            }
            new InitCommand();
            abort();
        } else {
            if (!Repository.isGitletInitialized()) {
                throw new GitletException("Not in an initialized Gitlet "
                        + "directory.");
            }
        }
    }

}
