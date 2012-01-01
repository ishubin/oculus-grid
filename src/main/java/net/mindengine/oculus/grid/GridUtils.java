/*******************************************************************************
 * 2011 Ivan Shubin http://mindengine.net
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
 * along with Oculus Experior.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.mindengine.oculus.grid;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.mindengine.jeremy.client.Client;
import net.mindengine.jeremy.messaging.LanguageHandler;
import net.mindengine.jeremy.messaging.binary.DefaultBinaryLanguageHandler;
import net.mindengine.jeremy.messaging.json.DefaultJsonLanguageHandler;
import net.mindengine.jeremy.registry.Lookup;
import net.mindengine.jeremy.registry.Registry;

public class GridUtils {
    
    public static String getMandatoryResourceFile(Class<?> clazz, String resource) throws FileNotFoundException {
        return getMandatoryResource(clazz, resource).getFile();
    }

    public static URL getMandatoryResource(Class<?> clazz, String resource) throws FileNotFoundException {
        URL url = clazz.getResource(resource);
        if(url==null) {
            throw new FileNotFoundException("Cannot find file "+resource);
        }
        return url;
    }
    
    public static Lookup createDefaultLookup() {
        Lookup lookup = new Lookup();
        lookup.setClient(new Client());
        lookup.setDefaultLanguage(Client.LANGUAGE_JSON);
        Map<String, LanguageHandler> languageHandlers = new HashMap<String, LanguageHandler>();
        languageHandlers.put(Client.LANGUAGE_BINARY, new DefaultBinaryLanguageHandler());
        languageHandlers.put(Client.LANGUAGE_JSON, new DefaultJsonLanguageHandler());
        lookup.setLanguageHandlers(languageHandlers);
        return lookup;
    }
    
    public static Registry createDefaultRegistry() {
        Registry registry = new Registry();
        registry.addLanguageHandler(Client.LANGUAGE_JSON, new DefaultJsonLanguageHandler());
        registry.addLanguageHandler(Client.LANGUAGE_BINARY, new DefaultBinaryLanguageHandler());
        registry.setDefaultLanguage(Client.LANGUAGE_JSON);
        return registry;
    }

}
