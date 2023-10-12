import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/*
 Author: Bryant Short
 Date: 10-4-23

 REFLECTION ANSWERS:
 1. The biggest obstacle I've had during this project is that I have not been able
 to properly remove the losing candidate from the ballot. I don't know if it's an issue
 with my result() method or my main method(). I went to the library for a drop-in tutor session 10-3-23 to get help,
 but I was told that the cs tutor didn't show up that day. I also struggled on figuring out how to return the printPercentages()
 method with only one decimal.

 2. One lesson I learned while doing this project is that it is best to do each method in steps. Making sure you don't
 have any errors or warnings in your code. By taking my time with each task for a method, I ran into fewer problems
 when it was time to run the code.

 3. I am in favor of ranked choice voting. Ranked choice voting gives allows
 for a broader range of candidates to contend in an election. However, I don't currently
 see ranked choice voting as a realistic option for presidential elections.
 Ranked choice voting might be too confusing for all US voters to understand
 and the process of counting the votes might take a long time as well.
 too long.

 */
public class ElectionResults {

    // the main method works as follows:
    // - provided code (leave this code as is):
    //   - prompts user for file name containing ballot data
    //   - reads data into array (one array item per line in file)
    //   - runs any testing code that you have written
    // - code you need to write:
    //   - execute the Ranked Choice Voting process as outlined
    //     in the project description document by calling the other
    //     methods that you will implement in this project
    public static void main(String[] args) {
        // Establish console Scanner for console input
        Scanner console = new Scanner(System.in);

        // Determine the file name containing the ballot data
        System.out.print("Ballots file: ");
        String fileName = console.nextLine();

        // Read the file contents into an array.  Each array
        // entry corresponds to a line in the file.
        String[] fileContents = getFileContents(fileName);

        // ***********************************************
        // Your code below here: execute the RCV process,
        // ensuring to make use of the remaining methods
        // ***********************************************
        ArrayList<Ballot> ballots = convert(fileContents);

        HashMap<String, Integer> voteTallies = tallies(ballots);

        printCounts(voteTallies);

        Result result = analyze(voteTallies);

        if (result != null) {
            System.out.println(result.getName() + " wins!");
        }
        remove("candidate", ballots);

        printPercentages(voteTallies);
    }

    // Create your methods below here

    public static ArrayList<Ballot> convert(String[] rawData){
        ArrayList<Ballot> ballots = new ArrayList<>();

        for (String data: rawData) {
            String [] contents = data.split(",");
            Ballot ballot = new Ballot();
            for (String candidate : contents) { //Iterating through each candidate in the  contents array
                ballot.addCandidate(candidate);
            }
            ballots.add(ballot); // new ballot objects are stored
        }
        return ballots;
    }

    public static HashMap<String, Integer> tallies(ArrayList<Ballot>ballots){
        HashMap<String, Integer> count = new HashMap<>();

        for (Ballot ballot : ballots) { // iterating through the ballots
            String currentChoice = ballot.getCurrentChoice();
            if (count.containsKey(currentChoice)) { // checking weather the candidate is in the hashmap
                count.put(currentChoice, count.get(currentChoice) + 1);
            } else {
                count.put(currentChoice, 1);
            }
        }
        return count;
    }

    public static int countTotalVotes(HashMap<String, Integer> votes) {
        int totalVotes = 0;
        for (Integer singleVote : votes.values()) { // iterating through the values in the hash map
            totalVotes += singleVote;
        }
        return totalVotes;
    }

    public static Result analyze(HashMap<String, Integer> totalVotes){
        int winThreshold = (countTotalVotes(totalVotes)) / 2 + 1; // what it takes to win the election

        String winner = null;
        String loser = null;

        for (String candidate : totalVotes.keySet()) {
            int candidateVotes = totalVotes.get(candidate);
            if (candidateVotes >= winThreshold) {
                winner = candidate;
            } else if (loser== null || candidateVotes > totalVotes.get(loser)) {
                loser = candidate;
            }
        }
        if (winner != null) {
            return new Result(winner, true);
        } else if (loser != null) {
            return new Result(loser, false);
        } else {
            return null;
        }
    }

    public static void printCounts(HashMap<String, Integer> votes){
        System.out.println("Vote Tallies");
        for (String candidate : votes.keySet()) {
            int voteCount = votes.get(candidate); // getting the vote count for the current candidate
            System.out.println(candidate + ": " + voteCount);
        }
    }

    public static void remove(String name, ArrayList<Ballot> ballots) {
        ArrayList<Integer> exhaustedBallot = new ArrayList<>();

        int index = 0;
        for (Ballot ballot : ballots) { //For each Ballot in the given ArrayList, remove the given candidate name from the Ballot
            ballot.removeCandidate(name);
            if (ballot.isExhausted()) {
                exhaustedBallot.add(index);
            }
            index++;
        }
        for (int i = exhaustedBallot.size() - 1; i >= 0; i--) {
            int indexToRemove = exhaustedBallot.get(i);
            ballots.remove(indexToRemove);
        }
    }

    public static void printPercentages(HashMap<String, Integer> votes){
        int totalVotes = countTotalVotes(votes);
        System.out.println("Vote Percentages");

        for (String candidate : votes.keySet()) {
            double votePercentage = (double) votes.get(candidate) / totalVotes * 100.0; // formula to get percentages
            System.out.println( votePercentage + "%" + " " + candidate);
        }
    }


    // DO NOT edit the methods below. These are provided to help you get started.
    public static String[] getFileContents(String fileName) {

        // first pass: determine number of lines in the file
        Scanner file = getFileScanner(fileName);
        int numLines = 0;
        while (file.hasNextLine()) {
            file.nextLine();
            numLines++;
        }

        // create array to hold the number of lines counted
        String[] contents = new String[numLines];

        // second pass: read each line into array
        file = getFileScanner(fileName);
        for (int i = 0; i < numLines; i++) {
            contents[i] = file.nextLine();
        }

        return contents;
    }


    public static Scanner getFileScanner(String fileName) {
        try {
            FileInputStream textFileStream = new FileInputStream(fileName);
            Scanner inputFile = new Scanner(textFileStream);
            return inputFile;
        }
        catch (IOException ex) {
            System.out.println("Warning: could not open " + fileName);
            return null;
        }
    }
}
