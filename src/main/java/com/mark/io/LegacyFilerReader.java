package com.mark.io;

import com.mark.play.Log;
import com.mark.play.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import java.io.StringWriter;
import java.util.Date;

public class LegacyFilerReader extends DefaultHandler {
	private StringBuilder data;
	private Resource resource;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].length() > 0) {
            String givenFile = args[0];
            File xmlFile = new File(givenFile);
            if (xmlFile.exists()) {
                Log.log("Given file: %s", givenFile);
                new LegacyFilerReader().read(xmlFile);
            }
            else {
                Log.err("Given file: '%s' does not exist.", givenFile);
            }
        }
        else {
            Log.log("Provide a legacy xml file as the first command line argument.");
        }
    }

    private void read(File xmlFile) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(xmlFile, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//Log.log("start element : %s", qName);

		/*
		if (qName.equalsIgnoreCase("Employee")) {
			// create a new Employee and put it in Map
			String id = attributes.getValue("id");
			// initialize Employee object and set id attribute
			emp = new Employee();
			emp.setId(Integer.parseInt(id));
			// initialize list
			if (empList == null)
				empList = new ArrayList<>();
		} else if (qName.equalsIgnoreCase("name")) {
			// set boolean values for fields, will be used in setting Employee variables
			bName = true;
		} else if (qName.equalsIgnoreCase("age")) {
			bAge = true;
		} else if (qName.equalsIgnoreCase("gender")) {
			bGender = true;
		} else if (qName.equalsIgnoreCase("role")) {
			bRole = true;
		}
		*/

		// create the data container
		data = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
    	//Log.log("end element : %s, %s", qName, data.toString());

		if (qName.equals("Path")) {
		    resource = new Resource(data.toString());
		}
        else if (qName.equals("Rating")) {
            resource.rating = Integer.parseInt(data.toString());
        }
        else if (qName.equals("Checked")) {
            resource.checked = Boolean.parseBoolean(data.toString());
        }
        else if (qName.equals("Duration")) {
            resource.duration = Float.parseFloat(data.toString());
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
            Log.log("resource: %s", resource.toString());
        }
        else if (qName.equals("Position")) {
            resource.addMarker(Float.parseFloat(data.toString()));
        }
        else if (qName.equals("Select")) {
            resource.setMarkerSelect(Boolean.parseBoolean(data.toString()));
        }
	}

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        Log.log("Legacy file parsed; %s", resource.path);

        String json = GsonHandler.toJsonString(this.resource);

        // write to string
        StringWriter writer = new StringWriter();
        writer.write(json);
        String stringValue = writer.toString();
        Log.log("Legacy file to json: %s", stringValue);
    }

    public String toJson() {
        return GsonHandler.toJsonString(this.resource);
    }

    @Override
	public void characters(char ch[], int start, int length) throws SAXException {
		data.append(new String(ch, start, length));
	}
}