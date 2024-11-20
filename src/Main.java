import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.commons.codec.binary.Hex;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java MD5HashCalculator generatehash <filepath> [buildpaths]");
            System.out.println("Usage: java MD5HashCalculator checkhash <hashfile>");
            return;
        }

        String command = args[0];
        String filePath = args[1];

        if ("generatehash".equals(command)) {
            if (args.length == 2) {
                calculateSingleFileHash(filePath);
            } else if (args.length == 3 && "buildpaths".equals(args[2])) {
                calculateMultipleFileHashes(filePath);
            }
        } else if ("checkhash".equals(command)) {
            checkHashes(filePath);
        }
    }

    private static void calculateSingleFileHash(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            try (PrintWriter out = new PrintWriter("erroredfiles.txt")) {
                out.println(filePath + " not found");
            }
            return;
        }
        String hash = calculateMD5(file);
        try (PrintWriter out = new PrintWriter(filePath + ".md5")) {
            out.println(file.getName() + " md5 = " + hash);
        }
    }

    private static void calculateMultipleFileHashes(String filePath) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new ArrayList<>();

        Path baseDir = Paths.get(filePath).toAbsolutePath().normalize();

        Files.walk(baseDir)
                .filter(Files::isRegularFile)
                .forEach(path -> futures.add(executor.submit(() -> {
                    File file = path.toFile();
                    if (!file.exists()) {
                        return baseDir.relativize(path).toString() + " not found";
                    }
                    String hash = calculateMD5(file);
                    String relativePath = baseDir.relativize(path).toString();
                    return relativePath + " md5 = " + hash;
                })));

        try (PrintWriter out = new PrintWriter(filePath + "_hashes.txt")) {
            for (Future<String> future : futures) {
                out.println(future.get());
            }
        } finally {
            executor.shutdown();
        }
    }

    private static void checkHashes(String hashFilePath) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new ArrayList<>();

        Path baseDir = Paths.get("").toAbsolutePath().normalize();
        List<String> erroredFiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(hashFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" md5 = ");
                String relativePath = parts[0];
                String expectedHash = parts[1];

                futures.add(executor.submit(() -> {
                    File file = baseDir.resolve(relativePath).toFile();
                    if (!file.exists()) {
                        erroredFiles.add(relativePath + " not found");
                        return relativePath + " md5 = " + expectedHash + " calcedmd5 = N/A FALSE";
                    }
                    String calculatedHash = calculateMD5(file);
                    boolean match = expectedHash.equals(calculatedHash);
                    if (!match) {
                        erroredFiles.add(relativePath);
                    }
                    return relativePath + " md5 = " + expectedHash + " calcedmd5 = " + calculatedHash + " " + match;
                }));
            }
        }

        try (PrintWriter out = new PrintWriter(hashFilePath + "_results.txt")) {
            for (Future<String> future : futures) {
                out.println(future.get());
            }
        }

        try (PrintWriter out = new PrintWriter("erroredfiles.txt")) {
            for (String erroredFile : erroredFiles) {
                out.println(erroredFile);
            }
        } finally {
            executor.shutdown();
        }
    }

    private static String calculateMD5(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] md5Bytes = digest.digest();
        return Hex.encodeHexString(md5Bytes);
    }
}


