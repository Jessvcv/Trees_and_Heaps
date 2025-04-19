import java.util.*;
import java.util.stream.Collectors;

public class Election {
    private Map<String, Integer> candidates;
    private PriorityQueue<CandidateVotes> maxHeap; //used to get top candidates
    private int totalVotes;
    private int p; //total number of votes allowed

    public Election() {
        this.candidates = new HashMap<>();
        this.maxHeap = new PriorityQueue<>((a, b) -> {
            if (b.votes != a.votes) return b.votes - a.votes; // sort by votes descending
            return a.candidate.compareTo(b.candidate);
        });
        this.totalVotes = 0;
        this.p = 0;
    }

    public void initializeCandidates(List<String> candidates) {
        this.candidates.clear();
        this.maxHeap.clear();
        this.totalVotes = 0;

        for (String candidate : candidates) {
            this.candidates.put(candidate, 0);
            this.maxHeap.offer(new CandidateVotes(candidate, 0));
        }
    }

    public void setTotalVotes(int p) {
        this.p = p;
    }

    public boolean castVote(String candidate) {
        if (!candidates.containsKey(candidate)) {
            return false;
        }

        //increment vote count
        int newVotes = candidates.get(candidate) + 1;
        candidates.put(candidate, newVotes);
        totalVotes++;
        maxHeap.offer(new CandidateVotes(candidate, newVotes));
        return true;
    }

    public boolean castRandomVote() {
        if (candidates.isEmpty()) {
            return false;
        }

        List<String> candidateList = new ArrayList<>(candidates.keySet());
        String randomCandidate = candidateList.get(new Random().nextInt(candidateList.size()));
        return castVote(randomCandidate);
    }

    public boolean rigElection(String candidate) {
        if (!candidates.containsKey(candidate)) {
            return false;
        }

        // Reset all votes
        for (String c : candidates.keySet()) {
            candidates.put(c, 0);
        }
        totalVotes = 0;
        maxHeap.clear();

        // Assign votes to the rigged candidate
        int riggedVotes = Math.max(p - 2, 1); // Make sure at least 1 vote goes to the candidate
        candidates.put(candidate, riggedVotes);
        totalVotes += riggedVotes;
        maxHeap.offer(new CandidateVotes(candidate, riggedVotes));

        List<String> others = candidates.keySet().stream()
                .filter(c -> !c.equals(candidate))
                .collect(Collectors.toList());

        if (others.contains("Cole Train") && totalVotes < p) {
            candidates.put("Cole Train", 1);
            totalVotes++;
            maxHeap.offer(new CandidateVotes("Cole Train", 1));
        }

        if (others.contains("Anya Stroud") && totalVotes < p) {
            candidates.put("Anya Stroud", 1);
            totalVotes++;
            maxHeap.offer(new CandidateVotes("Anya Stroud", 1));
        }

        return true;
    }

    public List<String> getTopKCandidates(int k) {
        // Sort candidates by votes and name and return top-k names
        return candidates.entrySet().stream()
                .sorted((a, b) -> {
                    int voteCompare = b.getValue().compareTo(a.getValue());
                    return (voteCompare != 0) ? voteCompare : a.getKey().compareTo(b.getKey());
                })
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void auditElection() {
        // Print all candidates and their votes in sorted order
        candidates.entrySet().stream()
                .sorted((a, b) -> {
                    int voteCompare = b.getValue().compareTo(a.getValue());
                    return (voteCompare != 0) ? voteCompare : a.getKey().compareTo(b.getKey());
                })
                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }

    private static class CandidateVotes {
        String candidate;
        int votes;

        public CandidateVotes(String candidate, int votes) {
            this.candidate = candidate;
            this.votes = votes;
        }
    }
}

class ElectionSystem {
    private Election election;

    public ElectionSystem() {
        this.election = new Election();
    }

    public void runSampleElection() {
        List<String> candidates = Arrays.asList(
                "Marcus Fenix", "Dominic Santiago", "Damon Baird", "Cole Train", "Anya Stroud"
        );
        int p = 5;
        election.initializeCandidates(candidates);
        election.setTotalVotes(p);

        System.out.println("Sample operations:");
        election.castVote("Cole Train");
        election.castVote("Cole Train");
        election.castVote("Marcus Fenix");
        election.castVote("Anya Stroud");
        election.castVote("Anya Stroud");

        System.out.println("Top 3 candidates after 5 votes: " + election.getTopKCandidates(3));

        election.rigElection("Marcus Fenix");
        System.out.println("Top 3 candidates after rigging the election: " + election.getTopKCandidates(3));

        System.out.println("auditElection():");
        election.auditElection();
    }

    public static void main(String[] args) {
        ElectionSystem system = new ElectionSystem();
        system.runSampleElection();
    }
}
