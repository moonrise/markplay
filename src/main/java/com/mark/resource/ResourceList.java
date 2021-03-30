package com.mark.resource;

import com.mark.Log;
import com.mark.Utils;
import com.mark.main.IMain;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class ResourceList {
    public static final String FileExtension = ".mrk";

    private IMain main;

    private ArrayList<Resource> resources = new ArrayList<>();
    private int currentIndex = -1;

    private transient String filePath;
    private transient boolean modified;


    public static void main(String[] args) {
        if (args.length > 0) {
            Log.log("The first parameter is taken as a resource list file.");
            new ResourceList(null, args[0]);
        }
        else {
            new ResourceList(null);
        }
    }

    public static boolean isFileExtensionMatch(String filePath) {
        return filePath.endsWith(FileExtension);
    }

    public static void instantiate(String path) {
        /*
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                Log.err("Given file: '%s' does not exist and creating one failed with error %s.", path, e.toString());
                return null;
            }
            Log.log("Given file: '%s' did not exist. So created an empty list file.", path);
            return new ResourceList(file);
        }
        else {
            return new ResourceList(file);
        }

        Log.log("ResourceList created with the file: %s", path);

        File file = new File("Hello1.txt");

        // creates the file
        file.createNewFile();

        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file);

        // Writes the content to the file
        writer.write("This\n is\n an\n example\n");
        writer.flush();
        writer.close();


        String stringValue = writer.toString();

         */

    }

    public ResourceList(IMain main) {
        this(main, null);
    }

    public ResourceList(IMain main, String filePath) {
        this.main = main;
        this.filePath = filePath;

        if (filePath != null) {
            read();
        }
    }

    public String getName() {
        return filePath == null ? Utils.NoName : FilenameUtils.getBaseName(filePath);
    }

    public boolean isDirty() {
        return modified;
    }

    public void clearModified() {
        modified = false;
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public int size() {
        return resources.size();
    }

    public void read() {
    }

    public void write() {
        StringWriter writer = new StringWriter();
    }

    public void saveAs() {
        File file = new File(filePath);

        try {
            file.createNewFile();
        }
        catch (IOException e) {
            Log.err("File '%s' creation failed with the error %s.", file.getPath(), e.toString());
            return;
        }

        write();
        modified = false;
    }

    public void save() {
        write();
        modified = false;

        /*
        // creates the file
        file.createNewFile();

        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file);

        // Writes the content to the file
        writer.write("This\n is\n an\n example\n");
        writer.flush();
        writer.close();

         */

    }

    public void addResource(Resource resource) {
        resources.add(resource);
        modified = true;

        int rowIndex = resources.size() - 1;
        notifyResourceListChange(ResourceListUpdate.RowsAdded(rowIndex, rowIndex));
    }

    public void setCurrentIndex(int index) {
        if (index != currentIndex) {
            currentIndex = index;
            //Log.log("ResourceList current index changed to %d", currentIndex);
            notifyResourceListChange(ResourceListUpdate.IndexChanged);
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Resource getCurrent() {
        if (resources.size() > 0 && currentIndex > -1) {
            return resources.get(currentIndex);
        }
        return null;
    }

    private void notifyResourceListChange(ResourceListUpdate update) {
        if (main != null) {
            main.notifyResourceListChange(this, update);
        }
    }

    public void dump() {
        int n = 0;
        for (Resource resource : resources) {
            Log.log("[%d] %s", ++n, resource.toString());
        }
    }
}
