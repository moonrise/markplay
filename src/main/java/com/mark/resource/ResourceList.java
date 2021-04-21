package com.mark.resource;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.Utils;
import com.mark.io.GsonHandler;
import com.mark.io.LegacyFilerReader;
import com.mark.main.IMain;
import com.mark.utils.ProgressDialog;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;

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

    public IMain getMain() {
        return main;
    }

    public void setMain(IMain main) {
        this.main = main;
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

    public boolean checkDuplicatePaths(String filePath) {
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
            resourceList.assignResourceKeys();

            //resourceList.dump();
            cloneFrom(resourceList);

            // cloned resources have the same keys from the de-serialised instance, but it should be OK mostly.
            /*
            for (Resource r : resources) {
                Log.log("resource key: %d", r.key);
            }
            */
        }
        catch (Exception e) {
            Log.err("File '%s' read failed with the error %s.", filePath, e.toString());
        }
    }

    // explicit key assignments to the children Resources may be necessary when de-serialized
    private void assignResourceKeys() {
        for (Resource r : resources) {
            r.assignKey();
        }
    }

    private void cloneFrom(ResourceList source) {
        // cloned resources have the same keys from the de-serialised instance, but it should be OK mostly.
        // we can always change that behavior if needed.
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

    public void addResources(String[] filePaths) {
        if (filePaths.length < 1) {
            return;
        }

        if (!validateRoot(filePaths[0])) {
            main.displayErrorMessage(String.format("Cannot add '%s' because it is not compatible with the root context '%s'.", filePath, getRoot()));
            return;      // all the rest will be likely no good
        }

        int skipped = 0;
        int added = 0;
        String skippedPath = "";
        for (String path : filePaths) {
            if (!Prefs.isAllowDuplicateResourcePath() && isDuplicatePath(path)) {
                skippedPath = path;     // only one instance works which is OK
                skipped++;
                continue;
            }
            added++;
            resources.add(new Resource(path, this));
        }

        if (added > 0) {
            modified = true;
            int rowIndex = resources.size() - filePaths.length;
            notifyResourceListChange(ResourceListUpdate.RowsAdded(rowIndex, resources.size()-1));
            setCurrentIndex(rowIndex);
        }

        if (skipped > 0) {
            if (skipped == 1 && added == 0) {
                main.displayInfoMessage(String.format("%s was not added because duplicate paths are not allowed (see Settings)", skippedPath));
            }
            else {
                main.displayInfoMessage(String.format("%d of %d were not added because duplicate paths are not allowed (see Settings)", skipped, skipped+added));
            }
        }
    }

    private boolean isDuplicatePath(String path) {
        // cannot use ArrayList.indexOf because the equals() of Resource is defined with a unique key, not path
        String filePath = new File(path).getPath();     // normalize the path (Mac, Windows, etc...)
        for (Resource r : resources) {
            if (new File(r.getPath()).getPath().equals(filePath)) {
                return true;
            }
        }
        return false;
    }

    private Resource isDuplicateFileContent(Resource other) {
        for (Resource r : resources) {
            if (r.isFileContentEqual(other)) {
                return r;
            }
        }
        return null;
    }

    public void removeResource(int modelIndex) {
        setCurrentIndex(-1);        // remove the current index before delete
        resources.remove(modelIndex);
        notifyResourceListChange(ResourceListUpdate.RowsRemoved(modelIndex, modelIndex));
    }

    public void mergeResources(String resourceListFilePath) {
        mergeResources(new ResourceList(this.main, resourceListFilePath));
    }

    private void mergeResources(ResourceList source) {
        if (!source.getRoot().equalsIgnoreCase(root)) {
            main.displayErrorMessage(String.format("Cannot merge '%s' because it is not compatible with the root context '%s'.", source.getFilePath(), source.getRoot()));
            return;
        }

        int merged = 0;
        ArrayList<Resource> additions = new ArrayList<>();
        for (Resource sourceResource : source.resources) {
            Resource myResource = isDuplicateFileContent(sourceResource);
            if (myResource != null) {
                myResource.mergeWith(sourceResource);
                merged++;
                Log.log("merged (%d): %s->%s", merged, sourceResource.getName(), myResource.getName());
            }
            else {
                sourceResource.setParentList(this);
                additions.add(sourceResource);
                Log.log("added (%d): %s", additions.size(), sourceResource.getName());
            }
        }

        if (merged > 0) {
            modified = true;
            notifyResourceListChange(ResourceListUpdate.AllRowsUpdated);
        }

        if (additions.size() > 0) {
            for (Resource r : additions) {
                resources.add(r);
            }

            modified = true;
            int rowIndex = resources.size() - additions.size();
            notifyResourceListChange(ResourceListUpdate.RowsAdded(rowIndex, resources.size()-1));
            setCurrentIndex(rowIndex);
        }
    }

    public void addLegacyResource(Resource resource) {
        resource.postProcessLegacyResource();
        resources.add(resource);
    }

    public void mergeLegacyResources(String legacyFilePath) {
        ResourceList converted = new LegacyFilerReader().read(null, new File(legacyFilePath));
        converted.setRoot(root, root);
        mergeResources(converted);
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
    public void findDuplicates() {
        // no duplicates by definition
        if (resources.size() < 2) {
            main.displayInfoMessage("No duplicates by definition (only one resource)");
            return;
        }

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

        // clone the resources and sort them
        ArrayList<Resource> clonedResources = (ArrayList<Resource>)this.resources.clone();
        Collections.sort(clonedResources, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return o1.fileSize == o2.fileSize ? 0 : o1.fileSize > o2.fileSize ? - 1 : 1;
            }
        });

        ProgressDialog progressDialog = new ProgressDialog(main.getAppFrame(), "Find Duplicates", clonedResources.size());

        SwingWorker longWork = new SwingWorker<Integer, Integer>() {
            // mark duplicates
            int duplicateTag = 1;
            int duplicates = 0;
            Resource prev = clonedResources.get(0);

            @Override
            protected Integer doInBackground() throws Exception {
                publish(1);

                for (int i=1; i<clonedResources.size(); i++) {
                    if (isCancelled()) {
                        return -1;
                    }

                    //Log.log("Find duplicate process : %d/%d", i, resources.size());
                    /*
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    */

                    Resource r = clonedResources.get(i);
                    if (r.isFileContentEqual(prev)) {
                        if (r.temp != -1) {
                            r.temp = duplicateTag;
                            duplicates = duplicateTag;
                        }
                        if (prev.temp != -1) {
                            prev.temp = duplicateTag;
                            duplicates = duplicateTag;
                        }
                    }
                    if (prev.fileSize != r.fileSize && prev.temp > 0) {
                        duplicateTag++;
                    }
                    prev = r;

                    publish(i+1);
                }

                return duplicates;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // show the progress status in UI
                int count = chunks.get(chunks.size()-1);
                String statusMessage = String.format("Processing %d/%d...", count, clonedResources.size());
                Log.log(statusMessage);
                //main.getAppFrame().getStatusBar().setStatusText(statusMessage);
                progressDialog.setStatusText(statusMessage);
                progressDialog.setProgressValue(count);
            }

            @Override
            protected void done() {
                String doneMessage = String.format("%d duplicate content found based on file sizes and hashes (see duplicate column).", duplicates);
                Log.log(doneMessage);
                progressDialog.setStatusText(doneMessage);
                progressDialog.cancelToCloseButton();

                notifyResourceListChange(ResourceListUpdate.AllRowsUpdated);

                // test dump
                /*
                for (Resource r : this.resources) {
                    Log.log("duplicates?: %s, %,d [%d]", r.getName(), r.fileSize, r.temp);o
                }
                */
            }
        };

        longWork.execute();
        progressDialog.setCancelListener(new ProgressDialog.ProgressCancelListener() {
            @Override
            public void onProgressCancel() {
                longWork.cancel(false);     // OK even when done (Close button)
            }
        });

        progressDialog.setVisible(true);
    }

    public void clearAllFileSizesAndHashes() {
        for (Resource r : resources) {
            r.clearFileSizeAndHash();
        }
        modified = true;
        notifyResourceListChange(ResourceListUpdate.AllRowsUpdated);
    }
}