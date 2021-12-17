import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.time.*;

public class WordPuzzle {

    public static void main(String[] args) {
        char[][] grid;
        QuadraticProbingHashTable<String> dictionary = new QuadraticProbingHashTable<>();
        SortedSet<String> result = new TreeSet<>();
        Map<String, Boolean> enhancementMap = new HashMap<>();
        String filename = "dictionary.txt";
        Instant start_time, end_time;

        //Taking user input for rows and columns to create a 2-D grid.
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the rows of the Grid: ");
        int rows = sc.nextInt();

        System.out.println("Enter the columns of the Grid: ");
        int columns = sc.nextInt();

        //Taking input for enhancement.
        System.out.println("Use Enhancement (true/false) : ");
        boolean enhancement = sc.nextBoolean();

        //creating a random grid based on input rows and columns.
        grid = generateGrid(rows, columns);

        //based on user input to use enhancement logic for reading the dictionary and finding words in grid is different.
        if (enhancement) {
            //read the dictionary text file and store the words in hashtable.
            readDictionaryWithEnhancement(filename, enhancementMap);

            //Start timer
            start_time = Instant.now();

            //find the words from the grid in the hash table.
            findWordsInGridWithEnhancement(grid, rows, columns, enhancementMap, result);

        } else {
            //read the dictionary text file and store the words in hashtable.
            readDictionary(filename, dictionary);

            //Start timer
            start_time = Instant.now();

            //find the words from the grid in the hash table.
            findWordsInGrid(grid, rows, columns, dictionary, result);
        }
        //end timer
        end_time = Instant.now();

        //calculate the total duration of search in nanoseconds.
        long duration = Duration.between(start_time, end_time).toNanos();

        System.out.println("Words in the Grids: ");

        //Display the matched words from the result set.
        Iterator<String> itr = result.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }

