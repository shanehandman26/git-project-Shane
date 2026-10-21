import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;

public class Git {
    public static void init() {
        int filesCreated = 0;
        File gitDir = new File("git");
        if (!gitDir.exists()) {
            gitDir.mkdir();
            filesCreated++;
        }
        File INDEX = new File("git/INDEX");
        if (!INDEX.exists()) {
            try {
                INDEX.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            filesCreated++;
        }
        File objects = new File("git/objects");
        if (!objects.exists()) {
            objects.mkdir();
            filesCreated++;
        }

        File HEAD = new File("git/HEAD");
        if (!HEAD.exists()) {
            try {
                HEAD.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            filesCreated++;
        }
        if (filesCreated > 0) {
            System.out.println("Git Repository Created");
        } else {
            System.out.println("Git Repository Already Exists");
        }

    }

    public static void createBlob(Path FilePath) throws IOException {
        // read file and turn to string
        StringBuilder contentOfFile = new StringBuilder();
        BufferedReader br = Files.newBufferedReader(FilePath);
        while (br.ready()) {
            String line = br.readLine();
            contentOfFile.append(line).append("\n");
        }
        br.close();

        contentOfFile.delete(contentOfFile.length() - 1, contentOfFile.length());

        String strContentOfFile = contentOfFile.toString();
        String hashOfContents = hashSHA1(strContentOfFile);
        File blobFile = new File("git/objects", hashOfContents);
        FileWriter blobWriter = new FileWriter(blobFile);
        blobWriter.write(strContentOfFile);
        blobWriter.close();

        Path pathToIndex = Path.of("git/INDEX");
        BufferedReader indexReader = Files.newBufferedReader(pathToIndex);
        ArrayList<String> linesOfIndex = new ArrayList<>();
        String pathToString = FilePath.toString();
        boolean fileAlreadyIndexed = false;
        while (indexReader.ready()) {
            String line = indexReader.readLine();
            if (line.endsWith(pathToString)) {
                linesOfIndex.add(hashOfContents + " " + FilePath);
                fileAlreadyIndexed = true;
            } else {
                linesOfIndex.add(line);
            }
        }
        indexReader.close();

        if (fileAlreadyIndexed) {
            FileWriter indexWriter = new FileWriter("git/INDEX");
            // for (String line : linesOfIndex) {
            // indexWriter.write(line + "\n");
            // }
            for (int line = 0; line < linesOfIndex.size(); line++) {
                indexWriter.write(linesOfIndex.get(line));
                if (line != linesOfIndex.size() - 1) {
                    indexWriter.write("\n");
                }
            }
            indexWriter.close();
        } else {
            FileWriter fwINDEX = new FileWriter("git/INDEX", true);
            // the true means that the data is being appended
            Path path = Paths.get(FilePath.toString());
            FileReader frINDEX = new FileReader("git/INDEX");
            if (frINDEX.read() == -1) {
                fwINDEX.write(hashOfContents + " " + path.toString());
            } else {
                fwINDEX.write("\n" + hashOfContents + " " + path.toString());
            }
            frINDEX.close();
            fwINDEX.close();

        }
    }

    public static void add(Path FilePath) throws IOException {
        createBlob(FilePath);
    }

    public static String hashSHA1(String input) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            System.out.println("cant");
            return null;
        }
    }

    public static String createTree(ArrayList<String> workingList, String dirPath)
            throws IOException {
        StringBuilder bcontent = new StringBuilder();
        // put in parts, for loop

        // then put all together, probably stringbuilder
        for (String current : workingList) {
            String[] parts = current.split(" ");
            String type = parts[0];
            String hash = parts[1];
            String path = parts[2];
            // fix the checks later, add root check
            String whole = type + " " + hash + " " + path;
            if (whole.contains(" " + dirPath + "/")) {
                String name = path.substring(dirPath.length() + 1);
                String add = type + " " + hash + " " + name + "\n";
                bcontent.append(add);
            } else if (dirPath.equals("")) {
                if (path.contains("/") == false) {
                    String add = type + " " + hash + " " + path + "\n";
                    bcontent.append(add);
                }
            }

        }
        String content = bcontent.toString().substring(0, bcontent.length() - 1);
        FileWriter fw = new FileWriter("git/objects/" + hashSHA1(content));
        fw.write(content);
        fw.close();
        return hashSHA1(content);
    }
    // count slashes to do done

