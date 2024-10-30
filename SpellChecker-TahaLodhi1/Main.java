// Taha Lodhi
// Period 2
// SpellChecker Assignment

class Main {
    public static void main(String[] args) {
        // Create an instance of a SpellChecker that uses
        // the dictionary named 'wordList.txt'
        SpellChecker checker = new SpellChecker("wordList.txt");
        //checker.printdictionary();
      

        // Ask the object to check the spelling of the document englishEssay.txt
        checker.doSpellCheck("englishEssay.txt");
    }
}