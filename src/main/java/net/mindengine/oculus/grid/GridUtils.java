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
        registry.addLanguageHandler(Client.LANGUAGE_BINARY, new DefaultJsonLanguageHandler());
        registry.setDefaultLanguage(Client.LANGUAGE_JSON);
        return registry;
    }

}