        System.out.println("Total time taken (nano seconds) :" + duration);

    }

    /*
     * private helper method to iterate the grid in 8 directions to form words and then compare against the
     * quadratic probing hash table to store the matched words in a sorted set.
     */
    private static void findWordsInGrid(char[][] grid, int rows, int columns,
                                        QuadraticProbingHashTable<String> dictionary, Set<String> result) {
        String newWord = "";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                newWord = "";
                //1. Iterating grid in right direction
                for (int k = j; k < columns; k++) {
                    newWord = newWord + String.valueOf(grid[i][k]);
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
                newWord = "";
                //2. Iterating grid in left direction
                for (int k = j; k >= 0; k--) {
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
                newWord = "";
                //3. Iterating grid in downwards direction
                for (int k = i; k < rows; k++) {
                    newWord = newWord + String.valueOf(grid[k][j]);
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
                newWord = "";
                //4. Iterating grid in upwards direction
                for (int k = i; k >= 0; k--) {
                    newWord = newWord + String.valueOf(grid[k][j]);
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
                newWord = "";
                //5. Iterating grid in diagonal down-right direction
                for (int k = i, l = j; k < rows && l < columns; k++, l++) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
                newWord = "";
                //6. Iterating grid in diagonal down-left
                for (int k = i, l = j; k < rows && l >= 0; k++, l--) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
                newWord = "";
                //7. Iterating grid in diagonal up-right
                for (int k = i, l = j; k >= 0 && l < columns; k--, l++) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
                newWord = "";
                //8. Iterating grid in diagonal up-left
                for (int k = i, l = j; k >= 0 && l >= 0; k--, l--) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (dictionary.contains(newWord) && newWord.length() > 1) result.add(newWord);
                }
            }
        }
    }

    /*
     * private helper method to iterate the grid in 8 directions to form words and then compare against the
     * hashmap which stores the words and its prefixes from the dictionary file. the loop will iterate in a
     * particular direction until the prefix of a word is matched and break if the particular prefix is not
     * present and continue to the form and search the words in other direction. The value of the hashmap stores a
     * boolean value indicating if the given word is a prefix or not. If the matched word is not a prefix indicated by
     * the boolean value then the word is added to the sorted set,
     */
    private static void findWordsInGridWithEnhancement(char[][] grid, int rows, int columns,
                                                       Map<String, Boolean> map, Set<String> result) {
        String newWord = "";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                newWord = "";
                //1. Iterating grid in right direction
                for (int k = j; k < columns; k++) {
                    newWord = newWord + String.valueOf(grid[i][k]);
                    //if hashmap contains the prefix we continue in the particular direction.
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                        //if hashmap contains the word we add to the result set.
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                        //if hashmap does not contain the word or the prefix we break from searching in the current direction.
                    } else {
                        break;
                    }
                }
                newWord = "";
                //2. Iterating grid in left direction
                for (int k = j; k >= 0; k--) {
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                    } else {
                        break;
                    }
                }
                newWord = "";
                //3. Iterating grid in downwards direction
                for (int k = i; k < rows; k++) {
                    newWord = newWord + String.valueOf(grid[k][j]);
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                    } else {
                        break;
                    }
                }
                newWord = "";
                //4. Iterating grid in upwards direction
                for (int k = i; k >= 0; k--) {
                    newWord = newWord + String.valueOf(grid[k][j]);
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                    } else {
                        break;
                    }
                }
                newWord = "";
                //5. Iterating grid in diagonal down-right direction
                for (int k = i, l = j; k < rows && l < columns; k++, l++) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                    } else {
                        break;
                    }
                }
                newWord = "";
                //6. Iterating grid in diagonal down-left
                for (int k = i, l = j; k < rows && l >= 0; k++, l--) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                    } else {
                        break;
                    }
                }
                newWord = "";
                //7. Iterating grid in diagonal up-right
                for (int k = i, l = j; k >= 0 && l < columns; k--, l++) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                    } else {
                        break;
                    }
                }
                newWord = "";
                //8. Iterating grid in diagonal up-left
                for (int k = i, l = j; k >= 0 && l >= 0; k--, l--) {
                    newWord = newWord + String.valueOf(grid[k][l]);
                    if (map.containsKey(newWord) && !map.get(newWord)) {
                        continue;
                    } else if (map.containsKey(newWord) && map.get(newWord) && newWord.length() > 1) {
                        result.add(newWord);
                    } else {
                        break;
                    }
                }
            }
        }

    }

    /*
     ** private helper method to generate random grid based on input rows and columns.
     */
    private static char[][] generateGrid(int rows, int columns) {
        char[][] grid = new char[rows][columns];
        System.out.println("Generated Grid (" + rows + "X" + columns + ") :");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = (char) (Math.random() * 26 + 'a');
                System.out.print(grid[i][j] + " ");
            }
            System.out.print('\n');
        }
        return grid;
    }

    /*
     ** private helper method to read from text file using file buffers and add words in each line to a hashtable.
     */
    private static void readDictionary(String filename, QuadraticProbingHashTable<String> dictionary) {
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            String dicWord;
            while ((dicWord = br.readLine()) != null) {

                if (!dictionary.contains(dicWord)) {
                    dictionary.insert(dicWord);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("FILE NOT FOUND");
        }
    }

    /*
     ** private helper method to read from text file using file buffers and add word and its prefix
     * to a hashtable. if the word is a prefix a boolean value of false is used to indicate the value in the hashmap
     * and a boolean value of true indicated it is a word.
     */
    private static void readDictionaryWithEnhancement(String filename, Map<String, Boolean> map) {
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            String dicWord;
            while ((dicWord = br.readLine()) != null) {
                //add the word first.
                map.put(dicWord, true);
                //find the prefixes and add them indicating it's a prefix.
                for (int i = dicWord.length() - 1; i > 0; i--) {
                    String prefix = dicWord.substring(0, i);
                    if (!map.containsKey(prefix))
                        map.put(prefix, false);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("FILE NOT FOUND");
        }
    }
}

