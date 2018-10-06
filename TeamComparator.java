/**
 * @author Prasetyo Adhi Putranto (GUID: 2340000P)
 *
 */

import java.util.Comparator;

public class TeamComparator implements Comparator<Team> {

	public TeamComparator() {
	}

	@Override
	public int compare(Team o1, Team o2) {
		int result = 0;
		if (o1.getMatchPoint() == o2.getMatchPoint()) {
			result = o2.getGoalDifference() - o1.getGoalDifference();
		} else {
			result = o2.getMatchPoint() - o1.getMatchPoint();
		}
		return result;
	}

}
