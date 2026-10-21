import java.io.IOException;

public class Verify {
    public static void main(String[] args) throws IOException {
        GitWrapper git = new GitWrapper();
        // git.init();
        // git.add("commit-test/a.txt");
        // git.commit("Claude", "A"); // 312e0bf9dbe4feeb00a8bde39ecc18f127093697

        // git.add("commit-test/a.txt");
        // git.add("docs/b.txt");
        // git.commit("Claude", "B"); // 074f63cfee22d7f941a390c44a8ac3d9ec62d6b1


        git.checkout("074f63cfee22d7f941a390c44a8ac3d9ec62d6b1");

    }

    // A:
    // fe05bcdcdc4928012781a5f1a2a77cbb5398e106 commit-test/a.txt

    // B:
    // ad782ecdac770fc6eb9a62e44f90873fb97fb26b commit-test/a.txt
    // 272e59e7ce8c9504acf79a13928c89d647145d53 docs/b.txt
}
