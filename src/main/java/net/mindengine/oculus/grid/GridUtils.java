/*******************************************************************************
* 2012 Ivan Shubin http://mindengine.net
* 
* This file is part of MindEngine.net Oculus Grid.
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with Oculus Grid.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
package net.mindengine.oculus.grid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.mindengine.jeremy.client.Client;
import net.mindengine.jeremy.messaging.LanguageHandler;
import net.mindengine.jeremy.messaging.json.DefaultJsonLanguageHandler;
import net.mindengine.jeremy.registry.Lookup;
import net.mindengine.jeremy.registry.Registry;
import net.mindengine.oculus.experior.utils.XmlUtils;
import net.mindengine.oculus.grid.domain.agent.AgentTag;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GridUtils {

    public static String getMandatoryResourceFile(Class<?> clazz, String resource) throws FileNotFoundException {
        return getMandatoryResource(clazz, resource).getFile();
    }

    public static URL getMandatoryResource(Class<?> clazz, String resource) throws FileNotFoundException {
        URL url = clazz.getResource(resource);
        if (url == null) {
            throw new FileNotFoundException("Cannot find file " + resource);
        }
        return url;
    }

    public static Lookup createDefaultLookup() {
        Lookup lookup = new Lookup();
        lookup.setClient(new Client());
        lookup.setDefaultLanguage(Client.LANGUAGE_JSON);
        Map<String, LanguageHandler> languageHandlers = new HashMap<String, LanguageHandler>();
        languageHandlers.put(Client.LANGUAGE_JSON, new DefaultJsonLanguageHandler());
        lookup.setLanguageHandlers(languageHandlers);
        lookup.setDefaultLanguage(Client.LANGUAGE_JSON);
        return lookup;
    }

    public static Registry createDefaultRegistry() {
        Registry registry = new Registry();
        registry.addLanguageHandler(Client.LANGUAGE_JSON, new DefaultJsonLanguageHandler());
        registry.setDefaultLanguage(Client.LANGUAGE_JSON);
        return registry;
    }

    public static AgentTag[] loadTags(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.parse(file);

        Node rootNode = d.getFirstChild();
        NodeList tagsList = rootNode.getChildNodes();

        ArrayList<AgentTag> agentTags = new ArrayList<AgentTag>();
        for (int i = 0; i < tagsList.getLength(); i++) {
            Node tagNode = tagsList.item(i);
            if (tagNode.getNodeType() == Node.ELEMENT_NODE && "tag".equals(tagNode.getNodeName())) {
                agentTags.add(loadTag(tagNode));
            }
        }
        return agentTags.toArray(new AgentTag[] {});
    }

    private static AgentTag loadTag(Node tagNode) {
        AgentTag tag = new AgentTag();

        String name = XmlUtils.getNodeAttribute(tagNode, "name");
        if (name == null) {
            throw new IllegalArgumentException("'name' attribute for tag is not specified in agent tags");
        }
        String type = XmlUtils.getNodeAttribute(tagNode, "type");
        if (type == null) {
            throw new IllegalArgumentException("'type' attribute for tag '" + name + "' is not specified in agent tags");
        }
        tag.setName(name);
        tag.setType(type);
        
        if (type.equals(AgentTag.STRING)) {
            tag.setValue(tagNode.getTextContent().trim());
        } 
        else if (type.equals(AgentTag.LIST)) {
            NodeList valuesList = tagNode.getChildNodes();
            ArrayList<String> values = new ArrayList<String>();
            for (int i = 0; i < valuesList.getLength(); i++) {
                Node valueNode = valuesList.item(i);
                if(valueNode.getNodeType() == Node.ELEMENT_NODE && "value".equals(valueNode.getNodeName())){
                    values.add(valueNode.getTextContent().trim());
                }
                tag.setValues(values.toArray(new String[]{}));
            }
        } else
            throw new IllegalArgumentException("Unknown type '" + type + "' for tag '" + name + "' in agent tags");

        return tag;
    }
}
