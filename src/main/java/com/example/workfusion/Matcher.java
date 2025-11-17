package com.example.workfusion;

import java.util.*;

/**
 * Matcher class for computing skills overlap between jobs and seekers.
 * Uses Jaccard similarity coefficient to calculate match scores.
 */
public class Matcher {
    
    /**
     * Result class for match operations.
     */
    public static class MatchResult {
        private final Seeker seeker;
        private final Job job;
        private final int overlapCount;
        private final int totalJobSkills;
        private final int totalSeekerSkills;
        private final int score; // 0-100
        
        public MatchResult(Seeker seeker, Job job, int overlapCount, int totalJobSkills, int totalSeekerSkills, int score) {
            this.seeker = seeker;
            this.job = job;
            this.overlapCount = overlapCount;
            this.totalJobSkills = totalJobSkills;
            this.totalSeekerSkills = totalSeekerSkills;
            this.score = score;
        }
        
        // Getters
        public Seeker getSeeker() { return seeker; }
        public Job getJob() { return job; }
        public int getOverlapCount() { return overlapCount; }
        public int getTotalJobSkills() { return totalJobSkills; }
        public int getTotalSeekerSkills() { return totalSeekerSkills; }
        public int getScore() { return score; }
        
        @Override
        public String toString() {
            return String.format("Seeker: %s, Job: %s, Overlap: %d, Score: %d%%", 
                seeker != null ? seeker.getName() : "N/A",
                job != null ? job.getTitle() : "N/A",
                overlapCount, score);
        }
    }
    
    /**
     * Match a single seeker with all jobs.
     * 
     * @param seeker The seeker to match
     * @param jobs The list of jobs to match against
     * @return List of match results sorted by score (highest first)
     */
    public static List<MatchResult> matchSeekerWithJobs(Seeker seeker, List<Job> jobs) {
        List<MatchResult> results = new ArrayList<>();
        
        Set<String> seekerSkills = normalizeSkills(seeker.getSkills());
        
        for (Job job : jobs) {
            Set<String> jobSkills = normalizeSkills(job.getSkills());
            MatchResult result = calculateMatch(seeker, job, seekerSkills, jobSkills);
            results.add(result);
        }
        
        // Sort by score descending
        results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return results;
    }
    
    /**
     * Match a single job with all seekers.
     * 
     * @param job The job to match
     * @param seekers The list of seekers to match against
     * @return List of match results sorted by score (highest first)
     */
    public static List<MatchResult> matchJobWithSeekers(Job job, List<Seeker> seekers) {
        List<MatchResult> results = new ArrayList<>();
        
        Set<String> jobSkills = normalizeSkills(job.getSkills());
        
        for (Seeker seeker : seekers) {
            Set<String> seekerSkills = normalizeSkills(seeker.getSkills());
            MatchResult result = calculateMatch(seeker, job, seekerSkills, jobSkills);
            results.add(result);
        }
        
        // Sort by score descending
        results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return results;
    }
    
    /**
     * Match all seekers with all jobs.
     * 
     * @param seekers The list of seekers
     * @param jobs The list of jobs
     * @return List of match results sorted by score (highest first)
     */
    public static List<MatchResult> matchAll(List<Seeker> seekers, List<Job> jobs) {
        List<MatchResult> results = new ArrayList<>();
        
        for (Seeker seeker : seekers) {
            Set<String> seekerSkills = normalizeSkills(seeker.getSkills());
            
            for (Job job : jobs) {
                Set<String> jobSkills = normalizeSkills(job.getSkills());
                MatchResult result = calculateMatch(seeker, job, seekerSkills, jobSkills);
                results.add(result);
            }
        }
        
        // Sort by score descending
        results.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return results;
    }
    
    /**
     * Calculate match between a seeker and a job.
     * 
     * @param seeker The seeker
     * @param job The job
     * @param seekerSkills Normalized set of seeker skills
     * @param jobSkills Normalized set of job skills
     * @return Match result with score and statistics
     */
    private static MatchResult calculateMatch(Seeker seeker, Job job, Set<String> seekerSkills, Set<String> jobSkills) {
        // Calculate intersection
        Set<String> intersection = new HashSet<>(seekerSkills);
        intersection.retainAll(jobSkills);
        
        int overlapCount = intersection.size();
        int totalJobSkills = jobSkills.size();
        int totalSeekerSkills = seekerSkills.size();
        
        // Calculate Jaccard similarity coefficient
        int unionSize = seekerSkills.size() + jobSkills.size() - overlapCount;
        double jaccard = unionSize == 0 ? 0 : (double) overlapCount / unionSize;
        
        // Convert to percentage (0-100)
        int score = (int) Math.round(jaccard * 100);
        
        return new MatchResult(seeker, job, overlapCount, totalJobSkills, totalSeekerSkills, score);
    }
    
    /**
     * Normalize skills string to a set of trimmed, lowercase skills.
     * 
     * @param skills Comma-separated skills string
     * @return Set of normalized skills
     */
    private static Set<String> normalizeSkills(String skills) {
        Set<String> skillSet = new HashSet<>();
        
        if (skills != null && !skills.trim().isEmpty()) {
            String[] skillArray = skills.split(",");
            
            for (String skill : skillArray) {
                String trimmedSkill = skill.trim().toLowerCase();
                if (!trimmedSkill.isEmpty()) {
                    skillSet.add(trimmedSkill);
                }
            }
        }
        
        return skillSet;
    }
}