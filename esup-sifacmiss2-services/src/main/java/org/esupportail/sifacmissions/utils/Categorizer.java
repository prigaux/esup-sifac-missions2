package org.esupportail.sifacmissions.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitaire permettant de détecter la catégorie associée à un mot clé.
 *
 * @author Florent Cailhol (Anyware Services)
 */
public class Categorizer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Properties words;
    private Map<String, String> dictionary;

    /**
     * Constructeur.
     */
    public Categorizer() {
        this(new Properties());
    }

    /**
     * Constructeur.
     *
     * @param words Liste des mots permettant d'identifier les catégories
     */
    public Categorizer(Properties words) {
        setWords(words);
    }

    /**
     * @return Liste des mots permettant d'identifier les catégories
     */
    public Properties getWords() {
        return words;
    }

    /**
     * @param words Liste des mots permettant d'identifier les catégories
     */
    public void setWords(Properties words) {
        this.words = words;
        this.dictionary = new HashMap<String, String>();

        StringBuilder sb = new StringBuilder("Loaded words:\n");
        Iterator<Entry<Object, Object>> it = words.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Object, Object> entry = it.next();

            String category = entry.getKey().toString().toLowerCase();
            String wordList = entry.getValue().toString().toLowerCase();

            String[] keywords = wordList.replaceAll("\\s+", ",").split(",");

            if (logger.isDebugEnabled()) {
                sb.append("\t" + category + ": " + Arrays.toString(keywords) + "\n");
            }

            for (String word : keywords) {
                dictionary.put(word, category);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug(sb.toString());
        }
    }

    /**
     * Retourne la catégorie associée à un mot clé.
     *
     * @param word Mot clé
     * @return Catégorie ou null si aucune catégorie ne correspond.
     */
    public String getCategory(String word) {
        return getCategory(word, null);
    }

    /**
     * @see #getCategory(String)
     * @param word Mot clé
     * @param hint Indice
     * @return Catégorie ou null si aucune catégorie ne correspond.
     */
    public String getCategory(String word, String hint) {
        String category = dictionary.get(word.toLowerCase());

        if (logger.isDebugEnabled()) {
            String hintText = hint != null ? " (" + hint + ")" : "";

            if (category != null) {
                logger.debug("Found category '" + category + "' using '" + word + "'" + hintText);
            } else {
                logger.debug("No category was found using '" + word + "'" + hintText);
            }
        }

        return category;
    }

}
