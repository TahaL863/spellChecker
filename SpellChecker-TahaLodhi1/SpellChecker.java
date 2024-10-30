// Taha Lodhi
// Period 2
// SpellChecker: Loads a dictionary into an array and
// outputs all the misspelled words of a file to the console window.

import java.io.*;
import java.util.*;

public class SpellChecker {

  // When printing, use SpellChecker.out instead of System.out.
  // Example:
  // SpellChecker.out.println("Document contains " + count + "Error(s).");
  public static PrintStream out = System.out;

  // Use this dictionary throughout your code
  private String[] dictionary;

  // Counter of words we failed to find in dictionary
  private int errorCount;

  // temporary hold for current words idx
  private int cur_idx;

  // This is called a constructor. It gets called every time an instance
  // of type SpellChecker is created. Note that there is no return
  // type at all, not even void!
  // This will load all the words in the dictionary file into an
  // array that will be used as the dictionary of correctly spelled words.
  //
  // Parameters:
  // - dictionaryFilename : relative path to the dictionary file.
  //
  // Pre-conditions:
  // - All words are already normalized
  // - We do not need to know the number of words in the file.
  // - The dictionary words are sorted.
  public SpellChecker(String dictionaryFilename) {
    this.dictionary = putWordsIntoArray(dictionaryFilename);
    this.errorCount = 0;
  }

  // This method takes in a file as a parameter and iterates through its elements
  // and stores their count. It then returns this count
  // Parameters:
  // - filename : relative path and name of the file.
  // - Returns int : the number of words in the file.

  // Pre-conditions:
  // - A valid file that exists at the specified path
  private int getSizeOfFile(String filename) {

    // word counter initialized to 0
    int count = 0;

    // read the path + name into a file object
    File file = new File(filename);

    // try catch block will catch the exception i.e. file not found
    // or invalid path or format
    try {

      // Read the file into a scanner looping through one word at a time
      Scanner read = new Scanner(file);

      while (read.hasNext()) {
        read.next();
        count++;
      }
      return count;
    } catch (FileNotFoundException ex) {
      SpellChecker.out.printf("Cannot find file at %s\n", file.getAbsolutePath());
    }

    // if we got here, then the file was not read successfully
    return 0;
  }

  // This is a helper method to read any file into an array.
  // It will read the file twice. The first time
  // is to simply get a count of words to create the array.
  //
  // Parameters:
  // - filename : relative path to the file to read
  //
  // Return:
  // A String Array with all all the words in it as found in the file.
  //
  // Pre-Conditions:
  // - The file exists
  // - The words in the file are NOT normalized.
  // - Words are "tokens" defined by surrounding whitespace.
  //
  // Post-conditions:
  // - The returned array is filled with each word found in the file.
  // - The file remains unchanged and may be read again if necessary.
  // - None of the words are normalized.

  private String[] putWordsIntoArray(String filename) {
    int sizeOfArray = getSizeOfFile(filename);

    // create array of Strings with size = number of words in file
    String[] localArray = new String[sizeOfArray];

    File file = new File(filename);
    try {
      int counter = 0;
      Scanner read = new Scanner(file);
      while (read.hasNext()) {

        String word = read.next();
        // add word to the array created above
        // Counter tracks the index
        localArray[counter] = word;
        counter++;
      }
      read.close();
    } catch (FileNotFoundException ex) {
      SpellChecker.out.printf("Cannot find file at %s\n", file.getAbsolutePath());
    }

    // returns the String array.
    return localArray;
  }

  // Parameters:
  // - filename : Name of file containing words to run spell check on.
  //
  // This method takes in the file as the parameter, stores it into an array using
  // the putWordsIntoArray function. It then iterates through the array and passes
  // each element to the isWordMisspelled function.

  // Post-conditions:
  // If isWordMisspelled returns true, the errorCount is incremented.
  // At the end it prints a list of all words with errors and their index, total
  // errorCount and total number of words in the file.
  public void doSpellCheck(String filename) {
    String normalword = "";

    String[] words = putWordsIntoArray(filename);
    // SpellChecker.out.println("Lines are " + this.line_ctr);

    for (int i = 0; (i < words.length); i++) {

      // Because array is zero based, each words location is i+1
      this.cur_idx = i + 1;
      if (isWordMisspelled(words[i])) {
        ArrayList<String> suggestions = suggestCorrection(words[i]);
        SpellChecker.out.println("Suggestions are: " + suggestions);
      }

    }
    SpellChecker.out.println("Document contains " + this.errorCount
        + " error(s).");

    // if println is used, it fails the 3rd test
    // SpellChecker.out.println("There are " + words.length + " words in the file.
    // ");

    // If i use printf it passes
    SpellChecker.out.printf("There are %d words in the file.\n", words.length);

    // reset the error counter for subsequent calls
    this.errorCount = 0;

  }

