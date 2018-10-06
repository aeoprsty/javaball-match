/**
 * @author Prasetyo Adhi Putranto (GUID: 2340000P)
 *
 */

public class Match {
	
	private boolean firstTime;
	private boolean hasResult;
	private String team1;
	private int team1Score;
	private String team2;
	private int team2Score;
	
	public String getKey2Update() {
		return this.team1 + this.team2 + "|" + this.team2 + this.team1;
	}

	public String toString() {
		return this.team1 + "  v  " + this.team2;
	}
	
	public String getMatchName() {
		return this.team1 + "  v  " + this.team2;
	}

	public Match() {
		firstTime = true;
		hasResult = false;
		team1Score = -1;
		team2Score = -1;
	}
	
	public boolean isHasResult() {
		return hasResult;
	}

	public void setHasResult(boolean hasResult) {
		this.hasResult = hasResult;
	}

	public boolean isFirstTime() {
		return firstTime;
	}
	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	public String getTeam1() {
		return team1;
	}
	public void setTeam1(String team1) {
		this.team1 = team1;
	}
	public int getTeam1Score() {
		return team1Score;
	}
	public void setTeam1Score(int team1Score) {
		this.team1Score = team1Score;
	}
	public String getTeam2() {
		return team2;
	}
	public void setTeam2(String team2) {
		this.team2 = team2;
	}
	public int getTeam2Score() {
		return team2Score;
	}
	public void setTeam2Score(int team2Score) {
		this.team2Score = team2Score;
	}
	
	
}
