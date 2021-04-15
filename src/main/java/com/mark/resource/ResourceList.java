package com.mark.resource;

import com.mark.Log;
import com.mark.Utils;
import com.mark.io.GsonHandler;
import com.mark.main.IMain;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResourceList {
    public static final String FileExtension = ".mrk";

    private ArrayList<Resource> resources = new ArrayList<>();
    private String root = "";
    private int currentIndex = -1;

    private transient IMain main;
    private transient String filePath;
    private transient boolean modified;

    // silence change notifications (like when deserializing or batch processing)
    private transient boolean silentMode;


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
        return FileExtension.equalsIgnoreCase("."+FilenameUtils.getExtension(filePath));
    }

    public ResourceList(IMain main) {
        this(main, null);
    }

    public ResourceList(IMain main, String filePath) {
        this.main = main;
        this.filePath = filePath;

        if (filePath != null) {
            read();

            // establish the child to parent link when deserialized
            for (Resource resource : resources) {
                resource.setParentList(this);
            }
        }
    }

    public String getRoot() {
        return root == null ? "" : root;
    }

    public String setRoot(String oldRoot, String newRoot) {
        String errorMessage = setRootsForAll(oldRoot, newRoot);
        if (errorMessage != null) {
            return errorMessage;        // NOT OK
        }

        this.root = root;
        modified = true;
        notifyResourceListChange(ResourceListUpdate.AllRowsUpdated);
        return null;        // OK
    }

    public boolean validateRoot(String filePath) {
        return filePath.startsWith(root);
    }

    // returns null if set correctly, error message otherwise
    private String setRootsForAll(String oldRoot, String newRoot) {
        // validate old root
        for (Resource resource : resources) {
            if (!resource.validateRoot(oldRoot)) {
                String errorMessage = String.format("Cannot re-root %s ('%s' -> '%s')", resource.getName(), oldRoot, newRoot);
                return errorMessage;
            }
        }

        // replace old root with new one
        for (Resource resource : resources) {
            resource.normalizePathToRoot(oldRoot);
        }

        // set the new root
        root = newRoot;
        return null;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getName() {
        return filePath == null ? Utils.NoName : filePath;
    }

    public String getShortName() {
        return filePath == null ? Utils.NoName : FilenameUtils.getName(filePath);
    }

    public boolean isDirty() {
        return modified;
    }

    public void setDirty(boolean modified) {
        this.modified = modified;
    }

    public void clearModified() {
        setDirty(false);
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public int size() {
        return resources.size();
    }

    public void read() {
        //Log.log("reading from %s", filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            ResourceList resourceList = GsonHandler.getHandler().fromJson(reader, ResourceList.class);
            //resourceList.dump();
            cloneFrom(resourceList);
        }
        catch (Exception e) {
            Log.err("File '%s' read failed with the error %s.", filePath, e.toString());
        }
    }

    private void cloneFrom(ResourceList source) {
        root = source.getRoot();
        resources = source.getResources();
        currentIndex = source.getCurrentIndex();
    }

    public String writeToString() {
        StringWriter writer = new StringWriter();
        //writer.write(GsonHandler.getHandler().toJson(resources));
        writer.write(GsonHandler.getHandler().toJson(this));
        return writer.toString();
    }

    public void saveAs(String filePath) {
        if (!isFileExtensionMatch(filePath)) {
            filePath += FileExtension;
        }

        File file = new File(filePath);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(writeToString());
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            Log.err("File '%s' creation failed with the error %s.", file.getPath(), e.toString());
            return;
        }

        modified = false;
        this.filePath = filePath;
        notifyResourceListChange(ResourceListUpdate.Saved);
    }

    public void addResourceSilently(Resource resource) {
        resources.add(resource);
    }

    public void addResource(Resource resource) {
        resources.add(resource);
        modified = true;

        int rowIndex = resources.size() - 1;
        notifyResourceListChange(ResourceListUpdate.RowsAdded(rowIndex, rowIndex));
    }

    public void removeResource(int modelIndex) {
        resources.remove(modelIndex);
        notifyResourceListChange(ResourceListUpdate.RowsRemoved(modelIndex, modelIndex));

        // adjust the current index
        setCurrentIndex(-1);
    }

    public void addLegacyResource(Resource resource) {
        resource.postProcessLegacyResource();
        resources.add(resource);
    }

    public void setCurrentIndex(int index) {
        if (index != currentIndex) {
            currentIndex = index;
            modified = true;
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
        if (main != null && !silentMode) {
            main.notifyResourceListChange(this, update);
        }
    }

    public void refreshAllRows() {
        notifyResourceListChange(ResourceListUpdate.AllRowsUpdated);
    }

    public void dump() {
        int n = 0;
        for (Resource resource : resources) {
            Log.log("[%d] %s", ++n, resource.toString());
        }
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
        for (Resource resource : resources) {
            resource.setSilentMode(silentMode);
        }
    }

    public void navigate(boolean forward, boolean favorite) {
        if (resources.size() <= 1) {
            return;
        }

        int step = forward ? 1 : -1;

        if (favorite) {
            for (int i = Utils.mod(currentIndex + step, resources.size()); i != currentIndex; i = Utils.mod(i+step, resources.size())) {
                if (resources.get(i).checked) {
                    setCurrentIndex(i);
                    break;
                }
            }
        }
        else {
            setCurrentIndex(Utils.mod(currentIndex + step, resources.size()));
        }
    }

    // this is the direct communication from the child (one of the resource list members)
    // Resource change is notified by listener notification from Resource, but the parent does not listen to it.
    public void onChildResourceChange(Resource resource, EResourceChangeType changeType) {
        modified = true;

        int index = resources.indexOf(resource);
        if (index >= 0) {
            notifyResourceListChange(ResourceListUpdate.ChildResourceChanged(index, resource, changeType));
        }
    }

    // returns duplicate set number
    public int findDuplicates() {
        // reset the work variable
        for (Resource r : this.resources) {
            File file = new File((r.getPath()));
            if (file.exists()) {
                r.fileSize = file.length();
                r.temp = 0;
            }
            else {
                r.temp = -1;
            }
        }

        // no duplicates by definition
        if (resources.size() < 2) {
            return 0;
        }

        // clone the resources and sort them
        ArrayList<Resource> resources = (ArrayList<Resource>)this.resources.clone();
        Collections.sort(resources, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return o1.fileSize == o2.fileSize ? 0 : o1.fileSize < o2.fileSize ? - 1 : 1;
            }
        });

        // mark duplicates
        int duplicateTag = 1;
        long prev = resources.get(0).fileSize;
        for (int i=1; i<resources.size(); i++) {
            Resource r = resources.get(i);
            if (r.fileSize == prev) {
                if (r.temp != -1) {
                    r.temp = duplicateTag;
                }
                if (resources.get(i-1).temp != -1) {
                    resources.get(i-1).temp = duplicateTag;
                }
            }
            if (prev != r.fileSize && resources.get(i-1).temp > 0) {
                duplicateTag++;
            }
            prev = r.fileSize;
        }

        // test dump
        /*
        for (Resource r : this.resources) {
            Log.log("duplicates?: %s, %,d [%d]", r.getName(), r.fileSize, r.temp);o
        }
        */

        notifyResourceListChange(ResourceListUpdate.AllRowsUpdated);
        return duplicateTag-1;
    }
}