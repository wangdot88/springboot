package dangod.springboot.util;

public class FitnessTestCalculator {

    public static Double calculateTotalScore(Double run1000, Double run800, Double run50, 
                                            Integer sitAndReach, Integer standingLongJump, 
                                            Integer sitUp, Integer pullUp, Double bmi) {
        double totalScore = 0;
        
        if (run1000 != null) {
            totalScore += calculateRun1000Score(run1000);
        }
        if (run800 != null) {
            totalScore += calculateRun800Score(run800);
        }
        if (run50 != null) {
            totalScore += calculateRun50Score(run50);
        }
        if (sitAndReach != null) {
            totalScore += calculateSitAndReachScore(sitAndReach);
        }
        if (standingLongJump != null) {
            totalScore += calculateStandingLongJumpScore(standingLongJump);
        }
        if (sitUp != null) {
            totalScore += calculateSitUpScore(sitUp);
        }
        if (pullUp != null) {
            totalScore += calculatePullUpScore(pullUp);
        }
        if (bmi != null) {
            totalScore += calculateBmiScore(bmi);
        }
        
        return totalScore;
    }

    public static String calculateLevel(Double totalScore) {
        if (totalScore == null) {
            return "D";
        }
        
        if (totalScore >= 90) {
            return "A";
        } else if (totalScore >= 80) {
            return "B";
        } else if (totalScore >= 60) {
            return "C";
        } else {
            return "D";
        }
    }

    public static Boolean isQualified(Double totalScore) {
        return totalScore != null && totalScore >= 60;
    }

    private static Double calculateRun1000Score(Double time) {
        if (time <= 3.5) return 20.0;
        if (time <= 3.6) return 19.0;
        if (time <= 3.7) return 18.0;
        if (time <= 3.8) return 17.0;
        if (time <= 3.9) return 16.0;
        if (time <= 4.0) return 15.0;
        if (time <= 4.1) return 14.0;
        if (time <= 4.2) return 13.0;
        if (time <= 4.3) return 12.0;
        if (time <= 4.4) return 11.0;
        if (time <= 4.5) return 10.0;
        if (time <= 4.6) return 9.0;
        if (time <= 4.7) return 8.0;
        if (time <= 4.8) return 7.0;
        if (time <= 4.9) return 6.0;
        if (time <= 5.0) return 5.0;
        if (time <= 5.1) return 4.0;
        if (time <= 5.2) return 3.0;
        if (time <= 5.3) return 2.0;
        if (time <= 5.4) return 1.0;
        return 0.0;
    }

    private static Double calculateRun800Score(Double time) {
        if (time <= 3.3) return 20.0;
        if (time <= 3.4) return 19.0;
        if (time <= 3.5) return 18.0;
        if (time <= 3.6) return 17.0;
        if (time <= 3.7) return 16.0;
        if (time <= 3.8) return 15.0;
        if (time <= 3.9) return 14.0;
        if (time <= 4.0) return 13.0;
        if (time <= 4.1) return 12.0;
        if (time <= 4.2) return 11.0;
        if (time <= 4.3) return 10.0;
        if (time <= 4.4) return 9.0;
        if (time <= 4.5) return 8.0;
        if (time <= 4.6) return 7.0;
        if (time <= 4.7) return 6.0;
        if (time <= 4.8) return 5.0;
        if (time <= 4.9) return 4.0;
        if (time <= 5.0) return 3.0;
        if (time <= 5.1) return 2.0;
        if (time <= 5.2) return 1.0;
        return 0.0;
    }

    private static Double calculateRun50Score(Double time) {
        if (time <= 6.5) return 10.0;
        if (time <= 6.6) return 9.5;
        if (time <= 6.7) return 9.0;
        if (time <= 6.8) return 8.5;
        if (time <= 6.9) return 8.0;
        if (time <= 7.0) return 7.5;
        if (time <= 7.1) return 7.0;
        if (time <= 7.2) return 6.5;
        if (time <= 7.3) return 6.0;
        if (time <= 7.4) return 5.5;
        if (time <= 7.5) return 5.0;
        if (time <= 7.6) return 4.5;
        if (time <= 7.7) return 4.0;
        if (time <= 7.8) return 3.5;
        if (time <= 7.9) return 3.0;
        if (time <= 8.0) return 2.5;
        if (time <= 8.1) return 2.0;
        if (time <= 8.2) return 1.5;
        if (time <= 8.3) return 1.0;
        if (time <= 8.4) return 0.5;
        return 0.0;
    }

    private static Double calculateSitAndReachScore(Integer cm) {
        if (cm >= 25) return 10.0;
        if (cm >= 23) return 9.0;
        if (cm >= 21) return 8.0;
        if (cm >= 19) return 7.0;
        if (cm >= 17) return 6.0;
        if (cm >= 15) return 5.0;
        if (cm >= 13) return 4.0;
        if (cm >= 11) return 3.0;
        if (cm >= 9) return 2.0;
        if (cm >= 7) return 1.0;
        return 0.0;
    }

    private static Double calculateStandingLongJumpScore(Integer cm) {
        if (cm >= 270) return 10.0;
        if (cm >= 260) return 9.0;
        if (cm >= 250) return 8.0;
        if (cm >= 240) return 7.0;
        if (cm >= 230) return 6.0;
        if (cm >= 220) return 5.0;
        if (cm >= 210) return 4.0;
        if (cm >= 200) return 3.0;
        if (cm >= 190) return 2.0;
        if (cm >= 180) return 1.0;
        return 0.0;
    }

    private static Double calculateSitUpScore(Integer count) {
        if (count >= 52) return 10.0;
        if (count >= 48) return 9.0;
        if (count >= 44) return 8.0;
        if (count >= 40) return 7.0;
        if (count >= 36) return 6.0;
        if (count >= 32) return 5.0;
        if (count >= 28) return 4.0;
        if (count >= 24) return 3.0;
        if (count >= 20) return 2.0;
        if (count >= 16) return 1.0;
        return 0.0;
    }

    private static Double calculatePullUpScore(Integer count) {
        if (count >= 18) return 10.0;
        if (count >= 16) return 9.0;
        if (count >= 14) return 8.0;
        if (count >= 12) return 7.0;
        if (count >= 10) return 6.0;
        if (count >= 8) return 5.0;
        if (count >= 6) return 4.0;
        if (count >= 4) return 3.0;
        if (count >= 2) return 2.0;
        if (count >= 1) return 1.0;
        return 0.0;
    }

    private static Double calculateBmiScore(Double bmi) {
        if (bmi >= 18.5 && bmi <= 23.9) return 10.0;
        if (bmi >= 17.5 && bmi <= 24.9) return 9.0;
        if (bmi >= 16.5 && bmi <= 25.9) return 8.0;
        if (bmi >= 15.5 && bmi <= 26.9) return 7.0;
        if (bmi >= 14.5 && bmi <= 27.9) return 6.0;
        if (bmi >= 13.5 && bmi <= 28.9) return 5.0;
        if (bmi >= 12.5 && bmi <= 29.9) return 4.0;
        if (bmi >= 11.5 && bmi <= 30.9) return 3.0;
        if (bmi >= 10.5 && bmi <= 31.9) return 2.0;
        if (bmi >= 9.5 && bmi <= 32.9) return 1.0;
        return 0.0;
    }
}
