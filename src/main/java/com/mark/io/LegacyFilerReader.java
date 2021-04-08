package com.mark.io;

import com.mark.Log;
import com.mark.main.IMain;
import com.mark.resource.Resource;
import com.mark.resource.ResourceList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.Date;

public class LegacyFilerReader extends DefaultHandler {
    public static final String FileExtension = ".cpd";

    private ResourceList resourceList;
    private StringBuilder data;
    private Resource resource;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].length() > 0) {
            String givenFile = args[0];
            File xmlFile = new File(givenFile);
            if (xmlFile.exists()) {
                Log.log("Given file: %s", givenFile);
                new LegacyFilerReader().read(null, xmlFile);
            }
            else {
                Log.err("Given file: '%s' does not exist.", givenFile);
            }
        }
        else {
            Log.log("Provide a legacy xml file as the first command line argument.");
        }
    }

    public static boolean isFileExtensionMatch(String filePath) {
        return filePath.endsWith(FileExtension);
    }

    public ResourceList read(IMain main, File xmlFile) {
        try {
            resourceList = new ResourceList(main, null);
            resourceList.setSilentMode(true);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(xmlFile, this);

            resourceList.setSilentMode(false);
            return resourceList;
        } catch (Exception e) {
            e.printStackTrace();
            main.displayErrorMessage("LegacyFileReader error: " + e.toString());
            return null;
        }
    }

    @Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//Log.log("start element : %s", qName);
		data = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
    	//Log.log("end element : %s, %s", qName, data.toString());

		if (qName.equals("Path")) {
		    resource = new Resource(data.toString());
		    resource.setSilentMode(true);       // un-silenced by resourceList when all resources are read
		}
        else if (qName.equals("Rating")) {
            resource.rating = Integer.parseInt(data.toString());
        }
        else if (qName.equals("Checked")) {
            resource.checked = Boolean.parseBoolean(data.toString());
        }
        else if (qName.equals("Duration")) {
            resource.duration = (long)(Float.parseFloat(data.toString())*1000F);
        }
        else if (qName.equals("FileSize")) {
            resource.fileSize = Long.parseLong(data.toString());
        }
        else if (qName.equals("ModifiedTime")) {
            resource.modifiedTime = new Date(data.toString());
        }
        else if (qName.equals("AccessedTime")) {
            resource.accessedTime = new Date(data.toString());
        }
        else if (qName.equals("CResourceItem")) {
            //Log.log("resource: %s", resource.toString());
            resourceList.addLegacyResource(resource);
        }
        else if (qName.equals("Position")) {
            resource.addMarker((long)(Float.parseFloat(data.toString())*1000F));
        }
        else if (qName.equals("Select")) {
            resource.setMarkerSelect(Boolean.parseBoolean(data.toString()));
        }
	}

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        resourceList.clearModified();
        // resourceList.dump();
    }

    @Override
	public void characters(char ch[], int start, int length) throws SAXException {
		data.append(new String(ch, start, length));
	}
}