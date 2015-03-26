package com.ira;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;

import org.apache.commons.io.FilenameUtils;

public class HideMyFiles {
    private static Path directoryPath;
    private static FileSystem fileSystem;
    private static String fileSystemProvider;

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.printf("The tool expects directory name as a parameter");
            System.exit(-1);
        }

        directoryPath = Paths.get(args[0]).toAbsolutePath();
        fileSystem = directoryPath.getFileSystem();
        fileSystemProvider = fileSystem.provider().toString();

        if (Files.isDirectory(directoryPath)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {

                for (Path path : directoryStream) {
                    if (!Files.isDirectory(path)) {
                        if (fileSystemProvider.contains("Windows")) {
                            Files.setAttribute(path, "dos:hidden", true);
                        } else if (fileSystemProvider.contains("Linux")) {
                            File file = path.toFile();
                            file.renameTo(new File(path.getParent() + "/" + "." + file.getName()));
                        }
                    }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            makeFilesVisible();
        }
    }

    private static void makeFilesVisible() {
        if (Files.isDirectory(directoryPath)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {

                for (Path path : directoryStream) {
                    if (!Files.isDirectory(path)) {
                        if (fileSystemProvider.contains("Windows")) {
                            Files.setAttribute(path, "dos:hidden", false);
                        } else if (fileSystemProvider.contains("Linux")) {
                            File file = path.toFile();
                            String changedName = file.getName();
                            String basename = FilenameUtils.getBaseName(changedName);
                            String extension = FilenameUtils.getExtension(changedName);
                            if (basename.startsWith(".")) {
                                basename = basename.replace(".", "");
                            }
                            file.renameTo(new File(path.getParent() + "/" + basename + "." + extension));
                        }
                    }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