  // This method takes in a single word from the array. It checks if it contains
  // an underscore by passing it through the processHyphon method. Else it passes
  // it through the checkMisspelledHelper method that returns an integer. The
  // method returns true if its greater than -1 else false

  // Parameters:
  // - word : A single word to run spell check on, could be hyphenated.

  // Post-conditions:
  // If isWordMisspelled returns true, the errorCount is incremented.
  // else returns false.
  public boolean isWordMisspelled(String word) {

    if (word.contains("-")) {
      return isHyphonedMisspelled(word);
    } else {

      // remove all non alpha characters from word
      String normalword = normalizer(word);
      int idx = checkMisspelledHelper(normalword);

      if (idx == -1) {
        this.errorCount++;
        SpellChecker.out.printf("Word %3d, %s\n",
            this.cur_idx, normalword);
        return true;
      }

      return false;
    }
  }

  // This method checks if the passed word matches with one in the dictionary.
  // It returns -1 if it is not found and index of word in dictionary if found.
  // - Parameters:
  // normalword - normalized word i.e. contains only alphabets
  // - Preconditions:
  // takes in a normalized word as a parameter.
  // - Post Conditions:
  // if found - return the index of word
  // return -1 if not found
  public int checkMisspelledHelper(String normalword) {
    // check if word is in dictionary while ignoring case
    for (int b = 0; b < dictionary.length; b++) {
      if (normalword.equalsIgnoreCase(this.dictionary[b])) {
        return b;
      }
    }

    return -1;
  }

  // This method takes in hyphenated words and splits them into two seperate
  // words. It passes each word into the checkMisspelledHelper method
  // If either one of the words is mispelled it will increase the errorCount,
  // print them out as a single word and return true.
  // Otherwise it returns false.
  // - Preconditions:
  // takes in a hyphenated word i.e. it must contain a "-"
  // - Parameters
  // takein - A hyphenated string
  // - Postconditions:
  // Error count is updated to reflect whether takein is a misspelled word
  private boolean isHyphonedMisspelled(String takein) {
    String firsthalf = takein.substring(0, takein.indexOf("-"));
    String secondhalf = takein.substring(takein.indexOf("-") + 1, takein.length());

    // remove all non alpha characters from both halves
    firsthalf = normalizer(firsthalf);
    secondhalf = normalizer(secondhalf);
    String fullhword = firsthalf + "-" + secondhalf;

    int idxFirst = checkMisspelledHelper(firsthalf);
    int idxScnd = checkMisspelledHelper(secondhalf);

    // Return true if at least one word is mispelled
    if (idxFirst == -1 || idxScnd == -1) {
      this.errorCount++;
      SpellChecker.out.printf("Word %3d, %s\n", this.cur_idx, fullhword);
      return true;
    }

    else // when both words are found in dictionary then no misspelling
      return false;
  }

  // Just a print method used for debugging
  private void printdictionary() {
    for (int i = 0; i < dictionary.length && i < 10; i++) {
      SpellChecker.out.println(dictionary[i]);
    }
  }

  // Normalizes a word by removing all non-alphabetic characters using regular
  // expression
  // - Parameters
  // intake - a word to be normalized
  // - Postconditions
  // Returns a newstring that only contains alphabets
  public String normalizer(String intake) {
    String newstring = "";

    // Use regular expression to match all non-alphabetic characters
    // and replace with empty string
    newstring = intake.replaceAll("[^a-zA-Z]", "");
    return newstring;
  }

  // Extra credit
  // Go through dictionary finding closely matched words and suggest them
  // - Parameters
  // word : a missspelled word, not found in dictionary
  // - Postconditions
  // Returns an array of strings that might be valid corrections
  public ArrayList<String> suggestCorrection(String word) {

    int minMatch = word.length() - 2;
    int sCount = 0;
    ArrayList<String> suggestionsArray = new ArrayList<String>();

    if (minMatch <= 0) {
      SpellChecker.out.println("Word has to be > 2 charters long for corrections");
    }

    else {
      for (int idx = 0; idx < dictionary.length && sCount<5; idx++) {
        if (compare(word, this.dictionary[idx]) >= minMatch) {
          suggestionsArray.add(this.dictionary[idx]);
          sCount++;
        }
      }
    }
    return suggestionsArray;
  }

  // Compares two words and returns the number of characters that match
  private int compare(String first, String second) {

    int count = 0;
    int f_len = first.length();
    int s_len = second.length();
    int smaller_len = f_len;

    if (s_len < f_len)
      smaller_len = s_len;

    for (int idx = 0; idx < smaller_len; idx++) {
      if (first.charAt(idx) == second.charAt(idx))
        count++;
    }

    return count;
  }

} // end of class