    // built list, blob for each done

    // loop:
    // sort list done
    // find deepest path
    // check if no slashes no slashes then done done
    // else go to the deepest folder done
    // and createtree
    // remove from all of the stuff from the workinglist
    // then add the tree to
    // wokrfjfrjoieefrevreifjrjoiefrjioferjioefojiefrijoferjoiferijoefriojefjoirjfioer
    public static String createTreeFromIndex() throws IOException {

        ArrayList<String> workingList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("git/INDEX"));
        while (br.ready()) {
            String line = br.readLine().toString();
            workingList.add("blob " + line);
        }

        br.close();
        boolean done = false;
        while (done == false) {
            for (int i = 0; i < workingList.size(); i++) {
                int min = i;
                for (int j = i + 1; j < workingList.size(); j++) {
                    String[] parts2 = workingList.get(j).split(" ");
                    String check = parts2[2];
                    String[] parts3 = workingList.get(min).split(" ");
                    String minp = parts3[2];
                    if (minp.compareTo(check) > 0) {
                        min = j;
                    }

                }
                String temp = workingList.get(i);
                workingList.set(i, workingList.get(min));
                workingList.set(min, temp);

            }

            int maxslash = 0;
            String deepestPath = "";
            for (int i = 0; i < workingList.size(); i++) {
                String[] parts = workingList.get(i).split(" ");
                String path = parts[2];
                if (numSlashes(path) > maxslash) {
                    maxslash = numSlashes(path);
                    deepestPath = path;
                }
            }

            if (maxslash == 0) {
                return createTree(workingList, "");
            } else {
                int last = 0;
                for (int j = 0; j < deepestPath.length(); j++) {
                    if (deepestPath.substring(j, j + 1).equals("/")) {
                        last = j;
                    }
                }
                String dir = deepestPath.substring(0, last);
                String tree = createTree(workingList, dir);

                int temp = 0;
                for (int line = workingList.size() - 1; line > -1; line--) {
                    if (workingList.get(line).contains(" " + dir + "/")) {
                        workingList.remove(line);
                        temp = line;
                    }
                }

                workingList.add(temp, "tree " + tree + " " + dir);
            }

        }

