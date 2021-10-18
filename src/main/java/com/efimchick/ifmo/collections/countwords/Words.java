package com.efimchick.ifmo.collections.countwords;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Words {
    private static final String ONE_OR_MANY_WHITESPACE = "\\s+";
    private static final String OUTPUT_FORMAT = "%s - %d%n";
    private static final int MIN_LENGTH = 4;
    private static final int MIN_OCCURS = 10;
    private static final String NON_LATIN_NON_CYRILLIC_CHAR = "[^a-zA-Z\u0430-\u044F\u0410-\u042F]";
    private static final String WHITESPACE = " ";
    public static final String WINDOWS_EOL = "\r\n";
    public static final String UNIX_EOL = "\n";
    public static final String CARRIAGE_RETURN = "\r";

    public String countWords(final List<String> lines) {
        List<String> words = getWordsFromText(lines);
        List<WordEntry> countedWords = countEachWord(words);
        countedWords.sort(new WordEntryComparator());
        StringBuilder output = new StringBuilder();
        for (WordEntry entry : countedWords) {
            if (entry.word.length() >= MIN_LENGTH && entry.counter >= MIN_OCCURS) {
                output.append(String.format(OUTPUT_FORMAT, entry.word, entry.counter));
            }
        }
        removeLastLineFeed(output);
        return normalizeEOL(output.toString());
    }

    private String normalizeEOL(String output) {
        output = output.replace(WINDOWS_EOL, UNIX_EOL);
        return output.replace(CARRIAGE_RETURN, UNIX_EOL);
    }

    private List<String> getWordsFromText(final List<String> lines) {
        List<String> words = new ArrayList<>();
        for (String line : lines) {
            line = line.replaceAll(NON_LATIN_NON_CYRILLIC_CHAR, WHITESPACE);
            Collections.addAll(words, line.toLowerCase().split(ONE_OR_MANY_WHITESPACE));
        }
        return words;
    }

    private List<WordEntry> countEachWord(final List<String> words) {
        Collections.sort(words);
        List<WordEntry> countedWords = new ArrayList<>();
        String wordToCount = words.get(0);
        int counter = 0;
        for (String word : words) {
            if (word.compareTo(wordToCount) == 0) {
                counter++;
            } else {
                countedWords.add(new WordEntry(wordToCount, counter));
                wordToCount = word;
                counter = 1;
            }
        }
        countedWords.add(new WordEntry(wordToCount, counter));
        return countedWords;
    }

    private void removeLastLineFeed(final StringBuilder output) {
        output.delete(output.length() - System.lineSeparator().length(), output.length());
    }

    private static class WordEntry {
        String word;
        int counter;

        public WordEntry(final String word, final int counter) {
            this.word = word;
            this.counter = counter;
        }
    }

    private static class WordEntryComparator implements Comparator<WordEntry> {
        @Override
        public int compare(final WordEntry o1, final WordEntry o2) {
            return o2.counter - o1.counter;
        }
    }
}
