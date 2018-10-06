/**
 * @author Prasetyo Adhi Putranto (GUID: 2340000P)
 *
 */

public class Team {

	private String name;
	private int matchPoint;
	private int won;
	private int lost;
	private int drawn;
	private int goalFor;
	private int goalAgainst;
	
	public Team(String name) {
		matchPoint = 0;
		won = 0;
		lost = 0;
		drawn = 0;
		goalFor = 0;
		goalAgainst = 0;	
		this.name = name;
	}

	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMatchPoint() {
		return matchPoint;
	}

	public int getGoalDifference() {
		return goalFor - goalAgainst;
	}

	public int getWon() {
		return won;
	}

	public void won(int goalOut, int goalIn) {
		this.won++;
		this.goalFor += goalOut;
		this.goalAgainst += goalIn;
		this.matchPoint += 3;
	}

	public int getLost() {
		return lost;
	}

	public void lost(int goalOut, int goalIn) {
		this.lost++;
		this.goalFor += goalOut;
		this.goalAgainst += goalIn;
	}

	public int getDrawn() {
		return drawn;
	}

	public void drawn(int goal) {
		this.drawn++;
		this.goalFor += goal;
		this.goalAgainst += goal;
		this.matchPoint++;
	}

	public int getGoalFor() {
		return goalFor;
	}

	public int getGoalAgainst() {
		return goalAgainst;
	}

}