        return "";
    }

    public static int numSlashes(String input) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.substring(i, i + 1).equals("/")) {
                count += 1;
            }
        }
        return count;
    }

    // Check README.md
    public static String commit(String author, String message) {
        StringBuilder fileContents = new StringBuilder();
        String tree = null, parent = null, date = null;
        try {
            tree = createTreeFromIndex();
            BufferedReader HEAD = new BufferedReader(new FileReader("git/HEAD"));
            parent = HEAD.readLine();
            HEAD.close();
            date = LocalDate.now().toString();

            if (parent == null) { // Handles first commit
                fileContents.append("tree: " + tree + "\nauthor: " + author
                        + "\ndate: " + date + "\nmessage: " + message);
            } else {
                fileContents.append("tree: " + tree + "\nparent: " + parent + "\nauthor: " + author
                        + "\ndate: " + date + "\nmessage: " + message);
            }

            String strContents = fileContents.toString();
            String commitHash = hashSHA1(strContents);
            FileWriter commitWriter = new FileWriter("git/objects/" + commitHash);
            commitWriter.write(strContents);
            commitWriter.close();

            FileWriter headWriter = new FileWriter("git/HEAD", false);
            headWriter.write(commitHash);
            headWriter.close();

            return commitHash;

        } catch (Exception e) {
            return parent;
        }

    }

    /*
     * After a clone or a checkout, Git throws the old index away and writes a new
     * one from a commit's tree. Uses recursiveFileList() to iterate through the
     * repository's tree and assemble the list of fiels in the index.
     */
    public static void regenerateIndex(String commitHash) throws IOException {
        // BufferedReader HEAD = new BufferedReader(new FileReader("git/HEAD"));
        // String currHead = HEAD.readLine();
        // HEAD.close();

        File commitFile = new File("git/objects/" + commitHash);
        BufferedReader commitReader = new BufferedReader(new FileReader(commitFile));
        String rootTree = commitReader.readLine().split(" ")[1];
        commitReader.close();

        File index = new File("git/INDEX");
        BufferedWriter indexWriter = new BufferedWriter(new FileWriter(index));

        StringBuilder fileList = recursiveFileList(rootTree, "");
        indexWriter.write(fileList.toString());
        indexWriter.close();
    }

    /*
     * Recursively searches for files and returns them as a StringBuilder object for
     * regenerateIndex() to process. Beginning with the tree hash stored in the
     * commit, if a line represents a tree, that directory is opened with the same
     * method. If a line represents a file, that file's hash and path (tracked
     * through recursive calls) are appended to the StringBuilder and returned.
     */
    public static StringBuilder recursiveFileList(String currentTree, String currentPath) throws IOException {
        StringBuilder index = new StringBuilder();
        BufferedReader fileReader = new BufferedReader(new FileReader(new File("git/objects/" + currentTree)));
        String blob;
        while ((blob = fileReader.readLine()) != null) {
            String[] lineParts = blob.split(" ");

            String entry;
            if (lineParts[0].equals("tree")) {
                if (currentPath.equals("")) {
                    entry = recursiveFileList(lineParts[1], lineParts[2]).toString();
                } else {
                    entry = recursiveFileList(lineParts[1], currentPath + "/" + lineParts[2]).toString();
                }

            } else if (lineParts[0].equals("blob")) {
                if (currentPath.equals("")) {
                    entry = lineParts[1] + " " + lineParts[2];
                } else {
                    entry = lineParts[1] + " " + currentPath + "/" + lineParts[2];
                }
            } else {
                System.out.println("Error occured, reached file " + currentPath);
                continue;
            }

            if (entry.isEmpty()) {
                continue;
            }
            if (index.length() > 0) {
                index.append("\n");
            }

            index.append(entry);
        }
        fileReader.close();

        return index;
    }

    public static void checkout(String commitHash) throws IOException {
        BufferedReader indexDeletionReader = new BufferedReader(new FileReader("git/INDEX"));
        String indexLine;
        while ((indexLine = indexDeletionReader.readLine()) != null) {
            String filePath = indexLine.split(" ")[1];
            try {
                File targetFile = new File(filePath);
                targetFile.delete();

                String[] pathParts = filePath.split("/");
                File parentDirectory = new File(pathParts[pathParts.length - 2]);
                parentDirectory.delete();

            } catch (Exception e) {
            }
        }

        indexDeletionReader.close();

        regenerateIndex(commitHash);

        BufferedReader indexCreateReader = new BufferedReader(new FileReader("git/INDEX"));
        while ((indexLine = indexCreateReader.readLine()) != null) {
            String[] lineParts = indexLine.split(" ");
            String[] pathParts = lineParts[1].split("/");
            String workingPath = "";
            for (int directory = 0; directory < pathParts.length; directory++) {
                if (workingPath.length() == 0) {
                    workingPath += pathParts[directory];
                } else {
                    workingPath += "/" + pathParts[directory];
                }
                File createdFile = new File(workingPath);
                if (directory < pathParts.length - 1) {
                    createdFile.mkdir();
                } else {
                    createdFile.createNewFile();
                }
            }

            StringBuilder fileContents = new StringBuilder();
            BufferedReader blobReader = new BufferedReader(new FileReader("git/objects/" + lineParts[0]));
            String blobLine;
            while ((blobLine = blobReader.readLine()) != null) {
                fileContents.append(blobLine);
            }
            blobReader.close();

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(lineParts[1]));
            fileWriter.append(fileContents.toString());
            fileWriter.close();
        }

        indexCreateReader.close();

        FileWriter headWriter = new FileWriter("git/HEAD", false);
        headWriter.write(commitHash);
        headWriter.close();

    }

}
