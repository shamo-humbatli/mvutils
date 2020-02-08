package mobvey.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shamo Humbatli
 */
public class Serializers {

    private static final Logger logger = Logger.getLogger(Serializers.class.getName());

    private String workingDirectory = "serializations";

    public Serializers() {

    }

    public Serializers(String workingDirectoryPath) {
        workingDirectory = workingDirectoryPath;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public <T extends Serializable> T DeserializeObjectFromFile(String fileName, Class<T> objectType) {
        T result = null;

        String rootPath = GetRootPath();

        try {
            try (FileInputStream fileIn = new FileInputStream(Paths.get(rootPath, fileName).toFile());
                    ObjectInputStream in = new ObjectInputStream(fileIn)) {
                result = objectType.cast(in.readObject());
            }
        } catch (IOException | ClassNotFoundException exp) {
            logger.log(Level.SEVERE, exp.toString());
        }
        return result;
    }

    public void SerializeObjectToFile(Object object, String fileName) {
        try {
            String rootPath = GetRootPath();
            try (FileOutputStream fileOut = new FileOutputStream(Paths.get(rootPath, fileName).toFile()); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(object);
            }
        } catch (IOException exp) {
            logger.log(Level.SEVERE, exp.toString());
        }
    }

    private String GetRootPath() {
        File file = new File(workingDirectory);

        if (!file.exists()) {
            if (file.mkdir()) {
                logger.log(Level.INFO, "Serialization folder created. Path: {0}", file.getAbsolutePath());
            } else {
                logger.log(Level.INFO, "Could not create serialization folder. Path: {0}", file.getAbsolutePath());
            }
        }

        return file.getAbsolutePath();
    }
}
